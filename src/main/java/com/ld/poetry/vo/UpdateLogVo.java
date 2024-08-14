package com.ld.poetry.vo;

import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.vo
 * @ClassName UpdateLogVo
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class UpdateLogVo {

    private String year;
    private String date;
    private String weather;
    private List<MonthEntry> body;

}


