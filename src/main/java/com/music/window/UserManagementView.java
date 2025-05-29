package com.music.window;

import com.music.dao.UserDao;
import com.music.pojo.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import java.util.List;
import java.util.Optional;

public class UserManagementView extends VBox {
    private UserDao userDao;
    private User currentUser;
    private TableView<User> userTable;
    private TextField usernameField;
    private PasswordField passwordField;
    private ComboBox<String> roleComboBox;
    private ComboBox<String> permissionsComboBox;

    public UserManagementView(User currentUser) {
        this.currentUser = currentUser;
        this.userDao = new UserDao();
        initialize();
    }

    private void initialize() {
        setSpacing(10);
        setPadding(new Insets(10));

        // 创建搜索面板
        HBox searchPanel = createSearchPanel();
        
        // 创建用户表格
        userTable = createUserTable();
        
        // 创建编辑面板
        VBox editPanel = createEditPanel();
        
        // 创建按钮面板
        HBox buttonPanel = createButtonPanel();
        
        // 添加所有组件
        getChildren().addAll(searchPanel, userTable, editPanel, buttonPanel);
        
        // 加载用户数据
        refreshUserTable();
    }

    private HBox createSearchPanel() {
        HBox searchPanel = new HBox(10);
        searchPanel.setPadding(new Insets(10));
        searchPanel.setStyle("-fx-background-color: #f5f5f5;");
        
        TextField searchField = new TextField();
        searchField.setPromptText("搜索用户名");
        
        ComboBox<String> roleFilter = new ComboBox<>(FXCollections.observableArrayList(
            "全部", "admin", "user"
        ));
        roleFilter.setValue("全部");
        
        Button searchButton = new Button("搜索");
        searchButton.setOnAction(e -> handleSearch(searchField.getText(), roleFilter.getValue()));
        
        Button clearButton = new Button("清空");
        clearButton.setOnAction(e -> {
            searchField.clear();
            roleFilter.setValue("全部");
            refreshUserTable();
        });
        
        searchPanel.getChildren().addAll(
            new Label("用户名:"), searchField,
            new Label("角色:"), roleFilter,
            searchButton, clearButton
        );
        
        return searchPanel;
    }

    private TableView<User> createUserTable() {
        TableView<User> table = new TableView<>();
        
        TableColumn<User, Integer> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getUserId()).asObject());
        
        TableColumn<User, String> usernameColumn = new TableColumn<>("用户名");
        usernameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUsername()));
        
        TableColumn<User, String> roleColumn = new TableColumn<>("角色");
        roleColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRole()));
        
        TableColumn<User, String> permissionsColumn = new TableColumn<>("权限");
        permissionsColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getPermissions()));
        
        table.getColumns().addAll(idColumn, usernameColumn, roleColumn, permissionsColumn);
        
        table.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                usernameField.setText(newSelection.getUsername());
                passwordField.clear();
                roleComboBox.setValue(newSelection.getRole());
                permissionsComboBox.setValue(newSelection.getPermissions());
            }
        });
        
        return table;
    }

    private VBox createEditPanel() {
        VBox editPanel = new VBox(10);
        editPanel.setPadding(new Insets(10));
        editPanel.setStyle("-fx-background-color: #f5f5f5;");
        
        usernameField = new TextField();
        usernameField.setPromptText("用户名");
        
        passwordField = new PasswordField();
        passwordField.setPromptText("密码");
        
        roleComboBox = new ComboBox<>(FXCollections.observableArrayList("admin", "user"));
        roleComboBox.setValue("user");
        
        permissionsComboBox = new ComboBox<>(FXCollections.observableArrayList(
            "viewer", "editor", "admin"
        ));
        permissionsComboBox.setValue("viewer");
        
        editPanel.getChildren().addAll(
            new Label("用户编辑"),
            usernameField,
            passwordField,
            new Label("角色:"),
            roleComboBox,
            new Label("权限:"),
            permissionsComboBox
        );
        
        return editPanel;
    }

    private HBox createButtonPanel() {
        HBox buttonPanel = new HBox(10);
        buttonPanel.setPadding(new Insets(10));
        
        Button addButton = new Button("添加");
        Button modifyButton = new Button("修改");
        Button deleteButton = new Button("删除");
        Button clearButton = new Button("清空");
        
        addButton.setOnAction(e -> handleAdd());
        modifyButton.setOnAction(e -> handleModify());
        deleteButton.setOnAction(e -> handleDelete());
        clearButton.setOnAction(e -> handleClear());
        
        buttonPanel.getChildren().addAll(addButton, modifyButton, deleteButton, clearButton);
        
        return buttonPanel;
    }

    private void handleSearch(String username, String role) {
        List<User> users = userDao.getAllUsers();
        ObservableList<User> filteredUsers = FXCollections.observableArrayList();
        
        for (User user : users) {
            boolean matchesUsername = username.isEmpty() || 
                user.getUsername().toLowerCase().contains(username.toLowerCase());
            boolean matchesRole = "全部".equals(role) || user.getRole().equals(role);
            
            if (matchesUsername && matchesRole) {
                filteredUsers.add(user);
            }
        }
        
        userTable.setItems(filteredUsers);
    }

    private void refreshUserTable() {
        List<User> users = userDao.getAllUsers();
        userTable.setItems(FXCollections.observableArrayList(users));
    }

    private void handleAdd() {
        try {
            User user = new User();
            user.setUsername(usernameField.getText());
            user.setPassword(passwordField.getText());
            user.setRole(roleComboBox.getValue());
            user.setPermissions(permissionsComboBox.getValue());
            
            boolean result = userDao.insert(user);
            if (result) {
                showAlert(Alert.AlertType.INFORMATION, "成功", "添加用户成功！");
                refreshUserTable();
                handleClear();
            } else {
                showAlert(Alert.AlertType.ERROR, "错误", "添加用户失败！");
            }
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "错误", "添加用户时发生错误：" + e.getMessage());
        }
    }

    private void handleModify() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            try {
                selectedUser.setUsername(usernameField.getText());
                if (!passwordField.getText().isEmpty()) {
                    selectedUser.setPassword(passwordField.getText());
                }
                selectedUser.setRole(roleComboBox.getValue());
                selectedUser.setPermissions(permissionsComboBox.getValue());
                
                boolean result = userDao.update(selectedUser);
                if (result) {
                    showAlert(Alert.AlertType.INFORMATION, "成功", "修改用户成功！");
                    refreshUserTable();
                    handleClear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "错误", "修改用户失败！");
                }
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "错误", "修改用户时发生错误：" + e.getMessage());
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "警告", "请先选择要修改的用户！");
        }
    }

    private void handleDelete() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            if (selectedUser.getUserId() == currentUser.getUserId()) {
                showAlert(Alert.AlertType.WARNING, "警告", "不能删除当前登录的用户！");
                return;
            }
            
            Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
            confirmDialog.setTitle("确认删除");
            confirmDialog.setHeaderText("删除用户");
            confirmDialog.setContentText("确定要删除用户 \"" + selectedUser.getUsername() + "\" 吗？");
            
            Optional<ButtonType> result = confirmDialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean deleteResult = userDao.delete(selectedUser.getUserId());
                if (deleteResult) {
                    showAlert(Alert.AlertType.INFORMATION, "成功", "删除用户成功！");
                    refreshUserTable();
                    handleClear();
                } else {
                    showAlert(Alert.AlertType.ERROR, "错误", "删除用户失败！");
                }
            }
        } else {
            showAlert(Alert.AlertType.WARNING, "警告", "请先选择要删除的用户！");
        }
    }

    private void handleClear() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.setValue("user");
        permissionsComboBox.setValue("viewer");
        userTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 