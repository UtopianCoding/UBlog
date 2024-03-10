package com.ld.poetry.auto;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.generation.models.QwenParam;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;

import com.alibaba.dashscope.common.*;
import com.alibaba.dashscope.tokenizers.Tokenization;
import com.alibaba.dashscope.tokenizers.TokenizationResult;
import com.baidubce.util.JsonUtils;

import java.util.*;
/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.auto
 * @ClassName AbstractArticle
 * @Author: utopia
 * @Description: TODO
 * @Version: 1.0
 */
public class AbstractArticle {


    public static String callWithMessage(String articleContent) throws NoApiKeyException, ApiException, InputRequiredException {
        Generation gen = new Generation();

        MessageManager msgManager = new MessageManager(3);
        Message systemMsg0 = Message.builder().role(Role.SYSTEM.getValue()).content("你是文章提纲生成器，我将会输入一段 Markdown 格式的文章，你需要解析输入的文章，理解其中的意思，最后给出它的概要，可以多一些，但是内容在200字以内。").build();
        Message userMsg1 = Message.builder().role(Role.USER.getValue()).content(articleContent).build();
        msgManager.add(systemMsg0);
        msgManager.add(userMsg1);


        QwenParam params = QwenParam.builder().model("qwen-turbo")
                .messages(msgManager.get())
                .seed(1234)
                .apiKey("sk-8dd6b9584ce14f4b8bc7a60eeef4fc02")
                .topP(0.8)
                .resultFormat("message")
                .enableSearch(false)
                .maxTokens(1500)
                .temperature((float)0.85)
                .repetitionPenalty((float)1.0)
                .build();


        GenerationResult result = gen.call(params);
        GenerationOutput output = result.getOutput();
    return  output.getChoices().get(0).getMessage().getContent();
//        String s = JsonUtils.toJsonString(result);
//
//        System.out.println(JsonUtils.toJsonString(result));
//        System.out.println(result);
    }


}
