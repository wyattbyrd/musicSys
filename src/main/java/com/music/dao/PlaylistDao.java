package com.music.dao;

import com.music.pojo.Playlist;
import com.music.pojo.Song;
import com.music.pojo.User;
import com.music.dao.JDBC;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlaylistDao {
    private JDBC jdbc;
    private Connection conn;
    private User currentUser;
    
    public PlaylistDao(User user) {
        this.currentUser = user;
        this.jdbc = new JDBC();
        try {
            this.conn = jdbc.getConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    // 创建播放列表
    public int insert(Playlist playlist) {
        String sql = "INSERT INTO playlists (name, description, user_id, created_at, updated_at) VALUES (?, ?, ?, NOW(), NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, playlist.getName());
            stmt.setString(2, playlist.getDescription());
            stmt.setInt(3, playlist.getUserId());
            
            int result = stmt.executeUpdate();
            if (result > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }
    
    // 更新播放列表
    public int update(Playlist playlist) {
        // 首先检查播放列表是否属于当前用户
        String checkSql = "SELECT user_id FROM playlists WHERE playlist_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, playlist.getPlaylistId());
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt("user_id") != playlist.getUserId()) {
                    return 0; // 如果播放列表不属于当前用户，返回0表示更新失败
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        String sql = "UPDATE playlists SET name = ?, description = ?, updated_at = NOW() WHERE playlist_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, playlist.getName());
            stmt.setString(2, playlist.getDescription());
            stmt.setInt(3, playlist.getPlaylistId());
            
            return stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // 删除播放列表
    public int delete(int playlistId) {
        // 首先检查播放列表是否属于当前用户
        String checkSql = "SELECT user_id FROM playlists WHERE playlist_id = ?";
        try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
            checkStmt.setInt(1, playlistId);
            try (ResultSet rs = checkStmt.executeQuery()) {
                if (rs.next() && rs.getInt("user_id") != currentUser.getUserId()) {
                    return 0; // 如果播放列表不属于当前用户，返回0表示删除失败
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }

        String sql = "DELETE FROM playlists WHERE playlist_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            return stmt.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    // 获取用户的所有播放列表
    public List<Playlist> getByUserId(int userId) {
        // 只返回当前用户的播放列表
        List<Playlist> playlists = new ArrayList<>();
        String sql = "SELECT * FROM playlists WHERE user_id = ? ORDER BY playlist_id";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, currentUser.getUserId());  // 使用当前用户的ID
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Playlist playlist = new Playlist();
                    playlist.setPlaylistId(rs.getInt("playlist_id"));
                    playlist.setName(rs.getString("name"));
                    playlist.setDescription(rs.getString("description"));
                    playlist.setUserId(rs.getInt("user_id"));
                    playlist.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                    playlist.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                    playlists.add(playlist);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return playlists;
    }
    
    // 获取播放列表中的所有歌曲
    public List<Song> getSongs(int playlistId) {
        List<Song> songs = new ArrayList<>();
        String sql = "SELECT s.* FROM songs s " +
                    "JOIN playlist_songs ps ON s.song_id = ps.song_id " +
                    "WHERE ps.playlist_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            try (ResultSet rs = stmt.executeQuery()) {
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
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return songs;
    }
    
    // 添加歌曲到播放列表
    public boolean addSong(int playlistId, int songId) {
        String sql = "INSERT INTO playlist_songs (playlist_id, song_id, added_at) VALUES (?, ?, NOW())";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // 从播放列表移除歌曲
    public boolean removeSong(int playlistId, int songId) {
        String sql = "DELETE FROM playlist_songs WHERE playlist_id = ? AND song_id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, playlistId);
            stmt.setInt(2, songId);
            return stmt.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
    
    // 关闭数据库连接
    public void close() {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 