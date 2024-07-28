package com.ld.poetry.entity.vo;

/**
 * @BelongsProject: UBlog
 * @BelongsPackage: com.ld.poetry.entity.vo
 * @ClassName SysUserOnline
 * @Author: Utopia
 * @Description: 当前在线会话
 * @Version: 1.0
 */
public class SysUserOnline {

    /** 会话编号 */
    private String tokenId;

    /** 部门名称 */
    private String deptName;

    /** 用户名称 */
    private String userName;

    /** 登录IP地址 */
    private String ipaddr;

    /** 登录地址 */
    private String loginLocation;

    /** 浏览器类型 */
    private String browser;

    /** 操作系统 */
    private String os;

    /** 登录时间 */
    private Long loginTime;


}
