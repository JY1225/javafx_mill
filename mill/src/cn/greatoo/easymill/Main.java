package cn.greatoo.easymill;

import java.util.Locale;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.util.CommonUtil;
import cn.greatoo.easymill.util.ExceptionUtil;
import cn.greatoo.easymill.util.PropertyManager;
import cn.greatoo.easymill.util.PropertyManager.Setting;
import cn.greatoo.easymill.util.Translator;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/** 
* @author Di: 
* @version 
* 入口
*/ 
public class Main extends Application {
	private final static Logger LOGGER = LogManager.getLogger(Main.class.getName());

	@Override
	public void start(Stage primaryStage) {
		try {
			PropertyManager.readPropertyFile();
			Parent root = FXMLLoader.load(getClass().getResource("/cn/greatoo/easymill/login/login.fxml"));

			Scene scene = new Scene(root);
			primaryStage.setScene(scene);//
			primaryStage.show();
			primaryStage.setTitle("Login");
			primaryStage.setResizable(false);
			CommonUtil.setStageIcon(primaryStage);
			primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				@Override
				public void handle(WindowEvent event) {
					System.exit(0);
				}
			});

			new Thread(() -> {
				ExceptionUtil.init();
				DBHandler.getInstance();
				
				Locale.setDefault(new Locale(PropertyManager.getValue(Setting.LANGUAGE)));
                if (PropertyManager.hasSettingValue(Setting.LANGUAGE, "cn")) {
                    Translator.setLanguageCN();
                } else if (PropertyManager.hasSettingValue(Setting.LANGUAGE, "de")) {
                    Translator.setLanguageDE();
                } else if (PropertyManager.hasSettingValue(Setting.LANGUAGE, "se")) {
                    Translator.setLanguageSE();
                } else if (PropertyManager.hasSettingValue(Setting.LANGUAGE, "fr")) {
                    Translator.setLanguageFR();
                } else if (PropertyManager.hasSettingValue(Setting.LANGUAGE, "en")) {
                    Translator.setLanguageEN();
                }  else {
                    Translator.setLanguageNL();
                }
			}).start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Long startTime = System.currentTimeMillis();
		LOGGER.log(Level.INFO, "mill launched on {}", CommonUtil.formatDateTimeString(startTime));
		launch(args);
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {

			}
		});
	}
	
}
