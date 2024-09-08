package com.ld.poetry.vo.friend;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.vo.friend
 * @ClassName FriendCommentVo
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class FriendCommentVo {
     private Integer id;

     @NotNull(message = "评论来源标识不能为空")
     private Integer source;

     /**
      * 评论来源类型
      */
     @NotBlank(message = "评论来源类型不能为空")
     private String type;

     //层主的parentCommentId是0，回复的parentCommentId是层主的id
     private Integer parentCommentId;

     //层主的parentUserId是null，回复的parentUserId是被回复的userId
     private Integer parentUserId;

     private Integer userId;

     private Integer likeCount;


     private String commentContent;

     private String commentInfo;

     //子评论必须传评论楼层ID
     private Integer floorCommentId;

     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
     @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
     private LocalDateTime createTime;

     private String systemversion;

     private String browser;

     // 需要查询封装
     private Page childComments;
     private String parentUsername;
     private String username;
     private String avatar;
}
