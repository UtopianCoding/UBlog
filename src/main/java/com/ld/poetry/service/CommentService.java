package com.ld.poetry.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.config.PoetryResult;
import com.ld.poetry.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.CommentVO;



public interface CommentService extends IService<Comment> {

    PoetryResult saveComment(CommentVO commentVO);

    PoetryResult deleteComment(Integer id);

    PoetryResult<BaseRequestVO> listComment(BaseRequestVO baseRequestVO);

    PoetryResult<Page> listAdminComment(BaseRequestVO baseRequestVO, Boolean isBoss);
}
