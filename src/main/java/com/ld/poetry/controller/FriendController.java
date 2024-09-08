package com.ld.poetry.controller;

import cn.hutool.json.JSONUtil;
import cn.hutool.json.ObjectMapper;
import com.alibaba.fastjson.JSON;
import com.ld.poetry.config.UResult;
import com.ld.poetry.constant.Constants;
import com.ld.poetry.dao.FriendBlogMapper;
import com.ld.poetry.dao.FriendCommentMapper;
import com.ld.poetry.entity.FriendBlog;
import com.ld.poetry.entity.FriendComment;
import com.ld.poetry.entity.WebInfo;
import com.ld.poetry.service.FriendService;
import com.ld.poetry.service.SysConfigService;
import com.ld.poetry.utils.*;
import com.ld.poetry.utils.system.IpUtils;
import com.ld.poetry.utils.system.ServletUtils;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.friend.FriendSaveRequest;
import com.ld.poetry.vo.friend.FriendVo;
import eu.bitwalker.useragentutils.UserAgent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.controller
 * @ClassName FriendController
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/friend")
public class FriendController {

    @Autowired
    private MailUtil mailUtil;

    @Autowired
    private SysConfigService sysConfigService;

    @Autowired
    private FriendBlogMapper friendBlogMapper;

    @Autowired
    private FriendCommentMapper friendCommentMapper;

    @Autowired
    private FriendService friendService;

    @Autowired
    private CommonQuery commonQuery;
    @PostMapping("/saveFriend")
    public UResult saveFriend(@RequestBody @Valid FriendSaveRequest request) {


       return friendService.saveFriend(request);


    }

    /**
     * 查询评论数量
     */
    @GetMapping("/getFriendCommentCount")
    public UResult<Integer> getFriendCommentCount() {
        Integer count = friendService.getFriendCommentCount();
        return UResult.success(count);
    }

    /**
     * 查询评论
     */
    @PostMapping("/listComment")
    public UResult<BaseRequestVO> listComment(@RequestBody BaseRequestVO baseRequestVO) {
        return friendService.listComment(baseRequestVO);
    }




}
