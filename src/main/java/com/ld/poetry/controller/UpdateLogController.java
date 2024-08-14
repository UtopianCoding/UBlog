package com.ld.poetry.controller;

import com.ld.poetry.config.UResult;
import com.ld.poetry.service.UpdateLogService;
import com.ld.poetry.vo.UpdateLogVo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.controller
 * @ClassName UpdateLogController
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@RestController
@RequestMapping("/api/updateLog")
public class UpdateLogController {

    @Resource
    private UpdateLogService updateLogService;

    @PostMapping("/listUpdateLog")
    public UResult updateLog() {
        List<UpdateLogVo> vos=updateLogService.listUpdateLog();
        return UResult.success(vos);
    }
}
