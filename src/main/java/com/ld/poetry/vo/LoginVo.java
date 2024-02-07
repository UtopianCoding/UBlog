package com.ld.poetry.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.vo
 * @ClassName LoginVo
 * @Author: utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class LoginVo implements Serializable {

    private String account;

    private String password;

    private Boolean isAdmin;

}
