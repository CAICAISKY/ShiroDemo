package com.shiro.test;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.text.IniRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

public class IniRealmTest {

    @Test
    public void testAuthentication() {
        //1.创建IniRealm实例
        IniRealm iniRealm = new IniRealm("classpath:user.ini");

        //2.创建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(iniRealm);
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        //3.用户提交认证请求
        UsernamePasswordToken token = new UsernamePasswordToken("Schuyler", "123456");
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);

        //4.认证
        System.out.println("authenticated: " + subject.isAuthenticated());

        //5.角色校验
        subject.checkRole("admin");

        //6.权限校验
        subject.checkPermissions("user:delete", "user:update", "user:add");
    }
}
