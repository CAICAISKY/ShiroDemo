package com.shiro.test;

import com.alibaba.druid.pool.DruidDataSource;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.mgt.DefaultSecurityManager;
import org.apache.shiro.realm.jdbc.JdbcRealm;
import org.apache.shiro.subject.Subject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;

import javax.sql.DataSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class JdbcRealmTest {
    //1.初始化数据源
    private  DruidDataSource dataSource = new DruidDataSource();

    @Before
    public void initDataSource () throws IOException {
        File file = new ClassPathResource("datasource.properties").getFile();
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
        dataSource.setConnectProperties(properties);
    }

    @Test
    public void testAuthentication () throws Exception {
        //2.创建JdbcRealm
        JdbcRealm jdbcRealm = new JdbcRealm();
        jdbcRealm.setDataSource(dataSource);
        //这里需要注意，JdcbRealm默认不会去查询权限，需要打开查询才可以进行校验
        jdbcRealm.setPermissionsLookupEnabled(true);

        //3.构建SecurityManager环境
        DefaultSecurityManager defaultSecurityManager = new DefaultSecurityManager();
        defaultSecurityManager.setRealm(jdbcRealm);
        SecurityUtils.setSecurityManager(defaultSecurityManager);

        //4.用户提供认证请求
        UsernamePasswordToken token = new UsernamePasswordToken("Schuyler", "123456");
        Subject subject = SecurityUtils.getSubject();
        subject.login(token);

        //5.认证
        System.out.println("isAuthenticated: " + subject.isAuthenticated());

        //6.角色校验
        subject.checkRole("admin");

        //7.权限校验
        subject.checkPermission("delete");
    }
}
