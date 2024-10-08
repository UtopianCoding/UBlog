package com.ld.poetry.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.config.LoginCheck;
import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.Resource;
import com.ld.poetry.service.ResourceService;
import com.ld.poetry.utils.storage.StoreService;
import com.ld.poetry.utils.*;
import com.ld.poetry.utils.storage.FileStorageService;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.FileVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 资源信息 前端控制器
 */
@RestController
@RequestMapping("/api/resource")
public class ResourceController {

    @Autowired
    private ResourceService resourceService;

    @Autowired
    private FileStorageService fileStorageService;

    /**
     * 保存
     */
    @PostMapping("/saveResource")
    @LoginCheck
    public UResult saveResource(@RequestBody Resource resource) {
        if (!StringUtils.hasText(resource.getType()) || !StringUtils.hasText(resource.getPath())) {
            return UResult.fail("资源类型和资源路径不能为空！");
        }
        Resource re = new Resource();
        re.setPath(resource.getPath());
        re.setType(resource.getType());
        re.setSize(resource.getSize());
        re.setMimeType(resource.getMimeType());
        re.setStoreType(resource.getStoreType());
        re.setUserId(UBUtil.getUserId());
        resourceService.save(re);
        return UResult.success();
    }

    /**
     * 上传文件
     */
    @PostMapping("/upload")
    @LoginCheck
    public UResult<String> upload(@RequestParam("file") MultipartFile file, FileVO fileVO) {
        if (file == null || !StringUtils.hasText(fileVO.getType()) || !StringUtils.hasText(fileVO.getRelativePath())) {
            return UResult.fail("文件和资源类型和资源路径不能为空！");
        }

        fileVO.setFile(file);
        StoreService storeService = fileStorageService.getFileStorage(fileVO.getStoreType());
        FileVO result = storeService.saveFile(fileVO);

        Resource re = new Resource();
        re.setPath(result.getVisitPath());
        re.setType(fileVO.getType());
        re.setSize(Integer.valueOf(Long.toString(file.getSize())));
        re.setMimeType(file.getContentType());
        re.setStoreType(fileVO.getStoreType());
        re.setUserId(UBUtil.getUserId());
        resourceService.save(re);
        return UResult.success(result.getVisitPath());
    }

    /**
     * 删除
     */
    @PostMapping("/deleteResource")
    @LoginCheck(0)
    public UResult deleteResource(@RequestParam("path") String path) {
        Resource resource = resourceService.lambdaQuery().select(Resource::getStoreType).eq(Resource::getPath, path).one();
        if (resource == null) {
            return UResult.fail("文件不存在：" + path);
        }

        StoreService storeService = fileStorageService.getFileStorageByStoreType(resource.getStoreType());
        storeService.deleteFile(Collections.singletonList(path));
        return UResult.success();
    }

    /**
     * 查询表情包
     */
    @GetMapping("/getImageList")
    @LoginCheck
    public UResult<List<String>> getImageList() {
        List<Resource> list = resourceService.lambdaQuery().select(Resource::getPath)
                .eq(Resource::getType, CommonConst.PATH_TYPE_INTERNET_MEME)
                .eq(Resource::getStatus, PoetryEnum.STATUS_ENABLE.getCode())
                .eq(Resource::getUserId, UBUtil.getAdminUser().getId())
                .orderByDesc(Resource::getCreateTime)
                .list();
        List<String> paths = list.stream().map(Resource::getPath).collect(Collectors.toList());
        return UResult.success(paths);
    }

    /**
     * 查询资源
     */
    @PostMapping("/listResource")
    @LoginCheck(0)
    public UResult<Page> listResource(@RequestBody BaseRequestVO baseRequestVO) {
        resourceService.lambdaQuery()
                .eq(StringUtils.hasText(baseRequestVO.getResourceType()), Resource::getType, baseRequestVO.getResourceType())
                .orderByDesc(Resource::getCreateTime).page(baseRequestVO);
        return UResult.success(baseRequestVO);
    }

    /**
     * 修改资源状态
     */
    @GetMapping("/changeResourceStatus")
    @LoginCheck(0)
    public UResult changeResourceStatus(@RequestParam("id") Integer id, @RequestParam("flag") Boolean flag) {
        resourceService.lambdaUpdate().eq(Resource::getId, id).set(Resource::getStatus, flag).update();
        return UResult.success();
    }
}

