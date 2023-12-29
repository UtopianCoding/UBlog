package com.ld.poetry.controller;


import com.ld.poetry.config.PoetryResult;
import com.ld.poetry.service.StatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/stat")
public class StatController {


    @Autowired
    StatService statService;


    /**
     * 统计文章每日文章的发布数量
     * @return
     */
    @GetMapping("/blogStat")
    public PoetryResult   blogStat(){
        List<Map<String,Object>> blogStat =statService.blogStat();
        return  PoetryResult.success(blogStat);

    }

    /**
     * 统计标签数量
     * @return
     */
    @GetMapping("/labelStat")
    public PoetryResult   labelStat(){
        List<Map<String,Object>> labelStat =statService.labelStat();
        return  PoetryResult.success(labelStat);

    }

    /**
     * 文章分类统计
     * @return
     */
    @GetMapping("/classifyStat")
    public PoetryResult   classifyStat(){
        List<Map<String,Object>> classifyStat =statService.classifyStat();
        return  PoetryResult.success(classifyStat);

    }



}
