package com.ld.poetry.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ld.poetry.config.LoginCheck;
import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.Family;
import com.ld.poetry.service.FamilyService;
import com.ld.poetry.utils.CommonConst;
import com.ld.poetry.utils.CommonQuery;
import com.ld.poetry.utils.UCache;
import com.ld.poetry.utils.PoetryUtil;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.FamilyVO;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

/**
 * 恋爱信息 前端控制器
 */
@RestController
@RequestMapping("/api/family")
public class FamilyController {

    @Autowired
    private FamilyService familyService;

    @Autowired
    private CommonQuery commonQuery;

    /**
     * 保存
     */
    @PostMapping("/saveFamily")
    @LoginCheck
    public UResult saveFamily(@Validated @RequestBody FamilyVO familyVO) {
        Integer userId = PoetryUtil.getUserId();
        familyVO.setUserId(userId);
        Family oldFamily = familyService.lambdaQuery().select(Family::getId).eq(Family::getUserId, userId).one();
        Family family = new Family();
        BeanUtils.copyProperties(familyVO, family);
        family.setStatus(Boolean.FALSE);
        if (oldFamily != null) {
            family.setId(oldFamily.getId());
            familyService.updateById(family);
        } else {
            family.setId(null);
            familyService.save(family);
        }
        if (userId.intValue() == PoetryUtil.getAdminUser().getId().intValue()) {
            UCache.put(CommonConst.ADMIN_FAMILY, family);
        }
        UCache.remove(CommonConst.FAMILY_LIST);
        return UResult.success();
    }

    /**
     * 删除
     */
    @GetMapping("/deleteFamily")
    @LoginCheck(0)
    public UResult deleteFamily(@RequestParam("id") Integer id) {
        familyService.removeById(id);
        UCache.remove(CommonConst.FAMILY_LIST);
        return UResult.success();
    }

    /**
     * 获取
     */
    @GetMapping("/getFamily")
    @LoginCheck
    public UResult<FamilyVO> getFamily() {
        Integer userId = PoetryUtil.getUserId();
        Family family = familyService.lambdaQuery().eq(Family::getUserId, userId).one();
        if (family == null) {
            return UResult.success();
        } else {
            FamilyVO familyVO = new FamilyVO();
            BeanUtils.copyProperties(family, familyVO);
            return UResult.success(familyVO);
        }
    }

    /**
     * 获取
     */
    @GetMapping("/getAdminFamily")
    public UResult<FamilyVO> getAdminFamily() {
        Family family = (Family) UCache.get(CommonConst.ADMIN_FAMILY);
//        if (family == null) {
//            return UResult.fail("请根据文档【https://poetize.cn/article?id=26】初始化表白墙");
//        }
        FamilyVO familyVO = new FamilyVO();
        BeanUtils.copyProperties(family, familyVO);
        return UResult.success(familyVO);
    }

    /**
     * 查询随机家庭
     */
    @GetMapping("/listRandomFamily")
    public UResult<List<FamilyVO>> listRandomFamily(@RequestParam(value = "size", defaultValue = "10") Integer size) {
        List<FamilyVO> familyList = commonQuery.getFamilyList();
        if (familyList.size() > size) {
            Collections.shuffle(familyList);
            familyList = familyList.subList(0, size);
        }
        return UResult.success(familyList);
    }

    /**
     * 查询
     */
    @PostMapping("/listFamily")
    @LoginCheck(0)
    public UResult<Page> listFamily(@RequestBody BaseRequestVO baseRequestVO) {
        familyService.lambdaQuery()
                .eq(baseRequestVO.getStatus() != null, Family::getStatus, baseRequestVO.getStatus())
                .orderByDesc(Family::getCreateTime).page(baseRequestVO);
        return UResult.success(baseRequestVO);
    }

    /**
     * 修改状态
     */
    @GetMapping("/changeLoveStatus")
    @LoginCheck(0)
    public UResult changeLoveStatus(@RequestParam("id") Integer id, @RequestParam("flag") Boolean flag) {
        familyService.lambdaUpdate().eq(Family::getId, id).set(Family::getStatus, flag).update();
        UCache.remove(CommonConst.FAMILY_LIST);
        return UResult.success();
    }
}
