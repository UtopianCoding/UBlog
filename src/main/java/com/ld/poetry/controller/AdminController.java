package com.ld.poetry.controller;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.conditions.update.LambdaUpdateChainWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.config.LoginCheck;
import com.ld.poetry.config.UResult;
import com.ld.poetry.dao.TreeHoleMapper;
import com.ld.poetry.dao.WebInfoMapper;
import com.ld.poetry.entity.*;

import com.ld.poetry.service.ArticleService;
import com.ld.poetry.service.CommentService;
import com.ld.poetry.service.UserService;
import com.ld.poetry.utils.*;
import com.ld.poetry.vo.ArticleVO;
import com.ld.poetry.vo.BaseRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import org.tio.core.Tio;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ArticleService articleService;

    @Resource
    private WebInfoMapper webInfoMapper;

    @Autowired
    private CommentService commentService;

    @Resource
    private TreeHoleMapper treeHoleMapper;



    /**
     * 查询用户
     */
    @PostMapping("/user/list")
    @LoginCheck(0)
    public UResult<Page> listUser(@RequestBody BaseRequestVO baseRequestVO) {
        return userService.listUser(baseRequestVO);
    }

    /**
     * 修改用户状态
     * <p>
     * flag = true：解禁
     * flag = false：封禁
     */
    @GetMapping("/user/changeUserStatus")
    @LoginCheck(0)
    public UResult changeUserStatus(@RequestParam("userId") Integer userId, @RequestParam("flag") Boolean flag) {
        LambdaUpdateChainWrapper<User> updateChainWrapper = userService.lambdaUpdate().eq(User::getId, userId);
        if (flag) {
            updateChainWrapper.eq(User::getUserStatus, PoetryEnum.STATUS_DISABLE.getCode()).set(User::getUserStatus, PoetryEnum.STATUS_ENABLE.getCode()).update();
        } else {
            updateChainWrapper.eq(User::getUserStatus, PoetryEnum.STATUS_ENABLE.getCode()).set(User::getUserStatus, PoetryEnum.STATUS_DISABLE.getCode()).update();
        }
        logout(userId);
        return UResult.success();
    }

    /**
     * 修改用户赞赏
     */
    @GetMapping("/user/changeUserAdmire")
    @LoginCheck(0)
    public UResult changeUserAdmire(@RequestParam("userId") Integer userId, @RequestParam("admire") String admire) {
        userService.lambdaUpdate()
                .eq(User::getId, userId)
                .set(User::getAdmire, admire)
                .update();
        UCache.remove(CommonConst.ADMIRE);
        return UResult.success();
    }

    /**
     * 修改用户类型
     */
    @GetMapping("/user/changeUserType")
    @LoginCheck(0)
    public UResult changeUserType(@RequestParam("userId") Integer userId, @RequestParam("userType") Integer userType) {
        if (userType != 0 && userType != 1 && userType != 2) {
            return UResult.fail(CodeMsg.PARAMETER_ERROR);
        }
        userService.lambdaUpdate().eq(User::getId, userId).set(User::getUserType, userType).update();

        logout(userId);
        return UResult.success();
    }

    private void logout(Integer userId) {
        if (UCache.get(CommonConst.ADMIN_TOKEN + userId) != null) {
            String token = (String) UCache.get(CommonConst.ADMIN_TOKEN + userId);
            UCache.remove(CommonConst.ADMIN_TOKEN + userId);
            UCache.remove(token);
        }

        if (UCache.get(CommonConst.USER_TOKEN + userId) != null) {
            String token = (String) UCache.get(CommonConst.USER_TOKEN + userId);
            UCache.remove(CommonConst.USER_TOKEN + userId);
            UCache.remove(token);
        }

    }

    /**
     * 获取网站信息
     */
    @GetMapping("/webInfo/getAdminWebInfo")
    @LoginCheck(0)
    public UResult<WebInfo> getWebInfo() {
        LambdaQueryChainWrapper<WebInfo> wrapper = new LambdaQueryChainWrapper<>(webInfoMapper);
        List<WebInfo> list = wrapper.list();
        if (!CollectionUtils.isEmpty(list)) {
            return UResult.success(list.get(0));
        } else {
            return UResult.success();
        }
    }


    /**
     * 用户查询文章
     */
    @PostMapping("/article/user/list")
    @LoginCheck(1)
    public UResult<Page> listUserArticle(@RequestBody BaseRequestVO baseRequestVO) {
        return articleService.listAdminArticle(baseRequestVO, false);
    }


    /**
     * Boss查询文章
     */
    @PostMapping("/article/boss/list")
    @LoginCheck(0)
    public UResult<Page> listBossArticle(@RequestBody BaseRequestVO baseRequestVO) {
        return articleService.listAdminArticle(baseRequestVO, true);
    }

    /**
     * 修改文章状态
     */
    @GetMapping("/article/changeArticleStatus")
    @LoginCheck(1)
    public UResult changeArticleStatus(@RequestParam("articleId") Integer articleId,
                                            @RequestParam(value = "viewStatus", required = false) Boolean viewStatus,
                                            @RequestParam(value = "commentStatus", required = false) Boolean commentStatus,
                                            @RequestParam(value = "recommendStatus", required = false) Boolean recommendStatus) {
        LambdaUpdateChainWrapper<Article> updateChainWrapper = articleService.lambdaUpdate()
                .eq(Article::getId, articleId)
                .eq(Article::getUserId, PoetryUtil.getUserId());
        if (viewStatus != null) {
            updateChainWrapper.set(Article::getViewStatus, viewStatus);
        }
        if (commentStatus != null) {
            updateChainWrapper.set(Article::getCommentStatus, commentStatus);
        }
        if (recommendStatus != null) {
            updateChainWrapper.set(Article::getRecommendStatus, recommendStatus);
        }
        updateChainWrapper.update();
        return UResult.success();
    }

    /**
     * 查询文章
     */
    @GetMapping("/article/getArticleById")
    @LoginCheck(1)
    public UResult<ArticleVO> getArticleByIdForUser(@RequestParam("id") Integer id) {
        return articleService.getArticleByIdForUser(id);
    }

    /**
     * 作者删除评论
     */
    @GetMapping("/comment/user/deleteComment")
    @LoginCheck(1)
    public UResult userDeleteComment(@RequestParam("id") Integer id) {
        Comment comment = commentService.lambdaQuery().select(Comment::getSource, Comment::getType).eq(Comment::getId, id).one();
        if (comment == null) {
            return UResult.success();
        }
        if (!CommentTypeEnum.COMMENT_TYPE_ARTICLE.getCode().equals(comment.getType())) {
            return UResult.fail("权限不足！");
        }
        Article one = articleService.lambdaQuery().eq(Article::getId, comment.getSource()).select(Article::getUserId).one();
        if (one == null || (PoetryUtil.getUserId().intValue() != one.getUserId().intValue())) {
            return UResult.fail("权限不足！");
        }
        commentService.removeById(id);
        return UResult.success();
    }

    /**
     * Boss删除评论
     */
    @GetMapping("/comment/boss/deleteComment")
    @LoginCheck(0)
    public UResult bossDeleteComment(@RequestParam("id") Integer id) {
        commentService.removeById(id);
        return UResult.success();
    }

    /**
     * 用户查询评论
     */
    @PostMapping("/comment/user/list")
    @LoginCheck(1)
    public UResult<Page> listUserComment(@RequestBody BaseRequestVO baseRequestVO) {
        return commentService.listAdminComment(baseRequestVO, false);
    }

    /**
     * Boss查询评论
     */
    @PostMapping("/comment/boss/list")
    @LoginCheck(0)
    public UResult<Page> listBossComment(@RequestBody BaseRequestVO baseRequestVO) {
        return commentService.listAdminComment(baseRequestVO, true);
    }

    /**
     * Boss查询树洞
     */
    @PostMapping("/treeHole/boss/list")
    @LoginCheck(0)
    public UResult<Page> listBossTreeHole(@RequestBody BaseRequestVO baseRequestVO) {
        LambdaQueryChainWrapper<TreeHole> wrapper = new LambdaQueryChainWrapper<>(treeHoleMapper);
        wrapper.orderByDesc(TreeHole::getCreateTime).page(baseRequestVO);
        return UResult.success(baseRequestVO);
    }
}
