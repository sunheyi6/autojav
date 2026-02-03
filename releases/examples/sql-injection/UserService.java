package com.example.service;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 用户服务类 - 包含SQL注入漏洞的示例
 */
public class UserService {
    
    private Connection connection;
    
    public UserService(Connection connection) {
        this.connection = connection;
    }
    
    /**
     * 根据用户名查询用户 - 存在SQL注入漏洞
     * 
     * 漏洞说明：直接拼接用户输入到SQL语句中，攻击者可以注入恶意SQL代码
     * 例如：用户名输入 ' OR '1'='1 会导致查询所有用户
     */
    public ResultSet findUserByUsername(String username) throws SQLException {
        Statement stmt = connection.createStatement();
        // 存在SQL注入漏洞：直接拼接用户输入
        String sql = "SELECT * FROM users WHERE username = '" + username + "'";
        return stmt.executeQuery(sql);
    }
    
    /**
     * 用户登录验证 - 存在SQL注入漏洞
     * 
     * 漏洞说明：直接拼接用户名和密码到SQL语句中
     * 攻击者可以绕过身份验证
     */
    public boolean login(String username, String password) throws SQLException {
        Statement stmt = connection.createStatement();
        // 存在SQL注入漏洞
        String sql = "SELECT * FROM users WHERE username = '" + username + 
                     "' AND password = '" + password + "'";
        ResultSet rs = stmt.executeQuery(sql);
        return rs.next();
    }
    
    /**
     * 根据ID查询用户 - 安全写法（使用预编译语句）
     */
    public ResultSet findUserByIdSecure(int id) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ?";
        java.sql.PreparedStatement pstmt = connection.prepareStatement(sql);
        pstmt.setInt(1, id);
        return pstmt.executeQuery();
    }
}
