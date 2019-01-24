package cn.greatoo.easymill.ui.teach;

import java.sql.SQLException;
import java.util.List;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.db.util.CoordinatesHandler;
import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.db.util.Programhandler;
import cn.greatoo.easymill.entity.Program;
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
	private Button auto;
	public void init(List<Button> bts, Button auto) {	
		this.bts = bts;
		stopBt.setDisable(true);
		this.auto = auto;
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
		Program program = DBHandler.getInstance().getProgramBuffer().get(DBHandler.getInstance().getProgramName());
		if(program != null) {
		try {
			CoordinatesHandler.saveCoordinates(program.getUnloadstacker().getOffset());
			CoordinatesHandler.saveCoordinates(program.getLoadCNC().getOffset());
			CoordinatesHandler.saveCoordinates(program.getUnloadCNC().getOffset());
			CoordinatesHandler.saveCoordinates(program.getLoadstacker().getOffset());
			Programhandler.updateProgramTeachStatu();
			auto.setDisable(false);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		}
	}
	@FXML
	public void stopBtAction(ActionEvent event) {
		stopBt.setDisable(true);
		reset.setDisable(true);
		save.setDisable(true);
		btnStartAll.setDisable(false);
		for(int i = 0;i < bts.size(); i++) {
			if(TeachAndAutoThread.isFinishTeach) {
				bts.get(i).setDisable(false);
			}
			else if(bts.get(i) != auto) {
				bts.get(i).setDisable(false);
			}
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
