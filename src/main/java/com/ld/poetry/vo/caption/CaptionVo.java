package com.ld.poetry.vo.caption;

import lombok.Data;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.vo
 * @ClassName CaptionVo
 * @Author: Utopia
 * @Description: 字幕转换
 * @Version: 1.0
 */
@Data
public class CaptionVo {

    private Integer from;

    private Integer to;

    private Integer sid;

    private Integer location;

    private String content;

    private Integer music;

}
