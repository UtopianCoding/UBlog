package com.ld.poetry.service.impl;

import com.ld.poetry.entity.WebInfo;
import com.ld.poetry.dao.WebInfoMapper;
import com.ld.poetry.service.WebInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class WebInfoServiceImpl extends ServiceImpl<WebInfoMapper, WebInfo> implements WebInfoService {

}
