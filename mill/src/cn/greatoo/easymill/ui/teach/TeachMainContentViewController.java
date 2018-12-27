package cn.greatoo.easymill.ui.teach;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.external.communication.socket.CNCSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.RobotSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.StatusChangeThread;
import cn.greatoo.easymill.external.communication.socket.TeachAndAutoThread;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.ui.main.MainViewController;
import cn.greatoo.easymill.util.ThreadManager;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToolBar;

public class TeachMainContentViewController extends Controller{

	@FXML
	private Button btnStart;
	@FXML
	private Button btnStartAll;
	@FXML
	private Button reset;
	@FXML
	private Button save;
	@FXML
	private Label messegeText;
	@FXML
	private Button stopBt;
	private ToolBar toolBarMenu;
	
	public void init(ToolBar toolBarMenu) {	

	}
	@FXML
	public void btnStartAction(ActionEvent event) {
		
	}
	
	int i = 0;
	@FXML
	public void btnStartAllAction(ActionEvent event) {		
		RobotSocketCommunication roboSocketConnection = StatusChangeThread.roboSocketConnection;
		CNCSocketCommunication cncSocketConnection = StatusChangeThread.cncSocketConnection;	
		if(roboSocketConnection != null && cncSocketConnection != null) {	
			TeachAndAutoThread teachSocketThread = new TeachAndAutoThread(roboSocketConnection,cncSocketConnection,true,this);
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
	@FXML
	public void stopBtAction(ActionEvent event) {
		FanucRobot.getInstance(null).interruptCurrentAction();
		CNCMachine.getInstance(null,null,null).interruptCurrentAction();
	}
	
	public void setMessege(String messege) {
		if(messegeText != null) {
			messegeText.setText(messege);
		}
	}
	
}
