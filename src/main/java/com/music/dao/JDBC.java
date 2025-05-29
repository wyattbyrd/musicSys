package com.music.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * JDBC数据库连接管理类
 * 支持MySQL 8.0数据库连接
 */
public class JDBC {
    private String driver;
    private String url;
    private String user;
    private String pass;
    private Connection conn;

    public JDBC() {
        // MySQL 8.0配置
        this.driver = "com.mysql.cj.jdbc.Driver";
        this.url = "jdbc:mysql://127.0.0.1:3306/music_sys?characterEncoding=utf8&serverTimezone=Asia/Shanghai&useSSL=false";
        this.user = "root";
        this.pass = "root";
    }

    public JDBC(String driver, String url, String user, String pass) {
        this.driver = driver;
        this.url = url;
        this.user = user;
        this.pass = pass;
    }

    /**
     * 初始化数据库连接
     */
    public void startConnection() throws Exception {
        Class.forName(this.driver);
        this.conn = DriverManager.getConnection(this.url, this.user, this.pass);
    }

    /**
     * 关闭数据库连接
     */
    public void stopConnection() throws Exception {
        if (this.conn != null) {
            this.conn.close();
            this.conn = null;
        }
    }

    /**
     * 获取数据库连接
     */
    public Connection getConnection() throws Exception {
        if (conn == null) {
            startConnection();
        }
        return conn;
    }

    /**
     * 执行查询操作
     */
    public ResultSet query(String sql) throws Exception {
        if (conn == null) {
            startConnection();
        }
        Statement tmpstmt = conn.createStatement();
        return tmpstmt.executeQuery(sql);
    }

    /**
     * 执行更新操作
     */
    public int update(String sql) throws Exception {
        if (conn == null) {
            startConnection();
        }
        Statement tmpstmt = conn.createStatement();
        return tmpstmt.executeUpdate(sql);
    }

    /**
     * 开启事务
     */
    public void startTransaction() throws Exception {
        if (conn == null) {
            startConnection();
        }
        this.conn.setAutoCommit(false);
    }

    /**
     * 关闭事务
     */
    public void stopTransaction() throws Exception {
        if (this.conn == null || this.conn.getAutoCommit())
            return;
        this.conn.setAutoCommit(true);
    }

    /**
     * 提交事务
     */
    public void commit() throws Exception {
        if (this.conn == null || this.conn.getAutoCommit())
            return;
        this.conn.commit();
    }

    /**
     * 回滚事务
     */
    public void rollback() throws Exception {
        if (this.conn == null || this.conn.getAutoCommit())
            return;
        this.conn.rollback();
    }

    // Getters and Setters
    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
} 