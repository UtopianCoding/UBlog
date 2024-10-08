package com.ld.poetry.controller;


import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ld.poetry.config.LoginCheck;
import com.ld.poetry.config.UResult;
import com.ld.poetry.config.SaveCheck;
import com.ld.poetry.dao.ArticleMapper;
import com.ld.poetry.entity.Article;
import com.ld.poetry.entity.WeiYan;
import com.ld.poetry.service.WeiYanService;
import com.ld.poetry.utils.CommonConst;
import com.ld.poetry.utils.PoetryEnum;
import com.ld.poetry.utils.UBUtil;
import com.ld.poetry.utils.StringUtil;
import com.ld.poetry.vo.BaseRequestVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 微言表 前端控制器
 */
@RestController
@RequestMapping("/api/weiYan")
public class WeiYanController {

    @Autowired
    private WeiYanService weiYanService;

    @Autowired
    private ArticleMapper articleMapper;

    /**
     * 保存
     */
    @PostMapping("/saveWeiYan")
    @LoginCheck
    @SaveCheck
    public UResult saveWeiYan(@RequestBody WeiYan weiYanVO) {
        if (!StringUtils.hasText(weiYanVO.getContent())) {
            return UResult.fail("微言不能为空！");
        }

        String content = StringUtil.removeHtml(weiYanVO.getContent());
        if (!StringUtils.hasText(content)) {
            return UResult.fail("微言内容不合法！");
        }
        weiYanVO.setContent(content);

        WeiYan weiYan = new WeiYan();
        weiYan.setUserId(UBUtil.getUserId());
        weiYan.setContent(weiYanVO.getContent());
        weiYan.setIsPublic(weiYanVO.getIsPublic());
        weiYan.setUserName(UBUtil.getUsername());
        weiYan.setRealm("一年级");
        weiYan.setType(CommonConst.WEIYAN_TYPE_FRIEND);
        weiYanService.save(weiYan);
        return UResult.success();
    }


    /**
     * 保存
     */
    @PostMapping("/saveNews")
    @LoginCheck
    public UResult saveNews(@RequestBody WeiYan weiYanVO) {
        if (!StringUtils.hasText(weiYanVO.getContent()) || weiYanVO.getSource() == null || weiYanVO.getCreateTime() == null) {
            return UResult.fail("信息不全！");
        }

        Integer userId = UBUtil.getUserId();

        LambdaQueryChainWrapper<Article> wrapper = new LambdaQueryChainWrapper<>(articleMapper);
        Integer count = wrapper.eq(Article::getId, weiYanVO.getSource()).eq(Article::getUserId, userId).count();

        if (count == null || count < 1) {
            return UResult.fail("来源不存在！");
        }

        WeiYan weiYan = new WeiYan();
        weiYan.setUserId(userId);
        weiYan.setContent(weiYanVO.getContent());
        weiYan.setIsPublic(Boolean.TRUE);
        weiYan.setSource(weiYanVO.getSource());
        weiYan.setCreateTime(weiYanVO.getCreateTime());
        weiYan.setType(CommonConst.WEIYAN_TYPE_NEWS);
        weiYan.setUserName(UBUtil.getUsername());
        weiYan.setRealm("一年级");
        weiYanService.save(weiYan);
        return UResult.success();
    }

    /**
     * 查询List
     */
    @PostMapping("/listNews")
    public UResult<BaseRequestVO> listNews(@RequestBody BaseRequestVO baseRequestVO) {
        if (baseRequestVO.getSource() == null) {
            return UResult.fail("来源不能为空！");
        }
        LambdaQueryChainWrapper<WeiYan> lambdaQuery = weiYanService.lambdaQuery();
        lambdaQuery.eq(WeiYan::getType, CommonConst.WEIYAN_TYPE_NEWS);
        lambdaQuery.eq(WeiYan::getSource, baseRequestVO.getSource());
        lambdaQuery.eq(WeiYan::getIsPublic, PoetryEnum.PUBLIC.getCode());

        lambdaQuery.orderByDesc(WeiYan::getCreateTime).page(baseRequestVO);
        return UResult.success(baseRequestVO);
    }

    /**
     * 删除
     */
    @GetMapping("/deleteWeiYan")
    @LoginCheck
    public UResult deleteWeiYan(@RequestParam("id") Integer id) {
        Integer userId = UBUtil.getUserId();
        weiYanService.lambdaUpdate().eq(WeiYan::getId, id)
                .eq(WeiYan::getUserId, userId)
                .remove();
        return UResult.success();
    }


    /**
     * 查询List
     */
    @PostMapping("/listWeiYan")
    public UResult<BaseRequestVO> listWeiYan(@RequestBody BaseRequestVO baseRequestVO) {
        LambdaQueryChainWrapper<WeiYan> lambdaQuery = weiYanService.lambdaQuery();
        lambdaQuery.eq(WeiYan::getType, CommonConst.WEIYAN_TYPE_FRIEND);
        System.out.println("..............."+baseRequestVO.getUserId());
        System.out.println("***************"+UBUtil.getUserId());
        if (baseRequestVO.getUserId() == null) {
            if (UBUtil.getUserId() != null) {
                lambdaQuery.eq(WeiYan::getUserId, UBUtil.getUserId());
            } else {
                lambdaQuery.eq(WeiYan::getIsPublic, PoetryEnum.PUBLIC.getCode());
                lambdaQuery.eq(WeiYan::getUserId, UBUtil.getAdminUser().getId());
            }
        } else {
            if (!baseRequestVO.getUserId().equals(UBUtil.getUserId())) {
                lambdaQuery.eq(WeiYan::getIsPublic, PoetryEnum.PUBLIC.getCode());
            }
            lambdaQuery.eq(WeiYan::getUserId, baseRequestVO.getUserId());
        }

        lambdaQuery.orderByDesc(WeiYan::getCreateTime).page(baseRequestVO);
        return UResult.success(baseRequestVO);
    }
}
