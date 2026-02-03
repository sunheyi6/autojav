package com.autojav.examples;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FileProcessor {
    
    private String databaseUrl;
    
    public FileProcessor(String databaseUrl) {
        this.databaseUrl = databaseUrl;
    }
    
    public void processFile(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        
        while ((line = reader.readLine()) != null) {
            processLine(line);
        }
    }
    
    public void exportData(String filePath) throws IOException {
        FileWriter writer = new FileWriter(filePath);
        
        writer.write("ID,Name,Email\n");
        
        Connection conn = DriverManager.getConnection(databaseUrl);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            writer.write(rs.getString("id") + ",");
            writer.write(rs.getString("name") + ",");
            writer.write(rs.getString("email") + "\n");
        }
    }
    
    public void importData(String filePath) throws IOException, SQLException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        String line;
        
        Connection conn = DriverManager.getConnection(databaseUrl);
        PreparedStatement stmt = conn.prepareStatement("INSERT INTO users (id, name, email) VALUES (?, ?, ?)");
        
        reader.readLine();
        
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            stmt.setString(1, parts[0]);
            stmt.setString(2, parts[1]);
            stmt.setString(3, parts[2]);
            stmt.executeUpdate();
        }
    }
    
    public String readFileContent(String filePath) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        StringBuilder content = new StringBuilder();
        String line;
        
        while ((line = reader.readLine()) != null) {
            content.append(line).append("\n");
        }
        
        return content.toString();
    }
    
    public void backupData(String filePath) throws IOException, SQLException {
        FileWriter writer = new FileWriter(filePath);
        
        Connection conn = DriverManager.getConnection(databaseUrl);
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM orders");
        ResultSet rs = stmt.executeQuery();
        
        while (rs.next()) {
            writer.write(rs.getString("id") + "|");
            writer.write(rs.getString("user_id") + "|");
            writer.write(rs.getString("status") + "\n");
        }
    }
    
    private void processLine(String line) {
        System.out.println("Processing: " + line);
    }
}