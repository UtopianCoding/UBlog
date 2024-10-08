package com.ld.poetry.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.ld.poetry.utils.UBUtil;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class DataAutoFill implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "createBy", String.class, !StringUtils.hasText(UBUtil.getUsername()) ? "Sara" : UBUtil.getUsername());
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        this.strictInsertFill(metaObject, "updateBy", String.class, !StringUtils.hasText(UBUtil.getUsername()) ? "Sara" : UBUtil.getUsername());
    }
}
