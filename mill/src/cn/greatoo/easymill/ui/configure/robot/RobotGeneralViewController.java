package cn.greatoo.easymill.ui.configure.robot;

import java.sql.SQLException;

import cn.greatoo.easymill.db.util.RobotHandler;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.robot.AbstractRobot;
import cn.greatoo.easymill.robot.FanucRobot;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class RobotGeneralViewController {
	@FXML
	private GridPane generalGridPane;
	@FXML
	private TextField nameText;
	@FXML
	private Button saveBt;
	@FXML
	private TextField ipText;
	@FXML
	private TextField portText;
	@FXML
	private TextField payload;
	
	private static AbstractRobot fanucRobot;
	
	public void init() {
		
		fanucRobot =  FanucRobot.getInstance(null, 0f, null);
		if(fanucRobot != null && fanucRobot.getId() > 0) {
			nameText.setText(fanucRobot.getName());
			ipText.setText(fanucRobot.getSocketConnection().getIpAddress());
			portText.setText(String.valueOf(fanucRobot.getSocketConnection().getPortNumber()));
			payload.setText(String.valueOf(fanucRobot.getPayload()));
		}
	}
	@FXML
	public void saveBtAction(ActionEvent event) {
		if(fanucRobot != null && fanucRobot.getId() > 0) {
			fanucRobot.setName(nameText.getText());
			fanucRobot.getSocketConnection().setName(nameText.getText());
			fanucRobot.getSocketConnection().setIpAddress(ipText.getText());
			fanucRobot.getSocketConnection().setPortNumber(Integer.valueOf(portText.getText()));
			fanucRobot.setPayload(Float.parseFloat(payload.getText()));
			FanucRobot.getInstance(null, 0f, null).setName(nameText.getText());
			FanucRobot.getInstance(null, 0f, null).getSocketConnection().setName(nameText.getText());
			FanucRobot.getInstance(null, 0f, null).getSocketConnection().setIpAddress(ipText.getText());
			FanucRobot.getInstance(null, 0f, null).getSocketConnection().setPortNumber(Integer.valueOf(portText.getText()));
			FanucRobot.getInstance(null, 0f, null).setPayload(Float.parseFloat(payload.getText()));
		}else {
			SocketConnection socketConnection = new SocketConnection(SocketConnection.Type.CLIENT,nameText.getText(),ipText.getText(),Integer.valueOf(portText.getText()));
			fanucRobot = (FanucRobot) FanucRobot.getInstance(nameText.getText(), Float.parseFloat(payload.getText()), socketConnection);
		}
		try {
			RobotHandler.saveRobot(fanucRobot);
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	@FXML
	public void removeProcess(MouseEvent event) {
		
	}

	
}
