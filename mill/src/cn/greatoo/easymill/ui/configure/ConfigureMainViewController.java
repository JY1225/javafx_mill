package cn.greatoo.easymill.ui.configure;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class ConfigureMainViewController extends Controller{
	@FXML
	private GridPane gridPane;
	@FXML
	private Button robotBt;
	@FXML
	private Button configBt;
	private List<Button> bts;
	private Parent configParent;
	private FXMLLoader fxmlLoader;
	
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(robotBt);
		bts.add(configBt);
		openConfigMenuView();

	}
		
	private void openConfigMenuView() {
		if (!gridPane.getChildren().contains(configParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/configure/ConfigureMenuView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				configParent = fxmlLoader.load();
				ConfigureMenuViewController configureMenuViewController = fxmlLoader.getController(); 
				configureMenuViewController.init(gridPane);
				gridPane.add(configParent, 0, 0);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(1, 1, gridPane, configParent);		
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
