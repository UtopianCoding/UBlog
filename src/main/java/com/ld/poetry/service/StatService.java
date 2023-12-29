package com.ld.poetry.service;


import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


public interface StatService {

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
}
