package com.music.window;

import com.music.dao.UserDao;
import com.music.pojo.User;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class LoginDialog extends Application {
    private UserDao userDao;
    private User loggedInUser;
    private Stage loginStage;

    @Override
    public void start(Stage primaryStage) {
        this.userDao = new UserDao();
        this.loginStage = new Stage();
        initialize();
    }

    private void initialize() {
        loginStage.setTitle("登录");
        loginStage.initModality(Modality.APPLICATION_MODAL);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text scenetitle = new Text("欢迎登录");
        scenetitle.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
        grid.add(scenetitle, 0, 0, 2, 1);

        Label userName = new Label("用户名:");
        grid.add(userName, 0, 1);

        TextField userTextField = new TextField();
        grid.add(userTextField, 1, 1);

        Label pw = new Label("密码:");
        grid.add(pw, 0, 2);

        PasswordField pwBox = new PasswordField();
        grid.add(pwBox, 1, 2);

        Button btn = new Button("登录");
        HBox hbBtn = new HBox(10);
        hbBtn.setAlignment(Pos.BOTTOM_RIGHT);
        hbBtn.getChildren().add(btn);
        grid.add(hbBtn, 1, 4);

        Text actiontarget = new Text();
        grid.add(actiontarget, 1, 6);

        btn.setOnAction(e -> {
            String username = userTextField.getText();
            String password = pwBox.getText();

            if (username.isEmpty() || password.isEmpty()) {
                actiontarget.setText("请输入用户名和密码");
                return;
            }

            loggedInUser = userDao.login(username, password);
            if (loggedInUser != null) {
                // 登录成功，打开主窗口
                MainWindow mainWindow = new MainWindow(loggedInUser);
                mainWindow.show();
                loginStage.close();
            } else {
                actiontarget.setText("用户名或密码错误");
            }
        });

        Scene scene = new Scene(grid, 300, 200);
        loginStage.setScene(scene);
        loginStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        if (userDao != null) {
            userDao.close();
        }
    }
} 