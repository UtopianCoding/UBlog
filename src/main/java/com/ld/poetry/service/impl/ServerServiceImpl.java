package com.ld.poetry.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.vo.SysUserOnline;
import com.ld.poetry.service.ServerService;
import com.ld.poetry.utils.StringUtils;
import com.ld.poetry.vo.caption.CaptionRequest;
import com.ld.poetry.vo.caption.CaptionVo;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service.impl
 * @ClassName ServerServiceImpl
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class ServerServiceImpl implements ServerService {
    @Override
    public List<SysUserOnline> getUserList() {
        return Collections.emptyList();
    }

    @Override
    public UResult getCaption(CaptionRequest request) {
        if (StringUtils.isEmpty(request.getContent())){
            return UResult.fail("请输入内容");
        }
        StringBuilder str=new StringBuilder();
        ObjectMapper objectMapper = new ObjectMapper();
        String type="\r\n";
        if (request.getType()==2){
            type="\n";
        }
        try {
            List<CaptionVo> captionVos = objectMapper.readValue(request.getContent(), objectMapper.getTypeFactory().constructCollectionType(List.class, CaptionVo.class));
            for (CaptionVo captionVo : captionVos) {
                str.append(captionVo.getContent()).append(type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return UResult.success(str.toString());
    }


}
