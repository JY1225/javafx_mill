package cn.greatoo.easymill.ui.teach;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javax.swing.JOptionPane;

import cn.greatoo.easymill.external.communication.socket.CNCSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.RobotSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.external.communication.socket.TeachSocketThread;
import cn.greatoo.easymill.ui.alarms.AlarmListenThread;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.ui.main.MainViewController;
import cn.greatoo.easymill.util.AlertMaker;
import cn.greatoo.easymill.util.ThreadManager;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;

public class TeachMainContentViewController extends Controller{
	@FXML
	private Button btnStart;
	@FXML
	private Button btnStartAll;
	@FXML
	private Button reset;
	@FXML
	private Button save;

	public void init() {
		
	}
	@FXML
	public void btnStartAction(ActionEvent event) {
		
	}
	
	@FXML
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
	public void btnStartAllAction(ActionEvent event) {
=======
	public void btnStartAllAction(ActionEvent event) {		
>>>>>>> e5cfef5 commit
		RobotSocketCommunication roboSocketConnection = AlarmListenThread.roboSocketConnection;
		CNCSocketCommunication cncSocketConnection = AlarmListenThread.cncSocketConnection;	
		if(roboSocketConnection != null && cncSocketConnection != null) {
			toolBarMenu.setDisable(true);
			gridPane.setVisible(false);
			stopBt.setVisible(true);
			messegeText.setVisible(true);
			TeachSocketThread teachSocketThread = new TeachSocketThread(roboSocketConnection,cncSocketConnection);
			ThreadManager.submit(teachSocketThread);
		}else {
			showNotificationOverlay(MainViewController.parentStackPane, "示教错误", "请注意，设备连接错误！");
		}
	}
	
	@FXML
	public void resetAction(ActionEvent event) {
		
	}
	
	@FXML
	public void saveAction(ActionEvent event) {
		
	}
<<<<<<< Upstream, based on branch 'master' of http://jy@192.168.1.73:1000/r/mill/v1.0.git
=======
	@FXML
	public void stopBtAction(ActionEvent event) {
		toolBarMenu.setDisable(false);
		stopBt.setVisible(false);
		messegeText.setVisible(false);
		gridPane.setVisible(true);
		//ThreadManager.shutDown();
	}
	
	public void setMessege(String messege) {
		if(messegeText != null) {
			
			messegeText.setText(messege);
		}
	}
>>>>>>> e5cfef5 commit
	
}
