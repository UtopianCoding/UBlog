package com.ld.poetry.controller;


import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.config.LoginCheck;
import com.ld.poetry.config.UResult;
import com.ld.poetry.config.SaveCheck;
import com.ld.poetry.dao.*;
import com.ld.poetry.entity.*;
import com.ld.poetry.service.WebInfoService;
import com.ld.poetry.utils.*;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.ResourcePathVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 *  网站信息表 前端控制器
 * 仅站长可以操作
 */
@RestController
@RequestMapping("/api/webInfo")
public class WebInfoController {

    @Value("${store.type}")
    private String defaultType;

    @Autowired
    private WebInfoService webInfoService;

    @Resource
    private ResourcePathMapper resourcePathMapper;

    @Resource
    private TreeHoleMapper treeHoleMapper;

    @Resource
    private SortMapper sortMapper;

    @Resource
    private LabelMapper labelMapper;

    @Resource
    private HistoryInfoMapper historyInfoMapper;

    @Resource
    private CommonQuery commonQuery;

    @Resource
    private FriendBlogMapper friendBlogMapper;

    /**
     * 更新网站信息
     */
    @LoginCheck(0)
    @PostMapping("/updateWebInfo")
    public UResult<WebInfo> updateWebInfo(@RequestBody WebInfo webInfo) {
        webInfoService.updateById(webInfo);

        LambdaQueryChainWrapper<WebInfo> wrapper = new LambdaQueryChainWrapper<>(webInfoService.getBaseMapper());
        List<WebInfo> list = wrapper.list();
        if (!CollectionUtils.isEmpty(list)) {
            list.get(0).setDefaultStoreType(defaultType);
            PoetryCache.put(CommonConst.WEB_INFO, list.get(0));
        }
        return UResult.success();
    }


    /**
     * 获取网站信息
     */
    @GetMapping("/getWebInfo")
    public UResult<WebInfo> getWebInfo() {
        WebInfo webInfo = (WebInfo) PoetryCache.get(CommonConst.WEB_INFO);
        if (webInfo != null) {
            WebInfo result = new WebInfo();
            BeanUtils.copyProperties(webInfo, result);
            result.setRandomAvatar(null);
            result.setRandomCover(null);
            result.setRandomName(null);
            result.setWaifuJson(null);

            webInfo.setHistoryAllCount(((Long) ((Map<String, Object>) PoetryCache.get(CommonConst.IP_HISTORY_STATISTICS)).get(CommonConst.IP_HISTORY_COUNT)).toString());
            webInfo.setHistoryDayCount(Integer.toString(((List<Map<String, Object>>) ((Map<String, Object>) PoetryCache.get(CommonConst.IP_HISTORY_STATISTICS)).get(CommonConst.IP_HISTORY_HOUR)).size()));
            return UResult.success(result);
        }
        return UResult.success();
    }

    /**
     * 获取网站统计信息
     */
    @LoginCheck(0)
    @GetMapping("/getHistoryInfo")
    public UResult<Map<String, Object>> getHistoryInfo() {
        Map<String, Object> result = new HashMap<>();

        Map<String, Object> history = (Map<String, Object>) PoetryCache.get(CommonConst.IP_HISTORY_STATISTICS);
        List<HistoryInfo> infoList = new LambdaQueryChainWrapper<>(historyInfoMapper)
                .select(HistoryInfo::getIp, HistoryInfo::getUserId, HistoryInfo::getNation, HistoryInfo::getProvince, HistoryInfo::getCity)
                .ge(HistoryInfo::getCreateTime, LocalDateTime.now().with(LocalTime.MIN))
                .list();

        result.put(CommonConst.IP_HISTORY_PROVINCE, history.get(CommonConst.IP_HISTORY_PROVINCE));
        result.put(CommonConst.IP_HISTORY_IP, history.get(CommonConst.IP_HISTORY_IP));
        result.put(CommonConst.IP_HISTORY_COUNT, history.get(CommonConst.IP_HISTORY_COUNT));
        List<Map<String, Object>> ipHistoryCount = (List<Map<String, Object>>) history.get(CommonConst.IP_HISTORY_HOUR);
        result.put("ip_count_yest", ipHistoryCount.stream().map(m -> m.get("ip")).distinct().count());
        result.put("username_yest", ipHistoryCount.stream().map(m -> {
            Object userId = m.get("user_id");
            if (userId != null) {
                User user = commonQuery.getUser(Integer.valueOf(userId.toString()));
                if (user != null) {
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put("avatar", user.getAvatar());
                    userInfo.put("username", user.getUsername());
                    return userInfo;
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList()));
        result.put("ip_count_today", infoList.stream().map(HistoryInfo::getIp).distinct().count());
        result.put("username_today", infoList.stream().map(m -> {
            Integer userId = m.getUserId();
            if (userId != null) {
                User user = commonQuery.getUser(userId);
                if (user != null) {
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put("avatar", user.getAvatar());
                    userInfo.put("username", user.getUsername());
                    return userInfo;
                }
            }
            return null;
        }).filter(Objects::nonNull).collect(Collectors.toList()));

        List<Map<String, Object>> list = infoList.stream()
                .map(HistoryInfo::getProvince).filter(Objects::nonNull)
                .collect(Collectors.groupingBy(m -> m, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("province", entry.getKey());
                    map.put("num", entry.getValue());
                    return map;
                })
                .sorted((o1, o2) -> Long.valueOf(o2.get("num").toString()).compareTo(Long.valueOf(o1.get("num").toString())))
                .collect(Collectors.toList());

        result.put("province_today", list);

        return UResult.success(result);
    }

    /**
     * 获取赞赏
     */
    @GetMapping("/getAdmire")
    public UResult<List<User>> getAdmire() {
        return UResult.success(commonQuery.getAdmire());
    }

    /**
     * 获取分类标签信息
     */
    @GetMapping("/getSortInfo")
    public UResult<List<Sort>> getSortInfo() {
        List<Sort> sortInfo = (List<Sort>) PoetryCache.get(CommonConst.SORT_INFO);
        if (sortInfo != null) {
            return UResult.success(sortInfo);
        }
        return UResult.success();
    }

    /**
     * 获取看板娘消息
     */
    @GetMapping("/getWaifuJson")
    public String getWaifuJson() {
        WebInfo webInfo = (WebInfo) PoetryCache.get(CommonConst.WEB_INFO);
        if (webInfo != null && StringUtils.hasText(webInfo.getWaifuJson())) {
            return webInfo.getWaifuJson();
        }
        return "{}";
    }


    /**
     * 保存
     */
    @LoginCheck(0)
    @PostMapping("/saveResourcePath")
    public UResult saveResourcePath(@RequestBody ResourcePathVO resourcePathVO) {
        if (!StringUtils.hasText(resourcePathVO.getTitle()) || !StringUtils.hasText(resourcePathVO.getType())) {
            return UResult.fail("标题和资源类型不能为空！");
        }
        if (CommonConst.RESOURCE_PATH_TYPE_LOVE_PHOTO.equals(resourcePathVO.getType())) {
            resourcePathVO.setRemark(PoetryUtil.getAdminUser().getId().toString());
        }
        ResourcePath resourcePath = new ResourcePath();
        BeanUtils.copyProperties(resourcePathVO, resourcePath);
        if (resourcePathVO.getClassify().equals("1")){
            FriendBlog friendBlog=new FriendBlog();
            friendBlog.setTitle(resourcePathVO.getTitle());
            friendBlog.setCover(resourcePathVO.getCover());
            friendBlog.setUrl(resourcePathVO.getUrl());
            friendBlog.setIntroduction(resourcePathVO.getIntroduction());
            friendBlog.setState(1);
//            friendBlog.setCreatetime(LocalDateTime.now());
            friendBlog.setClassify("技术博主");
            friendBlogMapper.insert(friendBlog);

        }
        resourcePathMapper.insert(resourcePath);
        return UResult.success();
    }

    /**
     * 保存友链
     */
//    @LoginCheck
    @PostMapping("/saveFriend")
    @SaveCheck
    public UResult saveFriend(@RequestBody ResourcePathVO resourcePathVO) {
        if (!StringUtils.hasText(resourcePathVO.getTitle()) || !StringUtils.hasText(resourcePathVO.getCover()) ||
                !StringUtils.hasText(resourcePathVO.getUrl()) || !StringUtils.hasText(resourcePathVO.getIntroduction())) {
            return UResult.fail("信息不全！");
        }
        FriendBlog friendBlog=new FriendBlog();
        friendBlog.setTitle(resourcePathVO.getTitle());
        friendBlog.setCover(resourcePathVO.getCover());
        friendBlog.setUrl(resourcePathVO.getUrl());
        friendBlog.setIntroduction(resourcePathVO.getIntroduction());
        friendBlog.setState(1);
        friendBlog.setClassify("技术博主");
        friendBlogMapper.insert(friendBlog);
        return UResult.success();


//        ResourcePath friend = new ResourcePath();
//        friend.setClassify(CommonConst.DEFAULT_FRIEND);
//        friend.setTitle(resourcePathVO.getTitle());
//        friend.setIntroduction(resourcePathVO.getIntroduction());
//        friend.setCover(resourcePathVO.getCover());
//        friend.setUrl(resourcePathVO.getUrl());
//        friend.setRemark(PoetryUtil.getUserId().toString());
//        friend.setType(CommonConst.RESOURCE_PATH_TYPE_FRIEND);
//        friend.setStatus(Boolean.FALSE);
//        resourcePathMapper.insert(friend);
//        return UResult.success();
    }

    /**
     * 查询友链
     */
    @GetMapping("/listFriend")
    public UResult<Map<String, List<FriendBlog>>> listFriend() {
        LambdaQueryChainWrapper<FriendBlog> wrapper=new LambdaQueryChainWrapper<>(friendBlogMapper);
        List<FriendBlog> friendBlogs = wrapper.eq(FriendBlog::getState, 1).orderByAsc(FriendBlog::getCreatetime).list();

        Map<String, List<FriendBlog>> collect = new HashMap<>();
        if (!CollectionUtils.isEmpty(friendBlogs)) {
            collect = friendBlogs.stream().map(rp -> {
                FriendBlog friendBlog = new FriendBlog();
                BeanUtils.copyProperties(rp, friendBlog);
                return friendBlog;
            }).collect(Collectors.groupingBy(FriendBlog::getClassify));
        }


//        LambdaQueryChainWrapper<ResourcePath> wrapper = new LambdaQueryChainWrapper<>(resourcePathMapper);
//        List<ResourcePath> resourcePaths = wrapper.eq(ResourcePath::getType, CommonConst.RESOURCE_PATH_TYPE_FRIEND)
//                .eq(ResourcePath::getStatus, Boolean.TRUE)
//                .orderByAsc(ResourcePath::getCreateTime)
//                .list();
//        Map<String, List<ResourcePathVO>> collect = new HashMap<>();
//        if (!CollectionUtils.isEmpty(resourcePaths)) {
//            collect = resourcePaths.stream().map(rp -> {
//                ResourcePathVO resourcePathVO = new ResourcePathVO();
//                BeanUtils.copyProperties(rp, resourcePathVO);
//                return resourcePathVO;
//            }).collect(Collectors.groupingBy(ResourcePathVO::getClassify));
//        }
        return UResult.success(collect);
    }


    /**
     * 删除
     */
    @GetMapping("/deleteResourcePath")
    @LoginCheck(0)
    public UResult deleteResourcePath(@RequestParam("id") Integer id) {
        resourcePathMapper.deleteById(id);
        return UResult.success();
    }


    /**
     * 更新
     */
    @PostMapping("/updateResourcePath")
    @LoginCheck(0)
    public UResult updateResourcePath(@RequestBody ResourcePathVO resourcePathVO) {
        if (!StringUtils.hasText(resourcePathVO.getTitle()) || !StringUtils.hasText(resourcePathVO.getType())) {
            return UResult.fail("标题和资源类型不能为空！");
        }
        if (resourcePathVO.getId() == null) {
            return UResult.fail("Id不能为空！");
        }
        if (CommonConst.RESOURCE_PATH_TYPE_LOVE_PHOTO.equals(resourcePathVO.getType())) {
            resourcePathVO.setRemark(PoetryUtil.getAdminUser().getId().toString());
        }
        ResourcePath resourcePath = new ResourcePath();
        BeanUtils.copyProperties(resourcePathVO, resourcePath);
        resourcePathMapper.updateById(resourcePath);
        return UResult.success();
    }


    /**
     * 查询资源
     */
    @PostMapping("/listResourcePath")
    public UResult<Page> listResourcePath(@RequestBody BaseRequestVO baseRequestVO) {
        LambdaQueryChainWrapper<ResourcePath> wrapper = new LambdaQueryChainWrapper<>(resourcePathMapper);
        wrapper.eq(StringUtils.hasText(baseRequestVO.getResourceType()), ResourcePath::getType, baseRequestVO.getResourceType());
        wrapper.eq(StringUtils.hasText(baseRequestVO.getClassify()), ResourcePath::getClassify, baseRequestVO.getClassify());

        Integer userId = PoetryUtil.getUserId();
        if (!PoetryUtil.getAdminUser().getId().equals(userId)) {
            wrapper.eq(ResourcePath::getStatus, Boolean.TRUE);
        } else {
            wrapper.eq(baseRequestVO.getStatus() != null, ResourcePath::getStatus, baseRequestVO.getStatus());
        }

        OrderItem orderItem = new OrderItem();
        orderItem.setColumn(StringUtils.hasText(baseRequestVO.getOrder()) ? StrUtil.toUnderlineCase(baseRequestVO.getOrder()) : "create_time");
        orderItem.setAsc(!baseRequestVO.isDesc());
        List<OrderItem> orderItemList = new ArrayList<>();
        orderItemList.add(orderItem);
        baseRequestVO.setOrders(orderItemList);

        wrapper.page(baseRequestVO);

        List<ResourcePath> resourcePaths = baseRequestVO.getRecords();
        if (!CollectionUtils.isEmpty(resourcePaths)) {
            List<ResourcePathVO> resourcePathVOs = resourcePaths.stream().map(rp -> {
                ResourcePathVO resourcePathVO = new ResourcePathVO();
                BeanUtils.copyProperties(rp, resourcePathVO);
                return resourcePathVO;
            }).collect(Collectors.toList());
            baseRequestVO.setRecords(resourcePathVOs);
        }
        return UResult.success(baseRequestVO);
    }


    /**
     * 查询音乐
     */
    @GetMapping("/listFunny")
    public UResult<List<Map<String, Object>>> listFunny() {
        QueryWrapper<ResourcePath> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("classify, count(*) as count")
                .eq("status", Boolean.TRUE)
                .eq("type", CommonConst.RESOURCE_PATH_TYPE_FUNNY)
                .groupBy("classify");
        List<Map<String, Object>> maps = resourcePathMapper.selectMaps(queryWrapper);
        return UResult.success(maps);
    }

    @GetMapping("/listPhoto")
    public UResult<List<Map<String,Object>>> listPhoto(){
        QueryWrapper<ResourcePath> queryWrapper=new QueryWrapper<>();
        queryWrapper.eq("type",CommonConst.RESOURCE_PATH_TYPE_PHOTO);
        List<Map<String, Object>> maps = resourcePathMapper.selectMaps(queryWrapper);
        return UResult.success(maps);
    }



    /**
     * 查询收藏
     */
    @GetMapping("/listCollect")
    public UResult<Map<String, List<ResourcePathVO>>> listCollect() {
        LambdaQueryChainWrapper<ResourcePath> wrapper = new LambdaQueryChainWrapper<>(resourcePathMapper);
        List<ResourcePath> resourcePaths = wrapper.eq(ResourcePath::getType, CommonConst.RESOURCE_PATH_TYPE_FAVORITES)
                .eq(ResourcePath::getStatus, Boolean.TRUE)
                .orderByAsc(ResourcePath::getTitle)
                .list();
        Map<String, List<ResourcePathVO>> collect = new HashMap<>();
        if (!CollectionUtils.isEmpty(resourcePaths)) {
            collect = resourcePaths.stream().map(rp -> {
                ResourcePathVO resourcePathVO = new ResourcePathVO();
                BeanUtils.copyProperties(rp, resourcePathVO);
                return resourcePathVO;
            }).collect(Collectors.groupingBy(ResourcePathVO::getClassify));
        }
        return UResult.success(collect);
    }


    /**
     * 保存音乐
     */
    @LoginCheck
    @SaveCheck
    @PostMapping("/saveFunny")
    public UResult saveFunny(@RequestBody ResourcePathVO resourcePathVO) {
        if (!StringUtils.hasText(resourcePathVO.getClassify()) || !StringUtils.hasText(resourcePathVO.getCover()) ||
                !StringUtils.hasText(resourcePathVO.getUrl()) || !StringUtils.hasText(resourcePathVO.getTitle())) {
            return UResult.fail("信息不全！");
        }
        ResourcePath funny = new ResourcePath();
        funny.setClassify(resourcePathVO.getClassify());
        funny.setTitle(resourcePathVO.getTitle());
        funny.setCover(resourcePathVO.getCover());
        funny.setUrl(resourcePathVO.getUrl());
        funny.setType(CommonConst.RESOURCE_PATH_TYPE_FUNNY);
        funny.setStatus(Boolean.FALSE);
        resourcePathMapper.insert(funny);
        return UResult.success();
    }

    /**
     * 查询爱情
     */
    @GetMapping("/listAdminLovePhoto")
    public UResult<List<Map<String, Object>>> listAdminLovePhoto() {
        QueryWrapper<ResourcePath> queryWrapper = new QueryWrapper<>();
        queryWrapper.select("classify, count(*) as count")
                .eq("status", Boolean.TRUE)
                .eq("remark", PoetryUtil.getAdminUser().getId().toString())
                .eq("type", CommonConst.RESOURCE_PATH_TYPE_LOVE_PHOTO)
                .groupBy("classify");
        List<Map<String, Object>> maps = resourcePathMapper.selectMaps(queryWrapper);
        return UResult.success(maps);
    }

    /**
     * 保存爱情
     */
    @LoginCheck
    @SaveCheck
    @PostMapping("/saveLovePhoto")
    public UResult saveLovePhoto(@RequestBody ResourcePathVO resourcePathVO) {
        if (!StringUtils.hasText(resourcePathVO.getClassify()) || !StringUtils.hasText(resourcePathVO.getCover()) ||
                !StringUtils.hasText(resourcePathVO.getTitle())) {
            return UResult.fail("信息不全！");
        }
        ResourcePath lovePhoto = new ResourcePath();
        lovePhoto.setClassify(resourcePathVO.getClassify());
        lovePhoto.setTitle(resourcePathVO.getTitle());
        lovePhoto.setCover(resourcePathVO.getCover());
        lovePhoto.setRemark(PoetryUtil.getUserId().toString());
        lovePhoto.setType(CommonConst.RESOURCE_PATH_TYPE_LOVE_PHOTO);
        lovePhoto.setStatus(Boolean.FALSE);
        resourcePathMapper.insert(lovePhoto);
        return UResult.success();
    }

    /**
     * 保存
     */
    @PostMapping("/saveTreeHole")
    @SaveCheck
    public UResult<TreeHole> saveTreeHole(@RequestBody TreeHole treeHole) {
        if (!StringUtils.hasText(treeHole.getMessage())) {
            return UResult.fail("留言不能为空！");
        }
        treeHoleMapper.insert(treeHole);
        if (!StringUtils.hasText(treeHole.getAvatar())) {
            treeHole.setAvatar(PoetryUtil.getRandomAvatar(null));
        }
        return UResult.success(treeHole);
    }


    /**
     * 删除
     */
    @GetMapping("/deleteTreeHole")
    @LoginCheck(0)
    public UResult deleteTreeHole(@RequestParam("id") Integer id) {
        treeHoleMapper.deleteById(id);
        return UResult.success();
    }


    /**
     * 查询List
     */
    @GetMapping("/listTreeHole")
    public UResult<List<TreeHole>> listTreeHole() {
        List<TreeHole> treeHoles;
        Integer count = new LambdaQueryChainWrapper<>(treeHoleMapper).count();
        if (count > CommonConst.TREE_HOLE_COUNT) {
            int i = new Random().nextInt(count + 1 - CommonConst.TREE_HOLE_COUNT);
            treeHoles = treeHoleMapper.queryAllByLimit(i, CommonConst.TREE_HOLE_COUNT);
        } else {
            treeHoles = new LambdaQueryChainWrapper<>(treeHoleMapper).list();
        }

        treeHoles.forEach(treeHole -> {
            if (!StringUtils.hasText(treeHole.getAvatar())) {
                treeHole.setAvatar(PoetryUtil.getRandomAvatar(treeHole.getId().toString()));
            }
        });
        return UResult.success(treeHoles);
    }


    /**
     * 保存
     */
    @PostMapping("/saveSort")
    @LoginCheck(0)
    public UResult saveSort(@RequestBody Sort sort) {
        if (!StringUtils.hasText(sort.getSortName()) || !StringUtils.hasText(sort.getSortDescription())) {
            return UResult.fail("分类名称和分类描述不能为空！");
        }

        if (sort.getSortType() != null && sort.getSortType() == PoetryEnum.SORT_TYPE_BAR.getCode() && sort.getPriority() == null) {
            return UResult.fail("导航栏分类必须配置优先级！");
        }

        sortMapper.insert(sort);
        List<Sort> sortInfo = commonQuery.getSortInfo();
        if (!CollectionUtils.isEmpty(sortInfo)) {
            PoetryCache.put(CommonConst.SORT_INFO, sortInfo);
        }
        return UResult.success();
    }


    /**
     * 删除
     */
    @GetMapping("/deleteSort")
    @LoginCheck(0)
    public UResult deleteSort(@RequestParam("id") Integer id) {
        sortMapper.deleteById(id);
        List<Sort> sortInfo = commonQuery.getSortInfo();
        if (!CollectionUtils.isEmpty(sortInfo)) {
            PoetryCache.put(CommonConst.SORT_INFO, sortInfo);
        }
        return UResult.success();
    }


    /**
     * 更新
     */
    @PostMapping("/updateSort")
    @LoginCheck(0)
    public UResult updateSort(@RequestBody Sort sort) {
        sortMapper.updateById(sort);
        List<Sort> sortInfo = commonQuery.getSortInfo();
        if (!CollectionUtils.isEmpty(sortInfo)) {
            PoetryCache.put(CommonConst.SORT_INFO, sortInfo);
        }
        return UResult.success();
    }


    /**
     * 查询List
     */
    @GetMapping("/listSort")
    public UResult<List<Sort>> listSort() {
        return UResult.success(new LambdaQueryChainWrapper<>(sortMapper).list());
    }


    /**
     * 保存
     */
    @PostMapping("/saveLabel")
    @LoginCheck(0)
    public UResult saveLabel(@RequestBody Label label) {
        if (!StringUtils.hasText(label.getLabelName()) || !StringUtils.hasText(label.getLabelDescription()) || label.getSortId() == null) {
            return UResult.fail("标签名称和标签描述和分类Id不能为空！");
        }
        labelMapper.insert(label);
        List<Sort> sortInfo = commonQuery.getSortInfo();
        if (!CollectionUtils.isEmpty(sortInfo)) {
            PoetryCache.put(CommonConst.SORT_INFO, sortInfo);
        }
        return UResult.success();
    }


    /**
     * 删除
     */
    @GetMapping("/deleteLabel")
    @LoginCheck(0)
    public UResult deleteLabel(@RequestParam("id") Integer id) {
        labelMapper.deleteById(id);
        List<Sort> sortInfo = commonQuery.getSortInfo();
        if (!CollectionUtils.isEmpty(sortInfo)) {
            PoetryCache.put(CommonConst.SORT_INFO, sortInfo);
        }
        return UResult.success();
    }


    /**
     * 更新
     */
    @PostMapping("/updateLabel")
    @LoginCheck(0)
    public UResult updateLabel(@RequestBody Label label) {
        labelMapper.updateById(label);
        List<Sort> sortInfo = commonQuery.getSortInfo();
        if (!CollectionUtils.isEmpty(sortInfo)) {
            PoetryCache.put(CommonConst.SORT_INFO, sortInfo);
        }
        return UResult.success();
    }


    /**
     * 查询List
     */
    @GetMapping("/listLabel")
    public UResult<List<Label>> listLabel() {
        return UResult.success(new LambdaQueryChainWrapper<>(labelMapper).list());
    }


    /**
     * 查询List
     */
    @GetMapping("/listSortAndLabel")
    public UResult<Map> listSortAndLabel() {
        Map<String, List> map = new HashMap<>();
        map.put("sorts", new LambdaQueryChainWrapper<>(sortMapper).list());
        map.put("labels", new LambdaQueryChainWrapper<>(labelMapper).list());
        return UResult.success(map);
    }
}

