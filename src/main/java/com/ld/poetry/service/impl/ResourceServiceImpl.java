package com.ld.poetry.service.impl;

import com.ld.poetry.entity.Resource;
import com.ld.poetry.dao.ResourceMapper;
import com.ld.poetry.service.ResourceService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;


@Service
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, Resource> implements ResourceService {

}
