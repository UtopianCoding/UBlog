package com.ld.poetry.utils;

import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baidubce.http.ApiExplorerClient;
import com.baidubce.http.AppSigner;
import com.baidubce.http.HttpMethodName;
import com.baidubce.model.ApiExplorerRequest;
import com.baidubce.model.ApiExplorerResponse;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.ld.poetry.dao.*;
import com.ld.poetry.entity.*;
import com.ld.poetry.service.UserService;
import com.ld.poetry.vo.FamilyVO;
import org.apache.commons.io.IOUtils;
import org.lionsoul.ip2region.xdb.Searcher;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.stream.Collectors;


@Component
public class CommonQuery {
    @Autowired
    private CommentMapper commentMapper;

    @Autowired
    private HistoryInfoMapper historyInfoMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private SortMapper sortMapper;

    @Autowired
    private LabelMapper labelMapper;

    @Autowired
    private ArticleMapper articleMapper;

    @Autowired
    private FamilyMapper familyMapper;

    private Searcher searcher;



    @PostConstruct
    public void init() {
        try {
            searcher = Searcher.newWithBuffer(IOUtils.toByteArray(new ClassPathResource("ip2region.xdb").getInputStream()));
        } catch (Exception e) {
        }
    }

    public String queryIp(String ip){
//        String path = "http://gwgp-hrtx4zoqeyk.n.bdcloudapi.com/iplocaltion";
//        ApiExplorerRequest request = new ApiExplorerRequest(HttpMethodName.GET, path);
//        request.setCredentials("eb502f1a2c244089899d1888707a136a", "48ba317adf384e738acee1f7f0808905");
//
//        request.addHeaderParameter("Content-Type", "application/json;charset=UTF-8");
//
//        request.addQueryParameter("ip", ip);
//
//
//
//        ApiExplorerClient client = new ApiExplorerClient(new AppSigner());
//
//        try {
//            ApiExplorerResponse response = client.sendRequest(request);
//            // 返回结果格式为Json字符串
//            return response.getResult();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return null;
        String path="https://searchplugin.csdn.net/api/v1/ip/get?ip="+ip;
        HttpResponse httpResponse = HttpRequest.get(path).execute();
        return httpResponse.body();

    }

//    public static void main(String[] args) {
//        String path="https://searchplugin.csdn.net/api/v1/ip/get?ip=47.93.39.67";
//        HttpResponse httpResponse = HttpRequest.get(path).execute();
//        String body = httpResponse.body();
//        JSONObject jsonObject = (JSONObject) JSONObject.parseObject(body).get("data");
//        String address = (String) jsonObject.get("address");
//        String[] s = address.split(" ");
//        System.out.println(jsonObject.get("address"));
//    }

    public void saveHistory(String ip) {
        Integer userId = UBUtil.getUserId();
        String ipUser = ip + (userId != null ? "_" + userId.toString() : "");

        CopyOnWriteArraySet<String> ipHistory = (CopyOnWriteArraySet<String>) UCache.get(CommonConst.IP_HISTORY);
        if (!ipHistory.contains(ipUser)) {
            synchronized (ipUser.intern()) {
                if (!ipHistory.contains(ipUser)) {
                    ipHistory.add(ipUser);
                    HistoryInfo historyInfo = new HistoryInfo();
                    historyInfo.setIp(ip);
                    historyInfo.setUserId(userId);
                    String ipResult = queryIp(ip);

                    if (ipResult!=null){
//                        JSONObject jsonObject = (JSONObject) JSONObject.parseObject(ipResult).get("result");
                        JSONObject jsonObject = (JSONObject) JSONObject.parseObject(ipResult).get("data");
                        String address = (String) jsonObject.get("address");
                        String[] s = address.split(" ");
                        if (ip!="127.0.0.1"){
                            historyInfo.setNation(s[0]);
                            historyInfo.setProvince(s[1]);
                            historyInfo.setCity(s[2]);
                        }else{
                            historyInfo.setNation(s[0]);
                            historyInfo.setProvince(s[0]);
                            historyInfo.setCity(s[0]);
                        }

                    }
                    historyInfoMapper.insert(historyInfo);
                }
            }
        }
    }

    public User getUser(Integer userId) {
        User user = (User) UCache.get(CommonConst.USER_CACHE + userId.toString());
        if (user != null) {
            return user;
        }
        User u = userService.getById(userId);
        if (u != null) {
            UCache.put(CommonConst.USER_CACHE + userId.toString(), u, CommonConst.EXPIRE);
            return u;
        }
        return null;
    }

    public List<User> getAdmire() {
        List<User> admire = (List<User>) UCache.get(CommonConst.ADMIRE);
        if (admire != null) {
            return admire;
        }
        List<User> users = userService.lambdaQuery().select(User::getUsername, User::getAdmire, User::getAvatar).isNotNull(User::getAdmire).list();

        UCache.put(CommonConst.ADMIRE, users, CommonConst.EXPIRE);

        return users;
    }

    public List<FamilyVO> getFamilyList() {
        List<FamilyVO> familyVOList = (List<FamilyVO>) UCache.get(CommonConst.FAMILY_LIST);
        if (familyVOList != null) {
            return familyVOList;
        }

        LambdaQueryChainWrapper<Family> queryChainWrapper = new LambdaQueryChainWrapper<>(familyMapper);
        List<Family> familyList = queryChainWrapper.eq(Family::getStatus, Boolean.TRUE).list();
        if (!CollectionUtils.isEmpty(familyList)) {
            familyVOList = familyList.stream().map(family -> {
                FamilyVO familyVO = new FamilyVO();
                BeanUtils.copyProperties(family, familyVO);
                return familyVO;
            }).collect(Collectors.toList());
        } else {
            familyVOList = new ArrayList<>();
        }

        UCache.put(CommonConst.FAMILY_LIST, familyVOList);
        return familyVOList;
    }

    public Integer getCommentCount(Integer source, String type) {
        Integer count = (Integer) UCache.get(CommonConst.COMMENT_COUNT_CACHE + source.toString() + "_" + type);
        if (count != null) {
            return count;
        }
        LambdaQueryChainWrapper<Comment> wrapper = new LambdaQueryChainWrapper<>(commentMapper);
        Integer c = wrapper.eq(Comment::getSource, source).eq(Comment::getType, type).count();
        UCache.put(CommonConst.COMMENT_COUNT_CACHE + source.toString() + "_" + type, c, CommonConst.EXPIRE);
        return c;
    }

    public List<Integer> getUserArticleIds(Integer userId) {
        List<Integer> ids = (List<Integer>) UCache.get(CommonConst.USER_ARTICLE_LIST + userId.toString());
        if (ids != null) {
            return ids;
        }
        LambdaQueryChainWrapper<Article> wrapper = new LambdaQueryChainWrapper<>(articleMapper);
        List<Article> articles = wrapper.eq(Article::getUserId, userId).select(Article::getId).list();
        List<Integer> collect = articles.stream().map(Article::getId).collect(Collectors.toList());
        UCache.put(CommonConst.USER_ARTICLE_LIST + userId.toString(), collect, CommonConst.EXPIRE);
        return collect;
    }

    public List<List<Integer>> getArticleIds(String searchText) {
        List<Article> articles = (List<Article>) UCache.get(CommonConst.ARTICLE_LIST);
        if (articles == null) {
            LambdaQueryChainWrapper<Article> wrapper = new LambdaQueryChainWrapper<>(articleMapper);
            articles = wrapper.select(Article::getId, Article::getArticleTitle, Article::getArticleContent)
                    .orderByDesc(Article::getCreateTime)
                    .list();
            UCache.put(CommonConst.ARTICLE_LIST, articles);
        }

        List<List<Integer>> ids = new ArrayList<>();
        List<Integer> titleIds = new ArrayList<>();
        List<Integer> contentIds = new ArrayList<>();

        for (Article article : articles) {
            if (StringUtil.matchString(article.getArticleTitle(), searchText)) {
                titleIds.add(article.getId());
            } else if (StringUtil.matchString(article.getArticleContent(), searchText)) {
                contentIds.add(article.getId());
            }
        }

        ids.add(titleIds);
        ids.add(contentIds);
        return ids;
    }

    public List<Sort> getSortInfo() {
        List<Sort> sorts = new LambdaQueryChainWrapper<>(sortMapper).list();
        if (!CollectionUtils.isEmpty(sorts)) {
            sorts.forEach(sort -> {
                LambdaQueryChainWrapper<Article> sortWrapper = new LambdaQueryChainWrapper<>(articleMapper);
                Integer countOfSort = sortWrapper.eq(Article::getSortId, sort.getId()).eq(Article::getViewStatus, PoetryEnum.STATUS_ENABLE.getCode()).count();
                sort.setCountOfSort(countOfSort);

                LambdaQueryChainWrapper<Label> wrapper = new LambdaQueryChainWrapper<>(labelMapper);
                List<Label> labels = wrapper.eq(Label::getSortId, sort.getId()).list();
                if (!CollectionUtils.isEmpty(labels)) {
                    labels.forEach(label -> {
                        LambdaQueryChainWrapper<Article> labelWrapper = new LambdaQueryChainWrapper<>(articleMapper);
                        Integer countOfLabel = labelWrapper.eq(Article::getLabelId, label.getId()).eq(Article::getViewStatus, PoetryEnum.STATUS_ENABLE.getCode()).count();
                        label.setCountOfLabel(countOfLabel);
                    });
                    sort.setLabels(labels);
                }
            });
            return sorts;
        } else {
            return null;
        }
    }
}
