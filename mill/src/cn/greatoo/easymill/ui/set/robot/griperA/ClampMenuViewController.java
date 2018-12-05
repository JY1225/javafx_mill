package cn.greatoo.easymill.ui.set.robot.griperA;

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
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

/**
 * 选择夹具页面Controller
 *
 */
public class ClampMenuViewController extends Controller {
	protected SVGPath imagePath;
	protected SVGPath svgPauseLeft;
	private static final String ICON_GRIP = "M 10.6875 -0.34375 L -0.125 -0.15625 L 11.21875 6.6875 L 8.875 9.9375 L 9.03125 11.5625 L 10.84375 12.09375 L 13.1875 12.28125 L 13.375 11.1875 L 12.3125 10.46875 L 13.375 7.59375 L 16.78125 6.15625 L 17.53125 8.6875 L 18.25 8.125 L 18.59375 5.0625 L 16.78125 3.46875 L 13.125 4.125 L 10.6875 -0.34375 z M 17.84375 11.1875 C 16.72967 11.1875 15.84375 12.07342 15.84375 13.1875 C 15.84375 14.301579 16.72967 15.21875 17.84375 15.21875 C 18.957829 15.21875 19.875 14.301579 19.875 13.1875 C 19.875 12.07342 18.957829 11.1875 17.84375 11.1875 z";
	private static final String ICON_INTERVENTION = "M 10 0 C 4.4775 0 0 4.4762499 0 10 C 0 15.52125 4.4775 20 10 20 C 15.5225 20 20 15.52125 20 10 C 20 4.4762499 15.5225 -5.7824116e-019 10 0 z M 6.625 5.5 L 8.875 5.5 L 8.875 14.5 L 6.625 14.5 L 6.625 5.5 z M 11.125 5.5 L 13.375 5.5 L 13.375 14.5 L 11.125 14.5 L 11.125 5.5 z";

	@FXML
	private Button clampBt;
	@FXML
	private Button interveneBt;
	@FXML
	private VBox prosessVBox;
	private GridPane setProsessPane;
	List<Button> bts;
	private Parent clampParent;
	private Parent interveneParent;
	FXMLLoader fxmlLoader;

	public void init(GridPane setProsessPane) {
		this.setProsessPane = setProsessPane;
		bts = new ArrayList<Button>();
		bts.add(clampBt);
		bts.add(interveneBt);
		// 默认选择夹具按钮
		isClicked(bts, clampBt);
		openClampView();

		addMenuItem(prosessVBox,clampBt, 0, ICON_GRIP, "选择夹具", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				isClicked(bts, clampBt);
				openClampView();
			}
		});
		addMenuItem(prosessVBox,interveneBt, 1, ICON_INTERVENTION, "干预", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {
				isClicked(bts, interveneBt);				
				if (!setProsessPane.getChildren().contains(interveneParent)) {
					try {
						URL location = getClass()
								.getResource("/cn/greatoo/easymill/ui/set/robot/griperA/InterveneView.fxml");
						fxmlLoader = new FXMLLoader();
						fxmlLoader.setLocation(location);
						fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
						interveneParent = fxmlLoader.load();
						InterveneViewController interveneViewController = fxmlLoader.getController();
						// 中写的初始化方法
						interveneViewController.init();
						setProsessPane.add(interveneParent, 1, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					setDisVisible(2, 1, setProsessPane, interveneParent);
			}
		});
	}


	private void openClampView() {
		if (!setProsessPane.getChildren().contains(clampParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/set/robot/griperA/ClampView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				clampParent = fxmlLoader.load();
				ClampViewController clampViewController = fxmlLoader.getController();
				// 中写的初始化方法
				clampViewController.init();
				setProsessPane.add(clampParent, 1, 2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(2, 1, setProsessPane, clampParent);
	}
}
