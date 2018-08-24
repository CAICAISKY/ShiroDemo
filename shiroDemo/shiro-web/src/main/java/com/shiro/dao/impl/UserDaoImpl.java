package com.shiro.dao.impl;

import com.shiro.dao.UserDao;
import com.shiro.entity.User;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author CaiShunfeng
 */
@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public User getUserByUserName(String userName) {
        String sql = "SELECT username, password, password_salt FROM users WHERE username = ?";
        List<User> userList = jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<User>() {
            @Override
            public User mapRow(ResultSet resultSet, int i) throws SQLException {
                User user = new User();
                user.setUserName(resultSet.getString("username"));
                user.setPassword(resultSet.getString("password"));
                user.setPasswordSalt(resultSet.getString("password_salt"));
                return user;
            }
        });
        if (CollectionUtils.isEmpty(userList)) {
            return null;
        }
        return userList.get(0);
    }

    @Override
    public List<String> getRolesByUserName(String userName) {
        String sql = "SELECT role_name FROM user_roles WHERE username = ?";
        List<String> roles = jdbcTemplate.query(sql, new String[]{userName}, new RowMapper<String>(){
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("role_name");
            }
        });
        if (CollectionUtils.isEmpty(roles)) {
            return null;
        }
        return roles;
    }

    @Override
    public List<String> getPermissionsByroleName(String roleName) {
        String sql = "SELECT permission FROM roles_permissions WHERE role_name = ?";
        List<String> permissions = jdbcTemplate.query(sql, new String[]{roleName}, new RowMapper<String>() {
            @Override
            public String mapRow(ResultSet resultSet, int i) throws SQLException {
                return resultSet.getString("permission");
            }
        });
        if (permissions.size() == 0) {
            return null;
        }
        return permissions;
    }
}
