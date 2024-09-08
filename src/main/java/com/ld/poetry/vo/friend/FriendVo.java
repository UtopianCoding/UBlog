package com.ld.poetry.vo.friend;

import lombok.Data;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.vo.friend
 * @ClassName FriendVo
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class FriendVo {
    /**
     * 标题
     */
    private String name;



    /**
     * 封面
     */
    private String avatar;

    /**
     * 链接
     */
    private String link;


    /**
     * 简介
     */
    private String descr;
}
