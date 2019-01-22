package cn.greatoo.easymill.ui.configure.robot;

import java.sql.SQLException;

import javax.swing.JOptionPane;

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
			//saveBt.setDisable(true);
		}
	}
	@FXML
	public void saveBtAction(ActionEvent event) {
		fanucRobot =  FanucRobot.getInstance(null, 0f, null);
		if (!nameText.getText().equals("") 
				&& !ipText.getText().equals("") && !portText.getText().equals("") && (Integer.parseInt(portText.getText()) > 0)
				&& !payload.getText().equals("") && (Float.parseFloat(payload.getText()) > 0))
		{
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
				//JOptionPane.showMessageDialog(null, "是否确定保存", "DataBase ok", JOptionPane.OK_CANCEL_OPTION);
				RobotHandler.saveRobot(fanucRobot);

			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		 else {
				//saveBt.setDisable(true);
				JOptionPane.showMessageDialog(null, "Robot数据不全，请填写完整数据后保存", "Database Error", JOptionPane.WARNING_MESSAGE);
			}
	}
	
	@FXML
	public void removeProcess(MouseEvent event) {
		
	}

}
