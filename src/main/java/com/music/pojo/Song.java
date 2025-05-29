package com.music.pojo;

import java.sql.Date;

/**
 * 歌曲实体类
 */
public class Song {
    private int songId;
    private String title;
    private String genre;
    private int artistId;
    private Date createdAt;
    private Date updatedAt;

    public Song() {}

    public Song(String title, String genre, int artistId) {
        this.title = title;
        this.genre = genre;
        this.artistId = artistId;
    }

    public Song(int songId, String title, String genre, int artistId, Date createdAt, Date updatedAt) {
        this.songId = songId;
        this.title = title;
        this.genre = genre;
        this.artistId = artistId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    // Getters and Setters
    public int getSongId() {
        return songId;
    }

    public void setSongId(int songId) {
        this.songId = songId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getArtistId() {
        return artistId;
    }

    public void setArtistId(int artistId) {
        this.artistId = artistId;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public String toString() {
        return "Song{" +
                "songId=" + songId +
                ", title='" + title + '\'' +
                ", genre='" + genre + '\'' +
                ", artistId=" + artistId +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
} 