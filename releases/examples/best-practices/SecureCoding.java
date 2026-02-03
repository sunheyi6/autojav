package com.example;

import java.sql.*;
import java.util.Objects;

/**
 * 安全编码最佳实践示例
 * 展示如何编写避免常见漏洞的代码
 */
public class SecureCoding {
    
    private Connection conn;
    
    // ==================== SQL注入防护 ====================
    
    /**
     * ✅ 使用PreparedStatement防止SQL注入
     */
    public ResultSet findUserSecure(String userId) throws SQLException {
        String sql = "SELECT * FROM users WHERE id = ? AND status = ?";
        try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, userId);
            pstmt.setString(2, "ACTIVE");
            return pstmt.executeQuery();
        }
    }
    
    /**
     * ✅ 使用存储过程（额外安全层）
     */
    public ResultSet callStoredProcedure(String userId) throws SQLException {
        String sql = "{call GetUserById(?)}";
        try (CallableStatement cstmt = conn.prepareCall(sql)) {
            cstmt.setString(1, userId);
            return cstmt.executeQuery();
        }
    }
    
    // ==================== 空指针防护 ====================
    
    /**
     * ✅ 使用Objects.requireNonNull进行参数校验
     */
    public void processUserSecure(String userId, String email) {
        Objects.requireNonNull(userId, "userId must not be null");
        Objects.requireNonNull(email, "email must not be null");
        
        // 已经验证非null，可以安全使用
        System.out.println("Processing: " + userId + ", " + email.toLowerCase());
    }
    
    /**
     * ✅ 显式空检查
     */
    public int getStringLengthSafe(String input) {
        // 显式null检查
        if (input == null) {
            return 0;
        }
        return input.length();
    }
    
    /**
     * ✅ 使用Optional避免NPE
     */
    public String getUserNameSecure(String userId) {
        return java.util.Optional.ofNullable(findUserById(userId))
                .map(user -> user.name)
                .orElse("Unknown");
    }
    
    /**
     * ✅ 链式调用前进行空检查
     */
    public int getAddressZipCodeSafe(User user) {
        if (user == null) {
            return 0;
        }
        if (user.getAddress() == null) {
            return 0;
        }
        if (user.getAddress().getZipCode() == null) {
            return 0;
        }
        return user.getAddress().getZipCode();
    }
    
    /**
     * ✅ Java 8+ 使用Optional链式调用
     */
    public int getAddressZipCodeModern(User user) {
        return java.util.Optional.ofNullable(user)
                .map(User::getAddress)
                .map(Address::getZipCode)
                .orElse(0);
    }
    
    // ==================== 资源管理 ====================
    
    /**
     * ✅ 使用try-with-resources确保资源关闭
     */
    public void queryWithResourceManagement() throws SQLException {
        String sql = "SELECT * FROM users";
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } // 自动关闭stmt和rs
    }
    
    // ==================== 辅助类 ====================
    
    private User findUserById(String userId) {
        // 模拟查找用户
        return null;
    }
    
    static class User {
        String name;
        Address address;
        
        String getName() { return name; }
        Address getAddress() { return address; }
    }
    
    static class Address {
        Integer zipCode;
        
        Integer getZipCode() { return zipCode; }
    }
}
