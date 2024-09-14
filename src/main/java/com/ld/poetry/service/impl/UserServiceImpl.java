package com.ld.poetry.service.impl;

import cn.hutool.crypto.SecureUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ld.poetry.config.UResult;
import com.ld.poetry.dao.UserMapper;
import com.ld.poetry.entity.User;
import com.ld.poetry.entity.WebInfo;
import com.ld.poetry.entity.WeiYan;
import com.ld.poetry.handle.PoetryRuntimeException;

import com.ld.poetry.service.UserService;
import com.ld.poetry.service.WeiYanService;
import com.ld.poetry.utils.*;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.SocialUser;
import com.ld.poetry.vo.UserVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;
import org.tio.core.Tio;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;


@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private WeiYanService weiYanService;

    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private MailUtil mailUtil;

    @Value("${user.code.format}")
    private String codeFormat;


    @Autowired
    private UserMapper userMapper;

    @Override
    public UResult<UserVO> login(String account, String password, Boolean isAdmin) {
        password = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(password));


        User one = lambdaQuery().and(wrapper -> wrapper
                .eq(User::getUsername, account)
                .or()
                .eq(User::getEmail, account)
                .or()
                .eq(User::getPhoneNumber, account))
                .eq(User::getPassword, DigestUtils.md5DigestAsHex(password.getBytes()))
                .one();

        if (one == null) {
            return UResult.fail("账号/密码错误，请重新输入！");
        }

        if (!one.getUserStatus()) {
            return UResult.fail("账号被冻结！");
        }

        String adminToken = "";
        String userToken = "";

        if (isAdmin) {
            if (UCache.get(CommonConst.ADMIN_TOKEN + one.getId()) != null) {
                adminToken = (String) UCache.get(CommonConst.ADMIN_TOKEN + one.getId());
            }
        } else {
            if (UCache.get(CommonConst.USER_TOKEN + one.getId()) != null) {
                userToken = (String) UCache.get(CommonConst.USER_TOKEN + one.getId());
            }
        }


        if (isAdmin && !StringUtils.hasText(adminToken)) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            adminToken = CommonConst.ADMIN_ACCESS_TOKEN + uuid;
            UCache.put(adminToken, one, CommonConst.TOKEN_EXPIRE);
            UCache.put(CommonConst.ADMIN_TOKEN + one.getId(), adminToken, CommonConst.TOKEN_EXPIRE);
        } else if (!isAdmin && !StringUtils.hasText(userToken)) {
            String uuid = UUID.randomUUID().toString().replaceAll("-", "");
            userToken = CommonConst.USER_ACCESS_TOKEN + uuid;
            UCache.put(userToken, one, CommonConst.TOKEN_EXPIRE);
            UCache.put(CommonConst.USER_TOKEN + one.getId(), userToken, CommonConst.TOKEN_EXPIRE);
        }


        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(one, userVO);
        userVO.setPassword(null);
        if (isAdmin && one.getUserType() == PoetryEnum.USER_TYPE_ADMIN.getCode()) {
            userVO.setIsBoss(true);
        }

        if (isAdmin) {
            userVO.setAccessToken(adminToken);
        } else {
            userVO.setAccessToken(userToken);
        }
        return UResult.success(userVO);
    }

    @Override
    public UResult exit() {
        String token = UBUtil.getToken();
        Integer userId = UBUtil.getUserId();
        if (token.contains(CommonConst.USER_ACCESS_TOKEN)) {
            UCache.remove(CommonConst.USER_TOKEN + userId);
        } else if (token.contains(CommonConst.ADMIN_ACCESS_TOKEN)) {
            UCache.remove(CommonConst.ADMIN_TOKEN + userId);
        }
        UCache.remove(token);
        return UResult.success();
    }

    @Override
    public UResult<UserVO> regist(UserVO user) {
        String regex = "\\d{11}";
        if (user.getUsername().matches(regex)) {
            return UResult.fail("用户名不能为11位数字！");
        }

        if (user.getUsername().contains("@")) {
            return UResult.fail("用户名不能包含@！");
        }

//        if (StringUtils.hasText(user.getPhoneNumber()) && StringUtils.hasText(user.getEmail())) {
//            return UResult.fail("手机号与邮箱只能选择其中一个！");
//        }
//
//        if (StringUtils.hasText(user.getPhoneNumber())) {
//            Integer codeCache = (Integer) UCache.get(CommonConst.FORGET_PASSWORD + user.getPhoneNumber() + "_1");
//            if (codeCache == null || codeCache != Integer.parseInt(user.getCode())) {
//                return UResult.fail("验证码错误！");
//            }
//            UCache.remove(CommonConst.FORGET_PASSWORD + user.getPhoneNumber() + "_1");
//        } else if (StringUtils.hasText(user.getEmail())) {
//            Integer codeCache = (Integer) UCache.get(CommonConst.FORGET_PASSWORD + user.getEmail() + "_2");
//            if (codeCache == null || codeCache != Integer.parseInt(user.getCode())) {
//                return UResult.fail("验证码错误！");
//            }
//            UCache.remove(CommonConst.FORGET_PASSWORD + user.getEmail() + "_2");
//        } else {
//            return UResult.fail("请输入邮箱或手机号！");
//        }


        user.setPassword(new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(user.getPassword())));

        Integer count = lambdaQuery().eq(User::getUsername, user.getUsername()).count();
        if (count != 0) {
            return UResult.fail("用户名重复！");
        }
        if (StringUtils.hasText(user.getPhoneNumber())) {
            Integer phoneNumberCount = lambdaQuery().eq(User::getPhoneNumber, user.getPhoneNumber()).count();
            if (phoneNumberCount != 0) {
                return UResult.fail("手机号重复！");
            }
        } else if (StringUtils.hasText(user.getEmail())) {
            Integer emailCount = lambdaQuery().eq(User::getEmail, user.getEmail()).count();
            if (emailCount != 0) {
                return UResult.fail("邮箱重复！");
            }
        }

        User u = new User();
        u.setUsername(user.getUsername());
        u.setPhoneNumber(user.getPhoneNumber());
        u.setEmail(user.getEmail());
        u.setPassword(DigestUtils.md5DigestAsHex(user.getPassword().getBytes()));
        u.setAvatar(UBUtil.getRandomAvatar(null));
        save(u);

        User one = lambdaQuery().eq(User::getId, u.getId()).one();

        String userToken = CommonConst.USER_ACCESS_TOKEN + UUID.randomUUID().toString().replaceAll("-", "");
        UCache.put(userToken, one, CommonConst.TOKEN_EXPIRE);
        UCache.put(CommonConst.USER_TOKEN + one.getId(), userToken, CommonConst.TOKEN_EXPIRE);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(one, userVO);
        userVO.setPassword(null);
        userVO.setAccessToken(userToken);

        WeiYan weiYan = new WeiYan();
        weiYan.setUserId(one.getId());
        weiYan.setContent("到此一游");
        weiYan.setType(CommonConst.WEIYAN_TYPE_FRIEND);
        weiYan.setIsPublic(Boolean.TRUE);
        weiYanService.save(weiYan);



        return UResult.success(userVO);
    }

    @Override
    public UResult<UserVO> updateUserInfo(UserVO user) {
        if (StringUtils.hasText(user.getUsername())) {
            String regex = "\\d{11}";
            if (user.getUsername().matches(regex)) {
                return UResult.fail("用户名不能为11位数字！");
            }

            if (user.getUsername().contains("@")) {
                return UResult.fail("用户名不能包含@！");
            }

            Integer count = lambdaQuery().eq(User::getUsername, user.getUsername()).ne(User::getId, UBUtil.getUserId()).count();
            if (count != 0) {
                return UResult.fail("用户名重复！");
            }
        }
        User u = new User();
        u.setId(UBUtil.getUserId());
        u.setUsername(user.getUsername());
        u.setAvatar(user.getAvatar());
        u.setGender(user.getGender());
        u.setIntroduction(user.getIntroduction());
        updateById(u);
        User one = lambdaQuery().eq(User::getId, u.getId()).one();
        UCache.put(UBUtil.getToken(), one, CommonConst.TOKEN_EXPIRE);
        UCache.put(CommonConst.USER_TOKEN + one.getId(), UBUtil.getToken(), CommonConst.TOKEN_EXPIRE);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(one, userVO);
        userVO.setPassword(null);
        userVO.setAccessToken(UBUtil.getToken());
        return UResult.success(userVO);
    }

    @Override
    public UResult getCode(Integer flag) {
        User user = UBUtil.getCurrentUser();
        int i = new Random().nextInt(900000) + 100000;
        if (flag == 1) {
            if (!StringUtils.hasText(user.getPhoneNumber())) {
                return UResult.fail("请先绑定手机号！");
            }

            log.info(user.getId() + "---" + user.getUsername() + "---" + "手机验证码---" + i);
        } else if (flag == 2) {
            if (!StringUtils.hasText(user.getEmail())) {
                return UResult.fail("请先绑定邮箱！");
            }

            log.info(user.getId() + "---" + user.getUsername() + "---" + "邮箱验证码---" + i);

            List<String> mail = new ArrayList<>();
            mail.add(user.getEmail());
            String text = getCodeMail(i);
            WebInfo webInfo = (WebInfo) UCache.get(CommonConst.WEB_INFO);

            AtomicInteger count = (AtomicInteger) UCache.get(CommonConst.CODE_MAIL + mail.get(0));
            if (count == null || count.get() < CommonConst.CODE_MAIL_COUNT) {
                mailUtil.sendMailMessage(mail, "您有一封来自" + (webInfo == null ? "Poetize" : webInfo.getWebName()) + "的回执！", text);
                if (count == null) {
                    UCache.put(CommonConst.CODE_MAIL + mail.get(0), new AtomicInteger(1), CommonConst.CODE_EXPIRE);
                } else {
                    count.incrementAndGet();
                }
            } else {
                return UResult.fail("验证码发送次数过多，请明天再试！");
            }
        }
        UCache.put(CommonConst.USER_CODE + UBUtil.getUserId() + "_" + flag, Integer.valueOf(i), 300);
        return UResult.success();
    }

    @Override
    public UResult getCodeForBind(String place, Integer flag) {
        int i = new Random().nextInt(900000) + 100000;
        if (flag == 1) {
            log.info(place + "---" + "手机验证码---" + i);
        } else if (flag == 2) {
            log.info(place + "---" + "邮箱验证码---" + i);
            List<String> mail = new ArrayList<>();
            mail.add(place);
            String text = getCodeMail(i);
            WebInfo webInfo = (WebInfo) UCache.get(CommonConst.WEB_INFO);

            AtomicInteger count = (AtomicInteger) UCache.get(CommonConst.CODE_MAIL + mail.get(0));
            if (count == null || count.get() < CommonConst.CODE_MAIL_COUNT) {
                mailUtil.sendMailMessage(mail, "您有一封来自" + (webInfo == null ? "Poetize" : webInfo.getWebName()) + "的回执！", text);
                if (count == null) {
                    UCache.put(CommonConst.CODE_MAIL + mail.get(0), new AtomicInteger(1), CommonConst.CODE_EXPIRE);
                } else {
                    count.incrementAndGet();
                }
            } else {
                return UResult.fail("验证码发送次数过多，请明天再试！");
            }
        }
        UCache.put(CommonConst.USER_CODE + UBUtil.getUserId() + "_" + place + "_" + flag, Integer.valueOf(i), 300);
        return UResult.success();
    }

    @Override
    public UResult<UserVO> updateSecretInfo(String place, Integer flag, String code, String password) {
        password = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(password));

        User user = UBUtil.getCurrentUser();
        if ((flag == 1 || flag == 2) && !DigestUtils.md5DigestAsHex(password.getBytes()).equals(user.getPassword())) {
            return UResult.fail("密码错误！");
        }
        if ((flag == 1 || flag == 2) && !StringUtils.hasText(code)) {
            return UResult.fail("请输入验证码！");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        if (flag == 1) {
            Integer count = lambdaQuery().eq(User::getPhoneNumber, place).count();
            if (count != 0) {
                return UResult.fail("手机号重复！");
            }
            Integer codeCache = (Integer) UCache.get(CommonConst.USER_CODE + UBUtil.getUserId() + "_" + place + "_" + flag);
            if (codeCache != null && codeCache.intValue() == Integer.parseInt(code)) {

                UCache.remove(CommonConst.USER_CODE + UBUtil.getUserId() + "_" + place + "_" + flag);

                updateUser.setPhoneNumber(place);
            } else {
                return UResult.fail("验证码错误！");
            }

        } else if (flag == 2) {
            Integer count = lambdaQuery().eq(User::getEmail, place).count();
            if (count != 0) {
                return UResult.fail("邮箱重复！");
            }
            Integer codeCache = (Integer) UCache.get(CommonConst.USER_CODE + UBUtil.getUserId() + "_" + place + "_" + flag);
            if (codeCache != null && codeCache.intValue() == Integer.parseInt(code)) {

                UCache.remove(CommonConst.USER_CODE + UBUtil.getUserId() + "_" + place + "_" + flag);

                updateUser.setEmail(place);
            } else {
                return UResult.fail("验证码错误！");
            }
        } else if (flag == 3) {
            if (DigestUtils.md5DigestAsHex(place.getBytes()).equals(user.getPassword())) {
                updateUser.setPassword(DigestUtils.md5DigestAsHex(password.getBytes()));
            } else {
                return UResult.fail("密码错误！");
            }
        }
        updateById(updateUser);

        User one = lambdaQuery().eq(User::getId, user.getId()).one();
        UCache.put(UBUtil.getToken(), one, CommonConst.TOKEN_EXPIRE);
        UCache.put(CommonConst.USER_TOKEN + one.getId(), UBUtil.getToken(), CommonConst.TOKEN_EXPIRE);

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(one, userVO);
        userVO.setPassword(null);
        return UResult.success(userVO);
    }

    @Override
    public UResult getCodeForForgetPassword(String place, Integer flag) {
        int i = new Random().nextInt(900000) + 100000;
        if (flag == 1) {
            log.info(place + "---" + "手机验证码---" + i);
        } else if (flag == 2) {
            log.info(place + "---" + "邮箱验证码---" + i);

            List<String> mail = new ArrayList<>();
            mail.add(place);
            String text = getCodeMail(i);
            WebInfo webInfo = (WebInfo) UCache.get(CommonConst.WEB_INFO);

            AtomicInteger count = (AtomicInteger) UCache.get(CommonConst.CODE_MAIL + mail.get(0));
            if (count == null || count.get() < CommonConst.CODE_MAIL_COUNT) {
                mailUtil.sendMailMessage(mail, "您有一封来自" + (webInfo == null ? "Poetize" : webInfo.getWebName()) + "的回执！", text);
                if (count == null) {
                    UCache.put(CommonConst.CODE_MAIL + mail.get(0), new AtomicInteger(1), CommonConst.CODE_EXPIRE);
                } else {
                    count.incrementAndGet();
                }
            } else {
                return UResult.fail("验证码发送次数过多，请明天再试！");
            }
        }
        UCache.put(CommonConst.FORGET_PASSWORD + place + "_" + flag, Integer.valueOf(i), 300);
        return UResult.success();
    }

    @Override
    public UResult updateForForgetPassword(String place, Integer flag, String code, String password) {
        password = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(password));

        Integer codeCache = (Integer) UCache.get(CommonConst.FORGET_PASSWORD + place + "_" + flag);
        if (codeCache == null || codeCache != Integer.parseInt(code)) {
            return UResult.fail("验证码错误！");
        }

        UCache.remove(CommonConst.FORGET_PASSWORD + place + "_" + flag);

        if (flag == 1) {
            User user = lambdaQuery().eq(User::getPhoneNumber, place).one();
            if (user == null) {
                return UResult.fail("该手机号未绑定账号！");
            }

            if (!user.getUserStatus()) {
                return UResult.fail("账号被冻结！");
            }

            lambdaUpdate().eq(User::getPhoneNumber, place).set(User::getPassword, DigestUtils.md5DigestAsHex(password.getBytes())).update();
            UCache.remove(CommonConst.USER_CACHE + user.getId().toString());
        } else if (flag == 2) {
            User user = lambdaQuery().eq(User::getEmail, place).one();
            if (user == null) {
                return UResult.fail("该邮箱未绑定账号！");
            }

            if (!user.getUserStatus()) {
                return UResult.fail("账号被冻结！");
            }

            lambdaUpdate().eq(User::getEmail, place).set(User::getPassword, DigestUtils.md5DigestAsHex(password.getBytes())).update();
            UCache.remove(CommonConst.USER_CACHE + user.getId().toString());
        }

        return UResult.success();
    }

    @Override
    public UResult<Page> listUser(BaseRequestVO baseRequestVO) {
        LambdaQueryChainWrapper<User> lambdaQuery = lambdaQuery();

        if (baseRequestVO.getUserStatus() != null) {
            lambdaQuery.eq(User::getUserStatus, baseRequestVO.getUserStatus());
        }

        if (baseRequestVO.getUserType() != null) {
            lambdaQuery.eq(User::getUserType, baseRequestVO.getUserType());
        }

        if (StringUtils.hasText(baseRequestVO.getSearchKey())) {
            lambdaQuery.and(lq -> lq.eq(User::getUsername, baseRequestVO.getSearchKey())
                    .or()
                    .eq(User::getPhoneNumber, baseRequestVO.getSearchKey()));
        }

        lambdaQuery.orderByDesc(User::getCreateTime).page(baseRequestVO);

        List<User> records = baseRequestVO.getRecords();
        if (!CollectionUtils.isEmpty(records)) {
            records.forEach(u -> {
                u.setPassword(null);
                u.setOpenId(null);
            });
        }
        return UResult.success(baseRequestVO);
    }

    @Override
    public UResult<List<UserVO>> getUserByUsername(String username) {
        List<User> users = lambdaQuery().select(User::getId, User::getUsername, User::getAvatar, User::getGender, User::getIntroduction).like(User::getUsername, username).last("limit 5").list();
        List<UserVO> userVOS = users.stream().map(u -> {
            UserVO userVO = new UserVO();
            userVO.setId(u.getId());
            userVO.setUsername(u.getUsername());
            userVO.setAvatar(u.getAvatar());
            userVO.setIntroduction(u.getIntroduction());
            userVO.setGender(u.getGender());
            return userVO;
        }).collect(Collectors.toList());
        return UResult.success(userVOS);
    }

    @Override
    public UResult<UserVO> token(String userToken) {
        userToken = new String(SecureUtil.aes(CommonConst.CRYPOTJS_KEY.getBytes(StandardCharsets.UTF_8)).decrypt(userToken));

        if (!StringUtils.hasText(userToken)) {
            throw new PoetryRuntimeException("未登陆，请登陆后再进行操作！");
        }

        User user = (User) UCache.get(userToken);

        if (user == null) {
            throw new PoetryRuntimeException("登录已过期，请重新登陆！");
        }

        UserVO userVO = new UserVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setPassword(null);

        userVO.setAccessToken(userToken);

        return UResult.success(userVO);
    }

    @Override
    public UResult<UserVO> subscribe(Integer labelId, Boolean flag) {
        UserVO userVO = null;
        User one = lambdaQuery().eq(User::getId, UBUtil.getUserId()).one();
        List<Integer> sub = JSON.parseArray(one.getSubscribe(), Integer.class);
        if (sub == null) sub = new ArrayList<>();
        if (flag) {
            if (!sub.contains(labelId)) {
                sub.add(labelId);
                User user = new User();
                user.setId(one.getId());
                user.setSubscribe(JSON.toJSONString(sub));
                updateById(user);

                userVO = new UserVO();
                BeanUtils.copyProperties(one, userVO);
                userVO.setPassword(null);
                userVO.setSubscribe(user.getSubscribe());
                userVO.setAccessToken(UBUtil.getToken());
            }
        } else {
            if (sub.contains(labelId)) {
                sub.remove(labelId);
                User user = new User();
                user.setId(one.getId());
                user.setSubscribe(JSON.toJSONString(sub));
                updateById(user);

                userVO = new UserVO();
                BeanUtils.copyProperties(one, userVO);
                userVO.setPassword(null);
                userVO.setSubscribe(user.getSubscribe());
                userVO.setAccessToken(UBUtil.getToken());
            }
        }
        return UResult.success(userVO);
    }

    private String getCodeMail(int i) {
        WebInfo webInfo = (WebInfo) UCache.get(CommonConst.WEB_INFO);
        String webName = (webInfo == null ? "Poetize" : webInfo.getWebName());
        return String.format(mailUtil.getMailText(),
                webName,
                String.format(MailUtil.imMail, UBUtil.getAdminUser().getUsername()),
                UBUtil.getAdminUser().getUsername(),
                String.format(codeFormat, i),
                "",
                webName);
    }

    @Override
    public void oauth2Login(SocialUser socialUser) throws Exception {

        //登录和注册合并逻辑
        //备注：视频中“微博”社交不需要Access_token也可以获取用户uid，而gitee则需要再发带token请求
        //2.1 查询当前社交用户的社交账号信息(uid,昵称，性别等)

        Map<String,String> query=new HashMap<>();
        query.put("access_token",socialUser.getAccess_token());
//        https://gitee.com/api/v5/user?access_token=22e75238964d4aac22a6f863c5fb67a3
        HttpResponse response = HttpUtils.doGet("https://gitee.com", "/api/v5/user", "get", new HashMap<String, String>(), query);

        if (response.getStatusLine().getStatusCode()==200){
            String json= EntityUtils.toString(response.getEntity());
            JSONObject jsonObject = JSON.parseObject(json);
            System.out.println("返回的json数据为:"+jsonObject);
            String id = jsonObject.get("id").toString();
            //1.判断当前社交用户是否已经登陆过系统
            User memberEntity = this.baseMapper.selectOne(new QueryWrapper<User>().eq("social_id", id));
            if (memberEntity!=null){
                //1、用户已有注册记录，更新信息
                User updateMemberEntity = new User();
                updateMemberEntity.setId(memberEntity.getId());
                updateMemberEntity.setAccessToken(socialUser.getAccess_token());
                updateMemberEntity.setExpiresIn(socialUser.getExpires_in());
                //...其他的不重要，更不更新无所谓
                updateById(updateMemberEntity);

                memberEntity.setAccessToken(socialUser.getAccess_token());
                memberEntity.setExpiresIn(socialUser.getExpires_in());

            }else {
                //2.没有查到当前社交用户记录，就需要注册一个

                User insertMemberEntity = new User();
                try{
                    String name = jsonObject.get("name").toString();
                    String email = jsonObject.get("email").toString();
                    String socialId = jsonObject.get("id").toString();
                    //.....等等信息
                    insertMemberEntity.setUsername(name);
                    insertMemberEntity.setEmail(email);
                    insertMemberEntity.setSocialId(socialId);

                }catch (Exception e){
                    /**
                     * 远程查询昵称这些不重要的，即是出现问题也可以忽略
                     */
                }
                insertMemberEntity.setAccessToken(socialUser.getAccess_token());
                insertMemberEntity.setExpiresIn(socialUser.getExpires_in());
                System.out.println(insertMemberEntity);
                userMapper.insert(insertMemberEntity);

            }
        }

    }
}
