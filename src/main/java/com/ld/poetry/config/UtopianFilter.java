package com.ld.poetry.config;

import com.alibaba.fastjson.JSON;
import com.ld.poetry.utils.CodeMsg;
import com.ld.poetry.utils.CommonQuery;
import com.ld.poetry.utils.StringUtils;
import com.ld.poetry.utils.UBUtil;
import com.ld.poetry.utils.cache.RedisCache;
import com.ld.poetry.utils.storage.FileFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import static com.ld.poetry.utils.CommonConst.REQUEST_IP;

@Component
public class UtopianFilter extends OncePerRequestFilter {

    @Autowired
    private CommonQuery commonQuery;

    @Autowired
    private FileFilter fileFilter;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws ServletException, IOException {
        if (!"OPTIONS".equals(httpServletRequest.getMethod())) {
            try {
                String ipAddr = UBUtil.getIpAddr(UBUtil.getRequest());
                ValueOperations<String, String> opsForValue =
                        redisTemplate.opsForValue();
                String s = opsForValue.get(REQUEST_IP + ipAddr);
                if (StringUtils.isEmpty(s)) {

                    opsForValue.set(REQUEST_IP+ipAddr, ipAddr,5, TimeUnit.MINUTES);
                }
//                commonQuery.saveHistory(UBUtil.getIpAddr(httpServletRequest));
            } catch (Exception e) {
            }

            if (fileFilter.doFilterFile(httpServletRequest, httpServletResponse)) {
                httpServletResponse.setHeader("Access-Control-Allow-Origin", "*");
                httpServletResponse.setContentType("application/json;charset=UTF-8");
                httpServletResponse.getWriter().write(JSON.toJSONString(UResult.fail(CodeMsg.PARAMETER_ERROR.getCode(), CodeMsg.PARAMETER_ERROR.getMsg())));
                return;
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}
