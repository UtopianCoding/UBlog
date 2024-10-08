package com.ld.poetry.utils.storage;

import com.ld.poetry.entity.User;
import com.ld.poetry.utils.CommonConst;
import com.ld.poetry.utils.UCache;
import com.ld.poetry.utils.UBUtil;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class FileFilter {

    private final AntPathMatcher matcher = new AntPathMatcher();

    public boolean doFilterFile(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse) {
        if (matcher.match("/resource/upload", httpServletRequest.getRequestURI())) {
            String token = UBUtil.getToken();
            if (StringUtils.hasText(token)) {
                User user = (User) UCache.get(token);

                if (user != null) {
                    if (user.getId().intValue() == UBUtil.getAdminUser().getId().intValue()) {
                        return false;
                    }

                    AtomicInteger atomicInteger = (AtomicInteger) UCache.get(CommonConst.SAVE_COUNT_USER_ID + user.getId().toString());
                    if (atomicInteger == null) {
                        atomicInteger = new AtomicInteger();
                        UCache.put(CommonConst.SAVE_COUNT_USER_ID + user.getId().toString(), atomicInteger, CommonConst.SAVE_EXPIRE);
                    }
                    int userIdCount = atomicInteger.getAndIncrement();

                    String ip = UBUtil.getIpAddr(UBUtil.getRequest());
                    AtomicInteger atomic = (AtomicInteger) UCache.get(CommonConst.SAVE_COUNT_IP + ip);
                    if (atomic == null) {
                        atomic = new AtomicInteger();
                        UCache.put(CommonConst.SAVE_COUNT_IP + ip, atomic, CommonConst.SAVE_EXPIRE);
                    }
                    int ipCount = atomic.getAndIncrement();

                    return userIdCount >= CommonConst.SAVE_MAX_COUNT || ipCount >= CommonConst.SAVE_MAX_COUNT;
                }
            }
            return true;
        } else {
            return false;
        }
    }
}
