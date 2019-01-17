package cn.greatoo.easymill.ui.configure.robot;

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

public class RobotMenuViewController extends Controller{
	@FXML
	private StackPane stackPane;
	@FXML
	private VBox prosessVBox;
	@FXML
	private Button generalBt;
	@FXML
	private Button griperBt;

	private Parent generalParent;
	private Parent griperParent;
	List<Button> bts;
	FXMLLoader fxmlLoader;
	private GridPane gridPane;
	public void init(GridPane gridPane) {
		this.gridPane = gridPane;
		bts = new ArrayList<Button>();
		bts.add(generalBt);
		bts.add(griperBt);
		setAllDisVisible(0, 2, gridPane);
		if(generalBt.getStyleClass().contains("selected")) {
			openGeneralView();
		}else if(griperBt.getStyleClass().contains("selected")) {
			openGriperView();
		}
		

		addMenuItem(prosessVBox,generalBt, 0, "通用", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				// 默认选择通用按钮
				isClicked(bts, generalBt);
				openGeneralView();
			}
		});
		addMenuItem(prosessVBox,griperBt, 1, "夹具", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, griperBt);
				openGriperView();
			}
		});
	}
	@FXML
	public void openConfig(MouseEvent event) {
		
	}
	
	@FXML
	public void openSave(MouseEvent event) {
		
	}
	
	private void openGeneralView(){
		if (!gridPane.getChildren().contains(generalParent)) {
			try {
				URL location = getClass()
						.getResource("/cn/greatoo/easymill/ui/configure/robot/RobotGeneralView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				generalParent = fxmlLoader.load();
				RobotGeneralViewController robotGeneralViewController = fxmlLoader.getController(); 
				robotGeneralViewController.init();
				gridPane.add(generalParent, 2, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(0, 2, gridPane, generalParent);
	}
	private void openGriperView(){
		if (!gridPane.getChildren().contains(griperParent)) {
			try {
				URL location = getClass()
						.getResource("/cn/greatoo/easymill/ui/configure/robot/RobotGriperView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				griperParent = fxmlLoader.load();
				RobotGriperViewController robotGriperViewController = fxmlLoader.getController(); 
				robotGriperViewController.init();
				gridPane.add(griperParent, 2, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(0, 2, gridPane, griperParent);
	}
	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
}
