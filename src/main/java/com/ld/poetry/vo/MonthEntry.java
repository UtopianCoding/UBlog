package com.ld.poetry.vo;

import lombok.Data;

import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.vo
 * @ClassName MonthEntry
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Data
public class MonthEntry {
    private String month;
    private List<BodyItem> children;
}
