package cn.greatoo.easymill.login;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;

import cn.greatoo.easymill.db.util.ClampingHandler;
import cn.greatoo.easymill.db.util.Gripperhandler;
import cn.greatoo.easymill.db.util.Programhandler;
import cn.greatoo.easymill.db.util.Stackerhandler;
import cn.greatoo.easymill.db.util.UserFrameHander;
import cn.greatoo.easymill.ui.main.MainViewController;
import cn.greatoo.easymill.util.CommonUtil;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import cn.greatoo.easymill.util.SizeManager;

public class LoginController implements Initializable {

    private final static Logger LOGGER = LogManager.getLogger(LoginController.class.getName());

    @FXML
    private JFXTextField username;
    @FXML
    private JFXPasswordField password;

    Preferences preference;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        preference = Preferences.getPreferences();
        System.out.println("");
    }

    @FXML
    private void handleLoginButtonAction(ActionEvent event) {
        String uname = username.getText();
        String pword = DigestUtils.shaHex(password.getText());

        if (true/*uname.equals(preference.getUsername()) && pword.equals(preference.getPassword())*/) {
        	//初始化数据，加载数据库
			try {
				Programhandler.getProgram();
				Gripperhandler.getAllGripper();		
				ClampingHandler.getClampings();
				Stackerhandler.getStacker();
				UserFrameHander.getAllUserFrames();
			} catch (SQLException e) {
				e.printStackTrace();
			}
            closeStage();
            loadMain();
            LOGGER.log(Level.INFO, "User successfully logged in {}", uname);
        }
        else {
            username.getStyleClass().add("wrong-credentials");
            password.getStyleClass().add("wrong-credentials");
        }
    }

    @FXML
    private void handleCancelButtonAction(ActionEvent event) {
        System.exit(0);
    }

    private void closeStage() {
        ((Stage) username.getScene().getWindow()).close();
    }

    void loadMain() {
        try {
        	URL location = getClass().getResource("/cn/greatoo/easymill/ui/main/MainView.fxml");
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(location);
            fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
            Parent parent = fxmlLoader.load();
            Stage stage = new Stage(StageStyle.DECORATED);
            stage.setResizable(false);
            stage.setTitle("RoboSoft");
            int width = 800;
            int heigth = 600;
            stage.setScene(new Scene(parent,width,heigth));
            SizeManager.setApplicationSizes(width, heigth);
            MainViewController mainViewController = fxmlLoader.getController();   //获取Controller的实例对?
            //中写的初始化方法
            mainViewController.Init();
            stage.show();
            stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
                @Override
                public void handle(WindowEvent event) {
                    System.exit(0);
                }
            });
  
            CommonUtil.setStageIcon(stage);
           
        }
        catch (Exception ex) {
            LOGGER.log(Level.ERROR, "{}", ex);
        }
    }

}
