package com.ld.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.entity
 * @ClassName SysConfig
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("sys_config")
public class SysConfig {


    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    @TableField("sysName")
    private String sysName;

    /**
     * 分类ID
     */
    @TableField("sysValue")
    private String sysValue;

    /**
     * 标签ID
     */
    @TableField("isState")
    private String isState;


}
