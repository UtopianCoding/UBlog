package com.ld.poetry.dao;

import com.ld.poetry.entity.TreeHole;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;


public interface TreeHoleMapper extends BaseMapper<TreeHole> {

    List<TreeHole> queryAllByLimit(@Param("offset") int offset, @Param("limit") int limit);

}
