package cn.greatoo.easymill.ui.configure.devicesConfig;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.ui.main.Controller;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
	private Button cncBt;
	@FXML
	private Button cncGriperBt;

	List<Button> bts;
	FXMLLoader fxmlLoader;
	private GridPane gridPane;

	public void init(GridPane gridPane) {
		this.gridPane = gridPane;
		bts = new ArrayList<Button>();
		bts.add(coordinateBt);
		bts.add(cncBt);
		bts.add(cncGriperBt);

		addMenuItem(prosessVBox, coordinateBt, 0, "用户坐标", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				// 默认选择通用按钮
				isClicked(bts, coordinateBt);
				openCoordinateView();
			}
		});
		addMenuItem(prosessVBox, cncBt, 1, "数控机床", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, cncBt);

			}
		});
		addMenuItem(prosessVBox, cncGriperBt, 2, "夹具", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, cncGriperBt);

			}
		});
	}

	protected void openCoordinateView() {

	}

	@FXML
	public void openConfig(MouseEvent event) {

	}

	@FXML
	public void openSave(MouseEvent event) {

	}
}
