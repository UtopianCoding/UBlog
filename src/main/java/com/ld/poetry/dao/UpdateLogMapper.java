package com.ld.poetry.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.poetry.entity.UpdateLog;

import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.dao
 * @ClassName UpdateLogMapper
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
public interface UpdateLogMapper extends BaseMapper<UpdateLog> {

    List<UpdateLog>  findAll();

}
