package com.music.window;

import com.music.dao.SongDao;
import com.music.pojo.Song;
import com.music.pojo.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.*;

public class SongManagementView extends VBox {
    private User currentUser;
    private TableView<Song> songTable;
    private TextField searchTitleField;
    private ComboBox<String> genreComboBox;
    private ComboBox<String> artistComboBox;
    private TextField titleField;
    private TextField genreField;
    private TextField artistIdField;
    private Button addButton;
    private Button modifyButton;
    private Button deleteButton;
    private Button clearButton;

    public SongManagementView(User user) {
        this.currentUser = user;
        initialize();
    }

    private void initialize() {
        setSpacing(10);
        setPadding(new Insets(10));

        // 创建主布局
        BorderPane mainLayout = new BorderPane();
        
        // 创建搜索面板
        HBox searchPanel = createSearchPanel();
        mainLayout.setTop(searchPanel);
        
        // 创建编辑面板
        VBox editPanel = createEditPanel();
        mainLayout.setLeft(editPanel);
        
        // 创建表格
        songTable = createSongTable();
        mainLayout.setCenter(songTable);
        
        getChildren().add(mainLayout);
        refreshSongTable();
        
        // 设置权限
        setupPermissions();
    }

    private void setupPermissions() {
        if (currentUser != null) {
            String permissions = currentUser.getPermissions();
            if ("view".equals(permissions)) {
                // 禁用所有编辑功能
                titleField.setEditable(false);
                genreField.setEditable(false);
                artistIdField.setEditable(false);
                
                // 禁用所有按钮
                addButton.setDisable(true);
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
                clearButton.setDisable(true);
            } else if ("edit".equals(permissions)) {
                // 只允许添加，不允许修改和删除
                modifyButton.setDisable(true);
                deleteButton.setDisable(true);
            }
        }
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
        addButton = new Button("添加");
        modifyButton = new Button("修改");
        deleteButton = new Button("删除");
        clearButton = new Button("清空");
        
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

    private void handleClearSearch() {
        searchTitleField.clear();
        genreComboBox.setValue("全部");
        artistComboBox.setValue("全部");
        refreshSongTable();
    }

    private void handleClearEdit() {
        titleField.clear();
        genreField.clear();
        artistIdField.clear();
        songTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 