package com.ld.poetry.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.Comment;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.CommentVO;



public interface CommentService extends IService<Comment> {

    UResult saveComment(CommentVO commentVO);

    UResult deleteComment(Integer id);

    UResult<BaseRequestVO> listComment(BaseRequestVO baseRequestVO);

    UResult<Page> listAdminComment(BaseRequestVO baseRequestVO, Boolean isBoss);
}
