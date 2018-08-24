package com.shiro.realm;

import com.shiro.dao.UserDao;
import com.shiro.entity.User;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CustomRealm extends AuthorizingRealm {

    @Autowired
    private UserDao userDao;
    {
        //定义reaml名
        super.setName("customRealm");
    }
    /**
     * 这里是授权的方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {

        //1.从认证信息中获取用户名称
        String userName = (String) principalCollection.getPrimaryPrincipal();

        //2.根据用户名称获取对应角色信息，从数据库中获取数据
        Set<String> roles = getRolesByUserName(userName);
        if (roles.size() == 0) {
            return null;
        }

        //3.根据角色到数据库中获取权限
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
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) {

        //1.从用户提供的信息中获取用户名称
        String userName = (String) authenticationToken.getPrincipal();

        //2.从数据库中通过userName去获取password的
        User user = getPasswordByUserName(userName);

        if (user == null) {
            return null;
        }

        //3.封装、返回结果
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(user.getUserName(), user.getPassword(), getName());
        simpleAuthenticationInfo.setCredentialsSalt(ByteSource.Util.bytes(user.getPasswordSalt()));
        return simpleAuthenticationInfo;
    }

    /**
     * 在数据库中获取密码数据
     */
    private User getPasswordByUserName(String userName) {
        User user = userDao.getUserByUserName(userName);
        if (user != null) {
            return user;
        }
        return null;
    }

    /**
     * 在数据库中获取角色数据
     */
    private Set<String> getRolesByUserName(String userName) {
        List<String> roles = userDao.getRolesByUserName(userName);
        if (roles.size() != 0) {
            Set<String> sets = new HashSet<>();
            for (String role : roles) {
                sets.add(role);
            }
            return sets;
        }
        return null;
    }

    /**
     * 在数据库中获取权限数据
     */
    private Set<String> getPermissionsByroleName(Set<String> roles) {
        Set<String> permissions = new HashSet<>();
        Iterator<String> iterator = roles.iterator();
        while (iterator.hasNext()) {
            String role = iterator.next();
            List<String> permissionList = userDao.getPermissionsByroleName(role);
            if (permissionList != null && permissionList.size() != 0) {
                for(String permission : permissionList) {
                    permissions.add(permission);
                }
            }
        }
        if (permissions.size() == 0) {
            return null;
        }
        return permissions;
    }
}
