package cn.greatoo.easymill.ui.set.cnc;

import javafx.fxml.FXML;

import javafx.scene.control.Button;

import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.ui.main.Controller;
import javafx.event.ActionEvent;

import javafx.scene.layout.GridPane;

public class CNCPutViewController extends Controller{
	@FXML
	private GridPane gridPane;
	@FXML
	private TextField XField;
	@FXML
	private TextField YField;
	@FXML
	private TextField ZField;
	@FXML
	private Button resetBt;
	@FXML
	private Button beBt;
	@FXML
	private Button aftBt;
	List<Button> bts;
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(beBt);
		bts.add(aftBt);
		isClicked(bts, beBt);
		
	}
	
	@FXML
	public void XFieldAction(ActionEvent event) {
		
	}
	
	@FXML
	public void YField(ActionEvent event) {
		
	}
	
	@FXML
	public void ZField(ActionEvent event) {
		
	}
	
	@FXML
	public void resetBtAction(ActionEvent event) {
		
	}
	
	@FXML
	public void beBtAction(ActionEvent event) {
		isClicked(bts, beBt);
	}
	
	@FXML
	public void aftBtAction(ActionEvent event) {
		isClicked(bts, aftBt);
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
	
}
