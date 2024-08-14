package com.ld.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.entity
 * @ClassName UpdateLog
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("UpdateLog")
public class UpdateLog {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 用户ID
     */
    @TableField("year")
    private String year;

    /**
     * 分类ID
     */
    @TableField("data")
    private String data;

    /**
     * 标签ID
     */
    @TableField("month")
    private String month;

    /**
     * 封面
     */
    @TableField("title")
    private String title;


}
