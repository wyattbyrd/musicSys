package com.music.window;

import com.music.dao.SongDao;
import com.music.dao.UserDao;
import com.music.pojo.Song;
import com.music.pojo.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public class MainWindow {
    private User currentUser;
    private UserDao userDao;
    private Stage stage;
    private StackPane contentArea;
    private String username;
    private TableView<Song> songTable;
    private TextField searchTitleField;
    private ComboBox<String> genreComboBox;
    private ComboBox<String> artistComboBox;
    private TextField titleField;
    private TextField genreField;
    private TextField artistIdField;

    public MainWindow(User user) {
        this.username = user.getUsername();
        this.currentUser = user;
        this.userDao = new UserDao();
        this.stage = new Stage();
        initialize();
    }

    private void initialize() {
        stage.setTitle("音乐管理系统");
        stage.setWidth(800);
        stage.setHeight(600);

        // 创建主布局
        BorderPane mainLayout = new BorderPane();
        
        // 创建顶部导航栏
        HBox topBar = createTopBar();
        mainLayout.setTop(topBar);
        
        // 创建左侧菜单
        VBox sideMenu = createSideMenu();
        mainLayout.setLeft(sideMenu);
        
        // 创建主内容区域
        contentArea = new StackPane();
        contentArea.setStyle("-fx-background-color: white;");
        mainLayout.setCenter(contentArea);
        
        // 创建状态栏
        HBox statusBar = createStatusBar();
        mainLayout.setBottom(statusBar);
        
        // 设置场景
        Scene scene = new Scene(mainLayout);
        stage.setScene(scene);
    }

    private HBox createTopBar() {
        HBox topBar = new HBox();
        topBar.setStyle("-fx-background-color: #2c3e50; -fx-padding: 10px;");
        topBar.setAlignment(Pos.CENTER_LEFT);
        topBar.setSpacing(20);

        // 系统标题
        Label titleLabel = new Label("音乐管理系统");
        titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");

        // 用户信息
        Label userLabel = new Label("欢迎, " + username);
        userLabel.setStyle("-fx-text-fill: white;");

        // 退出按钮
        Button logoutButton = new Button("退出");
        logoutButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
        logoutButton.setOnAction(e -> handleLogout());

        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        topBar.getChildren().addAll(titleLabel, userLabel, logoutButton);
        
        return topBar;
    }

    private VBox createSideMenu() {
        VBox sideMenu = new VBox();
        sideMenu.setStyle("-fx-background-color: #34495e; -fx-padding: 10px;");
        sideMenu.setSpacing(10);
        sideMenu.setPrefWidth(200);

        // 创建菜单按钮
        Button[] menuButtons = {
            createMenuButton("歌曲管理", () -> showSongManagement()),
            createMenuButton("播放列表", () -> showPlaylist()),
            createMenuButton("用户管理", () -> showUserManagement()),
            createMenuButton("系统设置", () -> showSettings())
        };

        sideMenu.getChildren().addAll(menuButtons);
        return sideMenu;
    }

    private Button createMenuButton(String text, Runnable action) {
        Button button = new Button(text);
        button.setMaxWidth(Double.MAX_VALUE);
        button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER-LEFT; -fx-padding: 10px;");
        
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: #2c3e50; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER-LEFT; -fx-padding: 10px;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 14px; -fx-alignment: CENTER-LEFT; -fx-padding: 10px;"));
        
        button.setOnAction(e -> action.run());
        return button;
    }

    private void showSongManagement() {
        contentArea.getChildren().clear();
        SongManagementView songManagementView = new SongManagementView(currentUser);
        contentArea.getChildren().add(songManagementView);
    }

    private HBox createSearchPanel() {
        HBox searchPanel = new HBox(10);
        searchPanel.setPadding(new Insets(10));
        searchPanel.setStyle("-fx-background-color: #f5f5f5;");
        
        searchTitleField = new TextField();
        searchTitleField.setPromptText("歌曲名称");
        
        genreComboBox = new ComboBox<>(FXCollections.observableArrayList(
            "全部", "Pop", "Rock", "Classical", "Jazz", "Hip Hop", "R&B"
        ));
        genreComboBox.setValue("全部");
        
        artistComboBox = new ComboBox<>(FXCollections.observableArrayList(
            "全部", "Charlie XCX", "Taylor Swift", "Billie Eilish", "Dua Lipa", 
            "The Weeknd", "Beyoncé", "Kanye West", "Kendrick Lamar"
        ));
        artistComboBox.setValue("全部");
        
        Button searchButton = new Button("搜索");
        searchButton.setOnAction(e -> handleSearch());
        
        Button clearButton = new Button("清空");
        clearButton.setOnAction(e -> handleClearSearch());
        
        searchPanel.getChildren().addAll(
            new Label("歌曲名称:"), searchTitleField,
            new Label("流派:"), genreComboBox,
            new Label("歌手:"), artistComboBox,
            searchButton, clearButton
        );
        
        return searchPanel;
    }

    private VBox createEditPanel() {
        VBox editPanel = new VBox(10);
        editPanel.setPadding(new Insets(10));
        editPanel.setPrefWidth(250);
        editPanel.setStyle("-fx-background-color: #f5f5f5;");
        
        titleField = new TextField();
        titleField.setPromptText("歌曲名称");
        
        genreField = new TextField();
        genreField.setPromptText("流派");
        
        artistIdField = new TextField();
        artistIdField.setPromptText("艺术家ID");
        
        HBox buttonBox = new HBox(10);
        Button addButton = new Button("添加");
        Button modifyButton = new Button("修改");
        Button deleteButton = new Button("删除");
        Button clearButton = new Button("清空");
        
        addButton.setOnAction(e -> handleAdd());
        modifyButton.setOnAction(e -> handleModify());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> handleClearEdit());
        
        buttonBox.getChildren().addAll(addButton, modifyButton, deleteButton, clearButton);
        
        editPanel.getChildren().addAll(
            new Label("歌曲编辑"),
            titleField,
            genreField,
            artistIdField,
            buttonBox
        );
        
        return editPanel;
    }

    private TableView<Song> createSongTable() {
        TableView<Song> table = new TableView<>();
        
        TableColumn<Song, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getSongId()).asObject());
        
        TableColumn<Song, String> titleColumn = new TableColumn<>("歌曲名称");
        titleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTitle()));
        
        TableColumn<Song, String> genreColumn = new TableColumn<>("流派");
        genreColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getGenre()));
        
        TableColumn<Song, Integer> artistIdColumn = new TableColumn<>("艺术家ID");
        artistIdColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getArtistId()).asObject());
        
        TableColumn<Song, String> createdAtColumn = new TableColumn<>("创建时间");
        createdAtColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getCreatedAt().toString()));
        
        TableColumn<Song, String> updatedAtColumn = new TableColumn<>("更新时间");
        updatedAtColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUpdatedAt().toString()));
        
        table.getColumns().addAll(idColumn, titleColumn, genreColumn, artistIdColumn, createdAtColumn, updatedAtColumn);
        
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                titleField.setText(newSelection.getTitle());
                genreField.setText(newSelection.getGenre());
                artistIdField.setText(String.valueOf(newSelection.getArtistId()));
            }
        });
        
        return table;
    }

    private void refreshSongTable() {
        List<Song> songs = SongDao.all();
        songTable.setItems(FXCollections.observableArrayList(songs));
    }

    private void handleSearch() {
        String title = searchTitleField.getText();
        String genre = genreComboBox.getValue();
        String artist = artistComboBox.getValue();
        
        List<Song> songs = SongDao.all();
        List<Song> filteredSongs = new ArrayList<>();
        
        for (Song song : songs) {
            boolean matchesTitle = title.isEmpty() || song.getTitle().toLowerCase().contains(title.toLowerCase());
            boolean matchesGenre = "全部".equals(genre) || song.getGenre().equals(genre);
            boolean matchesArtist = "全部".equals(artist) || getArtistName(song.getArtistId()).equals(artist);
            
            if (matchesTitle && matchesGenre && matchesArtist) {
                filteredSongs.add(song);
            }
        }
        
        songTable.setItems(FXCollections.observableArrayList(filteredSongs));
    }

    private String getArtistName(int artistId) {
        // TODO: 从数据库获取艺术家名称
        // 这里暂时使用硬编码的映射
        Map<Integer, String> artistMap = new HashMap<>();
        artistMap.put(1, "Charlie XCX");
        artistMap.put(2, "Taylor Swift");
        artistMap.put(3, "Billie Eilish");
        artistMap.put(4, "Dua Lipa");
        artistMap.put(5, "The Weeknd");
        artistMap.put(6, "Beyoncé");
        artistMap.put(7, "Kanye West");
        artistMap.put(8, "Kendrick Lamar");
        
        return artistMap.getOrDefault(artistId, "未知艺术家");
    }

    private void handleClearSearch() {
        searchTitleField.clear();
        genreComboBox.setValue("全部");
        artistComboBox.setValue("全部");
        refreshSongTable();
    }

    private void handleAdd() {
        try {
            Song song = new Song();
            song.setTitle(titleField.getText());
            song.setGenre(genreField.getText());
            song.setArtistId(Integer.parseInt(artistIdField.getText()));
            
            int result = SongDao.insert(song);
            if (result > 0) {
                showAlert(Alert.AlertType.INFORMATION, "成功", "添加歌曲成功！");
                refreshSongTable();
                handleClearEdit();
            } else {
                showAlert(Alert.AlertType.ERROR, "错误", "添加歌曲失败！");
            }
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "错误", "艺术家ID必须是数字！");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "错误", "添加歌曲时发生错误：" + e.getMessage());
        }
    }

    private void handleModify() {
        Song selectedSong = songTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            try {
                selectedSong.setTitle(titleField.getText());
                selectedSong.setGenre(genreField.getText());
                selectedSong.setArtistId(Integer.parseInt(artistIdField.getText()));
                
                int result = SongDao.update(selectedSong);
                if (result > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "成功", "修改歌曲成功！");
                    refreshSongTable();
                    handleClearEdit();
                } else {
                    showAlert(Alert.AlertType.ERROR, "错误", "修改歌曲失败！");
                }
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "错误", "艺术家ID必须是数字！");
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "错误", "修改歌曲时发生错误：" + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "警告", "请先选择要修改的歌曲！");
        }
    }

    private void handleDelete() {
        Song selectedSong = songTable.getSelectionModel().getSelectedItem();
        if (selectedSong != null) {
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("确认删除");
            confirmDialog.setHeaderText("删除歌曲");
            confirmDialog.setContentText("确定要删除歌曲 \"" + selectedSong.getTitle() + "\" 吗？");
            
            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                int deleteResult = SongDao.delete(selectedSong.getSongId());
                if (deleteResult > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "成功", "删除歌曲成功！");
                    refreshSongTable();
                    handleClearEdit();
                } else {
                    showAlert(Alert.AlertType.ERROR, "错误", "删除歌曲失败！");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "警告", "请先选择要删除的歌曲！");
        }
    }

    private void handleClearEdit() {
        titleField.clear();
        genreField.clear();
        artistIdField.clear();
        songTable.getSelectionModel().clearSelection();
    }

    private void showPlaylist() {
        contentArea.getChildren().clear();
        PlaylistView playlistView = new PlaylistView(currentUser);
        contentArea.getChildren().add(playlistView);
    }

    private void showUserManagement() {
        contentArea.getChildren().clear();
        if ("admin".equals(currentUser.getRole())) {
            UserManagementView userManagementView = new UserManagementView(currentUser);
            contentArea.getChildren().add(userManagementView);
        } else {
            showAlert(Alert.AlertType.WARNING, "警告", "只有管理员可以访问用户管理功能！");
        }
    }

    private void showSettings() {
        contentArea.getChildren().clear();
        SystemSettingsView settingsView = new SystemSettingsView(currentUser, stage.getScene());
        contentArea.getChildren().add(settingsView);
    }

    private void handleLogout() {
        stage.close();
        // TODO: 可以在这里添加登出逻辑，比如清理用户会话等
    }

    private HBox createStatusBar() {
        HBox statusBar = new HBox(10);
        statusBar.setPadding(new Insets(5));
        statusBar.setStyle("-fx-background-color: #f0f0f0;");
        
        Label userLabel = new Label("当前用户: " + username);
        Label roleLabel = new Label("角色: " + currentUser.getRole());
        
        statusBar.getChildren().addAll(userLabel, roleLabel);
        return statusBar;
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void show() {
        stage.show();
    }

    public void close() {
        if (userDao != null) {
            userDao.close();
        }
        stage.close();
    }
} 