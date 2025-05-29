package com.music.window;

import com.music.dao.PlaylistDao;
import com.music.dao.SongDao;
import com.music.pojo.Playlist;
import com.music.pojo.Song;
import com.music.pojo.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;

public class PlaylistView extends VBox {
    private User currentUser;
    private PlaylistDao playlistDao;
    private SongDao songDao;
    private TableView<Playlist> playlistTable;
    private TableView<Song> songTable;
    private TextField nameField;
    private TextArea descriptionField;
    private Button addButton;
    private Button modifyButton;
    private Button deleteButton;
    private Button clearButton;
    private Button addSongButton;
    private Button removeSongButton;

    public PlaylistView(User user) {
        this.currentUser = user;
        this.playlistDao = new PlaylistDao(user);
        this.songDao = new SongDao();
        initialize();
        
        // 添加窗口关闭事件监听器
        this.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (oldScene != null) {
                oldScene.windowProperty().addListener((obs2, oldWindow, newWindow) -> {
                    if (oldWindow != null) {
                        playlistDao.close();
                    }
                });
            }
        });
    }

    private void initialize() {
        setSpacing(10);
        setPadding(new Insets(10));

        // 创建主布局
        BorderPane mainLayout = new BorderPane();
        
        // 创建左侧播放列表面板
        VBox playlistPanel = createPlaylistPanel();
        mainLayout.setLeft(playlistPanel);
        
        // 创建右侧歌曲面板
        VBox songPanel = createSongPanel();
        mainLayout.setCenter(songPanel);
        
        getChildren().add(mainLayout);
        refreshPlaylistTable();
        setupPermissions();
    }

    private VBox createPlaylistPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setPrefWidth(300);
        panel.setStyle("-fx-background-color: #f5f5f5;");
        
        // 创建播放列表表格
        playlistTable = new TableView<>();
        TableColumn<Playlist, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getPlaylistId()).asObject());
        
        TableColumn<Playlist, String> nameColumn = new TableColumn<>("名称");
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        
        TableColumn<Playlist, String> descriptionColumn = new TableColumn<>("描述");
        descriptionColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDescription()));
        
        playlistTable.getColumns().addAll(idColumn, nameColumn, descriptionColumn);
        
        // 创建编辑区域
        nameField = new TextField();
        nameField.setPromptText("播放列表名称");
        
        descriptionField = new TextArea();
        descriptionField.setPromptText("播放列表描述");
        descriptionField.setPrefRowCount(3);
        
        HBox buttonBox = new HBox(10);
        addButton = new Button("添加");
        modifyButton = new Button("修改");
        deleteButton = new Button("删除");
        clearButton = new Button("清空");
        
        addButton.setOnAction(e -> handleAdd());
        modifyButton.setOnAction(e -> handleModify());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> handleClear());
        
        buttonBox.getChildren().addAll(addButton, modifyButton, deleteButton, clearButton);
        
        panel.getChildren().addAll(
            new Label("播放列表"),
            playlistTable,
            new Label("编辑播放列表"),
            nameField,
            descriptionField,
            buttonBox
        );
        
        // 添加选择监听器
        playlistTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                nameField.setText(newSelection.getName());
                descriptionField.setText(newSelection.getDescription());
                refreshSongTable(newSelection.getPlaylistId());
            }
        });
        
        return panel;
    }

    private VBox createSongPanel() {
        VBox panel = new VBox(10);
        panel.setPadding(new Insets(10));
        panel.setStyle("-fx-background-color: #f5f5f5;");
        
        // 创建歌曲表格
        songTable = new TableView<>();
        TableColumn<Song, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSongId()).asObject());
        
        TableColumn<Song, String> titleColumn = new TableColumn<>("歌曲名称");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        
        TableColumn<Song, String> genreColumn = new TableColumn<>("流派");
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));
        
        TableColumn<Song, Integer> artistIdColumn = new TableColumn<>("艺术家ID");
        artistIdColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getArtistId()).asObject());
        
        songTable.getColumns().addAll(idColumn, titleColumn, genreColumn, artistIdColumn);
        
        // 创建按钮
        HBox buttonBox = new HBox(10);
        addSongButton = new Button("添加歌曲");
        removeSongButton = new Button("移除歌曲");
        
        addSongButton.setOnAction(e -> handleAddSong());
        removeSongButton.setOnAction(e -> handleRemoveSong());
        
        buttonBox.getChildren().addAll(addSongButton, removeSongButton);
        
        panel.getChildren().addAll(
            new Label("播放列表歌曲"),
            songTable,
            buttonBox
        );
        
        return panel;
    }

    private void setupPermissions() {
        // 所有用户都可以管理自己的播放列表
        // 不需要任何权限限制
    }

    private void refreshPlaylistTable() {
        List<Playlist> playlists = playlistDao.getByUserId(currentUser.getUserId());
        // 按照ID排序
        playlists.sort((p1, p2) -> Integer.compare(p1.getPlaylistId(), p2.getPlaylistId()));
        playlistTable.setItems(FXCollections.observableArrayList(playlists));
    }

    private void refreshSongTable(int playlistId) {
        List<Song> songs = playlistDao.getSongs(playlistId);
        songTable.setItems(FXCollections.observableArrayList(songs));
    }

    private void handleAdd() {
        Playlist playlist = new Playlist();
        playlist.setName(nameField.getText());
        playlist.setDescription(descriptionField.getText());
        playlist.setUserId(currentUser.getUserId());
        
        int result = playlistDao.insert(playlist);
        if (result > 0) {
            showAlert(Alert.AlertType.INFORMATION, "成功", "添加播放列表成功！");
            refreshPlaylistTable();
            handleClear();
        } else {
            showAlert(Alert.AlertType.ERROR, "错误", "添加播放列表失败！");
        }
    }

    private void handleModify() {
        Playlist selectedPlaylist = playlistTable.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            selectedPlaylist.setName(nameField.getText());
            selectedPlaylist.setDescription(descriptionField.getText());
            
            int result = playlistDao.update(selectedPlaylist);
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "成功", "修改播放列表成功！");
                refreshPlaylistTable();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "错误", "修改播放列表失败！");
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "警告", "请先选择要修改的播放列表！");
        }
    }

    private void handleDelete() {
        Playlist selectedPlaylist = playlistTable.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("确认删除");
            confirmDialog.setHeaderText("删除播放列表");
            confirmDialog.setContentText("确定要删除播放列表 \"" + selectedPlaylist.getName() + "\" 吗？");
            
            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    int result = playlistDao.delete(selectedPlaylist.getPlaylistId());
                    if (result > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "成功", "删除播放列表成功！");
                        refreshPlaylistTable();
                        handleClear();
                    } else {
                        showAlert(Alert.AlertType.ERROR, "错误", "删除播放列表失败！");
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "警告", "请先选择要删除的播放列表！");
        }
    }

    private void handleClear() {
        nameField.clear();
        descriptionField.clear();
        playlistTable.getSelectionModel().clearSelection();
        songTable.getItems().clear();
    }

    private void handleAddSong() {
        Playlist selectedPlaylist = playlistTable.getSelectionModel().getSelectedItem();
        if (selectedPlaylist != null) {
            // 创建歌曲选择对话框
            Dialog<Song> dialog = new Dialog<>();
            dialog.setTitle("添加歌曲");
            dialog.setHeaderText("选择要添加的歌曲");
            
            // 创建歌曲选择表格
            TableView<Song> songSelectionTable = new TableView<>();
            TableColumn<Song, Integer> idColumn = new TableColumn<>("ID");
            idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSongId()).asObject());
            
            TableColumn<Song, String> titleColumn = new TableColumn<>("歌曲名称");
            titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
            
            TableColumn<Song, String> genreColumn = new TableColumn<>("流派");
            genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));
            
            songSelectionTable.getColumns().addAll(idColumn, titleColumn, genreColumn);
            songSelectionTable.setItems(FXCollections.observableArrayList(songDao.all()));
            
            dialog.getDialogPane().setContent(songSelectionTable);
            
            ButtonType addButtonType = new ButtonType("添加", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);
            
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == addButtonType) {
                    return songSelectionTable.getSelectionModel().getSelectedItem();
                }
                return null;
            });
            
            dialog.showAndWait().ifPresent(song -> {
                if (song != null) {
                    boolean result = playlistDao.addSong(selectedPlaylist.getPlaylistId(), song.getSongId());
                    if (result) {
                        showAlert(Alert.AlertType.INFORMATION, "成功", "添加歌曲成功！");
                        refreshSongTable(selectedPlaylist.getPlaylistId());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "错误", "添加歌曲失败！");
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "警告", "请先选择播放列表！");
        }
    }

    private void handleRemoveSong() {
        Playlist selectedPlaylist = playlistTable.getSelectionModel().getSelectedItem();
        Song selectedSong = songTable.getSelectionModel().getSelectedItem();
        
        if (selectedPlaylist != null && selectedSong != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("确认移除");
            confirmDialog.setHeaderText("移除歌曲");
            confirmDialog.setContentText("确定要从播放列表 \"" + selectedPlaylist.getName() + "\" 中移除歌曲 \"" + selectedSong.getTitle() + "\" 吗？");
            
            confirmDialog.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    boolean result = playlistDao.removeSong(selectedPlaylist.getPlaylistId(), selectedSong.getSongId());
                    if (result) {
                        showAlert(Alert.AlertType.INFORMATION, "成功", "移除歌曲成功！");
                        refreshSongTable(selectedPlaylist.getPlaylistId());
                    } else {
                        showAlert(Alert.AlertType.ERROR, "错误", "移除歌曲失败！");
                    }
                }
            });
        } else {
            showAlert(Alert.AlertType.WARNING, "警告", "请先选择播放列表和歌曲！");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 