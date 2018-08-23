package com.shiro.dao;

import com.shiro.entity.User;

import java.util.List;

/**
 * @author Schuyler
 */
public interface UserDao {
    /**
     * 根据用户提供的用户名查找认证信息
     * @param userName 用户名
     * @return 用户认证信息
     */
    User getUserByUserName(String userName);

    /**
     * 根据用户名称获取角色信息
     * @param userName 用户名
     * @return 角色信息
     */
    List<String> getRolesByUserName(String userName);

    /**
     * 根据角色名称获取权限信息
     * @param roleName 角色
     * @return 权限信息
     */
    List<String> getPermissionsByroleName(String roleName);
}
