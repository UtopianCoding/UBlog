package com.ld.poetry.handle;

import com.ld.poetry.dao.HistoryInfoMapper;
import com.ld.poetry.utils.CommonConst;
import com.ld.poetry.utils.UCache;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArraySet;

@Component
@EnableScheduling
@Slf4j
public class ScheduleTask {

    @Resource
    private HistoryInfoMapper historyInfoMapper;

    @Scheduled(cron = "0 0 0 * * ?")
    public void cleanIpHistory() {
        CopyOnWriteArraySet<String> ipHistory = (CopyOnWriteArraySet<String>) UCache.get(CommonConst.IP_HISTORY);
        if (ipHistory == null) {
            ipHistory = new CopyOnWriteArraySet<>();
            UCache.put(CommonConst.IP_HISTORY, ipHistory);
        }
        ipHistory.clear();

        UCache.remove(CommonConst.IP_HISTORY_STATISTICS);
        Map<String, Object> history = new HashMap<>();
        history.put(CommonConst.IP_HISTORY_PROVINCE, historyInfoMapper.getHistoryByProvince());
        history.put(CommonConst.IP_HISTORY_IP, historyInfoMapper.getHistoryByIp());
        history.put(CommonConst.IP_HISTORY_HOUR, historyInfoMapper.getHistoryBy24Hour());
        history.put(CommonConst.IP_HISTORY_COUNT, historyInfoMapper.getHistoryCount());
        UCache.put(CommonConst.IP_HISTORY_STATISTICS, history);
    }
}
