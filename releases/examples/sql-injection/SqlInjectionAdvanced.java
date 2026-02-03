package com.example;

import java.sql.*;

/**
 * SQL注入高级示例 - 更多SQL注入场景
 */
public class SqlInjectionAdvanced {
    
    private Connection conn;
    
    /**
     * 场景1：字符串拼接后直接执行（高风险）
     */
    public ResultSet directConcat(String userId) throws SQLException {
        Statement stmt = conn.createStatement();
        // ❌ 高风险：直接拼接用户输入
        String sql = "SELECT * FROM users WHERE id = '" + userId + "'";
        return stmt.executeQuery(sql);
    }
    
    /**
     * 场景2：使用String.format（高风险）
     */
    public ResultSet stringFormatVulnerable(String tableName, String condition) throws SQLException {
        Statement stmt = conn.createStatement();
        // ❌ 高风险：使用String.format拼接SQL
        String sql = String.format("SELECT * FROM %s WHERE %s", tableName, condition);
        return stmt.executeQuery(sql);
    }
    
    /**
     * 场景3：使用StringBuilder拼接（高风险）
     */
    public ResultSet stringBuilderVulnerable(String name, String email) throws SQLException {
        Statement stmt = conn.createStatement();
        // ❌ 高风险：使用StringBuilder动态构建SQL
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1");
        if (name != null) {
            sql.append(" AND name = '").append(name).append("'");
        }
        if (email != null) {
            sql.append(" AND email = '").append(email).append("'");
        }
        return stmt.executeQuery(sql.toString());
    }
    
    /**
     * 场景4：ORDER BY子句注入（中风险）
     */
    public ResultSet orderByInjection(String sortColumn, String sortOrder) throws SQLException {
        Statement stmt = conn.createStatement();
        // ❌ 风险：ORDER BY子句也可能被注入
        String sql = "SELECT * FROM products ORDER BY " + sortColumn + " " + sortOrder;
        return stmt.executeQuery(sql);
    }
    
    /**
     * 场景5：LIKE子句注入（高风险）
     */
    public ResultSet likeClauseInjection(String searchTerm) throws SQLException {
        Statement stmt = conn.createStatement();
        // ❌ 高风险：LIKE子句拼接用户输入
        String sql = "SELECT * FROM articles WHERE title LIKE '%" + searchTerm + "%'";
        return stmt.executeQuery(sql);
    }
    
    /**
     * 场景6：批量操作注入（高风险）
     */
    public void batchOperationVulnerable(String[] userIds) throws SQLException {
        Statement stmt = conn.createStatement();
        // ❌ 高风险：批量删除拼接用户输入
        for (String userId : userIds) {
            String sql = "DELETE FROM users WHERE id = '" + userId + "'";
            stmt.addBatch(sql);
        }
        stmt.executeBatch();
    }
    
    /**
     * 场景7：安全写法 - 使用PreparedStatement（推荐）
     */
    public ResultSet secureQuery(String userId, String status) throws SQLException {
        // ✅ 安全：使用PreparedStatement参数化查询
        String sql = "SELECT * FROM users WHERE id = ? AND status = ?";
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setString(1, userId);
        pstmt.setString(2, status);
        return pstmt.executeQuery();
    }
    
    /**
     * 场景8：安全写法 - 使用IN子句（Java 8+）
     */
    public ResultSet secureInClause(String[] userIds) throws SQLException {
        // ✅ 安全：动态构建参数化IN子句
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE id IN (");
        for (int i = 0; i < userIds.length; i++) {
            if (i > 0) sql.append(",");
            sql.append("?");
        }
        sql.append(")");
        
        PreparedStatement pstmt = conn.prepareStatement(sql.toString());
        for (int i = 0; i < userIds.length; i++) {
            pstmt.setString(i + 1, userIds[i]);
        }
        return pstmt.executeQuery();
    }
}
