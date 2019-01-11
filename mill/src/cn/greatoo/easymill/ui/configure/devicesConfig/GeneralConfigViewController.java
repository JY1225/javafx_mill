package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.external.communication.socket.SocketConnection;
import cn.greatoo.easymill.ui.main.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
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
	public void init(CNCMachine cnc) {
		bts = new ArrayList<Button>();
		bts.add(minusBt);
		bts.add(postiveBt);
		nameText.setDisable(true);
		if(cnc.getSocketConnection() == null) {
			nameText.setText(cnc.getName());
		}else {
			nameText.setText(cnc.getSocketConnection().getName());
			ipText.setText(cnc.getSocketConnection().getIpAddress());
			portText.setText(String.valueOf(cnc.getSocketConnection().getPortNumber()));
		}
	}
	
	public SocketConnection getSocketConnection() {
		return new SocketConnection(SocketConnection.Type.CLIENT,"CNC_CONN_THREAD", ipText.getText(), Integer.parseInt(portText.getText()));	
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
