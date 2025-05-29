package com.music.window;

import com.music.dao.UserDao;
import com.music.pojo.User;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class SystemSettingsView extends BorderPane {
    private final User currentUser;
    private final UserDao userDao;
    private final VBox menuBox;
    private final StackPane contentPane;
    private Scene parentScene;

    public SystemSettingsView(User user, Scene parentScene) {
        this.currentUser = user;
        this.userDao = new UserDao();
        this.parentScene = parentScene;
        this.menuBox = new VBox(10);
        this.menuBox.setPadding(new Insets(20));
        this.menuBox.setStyle("-fx-background-color: #f5f5f5;");
        this.contentPane = new StackPane();
        setLeft(menuBox);
        setCenter(contentPane);
        buildMenu();
        showPersonalInfo();
    }

    private void buildMenu() {
        Button personalInfoBtn = new Button("个人信息");
        Button themeBtn = new Button("主题设置");
        Button aboutBtn = new Button("关于");
        personalInfoBtn.setMaxWidth(Double.MAX_VALUE);
        themeBtn.setMaxWidth(Double.MAX_VALUE);
        aboutBtn.setMaxWidth(Double.MAX_VALUE);
        menuBox.getChildren().addAll(personalInfoBtn, themeBtn, aboutBtn);
        personalInfoBtn.setOnAction(e -> showPersonalInfo());
        themeBtn.setOnAction(e -> showThemeSettings());
        aboutBtn.setOnAction(e -> showAbout());
    }

    private void showPersonalInfo() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: white;");
        Label title = new Label("修改个人信息");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        TextField usernameField = new TextField(currentUser.getUsername());
        usernameField.setPromptText("用户名");
        TextField emailField = new TextField(currentUser.getEmail());
        emailField.setPromptText("邮箱");
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("新密码（留空则不修改）");
        Button saveBtn = new Button("保存");
        saveBtn.setOnAction(e -> {
            currentUser.setUsername(usernameField.getText());
            currentUser.setEmail(emailField.getText());
            if (!passwordField.getText().isEmpty()) {
                currentUser.setPassword(passwordField.getText());
            }
            boolean result = userDao.update(currentUser);
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "成功", "个人信息已更新");
            } else {
                showAlert(Alert.AlertType.ERROR, "失败", "更新失败，请重试");
            }
        });
        box.getChildren().addAll(title, new Label("用户名:"), usernameField, new Label("邮箱:"), emailField, new Label("密码:"), passwordField, saveBtn);
        contentPane.getChildren().setAll(box);
    }

    private void showThemeSettings() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: white;");
        Label title = new Label("主题设置");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        ToggleGroup themeGroup = new ToggleGroup();
        RadioButton lightBtn = new RadioButton("浅色主题");
        RadioButton darkBtn = new RadioButton("深色主题");
        lightBtn.setToggleGroup(themeGroup);
        darkBtn.setToggleGroup(themeGroup);
        // 默认根据当前scene判断
        if (parentScene.getStylesheets().stream().anyMatch(s -> s.contains("dark"))) {
            darkBtn.setSelected(true);
        } else {
            lightBtn.setSelected(true);
        }
        themeGroup.selectedToggleProperty().addListener((obs, old, sel) -> {
            if (sel == lightBtn) {
                parentScene.getStylesheets().removeIf(s -> s.contains("dark"));
            } else if (sel == darkBtn) {
                String darkThemePath = getClass().getResource("/dark-theme.css").toExternalForm();
                if (!parentScene.getStylesheets().contains(darkThemePath)) {
                    parentScene.getStylesheets().add(darkThemePath);
                }
            }
        });
        box.getChildren().addAll(title, lightBtn, darkBtn);
        contentPane.getChildren().setAll(box);
    }

    private void showAbout() {
        VBox box = new VBox(15);
        box.setPadding(new Insets(30));
        box.setStyle("-fx-background-color: white;");
        Label title = new Label("关于");
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        Label version = new Label("版本：1.0.0");
        Label dev = new Label("开发者：wyatt");
        box.getChildren().addAll(title, version, dev);
        contentPane.getChildren().setAll(box);
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 