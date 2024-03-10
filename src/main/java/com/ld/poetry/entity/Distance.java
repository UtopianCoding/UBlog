package com.ld.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.entity
 * @ClassName Distance
 * @Author: utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@TableName("Distance")
public class Distance implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     *
     */
    @TableField("ID")
    private Integer id;
    /**
     *
     */
    @TableField("Distance")
    private Long distance;

    @TableField("AU")
    private BigDecimal au;

}
