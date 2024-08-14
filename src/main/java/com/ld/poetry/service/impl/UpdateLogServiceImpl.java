package com.ld.poetry.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ld.poetry.dao.ArticleMapper;
import com.ld.poetry.dao.UpdateLogMapper;
import com.ld.poetry.entity.Article;
import com.ld.poetry.entity.UpdateLog;
import com.ld.poetry.service.ArticleService;
import com.ld.poetry.service.UpdateLogService;
import com.ld.poetry.vo.BodyItem;
import com.ld.poetry.vo.MonthEntry;
import com.ld.poetry.vo.UpdateLogVo;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.service.impl
 * @ClassName UpdateLogServiceImpl
 * @Author: Utopia
 * @Description: TODO
 * @Version: 1.0
 */
@Service
public class UpdateLogServiceImpl extends ServiceImpl<UpdateLogMapper, UpdateLog> implements UpdateLogService {

    @Resource
    private UpdateLogMapper updateLogMapper;

    @Override
    public List<UpdateLogVo> listUpdateLog() {

        List<UpdateLog> logs = updateLogMapper.findAll();


        if (logs.isEmpty()) return null;

        // 初始化第一个元素（外层结构）

        Map<String, List<UpdateLog>> vos = logs.stream().collect(Collectors.groupingBy(UpdateLog::getYear));
        List<String> years = logs.stream().map(UpdateLog::getYear).distinct().sorted(new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o2)-Integer.parseInt(o1);
            }
        }).collect(Collectors.toList());
        List<UpdateLogVo> logVos=new ArrayList<>();
        for (String year : years){
            UpdateLogVo vo = new UpdateLogVo();
            vo.setYear(year);
            List<UpdateLog> logs1 = vos.get(year);
            List<String> months = logs1.stream().map(UpdateLog::getMonth).distinct().sorted(new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return Integer.parseInt(o2)-Integer.parseInt(o1);
                }
            }).collect(Collectors.toList());
            Map<String, List<UpdateLog>> map = logs1.stream().collect(Collectors.groupingBy(UpdateLog::getMonth));
            List<MonthEntry> monthEntries=new ArrayList<>();
            for (String month : months){
                MonthEntry monthEntry = new MonthEntry();
                monthEntry.setMonth(month);
                List<UpdateLog> updateLogs = map.get(month);
                List<BodyItem> titles = updateLogs.stream().map(updateLog -> {
                    BodyItem bodyItem = new BodyItem();
                    bodyItem.setTitle(updateLog.getTitle());
                    return bodyItem;
                }).collect(Collectors.toList());
                monthEntry.setChildren(titles);
                monthEntries.add(monthEntry);

            }
            vo.setBody(monthEntries);
            logVos.add(vo);
        }


        return logVos;
    }
}
