package com.ld.poetry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ld.poetry.entity.Article;
import com.ld.poetry.entity.FriendBlog;
import com.ld.poetry.entity.SysConfig;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service
 * @ClassName FriendService
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
public interface SysConfigService extends IService<SysConfig> {

    public String getConfigValue(String key);
}
