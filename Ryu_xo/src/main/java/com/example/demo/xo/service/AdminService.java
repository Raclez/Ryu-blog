package com.example.demo.xo.service;

import com.example.demo.commons.entity.Admin;
import com.example.demo.xo.vo.AdminVO;
import com.example.demo.base.service.SuperService;

import java.util.List;

/**
 * 管理员表 服务类
 *
 * @author 
 * @date 2021-07-04
 */
public interface AdminService extends SuperService<Admin> {

    /**
     * 通过UID获取Admin
     *
     * @param uid
     * @return
     */
    public Admin getAdminByUid(String uid);

    /**
     * 获取在线用户列表
     *
     * @param adminVO
     * @return
     */
    public String getOnlineAdminList(AdminVO adminVO);

    /**
     * Web端通过用户名获取一个Admin
     *
     * @param userName
     * @return
     */
    public Admin getAdminByUser(String userName);

    /**
     * 获取当前管理员
     *
     * @return
     */
    public Admin getMe();

    /**
     * 添加在线用户
     *
     * @param admin            管理员
     * @param expirationSecond 过期时间【秒】
     */
    public void addOnlineAdmin(Admin admin, Long expirationSecond);

    /**
     * 获取管理员列表
     *
     * @param adminVO
     * @return
     */
    public String getList(AdminVO adminVO);

    /**
     * 添加管理员
     *
     * @param adminVO
     * @return
     */
    public String addAdmin(AdminVO adminVO);

    /**
     * 编辑管理员
     *
     * @param adminVO
     * @return
     */
    public String editAdmin(AdminVO adminVO);

    /**
     * 编辑当前管理员信息
     *
     * @return
     */
    public String editMe(AdminVO adminVO);

    /**
     * 修改密码
     *
     * @return
     */
    public String changePwd(String oldPwd, String newPwd);

    /**
     * 重置密码
     *
     * @param adminVO
     * @return
     */
    public String resetPwd(AdminVO adminVO);

    /**
     * 批量删除管理员
     *
     * @param adminUids
     * @return
     */
    public String deleteBatchAdmin(List<String> adminUids);

    public String forceLogout(List<String> tokenList);

    /**
     * 检查是否登录是同一用户
     * @@param username
     * @return
     */
    public  void  checkSameUser(String username);
}
