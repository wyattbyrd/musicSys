package com.music.dao;

import com.music.pojo.Song;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌曲数据访问对象类
 */
public class SongDao {
    /**
     * 获取所有歌曲
     */
    public static ArrayList<Song> all() {
        ArrayList<Song> songs = new ArrayList<>();
        JDBC jdbc = new JDBC();
        try {
            jdbc.startConnection();
            String sql = "SELECT * FROM songs";
            ResultSet rs = jdbc.query(sql);
            while (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setGenre(rs.getString("genre"));
                song.setArtistId(rs.getInt("artist_id"));
                song.setCreatedAt(rs.getDate("created_at"));
                song.setUpdatedAt(rs.getDate("updated_at"));
                songs.add(song);
            }
            rs.close();
            jdbc.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return songs;
    }

    /**
     * 根据标题搜索歌曲（模糊匹配）
     */
    public static ArrayList<Song> queryByTitle(String title) {
        ArrayList<Song> songs = new ArrayList<>();
        JDBC jdbc = new JDBC();
        try {
            jdbc.startConnection();
            String sql = "SELECT * FROM songs WHERE title LIKE ?";
            Connection conn = jdbc.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + title + "%");
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setGenre(rs.getString("genre"));
                song.setArtistId(rs.getInt("artist_id"));
                song.setCreatedAt(rs.getDate("created_at"));
                song.setUpdatedAt(rs.getDate("updated_at"));
                songs.add(song);
            }
            rs.close();
            jdbc.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return songs;
    }

    /**
     * 根据ID查询歌曲
     */
    public static Song queryById(int songId) {
        Song song = null;
        JDBC jdbc = new JDBC();
        try {
            jdbc.startConnection();
            String sql = "SELECT * FROM songs WHERE song_id = ?";
            Connection conn = jdbc.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, songId);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setGenre(rs.getString("genre"));
                song.setArtistId(rs.getInt("artist_id"));
                song.setCreatedAt(rs.getDate("created_at"));
                song.setUpdatedAt(rs.getDate("updated_at"));
            }
            rs.close();
            jdbc.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return song;
    }

    /**
     * 根据流派查询歌曲
     */
    public static List<Song> queryByGenre(String genre) {
        List<Song> songs = new ArrayList<>();
        JDBC jdbc = new JDBC();
        try {
            jdbc.startConnection();
            String sql = "SELECT * FROM songs WHERE genre = ?";
            Connection conn = jdbc.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, genre);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setGenre(rs.getString("genre"));
                song.setArtistId(rs.getInt("artist_id"));
                song.setCreatedAt(rs.getDate("created_at"));
                song.setUpdatedAt(rs.getDate("updated_at"));
                songs.add(song);
            }
            rs.close();
            jdbc.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return songs;
    }

    /**
     * 根据艺术家ID查询歌曲
     */
    public static List<Song> queryByArtistId(int artistId) {
        List<Song> songs = new ArrayList<>();
        JDBC jdbc = new JDBC();
        try {
            jdbc.startConnection();
            String sql = "SELECT * FROM songs WHERE artist_id = ?";
            Connection conn = jdbc.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, artistId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Song song = new Song();
                song.setSongId(rs.getInt("song_id"));
                song.setTitle(rs.getString("title"));
                song.setGenre(rs.getString("genre"));
                song.setArtistId(rs.getInt("artist_id"));
                song.setCreatedAt(rs.getDate("created_at"));
                song.setUpdatedAt(rs.getDate("updated_at"));
                songs.add(song);
            }
            rs.close();
            jdbc.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return songs;
    }

    /**
     * 添加新歌曲
     */
    public static int insert(Song song) {
        JDBC jdbc = new JDBC();
        int result = 0;
        try {
            jdbc.startConnection();
            String sql = "INSERT INTO songs (title, genre, artist_id, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
            Connection conn = jdbc.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, song.getTitle());
            pstmt.setString(2, song.getGenre());
            pstmt.setInt(3, song.getArtistId());
            result = pstmt.executeUpdate();
            jdbc.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return result;
    }

    /**
     * 更新歌曲信息
     */
    public static int update(Song song) {
        JDBC jdbc = new JDBC();
        int result = 0;
        try {
            jdbc.startConnection();
            String sql = "UPDATE songs SET title = ?, genre = ?, artist_id = ?, updated_at = NOW() WHERE song_id = ?";
            Connection conn = jdbc.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, song.getTitle());
            pstmt.setString(2, song.getGenre());
            pstmt.setInt(3, song.getArtistId());
            pstmt.setInt(4, song.getSongId());
            result = pstmt.executeUpdate();
            jdbc.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return result;
    }

    /**
     * 删除歌曲
     */
    public static int delete(int songId) {
        JDBC jdbc = new JDBC();
        int result = 0;
        try {
            jdbc.startConnection();
            String sql = "DELETE FROM songs WHERE song_id = ?";
            Connection conn = jdbc.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, songId);
            result = pstmt.executeUpdate();
            jdbc.stopConnection();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
        return result;
    }
} 