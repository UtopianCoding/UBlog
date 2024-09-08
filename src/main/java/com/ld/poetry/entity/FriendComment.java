package com.ld.poetry.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.entity
 * @ClassName FriendComment
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("FriendComment")
public class FriendComment {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;



    /**
     * 父评论ID
     */
    @TableField("parent_comment_id")
    private Integer parentCommentId;

    /**
     * 评论来源类型
     */
    @TableField("type")
    private String type;

    /**
     * 发表用户ID
     */
    @TableField("user_id")
    private Integer userId;

    /**
     * 父发表用户名ID
     */
    @TableField("parent_user_id")
    private Integer parentUserId;

    /**
     * 点赞数
     */
    @TableField("like_count")
    private Integer likeCount;

    /**
     * 评论内容
     */
    @TableField("comment_content")
    private String commentContent;

    /**
     * 评论额外信息
     */
    @TableField("comment_info")
    private String commentInfo;

    /**
     * 楼层评论ID
     */
    @TableField("floor_comment_id")
    private Integer floorCommentId;

    /**
     * 创建时间
     */
    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("systemversion")
    private String systemversion;

    @TableField("Browser")
    private String Browser;

    @TableField("name")
    private String name;

    @TableField("avatar")
    private String avatar;

}
