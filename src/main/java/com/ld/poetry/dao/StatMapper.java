package com.ld.poetry.dao;


import com.ld.poetry.entity.Distance;

import java.util.List;
import java.util.Map;

public interface StatMapper {

    /**
     * 统计文章每日文章的发布数量
     * @return
     */
    List<Map<String, Object>> blogStat();

    /**
     * 统计标签数量
     * @return
     */
    List<Map<String, Object>> labelStat();

    /**
     * 文章分类统计
     * @return
     */
    List<Map<String, Object>> classifyStat();

    Distance getDistances();

    void updateDistances(Distance distances);
}
