package com.shiro.realm;

import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;

import java.util.*;

public class CustomRealm extends AuthorizingRealm {

    Map<String, String> map = new HashMap<>();

    {
        map.put("Schuyler", "123456");
        //定义reaml名
        super.setName("customRealm");
    }

    /**
     * 这里是授权的方法
     */
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        //1.从认证信息中获取用户名称
        String userName = (String) principalCollection.getPrimaryPrincipal();

        //2.根据用户名称获取对应角色信息，本应从数据库中获取数据，为了方便，模拟数据
        Set<String> roles = getRolesByUserName(userName);
        if (roles.size() == 0) {
            return null;
        }

        //3.根据角色到数据库中获取权限，为了方便，这里模拟数据
        Set<String> permissions = getPermissionsByroleName(roles);

        //4.封装、返回结果
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        simpleAuthorizationInfo.setRoles(roles);
        simpleAuthorizationInfo.setStringPermissions(permissions);

        return simpleAuthorizationInfo;
    }

    /**
     * 这里是认证的方法
     */
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {

        //1.从用户提供的信息中获取用户名称
        String userName = (String) authenticationToken.getPrincipal();

        //2.这里本应该从数据库中通过userName去获取password的，但是为了方便，模拟数据
        String password = getPasswordByUserName(userName);

        //3.封装、返回结果
        if (password == null) {
            return null;
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(userName, password, "customRealm");
        return simpleAuthenticationInfo;
    }

    /**
     * 模拟在数据库中获取密码数据
     */
    private String getPasswordByUserName(String userName) {
        return map.get(userName);
    }

    /**
     * 模拟在数据库中获取角色数据
     */
    private Set<String> getRolesByUserName(String userName) {
        Set<String> sets = new HashSet<>();
        sets.add("admin");
        sets.add("user");
        return sets;
    }

    /**
     * 模拟在数据库中获取权限数据
     */
    private Set<String> getPermissionsByroleName(Set<String> roles) {
        Set<String> permissions = new HashSet<>();
        permissions.add("user:delete");
        permissions.add("user:add");
        return permissions;
    }
}
