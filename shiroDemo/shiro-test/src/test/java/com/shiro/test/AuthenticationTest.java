package com.shiro.test;


import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.SimpleAccountRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Test;

/**
 * 认证测试
 */
public class AuthenticationTest {

    @Test
    public void testAuthenticaition() {

        //1.这里先简单的使用SimpleAccountReaml来模拟数据
        SimpleAccountRealm simpleAccountRealm = new SimpleAccountRealm();
        simpleAccountRealm.addAccount("Schuyler", "123456", "admin");

        //2.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(simpleAccountRealm);
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        //3.主体提供认证请求
        UsernamePasswordToken token = new UsernamePasswordToken("Schuyler", "123456");
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);

        //4.认证校验
        System.out.println("IsAuthenticated: " + subject.isAuthenticated());

        //5.角色校验，当校验失败时会直接抛出异常
        subject.checkRole("admin");

        /*这里由于SimpleAccountRealm无法设置到权限所以不做权限校验，请看下回分解*/
    }
}
