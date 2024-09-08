package com.ld.poetry.service;

import com.ld.poetry.config.UResult;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.friend.FriendSaveRequest;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service
 * @ClassName FriendService
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
public interface FriendService  {
    UResult saveFriend(FriendSaveRequest request);

    Integer getFriendCommentCount();

    UResult<BaseRequestVO> listComment(BaseRequestVO baseRequestVO);
}
