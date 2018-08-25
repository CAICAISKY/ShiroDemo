package com.shiro.test;

import com.shiro.realm.CustomRealm;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.crypto.hash.Md5Hash;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.ByteSource;
import org.junit.Test;

public class CustomRealmTest {

    @Test
    public void testAuthentication () throws Exception {

        //1.创建自定义realm
        CustomRealm customRealm = new CustomRealm();

        //2.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(customRealm);
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        //加密处理
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("md5");
        hashedCredentialsMatcher.setHashIterations(1);
        customRealm.setCredentialsMatcher(hashedCredentialsMatcher);

        //3.用户提供认证请求
        UsernamePasswordToken token = new UsernamePasswordToken("Schuyler", "123456");
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);

        //4.认证
        System.out.println("isAuthenticated: " + subject.isAuthenticated());

        //5.角色校验
        subject.checkRole("admin");

        //6.权限校验
        subject.checkPermission("user:delete");
    }

    public static void main(String[] args) {
        Md5Hash md5Hash = new Md5Hash("123456","Schuyler");
        md5Hash.setIterations(1);
        System.out.println(md5Hash.toHex());

    }
}
