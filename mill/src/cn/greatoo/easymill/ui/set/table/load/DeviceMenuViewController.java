package cn.greatoo.easymill.ui.set.table.load;

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
import javafx.scene.shape.SVGPath;

/**
 * 设备，工件，夹具，布局页面Controller
 *
 */
public class DeviceMenuViewController extends Controller {
	private static String arrowRightPath = "M 1.6875 0 L 0 1.65625 L 3.375 5 L 0.0625 8.3125 L 1.71875 10 L 6.65625 5.0625 L 6.5625 4.96875 L 6.625 4.90625 L 1.6875 0 z ";
	protected SVGPath imagePath;
	protected SVGPath svgPauseLeft;
	private static final int BUTTON_WIDTH = 209;
	private static final int BUTTON_HEIGHT = 43;

	private static final int ICON_WIDTH = 20;
	private static final int ICON_MARGIN = 6;
	private static final int ICON_ARROW_WIDTH = 10;
	private static final String CSS_CLASS_MENU_ICON = "menu-icon";
	private static final String CSS_CLASS_LEFT_MENU_ITEM_LABEL = "left-menu-item-label";
	private static final String CSS_CLASS_LEFT_MENU_ITEM_PANEL = "left-menu-item-panel";
	private static final String CSS_CLASS_LEFT_MENU_BUTTON = "left-menu-button";
	private static final String FROM_ICON = "m 12.0625,-0.21875 0,2.8125 C 0.64945154,3.202482 0.15625,14.980153 0.15625,15.09375 l 1.96875,0 c 0,-0.02124 1.0769284,-6.861816 9.9375,-7.3125 l 0,3 8.09375,-5.5 -8.09375,-5.5 z";
	private static final String TO_ICON = "M 0.15625 -0.21875 C 0.15625 -0.105153 0.64945154 11.672518 12.0625 12.28125 L 12.0625 15.09375 L 20.15625 9.59375 L 12.0625 4.09375 L 12.0625 7.09375 C 3.2019284 6.643066 2.125 -0.19751 2.125 -0.21875 L 0.15625 -0.21875 z";
	private static final String WORKPIECE_ICON = "M 6.25 0 L 4 3.375 L 5.65625 3.375 L 5.65625 6.25 L 6.875 6.25 L 6.875 3.375 L 8.5625 3.375 L 6.25 0 z M 0 7.5 L 0 16.875 L 12.5 16.875 L 12.5 7.5 L 0 7.5 z M 16.65625 9.90625 L 16.65625 11.625 L 13.75 11.625 L 13.75 12.78125 L 16.65625 12.78125 L 16.65625 14.46875 L 20 12.1875 L 16.65625 9.90625 z";
	private static final String LAYOUT_ICON = "m 4.3125,0.12499999 c -1.9028305,0 -3.5,1.56110911 -3.5,3.50000001 0,1.938891 1.5971695,3.5 3.5,3.5 1.9028305,0 3.46875,-1.561109 3.46875,-3.5 0,-1.9388909 -1.5659195,-3.50000001 -3.46875,-3.50000001 z m 12.125,0 c -1.90283,0 -3.46875,1.56110911 -3.46875,3.50000001 0,1.938891 1.56592,3.5 3.46875,3.5 1.902831,0 3.5,-1.561109 3.5,-3.5 0,-1.9388909 -1.597169,-3.50000001 -3.5,-3.50000001 z M 4.3125,1.3125 c 1.309224,0 2.3125,1.0393364 2.3125,2.3125 0,1.273164 -1.003276,2.3125 -2.3125,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.2731636 1.034526,-2.3125 2.34375,-2.3125 z m 12.125,0 c 1.309224,0 2.34375,1.0393364 2.34375,2.3125 0,1.273164 -1.034526,2.3125 -2.34375,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.2731636 1.034526,-2.3125 2.34375,-2.3125 z m -12.125,9.03125 c -2.4090408,0 -4.375,1.965959 -4.375,4.375 0,2.409041 1.9659592,4.34375 4.375,4.34375 2.4090408,0 4.34375,-1.934709 4.34375,-4.34375 0,-2.409041 -1.9347092,-4.375 -4.34375,-4.375 z m 12.125,0.875 c -1.90283,0 -3.46875,1.561109 -3.46875,3.5 0,1.938891 1.56592,3.46875 3.46875,3.46875 1.902831,0 3.5,-1.529859 3.5,-3.46875 0,-1.938891 -1.597169,-3.5 -3.5,-3.5 z m 0,1.15625 c 1.309224,0 2.34375,1.070586 2.34375,2.34375 0,1.273164 -1.034526,2.3125 -2.34375,2.3125 -1.309224,0 -2.34375,-1.039336 -2.34375,-2.3125 0,-1.273164 1.034526,-2.34375 2.34375,-2.34375 z";
	private static final String LAYOUT_CONVEYOR_ICON = "m 14.78125,99.3125 -0.65625,0 0,2.46875 1.34375,0 0,-1.78125 1.75,0 0,-1.375 -2.4375,0 z M 19.9375,100 l 2.6875,0 0,-1.375 -2.6875,0 z m 5.375,0 2.71875,0 0,-1.375 -2.71875,0 z m 5.40625,0 1.3125,0 0,0.0312 1.34375,0 0,-0.71875 0,-0.6875 -0.6875,0 -1.96875,0 z m 1.3125,5.4375 1.34375,0 0,-2.6875 -1.34375,0 z m -17.90625,1.75 1.34375,0 0,-2.71875 -1.34375,0 z m 17.90625,1.65625 -0.65625,0 0,1.34375 1.3125,0 0.6875,0 0,-0.6875 0,-1.34375 -1.34375,0 z m -16.875,1.34375 2.71875,0 0,-1.34375 -2.71875,0 z m 5.40625,0 2.6875,0 0,-1.34375 -2.6875,0 z m 5.40625,0 2.6875,0 0,-1.34375 -2.6875,0 z";
	private static final String PICK_ICON = "M 4.6875 0.09375 C 2.161259 0.09375 0.09375 2.161495 0.09375 4.6875 C 0.09375 7.213504 2.161259 9.25 4.6875 9.25 C 7.0056475 9.25 8.9192752 7.517486 9.21875 5.28125 L 17.21875 5.28125 L 14.9375 7.40625 L 17.1875 7.40625 L 20.0625 4.71875 L 20.0625 4.65625 L 17.1875 2 L 14.9375 2 L 17.1875 4.0625 L 9.21875 4.0625 C 8.9192752 1.826023 7.0056475 0.09375 4.6875 0.09375 z";
	private static final String PUT_ICON = "m 15.161617,0.03741328 c -2.464229,0 -4.501754,1.83791802 -4.82032,4.21483102 l -5.5044426,0 -2.4140913,-2.209641 -2.34332005,0 2.06023435,1.918692 1.0301172,0.935755 0.00785,0.0079 -0.015727,0 -0.031454,0.03145 0,0.01573 -3.05889762,2.846583 2.35118352,0 2.4534088,-2.248958 5.4651257,0 c 0.318565,2.376913 2.35609,4.214831 4.820319,4.214831 2.684658,0 4.859637,-2.182339 4.859637,-4.8675 -2.52e-4,-2.68491 -2.174979,-4.85963702 -4.859637,-4.85963702 z M 3.1619311,4.9049143 l 0.00785,-0.0079 -0.039317,0 0.031454,0.0079 z";
	@FXML
	private StackPane stackPane;
	@FXML
	private VBox prosessVBox;
	@FXML
	private Button device;
	@FXML
	private Button workPiece;
	@FXML
	private Button pick;
	@FXML
	private Button layout;
	private GridPane setProsessPane;
	private Parent deviceParent;
	private Parent rawWPParent;
	private Parent pickParent;
	private Parent layoutParent;
	List<Button> bts;
	List<String> parents = new ArrayList<String>();
	FXMLLoader fxmlLoader;

	public void init(GridPane setProsessPane) {
		this.setProsessPane = setProsessPane;
		bts = new ArrayList<Button>();
//		bts.add(device);
		bts.add(workPiece);
		bts.add(pick);
		bts.add(layout);

		// 默认选择工件按钮
		isClicked(bts, workPiece);
		openRawWPView();

//		addMenuItem(prosessVBox,device, 0, FROM_ICON, "设备", true, new EventHandler<ActionEvent>() {
//			@Override
//			public void handle(final ActionEvent event) {
//				isClicked(bts, device);
//				if (!setProsessPane.getChildren().contains(deviceParent)) {
//					try {
//						URL location = getClass()
//								.getResource("/cn/greatoo/easymill/ui/set/table/load/DeviceView.fxml");
//						fxmlLoader = new FXMLLoader();
//						fxmlLoader.setLocation(location);
//						fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
//						deviceParent = fxmlLoader.load();
//						DeviceViewController deviceViewController = fxmlLoader.getController();
//						// 中写的初始化方法
//						deviceViewController.init();
//						setProsessPane.add(deviceParent, 1, 2);
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//				} else
//					setDisVisible(2, 1, setProsessPane,deviceParent);
//			}
//		});
		addMenuItem(prosessVBox,workPiece, 1, WORKPIECE_ICON, "工件", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, workPiece);
				openRawWPView();
			}
		});
		addMenuItem(prosessVBox,pick, 2, PICK_ICON, "夹取", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, pick);
				if (!setProsessPane.getChildren().contains(pickParent)) {
					try {
						URL location = getClass()
								.getResource("/cn/greatoo/easymill/ui/set/table/load/PickView.fxml");
						fxmlLoader = new FXMLLoader();
						fxmlLoader.setLocation(location);
						fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
						pickParent = fxmlLoader.load();
						PickViewController pickViewController = fxmlLoader.getController();
						// 中写的初始化方法
						pickViewController.init();
						setProsessPane.add(pickParent, 1, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					setDisVisible(2, 1, setProsessPane,pickParent);
			}
		});
		addMenuItem(prosessVBox,layout, 3, LAYOUT_ICON, "布局", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, layout);
				if (!setProsessPane.getChildren().contains(layoutParent)) {
					try {
						URL location = getClass()
								.getResource("/cn/greatoo/easymill/ui/set/table/load/LayoutView.fxml");
						fxmlLoader = new FXMLLoader();
						fxmlLoader.setLocation(location);
						fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
						layoutParent = fxmlLoader.load();
						LayoutViewController layoutViewController = fxmlLoader.getController();
						// 中写的初始化方法
						layoutViewController.init(setProsessPane);
						setProsessPane.add(layoutParent, 1, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					setDisVisible(2, 1, setProsessPane,layoutParent);
					
			}
		});
	}

	private void openRawWPView() {
		if (!setProsessPane.getChildren().contains(rawWPParent)) {
			try {
				URL location = getClass()
						.getResource("/cn/greatoo/easymill/ui/set/table/load/RawWPView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				rawWPParent = fxmlLoader.load();
				RawWPViewController rawWPViewController = fxmlLoader.getController();
				// 中写的初始化方法
				rawWPViewController.init();
				setProsessPane.add(rawWPParent, 1, 2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(2, 1, setProsessPane,rawWPParent);
	}

	@FXML
	public void openDevice(MouseEvent event) {

	}

	@FXML
	public void openWorkPiece(MouseEvent event) {

	}

	@FXML
	public void openPick(MouseEvent event) {

	}

	@FXML
	public void openLayout(MouseEvent event) {

	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
}
