package com.ld.poetry.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ld.poetry.service.UserService;
import com.ld.poetry.utils.HttpUtils;
import com.ld.poetry.vo.SocialUser;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.controller
 * @ClassName AuthController
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Controller
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/gitLogin")
    public String gitLogin(){
        String url="https://syml.online/api/oauth2/gitee/success";
        return "redirect:"+getUrl(url);
    }


    @GetMapping("/oauth2/gitee/success")
    public String gitee(@RequestParam("code") String code, HttpSession session) throws Exception {
        System.out.println("成功进入该方法");
        Map<String, String> map = new HashMap();
        map.put("client_id","04c46cc44b1eb1e123d04a7f08eaf4c83894c40d85d3e4a89a7dd2a78e4894b1");
        map.put("redirect_uri","https://syml.online/oauth2/gitee/success");
        map.put("client_secret","1692a0c1d270fdcee4f1a1982ab6801fd9f36892db9acaa9e9c2baf5c30c0788");
        map.put("code",code);
        map.put("grant_type","authorization_code");

        //根据code获取access_token
        HttpResponse response = HttpUtils.doPost("https://gitee.com", "/oauth/token", "post",  new HashMap<>(), map, new HashMap<>());

        //处理返回的json字符串
        if (response.getStatusLine().getStatusCode()==200){
            //获取access_token
            String json = EntityUtils.toString(response.getEntity());
            SocialUser socialUser = JSON.parseObject(json, SocialUser.class);
            System.out.println("socialUser用户信息:"+socialUser);
            //远程调用member服务来处理用户信息注册、校验功能

            userService.oauth2Login(socialUser);
            return "redirect:https://syml.online";


        }
        return "redirect:https://syml.online/user";


    }

    public String getUrl(String rollBackUrl){
        String cliendId="04c46cc44b1eb1e123d04a7f08eaf4c83894c40d85d3e4a89a7dd2a78e4894b1";
        String url="https://gitee.com/oauth/authorize?response_type=code"+"&client_id"+cliendId+
                "&redirect_uri"+ URLEncoder.encode(rollBackUrl);
        return url;
    }
}
