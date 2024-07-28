package com.ld.poetry.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.Article;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ld.poetry.vo.ArticleVO;
import com.ld.poetry.vo.BaseRequestVO;

import java.util.List;
import java.util.Map;


public interface ArticleService extends IService<Article> {

    UResult saveArticle(ArticleVO articleVO);

    UResult deleteArticle(Integer id);

    UResult updateArticle(ArticleVO articleVO);

    UResult<Page> listArticle(BaseRequestVO baseRequestVO);

    UResult<ArticleVO> getArticleById(Integer id, String password);

    UResult<Page> listAdminArticle(BaseRequestVO baseRequestVO, Boolean isBoss);

    UResult<ArticleVO> getArticleByIdForUser(Integer id);

    UResult<Map<Integer, List<ArticleVO>>> listSortArticle();
}
