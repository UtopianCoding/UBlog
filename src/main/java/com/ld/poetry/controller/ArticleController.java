package com.ld.poetry.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.config.LoginCheck;
import com.ld.poetry.config.UResult;
import com.ld.poetry.service.ArticleService;
import com.ld.poetry.utils.CommonConst;
import com.ld.poetry.utils.UCache;
import com.ld.poetry.utils.UBUtil;
import com.ld.poetry.vo.ArticleVO;
import com.ld.poetry.vo.BaseRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;


/**
 * 文章表
 */
@RestController
@RequestMapping("/api/article")
public class ArticleController {

    @Autowired
    private ArticleService articleService;


    /**
     * 保存文章
     */
//    @LoginCheck(1)
    @PostMapping("/saveArticle")
    public UResult saveArticle(@Validated @RequestBody ArticleVO articleVO) {
//        UCache.remove(CommonConst.USER_ARTICLE_LIST + UBUtil.getUserId().toString());
//        UCache.remove(CommonConst.ARTICLE_LIST);

        return articleService.saveArticle(articleVO);
    }


    /**
     * 删除文章
     */
    @GetMapping("/deleteArticle")
    @LoginCheck(1)
    public UResult deleteArticle(@RequestParam("id") Integer id) {
        UCache.remove(CommonConst.USER_ARTICLE_LIST + UBUtil.getUserId().toString());
        UCache.remove(CommonConst.ARTICLE_LIST);
        return articleService.deleteArticle(id);
    }


    /**
     * 更新文章
     */
    @PostMapping("/updateArticle")
    @LoginCheck(1)
    public UResult updateArticle(@Validated @RequestBody ArticleVO articleVO) {
        UCache.remove(CommonConst.ARTICLE_LIST);

        return articleService.updateArticle(articleVO);
    }


    /**
     * 查询文章List
     */
    @PostMapping("/listArticle")
    public UResult<Page> listArticle(@RequestBody BaseRequestVO baseRequestVO) {
        return articleService.listArticle(baseRequestVO);
    }
    @GetMapping("/listSortArticle")
    public UResult<Map<Integer, List<ArticleVO>>> listSortArticle() {
        return articleService.listSortArticle();
    }

    /**
     * 查询文章
     */
    @GetMapping("/getArticleById")
    public UResult<ArticleVO> getArticleById(@RequestParam("id") Integer id, @RequestParam(value = "password", required = false) String password) {
        return articleService.getArticleById(id, password);
    }
}

