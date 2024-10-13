package com.ld.poetry.service;

import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.vo.SysUserOnline;
import com.ld.poetry.vo.caption.CaptionRequest;

import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service
 * @ClassName ServerService
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
public interface ServerService {
     List<SysUserOnline> getUserList() ;

    UResult getCaption(CaptionRequest request);
}
