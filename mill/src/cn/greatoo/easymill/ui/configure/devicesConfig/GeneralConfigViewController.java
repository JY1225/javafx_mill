package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.util.ArrayList;
import java.util.List;

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
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(minusBt);
		bts.add(postiveBt);
	}
	@FXML
	private void minusBtAction(ActionEvent event) {
		isClicked(bts, minusBt);
	}
	@FXML
	private void postiveBtAction(ActionEvent event) {
		isClicked(bts, postiveBt);
	}
}
