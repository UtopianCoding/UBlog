package com.ld.poetry.utils;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ld.poetry.dao.FriendBlogMapper;
import com.ld.poetry.dao.StatMapper;
import com.ld.poetry.entity.Distance;
import com.ld.poetry.entity.FriendBlog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.utils
 * @ClassName TimeTaskSet
 * @Author: utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Slf4j
@EnableScheduling
public class TimeTaskSet {


    @Resource
    private FriendBlogMapper friendBlogMapper;

    @Resource
    private StatMapper statMapper;

    @Scheduled(cron = "0 0 1 * * ?")
    public void isJudgeUrlState(){
        LambdaQueryChainWrapper<FriendBlog> wrapper=new LambdaQueryChainWrapper<>(friendBlogMapper);
        List<FriendBlog> friendBlogs = wrapper.orderByAsc(FriendBlog::getCreatetime).list();
        for (FriendBlog friendBlog:friendBlogs){
            if (friendBlog.getUrl()!=null){
                try {
                    URL url = new URL(friendBlog.getUrl());
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("HEAD"); // 使用HEAD方法进行请求，不获取内容，仅获得响应头信息
                    connection.setConnectTimeout(3000); // 设置连接超时时间
                    connection.setReadTimeout(3000); // 设置读取超时时间
                    int responseCode = connection.getResponseCode();
                     if (!(200 <= responseCode && responseCode <= 399)){
                         friendBlog.setState(0);
                         friendBlogMapper.updateById(friendBlog);
                     }


                }catch (IOException e){
                    e.printStackTrace();
                }
            }

        }

    }


    @Async
    @Scheduled(cron = "0/1 * * * * ?")
    public void isAddDistances(){

        Distance distances = statMapper.getDistances();
        if (distances!=null){

            Long distancesDistance = distances.getDistance();

            distances.setDistance(distancesDistance+17);
            BigDecimal distanceInKm=new BigDecimal(distances.getDistance());
            BigDecimal auInKm = new BigDecimal("149597870.7");
            BigDecimal distanceInAU = distanceInKm.divide(auInKm, 6, RoundingMode.HALF_UP);
            distances.setAu(distanceInAU);
            System.out.println(distances);
            statMapper.updateDistances(distances);
        }
    }



}
