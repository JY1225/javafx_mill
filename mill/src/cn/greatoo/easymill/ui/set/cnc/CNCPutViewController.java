package cn.greatoo.easymill.ui.set.cnc;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.RobotPutSetting;
import cn.greatoo.easymill.entity.Smooth;
import cn.greatoo.easymill.ui.main.Controller;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
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
	public static Smooth loadCNCSmooth = new Smooth();
	public static RobotPutSetting RobotPutSetting = new RobotPutSetting();
	
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(beBt);
		bts.add(aftBt);
		isClicked(bts, beBt);
		
		loadCNCSmooth.setStep(Program.Step.LOADCNC);
		XField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	loadCNCSmooth.setX(Float.parseFloat(XField.getText()));
	        }
		});	
		YField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	loadCNCSmooth.setY(Float.parseFloat(YField.getText()));
	        }
		});	
		ZField.focusedProperty().addListener(new ChangeListener<Boolean>() {
	        @Override
	        public void changed(ObservableValue<? extends Boolean> arg0,Boolean arg1, Boolean arg2) {
	        	loadCNCSmooth.setZ(Float.parseFloat(ZField.getText()));
	        }
		});	
		
	}
	
	@FXML
	public void resetBtAction(ActionEvent event) {
		
	}
	
	@FXML
	public void beBtAction(ActionEvent event) {
		isClicked(bts, beBt);
		RobotPutSetting.setReleaseBeforeMachine(true);
	}
	
	@FXML
	public void aftBtAction(ActionEvent event) {
		isClicked(bts, aftBt);
		RobotPutSetting.setReleaseBeforeMachine(false);
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
	
}
