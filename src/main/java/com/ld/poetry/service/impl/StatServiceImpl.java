package com.ld.poetry.service.impl;

import com.ld.poetry.dao.StatMapper;
import com.ld.poetry.service.StatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;


@Service
public class StatServiceImpl implements StatService {

    @Autowired
    StatMapper statMapper;

    /**
     * 统计文章每日文章的发布数量
     * @return
     */
    @Override
    public List<Map<String, Object>> blogStat() {
        return statMapper.blogStat();
    }

    /**
     * 统计标签数量
     * @return
     */
    @Override
    public List<Map<String, Object>> labelStat() {
        return statMapper.labelStat();
    }

    /**
     * 文章分类统计
     * @return
     */
    @Override
    public List<Map<String, Object>> classifyStat() {
        return statMapper.classifyStat();
    }
}
