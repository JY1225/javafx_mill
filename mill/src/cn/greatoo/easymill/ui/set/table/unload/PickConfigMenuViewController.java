package cn.greatoo.easymill.ui.set.table.unload;

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
import javafx.scene.layout.VBox;

public class PickConfigMenuViewController extends Controller{
	private static final String PUT_ICON = "m 15.161617,0.03741328 c -2.464229,0 -4.501754,1.83791802 -4.82032,4.21483102 l -5.5044426,0 -2.4140913,-2.209641 -2.34332005,0 2.06023435,1.918692 1.0301172,0.935755 0.00785,0.0079 -0.015727,0 -0.031454,0.03145 0,0.01573 -3.05889762,2.846583 2.35118352,0 2.4534088,-2.248958 5.4651257,0 c 0.318565,2.376913 2.35609,4.214831 4.820319,4.214831 2.684658,0 4.859637,-2.182339 4.859637,-4.8675 -2.52e-4,-2.68491 -2.174979,-4.85963702 -4.859637,-4.85963702 z M 3.1619311,4.9049143 l 0.00785,-0.0079 -0.039317,0 0.031454,0.0079 z";
	@FXML
	private VBox prosessVBox;
	@FXML
	private Button put;
	List<Button> bts;
	FXMLLoader fxmlLoader;
	private Parent placeParent;
	private GridPane gridPane;
	public void init(GridPane gridPane) {
		this.gridPane = gridPane;
		bts = new ArrayList<Button>();
		bts.add(put);
		isClicked(bts, put);
		
		openGeneralView();

		addMenuItem(prosessVBox,put, 0, PUT_ICON, "放置", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, put);
				openGeneralView();
			}
		});
	
	}
	private void openGeneralView() {
		if (!gridPane.getChildren().contains(placeParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/set/table/unload/PlaceView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				placeParent = fxmlLoader.load();
				PlaceViewController clampViewController = fxmlLoader.getController();
				// 中写的初始化方法
				clampViewController.init();
				gridPane.add(placeParent, 1, 2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(2, 1, gridPane, placeParent);		
	}
	@FXML
	public void openPutV(MouseEvent event) {
		
	}

	
}
