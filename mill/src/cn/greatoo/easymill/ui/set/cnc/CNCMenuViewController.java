package cn.greatoo.easymill.ui.set.cnc;

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
import javafx.scene.shape.SVGPath;

public class CNCMenuViewController extends Controller {
	protected SVGPath imagePath;
	protected SVGPath svgPauseLeft;
	private static final String PICK_ICON = "M 4.6875 0.09375 C 2.161259 0.09375 0.09375 2.161495 0.09375 4.6875 C 0.09375 7.213504 2.161259 9.25 4.6875 9.25 C 7.0056475 9.25 8.9192752 7.517486 9.21875 5.28125 L 17.21875 5.28125 L 14.9375 7.40625 L 17.1875 7.40625 L 20.0625 4.71875 L 20.0625 4.65625 L 17.1875 2 L 14.9375 2 L 17.1875 4.0625 L 9.21875 4.0625 C 8.9192752 1.826023 7.0056475 0.09375 4.6875 0.09375 z";
	private static final String PUT_ICON = "m 15.161617,0.03741328 c -2.464229,0 -4.501754,1.83791802 -4.82032,4.21483102 l -5.5044426,0 -2.4140913,-2.209641 -2.34332005,0 2.06023435,1.918692 1.0301172,0.935755 0.00785,0.0079 -0.015727,0 -0.031454,0.03145 0,0.01573 -3.05889762,2.846583 2.35118352,0 2.4534088,-2.248958 5.4651257,0 c 0.318565,2.376913 2.35609,4.214831 4.820319,4.214831 2.684658,0 4.859637,-2.182339 4.859637,-4.8675 -2.52e-4,-2.68491 -2.174979,-4.85963702 -4.859637,-4.85963702 z M 3.1619311,4.9049143 l 0.00785,-0.0079 -0.039317,0 0.031454,0.0079 z";
	private static final String DEVICE_ICON = "M 10.3125,0.03125 C 10.090768,0.52621332 9.8777772,1.0417871 9.71875,1.5625 9.4096363,1.5775206 9.118395,1.5682556 8.8125,1.625 8.5046977,1.6662469 8.2053499,1.7261914 7.90625,1.8125 7.6095343,1.3671284 7.2594717,0.91151575 6.90625,0.5 6.1880048,0.74497824 5.4870116,1.0676056 4.84375,1.46875 4.9306547,2.0153316 5.0390825,2.5073184 5.1875,3.03125 4.9425218,3.2204376 4.6935816,3.4116126 4.46875,3.625 4.239746,3.8334997 3.9828355,4.0138434 3.78125,4.25 3.2901014,4.0348245 2.7766734,3.8501036 2.25,3.6875 1.7822167,4.2816467 1.3582453,4.9080494 1.03125,5.59375 1.3981809,6.0070539 1.7870709,6.3917715 2.1875,6.75 2.0828329,7.0418281 1.9567786,7.3257808 1.875,7.625 1.8035927,7.9267226 1.6929669,8.2227324 1.65625,8.53125 1.1273116,8.616843 0.59727974,8.7572365 0.0625,8.90625 c -0.0821363,0.7521011 -0.0822555,1.497899 0,2.25 0.53489895,0.148894 1.0648116,0.258157 1.59375,0.34375 0.036717,0.308398 0.1473427,0.604527 0.21875,0.90625 0.082017,0.299219 0.2078329,0.583053 0.3125,0.875 -0.4004291,0.358348 -0.7893191,0.742946 -1.15625,1.15625 0.3269953,0.685581 0.7509667,1.312103 1.21875,1.90625 0.5266734,-0.162604 1.0399822,-0.347205 1.53125,-0.5625 0.2013471,0.236037 0.458496,0.447869 0.6875,0.65625 0.2248316,0.213507 0.473891,0.373312 0.71875,0.5625 -0.1484175,0.524051 -0.2567261,1.047168 -0.34375,1.59375 0.6433808,0.401144 1.3441356,0.692522 2.0625,0.9375 0.3532217,-0.411635 0.7032843,-0.867128 1,-1.3125 0.2990999,0.08631 0.5985669,0.146491 0.90625,0.1875 0.3060142,0.05686 0.5971363,0.07885 0.90625,0.09375 0.1591464,0.520832 0.371899,1.036406 0.59375,1.53125 0.752578,-0.02539 1.486676,-0.139899 2.21875,-0.34375 0.072,-0.548727 0.120351,-1.075319 0.125,-1.625 0.289325,-0.111701 0.599742,-0.168016 0.875,-0.3125 0.280384,-0.133516 0.552263,-0.268101 0.8125,-0.4375 0.411039,0.344042 0.854015,0.667623 1.3125,0.96875 0.61632,-0.438696 1.214488,-0.935537 1.71875,-1.5 -0.236753,-0.499732 -0.49419,-0.946811 -0.78125,-1.40625 0.182035,-0.250224 0.373057,-0.514456 0.53125,-0.78125 0.159385,-0.26584 0.342863,-0.496694 0.46875,-0.78125 0.532753,0.06819 1.06912,0.08481 1.625,0.09375 0.281576,-0.704774 0.534774,-1.43826 0.65625,-2.1875 C 19.405786,11.426445 18.918635,11.166981 18.4375,10.9375 18.46671,10.629459 18.48522,10.340006 18.5,10.03125 18.485218,9.722494 18.466587,9.4016715 18.4375,9.09375 18.918635,8.86415 19.405786,8.6048049 19.875,8.3125 19.753524,7.56326 19.500445,6.8297744 19.21875,6.125 18.66287,6.1338216 18.126503,6.181573 17.59375,6.25 17.467863,5.9654438 17.284385,5.7033401 17.125,5.4375 16.966688,5.1707062 16.776023,4.9377235 16.59375,4.6875 16.88081,4.2280615 17.138247,3.7497318 17.375,3.25 16.870619,2.6855368 16.27257,2.2198266 15.65625,1.78125 15.197646,2.0824957 14.75467,2.4058384 14.34375,2.75 14.083394,2.5804822 13.811395,2.4460161 13.53125,2.3125 13.255873,2.1680165 12.945694,2.0803313 12.65625,1.96875 12.65165,1.4190689 12.60325,0.8924774 12.53125,0.34375 11.799057,0.13966108 11.065197,0.0566419 10.3125,0.03125 z m -0.40625,6.5 c 1.932997,0 3.5,1.567003 3.5,3.5 0,1.932997 -1.567003,3.5 -3.5,3.5 -0.6593574,0 -1.2859787,-0.183014 -1.8125,-0.5 C 8.0753954,13.019413 8.049401,13.012173 8.03125,13 7.9547977,12.951855 7.8848134,12.897496 7.8125,12.84375 7.7624,12.804835 7.7043834,12.760282 7.65625,12.71875 7.5735231,12.647554 7.5129398,12.57843 7.4375,12.5 7.3954618,12.457962 7.3523655,12.419123 7.3125,12.375 7.2688856,12.324586 7.2281751,12.271453 7.1875,12.21875 7.1207916,12.135796 7.0590481,12.057664 7,11.96875 6.9080065,11.825555 6.8207721,11.653996 6.75,11.5 6.6792279,11.346004 6.6418218,11.19281 6.59375,11.03125 6.4504288,10.546837 6.3970726,10.006961 6.46875,9.5 c 0.023892,-0.1689869 0.077197,-0.3072491 0.125,-0.46875 0.048042,-0.1615307 0.085495,-0.3460206 0.15625,-0.5 0.0098,-0.02132 0.021034,-0.04136 0.03125,-0.0625 C 6.8463389,8.3377627 6.9196157,8.2147914 7,8.09375 7.0044454,8.0868302 6.9955068,8.0693906 7,8.0625 7.0545483,7.9817579 7.1265816,7.9195038 7.1875,7.84375 7.2643495,7.7441743 7.3502951,7.6535994 7.4375,7.5625 7.5853343,7.4146657 7.7339978,7.2758485 7.90625,7.15625 7.9377056,7.1344097 7.9678054,7.1145734 8,7.09375 c 0.030384,-0.020922 0.062781,-0.04253 0.09375,-0.0625 0.5265213,-0.3169858 1.1531426,-0.5 1.8125,-0.5 z";
	static final String AFT_ICON = "M 6.25 0 L 4 3.375 L 5.65625 3.375 L 5.65625 6.25 L 6.875 6.25 L 6.875 3.375 L 8.5625 3.375 L 6.25 0 z M 0 7.5 L 0 16.875 L 12.5 16.875 L 12.5 7.5 L 0 7.5 z M 16.65625 9.90625 L 16.65625 11.625 L 13.75 11.625 L 13.75 12.78125 L 16.65625 12.78125 L 16.65625 14.46875 L 20 12.1875 L 16.65625 9.90625 z";
	
	@FXML
	private VBox prosessVBox;
	@FXML
	private Button device;
	@FXML
	private Button put;
	@FXML
	private Button pick;
	@FXML
	private Button aftProcess;
	List<Button> bts;
	FXMLLoader fxmlLoader;
	private GridPane gridPane;
	private Parent CNCDeviceParent;
	private Parent CNCPutParent;
	private Parent CNCPickParent;
	private Parent CNCFinishedWPParent;
	public void init(GridPane gridPane) {
		this.gridPane = gridPane;
		bts = new ArrayList<Button>();
		bts.add(device);
		bts.add(put);
		bts.add(pick);
		bts.add(aftProcess);

		// 默认选择通用按钮
		isClicked(bts, device);
		openCNCDeviceView();

		addMenuItem(prosessVBox,device, 0, DEVICE_ICON, "设备", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, device);
				openCNCDeviceView();
			}
		});
		addMenuItem(prosessVBox,put, 1, PUT_ICON, "上料", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, put);
				if (!gridPane.getChildren().contains(CNCPutParent)) {
					try {
						URL location = getClass()
								.getResource("/cn/greatoo/easymill/ui/set/cnc/CNCPutView.fxml");
						fxmlLoader = new FXMLLoader();
						fxmlLoader.setLocation(location);
						fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
						CNCPutParent = fxmlLoader.load();
						CNCPutViewController cNCPutViewController = fxmlLoader.getController();
						// 中写的初始化方法
						cNCPutViewController.init();
						gridPane.add(CNCPutParent, 1, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					setDisVisible(2, 1, gridPane,CNCPutParent);		
			}
		});
		addMenuItem(prosessVBox,pick, 2, PICK_ICON, "下料", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, pick);
				if (!gridPane.getChildren().contains(CNCPickParent)) {
					try {
						URL location = getClass()
								.getResource("/cn/greatoo/easymill/ui/set/cnc/CNCPickView.fxml");
						fxmlLoader = new FXMLLoader();
						fxmlLoader.setLocation(location);
						fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
						CNCPickParent = fxmlLoader.load();
						CNCPickViewController cNCPickViewController = fxmlLoader.getController();
						// 中写的初始化方法
						cNCPickViewController.init();
						gridPane.add(CNCPickParent, 1, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					setDisVisible(2, 1, gridPane,CNCPickParent);		
			}
		});
		addMenuItem(prosessVBox,aftProcess, 3, AFT_ICON, "加工后", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, aftProcess);
				if (!gridPane.getChildren().contains(CNCFinishedWPParent)) {
					try {
						URL location = getClass()
								.getResource("/cn/greatoo/easymill/ui/set/cnc/CNCFinishedWPView.fxml");
						fxmlLoader = new FXMLLoader();
						fxmlLoader.setLocation(location);
						fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
						
						CNCFinishedWPParent = fxmlLoader.load();
						CNCFinishedWPViewController cNCFinishedWPViewController = fxmlLoader.getController();
						// 中写的初始化方法
						cNCFinishedWPViewController.init();
						gridPane.add(CNCFinishedWPParent, 1, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else
					setDisVisible(2, 1, gridPane,CNCFinishedWPParent);	
			}
		});	
	}
	
	private void openCNCDeviceView() {
		if (!gridPane.getChildren().contains(CNCDeviceParent)) {
			try {
				URL location = getClass()
						.getResource("/cn/greatoo/easymill/ui/set/cnc/CNCDeviceView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				CNCDeviceParent = fxmlLoader.load();
				CNCDeviceViewController cNCDeviceViewController = fxmlLoader.getController();
				// 中写的初始化方法
				cNCDeviceViewController.init();
				gridPane.add(CNCDeviceParent, 1, 2);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else
			setDisVisible(2, 1, gridPane,CNCDeviceParent);		
	}

	@FXML
	public void openDeviceV(MouseEvent event) {
		
	}
	
	@FXML
	public void openPutV(MouseEvent event) {
		
	}
	
	@FXML
	public void openPickV(MouseEvent event) {
		
	}
	
	@FXML
	public void openAftV(MouseEvent event) {
		
	}

	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
	
}
