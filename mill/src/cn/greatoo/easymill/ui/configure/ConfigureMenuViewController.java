package cn.greatoo.easymill.ui.configure;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.ui.configure.devices.DevicesMenuViewController;
import cn.greatoo.easymill.ui.configure.robot.RobotMenuViewController;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ConfigureMenuViewController extends Controller{
	
	private GridPane gridPane;
	@FXML
	private Button robotBt;
	@FXML
	private Button configBt;
	private List<Button> bts;
	private Parent robotParent;
	private Parent configParent;
	private FXMLLoader fxmlLoader;
	
	public void init(GridPane gridPane) {
		this.gridPane = gridPane;
		bts = new ArrayList<Button>();
		bts.add(robotBt);
		bts.add(configBt);
		openRobotView();
	}
	@FXML
	public void robotAction(ActionEvent event) {
		openRobotView();
	}
	
	@FXML
	public void configAction(ActionEvent event) {
		openConfigView();
	}
	
	RobotMenuViewController robotMenuViewController;
	private void openRobotView() {
		isClicked(bts,robotBt);
		if (!gridPane.getChildren().contains(robotParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/configure/robot/RobotMenuView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				robotParent = fxmlLoader.load();
				robotMenuViewController = fxmlLoader.getController(); 
				robotMenuViewController.init(gridPane);
				gridPane.add(robotParent, 1, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			robotMenuViewController.init(gridPane);
			setDisVisible(0, 1, gridPane, robotParent);
		}
	}
	DevicesMenuViewController devicesMenuViewController;
	private void openConfigView() {
		isClicked(bts,configBt);
		if (!gridPane.getChildren().contains(configParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/configure/devices/DevicesMenuView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				configParent = fxmlLoader.load();
				devicesMenuViewController = fxmlLoader.getController(); 
				devicesMenuViewController.init(gridPane);
				gridPane.add(configParent, 1, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			devicesMenuViewController.init(gridPane);
			setDisVisible(0, 1, gridPane, configParent);
		}
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void setTextFieldListener(TextInputControlListener listener) {
		// TODO Auto-generated method stub
		
	}
}
