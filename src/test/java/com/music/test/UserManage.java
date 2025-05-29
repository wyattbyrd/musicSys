package com.music.test;

import com.music.dao.UserDao;
import com.music.pojo.User;
import org.junit.Test;

import java.sql.Date;
import java.util.ArrayList;

/**
 * 用户管理测试类
 */
public class UserManage {
    @Test
    public void testAll() {
        ArrayList<User> users = UserDao.all();
        if (users != null) {
            for (User user : users) {
                System.out.println(user);
            }
        } else {
            System.out.println("获取用户列表失败");
        }
    }

    @Test
    public void testQueryByUsername() {
        String username = "admin";
        User user = UserDao.queryByUsername(username);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("未找到用户名为 " + username + " 的用户");
        }
    }

    @Test
    public void testQueryById() {
        int userId = 1;
        User user = UserDao.queryById(userId);
        if (user != null) {
            System.out.println(user);
        } else {
            System.out.println("未找到ID为 " + userId + " 的用户");
        }
    }

    @Test
    public void testInsert() {
        try {
            Date currentDate = new Date(System.currentTimeMillis());
            
            User user = new User();
            user.setUsername("test");
            user.setPassword("123456");
            user.setRole("user");
            user.setEmail("test@example.com");
            
            int result = UserDao.insert(user);
            if (result > 0) {
                System.out.println("添加用户成功");
            } else {
                System.out.println("添加用户失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdate() {
        try {
            Date currentDate = new Date(System.currentTimeMillis());
            
            User user = new User();
            user.setUserId(1);
            user.setUsername("admin");
            user.setPassword("admin123");
            user.setRole("admin");
            user.setEmail("admin@example.com");
            
            int result = UserDao.update(user);
            if (result > 0) {
                System.out.println("更新用户成功");
            } else {
                System.out.println("更新用户失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        int userId = 1;
        int result = UserDao.delete(userId);
        if (result > 0) {
            System.out.println("删除用户成功");
        } else {
            System.out.println("删除用户失败");
        }
    }
} 