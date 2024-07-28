package com.ld.poetry.service.impl;

import com.ld.poetry.entity.vo.SysUserOnline;
import com.ld.poetry.service.ServerService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service.impl
 * @ClassName ServerServiceImpl
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class ServerServiceImpl implements ServerService {
    @Override
    public List<SysUserOnline> getUserList() {
        return Collections.emptyList();
    }
}
