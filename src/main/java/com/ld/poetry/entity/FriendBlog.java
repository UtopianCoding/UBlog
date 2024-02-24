package com.ld.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@TableName("FriendBlog")
public class FriendBlog implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableId(value = "id",type = IdType.AUTO)
    private Integer id;
    /**
     *
     */
    @TableField("Title")
    private String title;
    /**
     *
     */
    @TableField("Classify")
    private String classify;
    /**
     *
     */
    @TableField("Cover")
    private String cover;
    /**
     *
     */
    @TableField("Url")
    private String url;
    /**
     *
     */
    @TableField("Introduction")
    private String introduction;
    /**
     *
     */
    @TableField("State")
    private Integer state;
    /**
     *
     */
    @TableField("CreateTime")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")

    private LocalDateTime createtime;

}
