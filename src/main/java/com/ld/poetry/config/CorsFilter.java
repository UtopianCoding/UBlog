package com.ld.poetry.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.config
 * @ClassName CorsFilter
 * @Author: utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Component
@Slf4j
public class CorsFilter extends GenericFilterBean implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chaine) throws IOException, ServletException {
        log.info("跨域配置开始");
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        httpResponse.setHeader("Access-Control-Allow-Origin", "*");
        httpResponse.setHeader("Access-Control-Allow-Methods", "PUT, POST, GET, OPTIONS, DELETE");
        httpResponse.setHeader("Access-Control-Allow-Headers", "content-type, authorization");
        httpResponse.setHeader("Access-Control-Allow-Credentials", "true");
        httpResponse.setHeader("Access-Control-Max-Age", "3600");
        System.out.println("****************** CORS Configuration Completed *******************");
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        if (httpServletRequest.getMethod().equals("OPTIONS"))
            httpResponse.setStatus(HttpServletResponse.SC_OK);
        chaine.doFilter(request, response);

    }
}
