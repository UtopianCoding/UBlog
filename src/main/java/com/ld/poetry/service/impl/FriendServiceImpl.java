package com.ld.poetry.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ld.poetry.config.UResult;
import com.ld.poetry.dao.FriendBlogMapper;
import com.ld.poetry.dao.FriendCommentMapper;
import com.ld.poetry.entity.*;
import com.ld.poetry.service.FriendCommentService;
import com.ld.poetry.service.FriendService;
import com.ld.poetry.service.SysConfigService;
import com.ld.poetry.utils.*;
import com.ld.poetry.utils.system.IpUtils;
import com.ld.poetry.utils.system.ServletUtils;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.CommentVO;
import com.ld.poetry.vo.friend.FriendCommentVo;
import com.ld.poetry.vo.friend.FriendSaveRequest;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.baomidou.mybatisplus.core.toolkit.Wrappers.lambdaQuery;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service.impl
 * @ClassName FriendServiceImpl
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class FriendServiceImpl implements FriendService  {


    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private FriendBlogMapper friendBlogMapper;

    @Autowired
    private FriendCommentService friendCommentService;


    @Override
    @Transactional
    public UResult saveFriend(FriendSaveRequest request) {
        String value=sysConfigService.getConfigValue("userEmail");

        List<String> to = new ArrayList<>();
        to.add(value);
        String text = String.format(MailUtil.friendText, "友链",request.getComment().getName(),request.getComment().getLink(),request.getComment().getAvatar(),request.getComment().getDescr()
        );
        mailUtil.sendMailMessage(to, "您有一封来自友链的添加信息！", text);

        FriendBlog friendBlog=new FriendBlog();
        friendBlog.setTitle(request.getComment().getName());
        friendBlog.setCover(request.getComment().getAvatar());
        friendBlog.setUrl(request.getComment().getLink());
        friendBlog.setIntroduction(request.getComment().getDescr());
        friendBlog.setState(0);
        friendBlog.setClassify("技术博主");
        friendBlogMapper.insert(friendBlog);

        final UserAgent userAgent = UserAgent.parseUserAgentString(ServletUtils.getRequest().getHeader("User-Agent"));
        final String ip = IpUtils.getIpAddr();

        // 获取客户端操作系统
        String os = userAgent.getOperatingSystem().getName();
        // 获取客户端浏览器
        String browser = userAgent.getBrowser().getName();



        FriendComment comment=new FriendComment();
        comment.setName(request.getComment().getName());
        comment.setAvatar(request.getComment().getAvatar());
        comment.setCommentContent(request.getContent());
        comment.setSystemversion(os);
        comment.setBrowser(browser);
        comment.setType("friend");
        comment.setParentCommentId(0);
        comment.setCreateTime(LocalDateTime.now());
        friendCommentService.saveFriendComment(comment);


        return  UResult.success();
    }

    @Override
    public Integer getFriendCommentCount() {
        return  friendCommentService.getFriendCommentCount();
    }

    @Override
    public UResult<BaseRequestVO> listComment(BaseRequestVO baseRequestVO) {

        return friendCommentService.listComment(baseRequestVO);

    }


}
