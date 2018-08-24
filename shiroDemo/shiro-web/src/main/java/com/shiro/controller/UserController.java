package com.shiro.controller;

import com.shiro.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.shiro.subject.Subject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * @author CaiShunfeng
 */
@Controller
public class UserController {

    @RequestMapping(value = "/subLogin", method = RequestMethod.POST,
            produces = "application/json;charset=utf8")
    @ResponseBody
    public String subLogin(User user) {
        Subject subject = SecurityUtils.getSubject();
        UsernamePasswordToken token = new UsernamePasswordToken(user.getUserName(), user.getPassword());
        try {
            subject.login(token);
            //这里只是为了测试一下所写的dao层有没有问题，所以简单地把校验方法写在这里
            subject.checkRoles("admin");
            subject.checkPermission("user:delete");
        } catch (AuthenticationException e) {
            return e.getMessage();
        }
        return "登陆成功,为admin角色，有user:delete权限";
    }

    @RequiresRoles("admin")
    @RequestMapping(value = "/hasRoles", method = RequestMethod.GET,
            produces = "application/json;charset=utf8")
    @ResponseBody
    public String hasRoles() {
        return "有admin用户";
    }

    @RequiresPermissions("user:delete")
    @RequestMapping(value = "/hasPermissions", method = RequestMethod.GET,
            produces = "application/json;charset=utf8")
    @ResponseBody
    public String hasPermissions() {
        return "有user:delete权限";
    }
}
