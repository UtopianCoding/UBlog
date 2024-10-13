package com.ld.poetry.vo.caption;

import lombok.Data;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.vo.caption
 * @ClassName CaptionRequest
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class CaptionRequest {

    private String content;

    /**
     * 换行符
     * \r\n (Windows) 1
     * \n (Linux) 2
     */
    private Integer type;



}
