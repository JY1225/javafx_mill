package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.ui.main.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class DevicesConfigMenuViewController extends Controller {
	@FXML
	private StackPane stackPane;
	@FXML
	private VBox prosessVBox;
	@FXML
	private Button coordinateBt;
	@FXML
	private Button StackerBt;
	@FXML
	private Button cncBt;
	@FXML
	private Button cncGriperBt;

	List<Button> bts;
	FXMLLoader fxmlLoader;
	private GridPane gridPane;
	private Parent coordinateParent,stackerParent,CNCConfigParent,GriperParent;
	
	public void init(GridPane gridPane) {
		this.gridPane = gridPane;
		bts = new ArrayList<Button>();
		bts.add(coordinateBt);
		bts.add(StackerBt);
		bts.add(cncBt);
		bts.add(cncGriperBt);
		
		int i = 0;
		addMenuItem(prosessVBox, coordinateBt, i, "用户坐标", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				// 默认选择通用按钮
				isClicked(bts, coordinateBt);
				openCoordinateView();
			}
		});
		addMenuItem(prosessVBox, StackerBt, i++, "Stacker", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {				
				isClicked(bts, StackerBt);
				openStackerView();
			}
		});
		addMenuItem(prosessVBox, cncBt, i++, "数控机床", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, cncBt);
				openCNCConfigView();
			}
		});
		addMenuItem(prosessVBox, cncGriperBt, i++, "夹具", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, cncGriperBt);
				openGriperView();
			}
		});
	}

	protected void openCoordinateView() {
		if (!gridPane.getChildren().contains(coordinateParent)) {
			try {
				URL location = getClass()
						.getResource("/cn/greatoo/easymill/ui/configure/devicesConfig/CoordinateView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				coordinateParent = fxmlLoader.load();
				CoordinateViewController coordinateViewController = fxmlLoader.getController(); 
				coordinateViewController.init();
				gridPane.add(coordinateParent, 2, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(0, 2, gridPane, coordinateParent);
	}
	
	protected void openStackerView(){
		if (!gridPane.getChildren().contains(stackerParent)) {
			try {
				URL location = getClass()
						.getResource("/cn/greatoo/easymill/ui/configure/devicesConfig/StackerView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				stackerParent = fxmlLoader.load();
				StackerViewController stackerViewController = fxmlLoader.getController(); 
				stackerViewController.init();
				gridPane.add(stackerParent, 2, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(0, 2, gridPane, stackerParent);
	}
	
	protected void openCNCConfigView() {
		if (!gridPane.getChildren().contains(CNCConfigParent)) {
			try {
				URL location = getClass()
						.getResource("/cn/greatoo/easymill/ui/configure/devicesConfig/CNCConfigView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				CNCConfigParent = fxmlLoader.load();
				CNCConfigViewController cNCConfigViewController = fxmlLoader.getController(); 
				cNCConfigViewController.init();
				gridPane.add(CNCConfigParent, 2, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(0, 2, gridPane, CNCConfigParent);
	}
	protected void openGriperView() {
		if (!gridPane.getChildren().contains(GriperParent)) {
			try {
				URL location = getClass()
						.getResource("/cn/greatoo/easymill/ui/configure/devicesConfig/ConfigGriperView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				GriperParent = fxmlLoader.load();
				ConfigGriperViewController configGriperViewController = fxmlLoader.getController(); 
				configGriperViewController.init();
				gridPane.add(GriperParent, 2, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(0, 2, gridPane, GriperParent);
	}


	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
}
