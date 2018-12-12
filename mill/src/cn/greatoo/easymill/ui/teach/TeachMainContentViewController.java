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
	public void btnStartAllAction(ActionEvent event) {
		RobotSocketCommunication roboSocketConnection = AlarmListenThread.roboSocketConnection;
		CNCSocketCommunication cncSocketConnection = AlarmListenThread.cncSocketConnection;	
		if(roboSocketConnection != null && cncSocketConnection != null) {
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
	
}
