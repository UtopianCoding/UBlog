package com.ld.poetry.controller;


import com.ld.poetry.config.LoginCheck;
import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.Distance;
import com.ld.poetry.service.StatService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stat")
public class StatController {


    @Autowired
    StatService statService;


    /**
     * 统计文章每日文章的发布数量
     * @return
     */
    @GetMapping("/blogStat")
    public UResult   blogStat(){
        List<Map<String,Object>> blogStat =statService.blogStat();
        return  UResult.success(blogStat);

    }

    /**
     * 统计标签数量
     * @return
     */
    @GetMapping("/labelStat")
    public UResult   labelStat(){
        List<Map<String,Object>> labelStat =statService.labelStat();
        return  UResult.success(labelStat);

    }

    /**
     * 文章分类统计
     * @return
     */
    @GetMapping("/classifyStat")
    public UResult   classifyStat(){
        List<Map<String,Object>> classifyStat =statService.classifyStat();
        return  UResult.success(classifyStat);

    }

    @GetMapping("/distances")
    public UResult getDistance(){
        Distance distance=statService.getDistances();
        return UResult.success(distance);
    }



}
