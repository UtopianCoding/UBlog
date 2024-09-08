package com.ld.poetry.vo.friend;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.vo.friend
 * @ClassName friendSaveRequest
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class FriendSaveRequest {

    @NotBlank(message = "邮箱不能为空")
    private String email;


    private String website;

    @NotBlank(message = "昵称不能为空")
    private String nickname;

    @NotBlank(message = "留言不能为空")
    private FriendVo comment;

    private String content;

}
