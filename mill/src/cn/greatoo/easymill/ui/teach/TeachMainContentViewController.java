package cn.greatoo.easymill.ui.teach;

import java.util.List;

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
	private List<Button> bts;
	
	public void init(List<Button> bts) {	
		this.bts = bts;
		stopBt.setDisable(true);
	}
	@FXML
	public void btnStartAction(ActionEvent event) {
		
	}
	

	@FXML
	public void btnStartAllAction(ActionEvent event) {
		FanucRobot robot = FanucRobot.getInstance(null,0,null);
		CNCMachine cncMachine = CNCMachine.getInstance(null, null, null);
		if(robot != null && cncMachine != null) {
			btnStartAll.setDisable(true);
			stopBt.setDisable(false);
			reset.setDisable(false);
			save.setDisable(false);
			for(int i = 0;i < bts.size(); i++) {
				bts.get(i).setDisable(true);
			}
			TeachAndAutoThread teachSocketThread = new TeachAndAutoThread(robot,cncMachine,true,this);
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
		stopBt.setDisable(true);
		reset.setDisable(true);
		save.setDisable(true);
		btnStartAll.setDisable(false);
		for(int i = 0;i < bts.size(); i++) {
			bts.get(i).setDisable(false);
		}
		FanucRobot.getInstance(null,0,null).interruptCurrentAction();
		CNCMachine.getInstance(null,null,null).interruptCurrentAction();
	}
	
	public void setMessege(String messege) {
		if(messegeText != null) {
			messegeText.setText(messege);
		}
	}
	
}
