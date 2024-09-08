package com.ld.poetry.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ld.poetry.constant.Constants;
import com.ld.poetry.dao.HistoryInfoMapper;
import com.ld.poetry.dao.SysConfigMapper;
import com.ld.poetry.entity.HistoryInfo;
import com.ld.poetry.entity.SysConfig;
import com.ld.poetry.entity.User;
import com.ld.poetry.service.SysConfigService;
import com.ld.poetry.utils.cache.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import static com.ld.poetry.utils.CommonConst.REQUEST_IP;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service.impl
 * @ClassName SysConfigServiceImpl
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class SysConfigServiceImpl extends ServiceImpl<SysConfigMapper, SysConfig> implements SysConfigService {

    @Autowired
    private RedisCache redisCache;
    @Autowired
    private SysConfigMapper sysConfigMapper;



    public String getConfigValue(String key) {
        SysConfig sysConfig=new SysConfig();
        sysConfig.setSysName(key);
        String configKey=Constants.SYS_CONFIG+key;
        String value= (String) redisCache.get(configKey);


        if (value!=null||redisCache.hasKey(configKey)){
            return value;
        }

        SysConfig config=sysConfigMapper.findBySysName(key);
        if (config!=null){
            value=config.getSysValue();
        }
        redisCache.set(Constants.SYS_CONFIG+key, JSON.toJSONString(value));
        return value;

    }

}
