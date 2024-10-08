package com.ld.poetry.utils;

import com.alibaba.fastjson.JSON;
import com.ld.poetry.entity.User;
import com.ld.poetry.entity.WebInfo;
import com.ld.poetry.handle.PoetryRuntimeException;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.util.List;

public class UBUtil {

    public static HttpServletRequest getRequest() {
        return ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
    }

    public static void checkEmail() {
        User user = (User) UCache.get(UBUtil.getToken());
        if (!StringUtils.hasText(user.getEmail())) {
            throw new PoetryRuntimeException("请先绑定邮箱！");
        }
    }

    public static String getToken() {
        return UBUtil.getRequest().getHeader(CommonConst.TOKEN_HEADER);
    }

    public static User getCurrentUser() {
        User user = (User) UCache.get(UBUtil.getToken());
        return user;
    }

    public static User getAdminUser() {
        User admin = (User) UCache.get(CommonConst.ADMIN);
        return admin;
    }

    public static Integer getUserId() {
        User user = (User) UCache.get(UBUtil.getToken());
        return user == null ? null : user.getId();
    }

    public static String getUsername() {
        User user = (User) UCache.get(UBUtil.getToken());
        return user == null ? null : user.getUsername();
    }

    public static String getRandomAvatar(String key) {
        WebInfo webInfo = (WebInfo) UCache.get(CommonConst.WEB_INFO);
        if (webInfo != null) {
            String randomAvatar = webInfo.getRandomAvatar();
            List<String> randomAvatars = JSON.parseArray(randomAvatar, String.class);
            if (!CollectionUtils.isEmpty(randomAvatars)) {
                if (StringUtils.hasText(key)) {
                    return randomAvatars.get(UBUtil.hashLocation(key, randomAvatars.size()));
                } else {
                    String ipAddr = UBUtil.getIpAddr(UBUtil.getRequest());
                    if (StringUtils.hasText(ipAddr)) {
                        return randomAvatars.get(UBUtil.hashLocation(ipAddr, randomAvatars.size()));
                    } else {
                        return randomAvatars.get(0);
                    }
                }
            }
        }
        return null;
    }

    public static String getRandomName(String key) {
        WebInfo webInfo = (WebInfo) UCache.get(CommonConst.WEB_INFO);
        if (webInfo != null) {
            String randomName = webInfo.getRandomName();
            List<String> randomNames = JSON.parseArray(randomName, String.class);
            if (!CollectionUtils.isEmpty(randomNames)) {
                if (StringUtils.hasText(key)) {
                    return randomNames.get(UBUtil.hashLocation(key, randomNames.size()));
                } else {
                    String ipAddr = UBUtil.getIpAddr(UBUtil.getRequest());
                    if (StringUtils.hasText(ipAddr)) {
                        return randomNames.get(UBUtil.hashLocation(ipAddr, randomNames.size()));
                    } else {
                        return randomNames.get(0);
                    }
                }
            }
        }
        return null;
    }

    public static String getRandomCover(String key) {
        WebInfo webInfo = (WebInfo) UCache.get(CommonConst.WEB_INFO);
        if (webInfo != null) {
            String randomCover = webInfo.getRandomCover();
            List<String> randomCovers = JSON.parseArray(randomCover, String.class);
            if (!CollectionUtils.isEmpty(randomCovers)) {
                if (StringUtils.hasText(key)) {
                    return randomCovers.get(UBUtil.hashLocation(key, randomCovers.size()));
                } else {
                    String ipAddr = UBUtil.getIpAddr(UBUtil.getRequest());
                    if (StringUtils.hasText(ipAddr)) {
                        return randomCovers.get(UBUtil.hashLocation(ipAddr, randomCovers.size()));
                    } else {
                        return randomCovers.get(0);
                    }
                }
            }
        }
        return null;
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ipAddress == null || ipAddress.length() == 0 || "unknown".equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (ipAddress.equals("127.0.0.1") || ipAddress.equals("0:0:0:0:0:0:0:1")) {
                    // 根据网卡取本机配置的IP
                    ipAddress = InetAddress.getLocalHost().getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(",") > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = null;
        }
        return ipAddress;
    }


    public static int hashLocation(String key, int length) {
        int h = key.hashCode();
        return (h ^ (h >>> 16)) & (length - 1);
    }
}
