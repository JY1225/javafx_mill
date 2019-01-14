package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.external.communication.socket.SocketConnection.Type;
import cn.greatoo.easymill.ui.main.Controller;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;

public class GeneralConfigViewController extends Controller {
	@FXML
	private GridPane generalGridPane;
	@FXML
	private TextField nameText;
	@FXML
	private TextField ipText;
	@FXML
	private TextField portText;
	@FXML
	private Button minusBt;
	@FXML
	private Button postiveBt;
	List<Button> bts;
	private CNCMachine cnc;
	
	public void init(CNCMachine cnc) {
		bts = new ArrayList<Button>();
		bts.add(minusBt);
		bts.add(postiveBt);
		this.cnc = cnc;
		if(cnc.getSocketConnection() == null) {
			nameText.setText(cnc.getName());
		}else {
			nameText.setText(cnc.getSocketConnection().getName());
			ipText.setText(cnc.getSocketConnection().getIpAddress());
			portText.setText(String.valueOf(cnc.getSocketConnection().getPortNumber()));
		}
	}
	
	public SocketConnection getSocketConnection() {
		SocketConnection socketConnection = null;
		if(cnc.getSocketConnection() == null) {
			socketConnection = new SocketConnection(SocketConnection.Type.CLIENT,nameText.getText(), ipText.getText(), Integer.parseInt(portText.getText()));	
		}else {
			cnc.getSocketConnection().setName(nameText.getText());
			cnc.getSocketConnection().setIpAddress(ipText.getText());
			cnc.getSocketConnection().setPortNumber(Integer.parseInt(portText.getText()));
			socketConnection = cnc.getSocketConnection();
		}
		return socketConnection;		
	}

	@FXML
	private void minusBtAction(ActionEvent event) {
		isClicked(bts, minusBt);
	}
	@FXML
	private void postiveBtAction(ActionEvent event) {
		isClicked(bts, postiveBt);
	}
	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
}
