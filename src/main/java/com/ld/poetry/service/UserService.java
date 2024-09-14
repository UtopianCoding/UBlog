package com.ld.poetry.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ld.poetry.config.UResult;
import com.ld.poetry.entity.User;
import com.ld.poetry.vo.BaseRequestVO;
import com.ld.poetry.vo.SocialUser;
import com.ld.poetry.vo.UserVO;

import java.util.List;


public interface UserService extends IService<User> {

    /**
     * 用户名、邮箱、手机号/密码登录
     *
     * @param account
     * @param password
     * @return
     */
    UResult<UserVO> login(String account, String password, Boolean isAdmin);

    UResult exit();

    UResult<UserVO> regist(UserVO user);

    UResult<UserVO> updateUserInfo(UserVO user);

    UResult getCode(Integer flag);

    UResult getCodeForBind(String place, Integer flag);

    UResult<UserVO> updateSecretInfo(String place, Integer flag, String code, String password);

    UResult getCodeForForgetPassword(String place, Integer flag);

    UResult updateForForgetPassword(String place, Integer flag, String code, String password);

    UResult<Page> listUser(BaseRequestVO baseRequestVO);

    UResult<List<UserVO>> getUserByUsername(String username);

    UResult<UserVO> token(String userToken);

    UResult<UserVO> subscribe(Integer labelId, Boolean flag);

    void oauth2Login(SocialUser socialUser) throws Exception;
}
