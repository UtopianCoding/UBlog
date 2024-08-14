package com.ld.poetry.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ld.poetry.entity.Article;
import com.ld.poetry.entity.UpdateLog;
import com.ld.poetry.vo.UpdateLogVo;

import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service
 * @ClassName UpdateLogService
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
public interface UpdateLogService extends IService<UpdateLog> {
    List<UpdateLogVo> listUpdateLog();
}
