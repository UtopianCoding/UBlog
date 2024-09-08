package com.ld.poetry.dao;
import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ld.poetry.entity.FriendBlog;
import com.ld.poetry.entity.SysConfig;

import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.dao
 * @ClassName SysConfigMapper
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
public interface SysConfigMapper extends BaseMapper<SysConfig> {

    List<SysConfig> findAll();

    SysConfig findBySysName(@Param("sysName")String sysName);




}

