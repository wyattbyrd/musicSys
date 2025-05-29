-- 创建数据库
CREATE DATABASE IF NOT EXISTS music_sys;
USE music_sys;

-- 创建歌曲表
CREATE TABLE IF NOT EXISTS songs (
    song_id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100) NOT NULL,
    genre VARCHAR(50),
    artist_id INT,
    created_at DATETIME,
    updated_at DATETIME
);

-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    created_at DATETIME,
    updated_at DATETIME
);

-- 修改用户表添加权限字段
ALTER TABLE users ADD COLUMN permissions VARCHAR(255) NOT NULL DEFAULT 'view' AFTER email;

-- 更新现有用户的权限
UPDATE users SET permissions = 'admin' WHERE role = 'admin';

-- 插入新的测试用户
INSERT INTO users (username, password, role, email, permissions, created_at, updated_at)
VALUES 
('editor', 'editor123', 'editor', 'editor@example.com', 'edit', NOW(), NOW()),
('viewer', 'viewer123', 'viewer', 'viewer@example.com', 'view', NOW(), NOW());

-- 插入一些测试歌曲数据
INSERT INTO songs (title, genre, artist_id, created_at, updated_at)
VALUES 
('Girl, so confusing', 'Pop', 1, NOW(), NOW()),
('Shape of You', 'Pop', 2, NOW(), NOW()),
('Bohemian Rhapsody', 'Rock', 3, NOW(), NOW());

-- 首先添加一些艺术家ID的说明（这些ID在songs表中会被引用）
-- 1: Charlie XCX
-- 2: Taylor Swift
-- 3: Billie Eilish
-- 4: Dua Lipa
-- 5: The Weeknd

-- Charlie XCX的Brat专辑歌曲
INSERT INTO songs (title, genre, artist_id, created_at, updated_at) VALUES
('360', 'Pop', 1, NOW(), NOW()),
('Club classics', 'Pop', 1, NOW(), NOW()),
('Sympathy is a knife', 'Pop', 1, NOW(), NOW()),
('I might say something stupid', 'Pop', 1, NOW(), NOW()),
('Talk talk', 'Pop', 1, NOW(), NOW()),
('Von dutch', 'Pop', 1, NOW(), NOW()),
('Everything is romantic', 'Pop', 1, NOW(), NOW()),
('Rewind', 'Pop', 1, NOW(), NOW()),
('So I', 'Pop', 1, NOW(), NOW()),
('Girl, so confusing', 'Pop', 1, NOW(), NOW()),
('Apple', 'Pop', 1, NOW(), NOW()),
('B2b', 'Pop', 1, NOW(), NOW()),
('Mean girls', 'Pop', 1, NOW(), NOW()),
('I think about it all the time', 'Pop', 1, NOW(), NOW()),
('365', 'Pop', 1, NOW(), NOW());

-- Taylor Swift的歌曲
INSERT INTO songs (title, genre, artist_id, created_at, updated_at) VALUES
('Anti-Hero', 'Pop', 2, NOW(), NOW()),
('Cruel Summer', 'Pop', 2, NOW(), NOW()),
('Lavender Haze', 'Pop', 2, NOW(), NOW()),
('Karma', 'Pop', 2, NOW(), NOW()),
('Bejeweled', 'Pop', 2, NOW(), NOW());

-- Billie Eilish的歌曲
INSERT INTO songs (title, genre, artist_id, created_at, updated_at) VALUES
('bad guy', 'Pop', 3, NOW(), NOW()),
('when the party''s over', 'Pop', 3, NOW(), NOW()),
('everything i wanted', 'Pop', 3, NOW(), NOW()),
('Therefore I Am', 'Pop', 3, NOW(), NOW()),
('my future', 'Pop', 3, NOW(), NOW());

-- Dua Lipa的歌曲
INSERT INTO songs (title, genre, artist_id, created_at, updated_at) VALUES
('Levitating', 'Pop', 4, NOW(), NOW()),
('Don''t Start Now', 'Pop', 4, NOW(), NOW()),
('Physical', 'Pop', 4, NOW(), NOW()),
('Break My Heart', 'Pop', 4, NOW(), NOW()),
('New Rules', 'Pop', 4, NOW(), NOW());

-- The Weeknd的歌曲
INSERT INTO songs (title, genre, artist_id, created_at, updated_at) VALUES
('Blinding Lights', 'Pop', 5, NOW(), NOW()),
('Starboy', 'Pop', 5, NOW(), NOW()),
('Save Your Tears', 'Pop', 5, NOW(), NOW()),
('The Hills', 'Pop', 5, NOW(), NOW()),
('Die For You', 'Pop', 5, NOW(), NOW());

-- 继续添加艺术家ID说明
-- 6: Beyoncé
-- 7: Kanye West
-- 8: Kendrick Lamar

-- Beyoncé的歌曲
INSERT INTO songs (title, genre, artist_id, created_at, updated_at) VALUES
('Break My Soul', 'R&B', 6, NOW(), NOW()),
('Cuff It', 'R&B', 6, NOW(), NOW()),
('Formation', 'R&B', 6, NOW(), NOW()),
('Crazy in Love', 'R&B', 6, NOW(), NOW()),
('Halo', 'R&B', 6, NOW(), NOW()),
('Single Ladies', 'R&B', 6, NOW(), NOW()),
('Drunk in Love', 'R&B', 6, NOW(), NOW()),
('Run the World (Girls)', 'R&B', 6, NOW(), NOW()),
('Love on Top', 'R&B', 6, NOW(), NOW()),
('Irreplaceable', 'R&B', 6, NOW(), NOW());

-- Kanye West的歌曲
INSERT INTO songs (title, genre, artist_id, created_at, updated_at) VALUES
('Stronger', 'Hip Hop', 7, NOW(), NOW()),
('Gold Digger', 'Hip Hop', 7, NOW(), NOW()),
('Heartless', 'Hip Hop', 7, NOW(), NOW()),
('Runaway', 'Hip Hop', 7, NOW(), NOW()),
('Power', 'Hip Hop', 7, NOW(), NOW()),
('All of the Lights', 'Hip Hop', 7, NOW(), NOW()),
('Jesus Walks', 'Hip Hop', 7, NOW(), NOW()),
('Flashing Lights', 'Hip Hop', 7, NOW(), NOW()),
('Can''t Tell Me Nothing', 'Hip Hop', 7, NOW(), NOW()),
('Bound 2', 'Hip Hop', 7, NOW(), NOW());

-- Kendrick Lamar的歌曲
INSERT INTO songs (title, genre, artist_id, created_at, updated_at) VALUES
('HUMBLE.', 'Hip Hop', 8, NOW(), NOW()),
('DNA.', 'Hip Hop', 8, NOW(), NOW()),
('Alright', 'Hip Hop', 8, NOW(), NOW()),
('Swimming Pools (Drank)', 'Hip Hop', 8, NOW(), NOW()),
('King Kunta', 'Hip Hop', 8, NOW(), NOW()),
('Money Trees', 'Hip Hop', 8, NOW(), NOW()),
('Bitch, Don''t Kill My Vibe', 'Hip Hop', 8, NOW(), NOW()),
('LOVE.', 'Hip Hop', 8, NOW(), NOW()),
('Poetic Justice', 'Hip Hop', 8, NOW(), NOW()),
('m.A.A.d city', 'Hip Hop', 8, NOW(), NOW());

-- 创建播放列表表
CREATE TABLE IF NOT EXISTS playlists (
    playlist_id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL,
    description TEXT,
    user_id INT NOT NULL,
    created_at DATETIME,
    updated_at DATETIME,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

-- 创建播放列表歌曲关联表
CREATE TABLE IF NOT EXISTS playlist_songs (
    playlist_id INT NOT NULL,
    song_id INT NOT NULL,
    added_at DATETIME,
    PRIMARY KEY (playlist_id, song_id),
    FOREIGN KEY (playlist_id) REFERENCES playlists(playlist_id),
    FOREIGN KEY (song_id) REFERENCES songs(song_id)
);

-- 插入一些测试播放列表数据
INSERT INTO playlists (name, description, user_id, created_at, updated_at)
VALUES 
('我的最爱', '我最喜欢的歌曲集合', 1, NOW(), NOW()),
('工作音乐', '工作时听的音乐', 1, NOW(), NOW()),
('运动歌单', '运动时听的音乐', 1, NOW(), NOW());

-- 为播放列表添加一些歌曲
INSERT INTO playlist_songs (playlist_id, song_id, added_at)
VALUES 
(1, 1, NOW()), -- 我的最爱 - Girl, so confusing
(1, 21, NOW()), -- 我的最爱 - Anti-Hero
(1, 26, NOW()), -- 我的最爱 - bad guy
(2, 6, NOW()), -- 工作音乐 - 360
(2, 31, NOW()), -- 工作音乐 - Levitating
(3, 36, NOW()), -- 运动歌单 - Blinding Lights
(3, 51, NOW()); -- 运动歌单 - Stronger 