package com.music.test;

import com.music.dao.SongDao;
import com.music.pojo.Song;
import org.junit.Test;

import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌曲管理测试类
 */
public class SongManage {
    @Test
    public void testAll() {
        ArrayList<Song> songs = SongDao.all();
        if (songs != null) {
            for (Song song : songs) {
                System.out.println(song);
            }
        } else {
            System.out.println("获取歌曲列表失败");
        }
    }

    @Test
    public void testQueryByTitle() {
        String title = "Girl, so confusing";
        ArrayList<Song> songs = SongDao.queryByTitle(title);
        if (songs != null) {
            for (Song song : songs) {
                System.out.println(song);
            }
        } else {
            System.out.println("未找到标题包含 " + title + " 的歌曲");
        }
    }

    @Test
    public void testQueryById() {
        int songId = 1;
        Song song = SongDao.queryById(songId);
        if (song != null) {
            System.out.println(song);
        } else {
            System.out.println("未找到ID为 " + songId + " 的歌曲");
        }
    }

    @Test
    public void testQueryByGenre() {
        String genre = "Pop";
        List<Song> songs = SongDao.queryByGenre(genre);
        if (songs != null) {
            for (Song song : songs) {
                System.out.println(song);
            }
        } else {
            System.out.println("搜索流派失败");
        }
    }

    @Test
    public void testInsert() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date(System.currentTimeMillis());
            
            Song song = new Song();
            song.setTitle("360");
            song.setGenre("Pop");
            song.setArtistId(1);
            song.setCreatedAt(currentDate);
            song.setUpdatedAt(currentDate);
            
            int result = SongDao.insert(song);
            if (result > 0) {
                System.out.println("添加歌曲成功");
            } else {
                System.out.println("添加歌曲失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testUpdate() {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
            Date currentDate = new Date(System.currentTimeMillis());
            
            Song song = new Song();
            song.setSongId(4);
            song.setTitle("360 featuring robyn & yung lean");
            song.setGenre("Pop");
            song.setArtistId(1);
            song.setUpdatedAt(currentDate);
            
            int result = SongDao.update(song);
            if (result > 0) {
                System.out.println("更新歌曲成功");
            } else {
                System.out.println("更新歌曲失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testDelete() {
        int songId = 4;
        int result = SongDao.delete(songId);
        if (result > 0) {
            System.out.println("删除歌曲成功");
        } else {
            System.out.println("删除歌曲失败");
        }
    }
} 