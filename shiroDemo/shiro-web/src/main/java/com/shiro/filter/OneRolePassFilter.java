package com.shiro.filter;

import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义单角色过滤器
 * @author CaiShunfeng
 */
public class OneRolePassFilter extends AuthorizationFilter {

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o) throws Exception {
        //getSubject是父类中的方法，可以直接获取subject对象
        Subject subject = getSubject(servletRequest, servletResponse);
        //参数o是在使用该过滤器时传入的字符串数组，如 /test = oneRolePassFiter[a,b,c]中的a,b,c
        String[] roles = (String[]) o;
        if (roles == null || roles.length == 0) {
            return true;
        }
        for (String role : roles) {
            if (subject.hasRole(role)) {
                return true;
            }
        }
        return false;
    }
}
