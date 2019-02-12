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
import javafx.animation.RotateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class TeachMainContentViewController extends Controller{
	private RotateTransition rotateTransition;
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
	public void init(List<Button> bts, Button auto, RotateTransition rotateTransition) {	
		this.bts = bts;
		stopBt.setDisable(true);
		this.auto = auto;
		this.rotateTransition = rotateTransition;
		animate(false);
	}
	@FXML
	public void btnStartAction(ActionEvent event) {
		
	}
	

	@FXML
	public void btnStartAllAction(ActionEvent event) {
		FanucRobot robot = FanucRobot.getInstance(null,0,null);
		CNCMachine cncMachine = CNCMachine.getInstance(null, null, null);
		if((robot != null && robot.isConnected()) && (cncMachine != null && cncMachine.isConnected())) {
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
		Program program = DBHandler.getInstance().getProgramBuffer().get(DBHandler.getInstance().getProgramName());
		if(program != null) {
		try {			
			Programhandler.updateProgramTeachStatu(false);
			DBHandler.getInstance().getProgramBuffer().get(DBHandler.getInstance().getProgramName()).setHasTeach(false);
			auto.setDisable(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
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
			Programhandler.updateProgramTeachStatu(true);
			DBHandler.getInstance().getProgramBuffer().get(DBHandler.getInstance().getProgramName()).setHasTeach(true);
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
	public void animate(final boolean animate) {
        if (animate) {
            if (rotateTransition != null) {
                rotateTransition.play();
            }
        } else {
            if (rotateTransition != null) {
                rotateTransition.stop();
            }
        }
    }
	public void setMessege(String messege) {
		if(messegeText != null) {
			messegeText.setText(messege);
			if(messege.contains("机床加工中")) {
				animate(true);
			}else if(messege.contains("从机床下料")) {
				animate(false);
			}
		}
	}
	
}
