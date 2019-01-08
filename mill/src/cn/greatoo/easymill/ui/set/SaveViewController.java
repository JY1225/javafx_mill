package cn.greatoo.easymill.ui.set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.process.DuplicateProcessFlowNameException;
import cn.greatoo.easymill.ui.general.NotificationBox;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
public class SaveViewController {
	@FXML
	private GridPane saveGridPane;
	@FXML
	private Button saveBt;
	@FXML
	private TextField fulltxtName;
	
	private static Logger logger = LogManager.getLogger(SaveViewController.class.getName());
	private static NotificationBox notificationBox;

	public void init() {		
	
	}

	@FXML
	public void save(MouseEvent event) throws DuplicateProcessFlowNameException  {

	}			
	
	public static void showNotification(final String notification, NotificationBox.Type type) {
		notificationBox = SetMenuViewController.getnotificationBox();
		notificationBox.showNotification(notification, type);
		notificationBox.setVisible(true);
		notificationBox.setManaged(true);
	}
	
	public void hideNotification() {
		notificationBox = SetMenuViewController.getnotificationBox();
		notificationBox.setVisible(false);
		notificationBox.setManaged(false);
	}
}
