package com.ld.poetry.service;

import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.FriendComment;
import com.ld.poetry.vo.BaseRequestVO;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service
 * @ClassName FriendCommentService
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
public interface FriendCommentService {

    public UResult<BaseRequestVO> listComment(BaseRequestVO baseRequestVO);

    void saveFriendComment(FriendComment comment);

    Integer getFriendCommentCount();
}
