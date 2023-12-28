package com.ld.poetry.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.config.LoginCheck;
import com.ld.poetry.config.PoetryResult;
import com.ld.poetry.service.ArticleService;
import com.ld.poetry.utils.CommonConst;
import com.ld.poetry.utils.PoetryCache;
import com.ld.poetry.utils.PoetryUtil;
import com.ld.poetry.vo.ArticleVO;
import com.ld.poetry.vo.BaseRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 文章表
 */
@RestController
@RequestMapping("/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    /**
     * 保存文章
     */
//    @LoginCheck(1)
    @PostMapping("/saveArticle")
    public PoetryResult saveArticle(@Validated @RequestBody ArticleVO articleVO) {
//        PoetryCache.remove(CommonConst.USER_ARTICLE_LIST + PoetryUtil.getUserId().toString());
//        PoetryCache.remove(CommonConst.ARTICLE_LIST);

        return articleService.saveArticle(articleVO);
    }


    /**
     * 删除文章
     */
    @GetMapping("/deleteArticle")
    @LoginCheck(1)
    public PoetryResult deleteArticle(@RequestParam("id") Integer id) {
        PoetryCache.remove(CommonConst.USER_ARTICLE_LIST + PoetryUtil.getUserId().toString());
        PoetryCache.remove(CommonConst.ARTICLE_LIST);
        return articleService.deleteArticle(id);
    }


    /**
     * 更新文章
     */
    @PostMapping("/updateArticle")
    @LoginCheck(1)
    public PoetryResult updateArticle(@Validated @RequestBody ArticleVO articleVO) {
        PoetryCache.remove(CommonConst.ARTICLE_LIST);

        return articleService.updateArticle(articleVO);
    }


    /**
     * 查询文章List
     */
    @PostMapping("/listArticle")
    public PoetryResult<Page> listArticle(@RequestBody BaseRequestVO baseRequestVO) {
        return articleService.listArticle(baseRequestVO);
    }

    /**
     * 查询文章
     */
    @GetMapping("/getArticleById")
    public PoetryResult<ArticleVO> getArticleById(@RequestParam("id") Integer id, @RequestParam(value = "password", required = false) String password) {
        return articleService.getArticleById(id, password);
    }
}

