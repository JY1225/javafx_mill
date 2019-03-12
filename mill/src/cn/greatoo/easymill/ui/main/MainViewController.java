package cn.greatoo.easymill.ui.main;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.external.communication.socket.StatusChangeThread;
import cn.greatoo.easymill.ui.alarms.AlarmListenThread;
import cn.greatoo.easymill.ui.alarms.AlarmView;
import cn.greatoo.easymill.ui.auto.AutoViewController;
import cn.greatoo.easymill.ui.configure.ConfigureMainViewController;
import cn.greatoo.easymill.ui.set.SetViewController;
import cn.greatoo.easymill.ui.teach.TeachMainViewController;
import cn.greatoo.easymill.util.ButtonStyleChangingThread;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.JavaFXBuilderFactory;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import cn.greatoo.easymill.util.SizeManager;

public class MainViewController extends Controller {

	private SVGPath alarmsShape;
	private SVGPath adminShape;
	private SVGPath robotShape;
	private static final String CSS_CLASS_ALARMS_PRESENT = "alarms-present";
	private static final String CSS_CLASS_HEADER_BUTTON_SHAPE = "header-button-shape";
	private String alarmsPath = "m 21.845957,-0.0414886 c -0.32119,0.0484 -0.624,0.26975 -0.75,0.59375 l -5.09375,13.2812496 0,-8.0624996 c 0,-0.071 -0.0143,-0.1185 -0.0312,-0.1875 -0.061,-0.301 -0.2535,-0.5695 -0.5625,-0.6875 -0.48,-0.184 -1.03575,0.0522 -1.21875,0.53125 l -2.125,5.0937496 -7.3437507,0.375 0,0.84375 c -0.24,0 -0.4375,-0.1945 -0.4375,-0.4375 0,-0.239 0.1965,-0.40625 0.4375,-0.40625 l -0.28125,0 c -0.241,0 -0.4375,0.1985 -0.4375,0.4375 0,0.243 0.1975,0.4375 0.4375,0.4375 l 8.1250007,0.5625 c 0.425,0.06 0.844,-0.1465 1,-0.5625 l 0.59375,-1.375 0,8.4375 c 0,0.515 0.39225,0.9375 0.90625,0.9375 0.0529,0 0.10571,-0.0225 0.15625,-0.0312 -0.0419,-0.0129 -0.0856,-0.0204 -0.125,-0.0312 l 0.3125,0 c 0.1025,-0.0395 0.20025,-0.0873 0.28125,-0.15625 0.016,-0.012 0.0192,-0.0495 0.0312,-0.0625 0.092,-0.095 0.198,-0.21375 0.25,-0.34375 l 5.09375,-13.3124996 0,8.0937496 c 0,0.492 0.395,0.9015 0.875,0.9375 0.418,0.058 0.842,-0.17875 1,-0.59375 l 2.125,-5.0937496 7.3125,0.21875 c 0.38742,0 0.729515,-0.21494 0.906255,-0.53125 l 0,-1 c -0.17774,-0.31248 -0.521515,-0.53057 -0.906255,-0.53125 l -7.875,0.0312 c -0.399,-0.03 -0.7855,0.20175 -0.9375,0.59375 l -0.625,1.46875 0,-8.53125 c 0,-0.459 -0.34525,-0.83125 -0.78125,-0.90625 -0.1105,-0.023 -0.20544,-0.0474 -0.3125,-0.0312 z";
	private String adminPath = "M 19.71875 -0.21875 C 16.9575 -0.21875 14.71875 2.02125 14.71875 4.78125 C 14.71875 5.55125 14.90625 6.284375 15.21875 6.9375 L 5.1875 16.9375 C 4.8875 17.240625 4.71875 17.66125 4.71875 18.125 C 4.71875 19.043125 5.451875 19.78125 6.375 19.78125 C 6.83625 19.78125 7.264375 19.615 7.5625 19.3125 L 17.5625 9.28125 C 18.211875 9.59125 18.95 9.78125 19.71875 9.78125 C 22.48 9.78125 24.71875 7.54125 24.71875 4.78125 C 24.71875 4.265 24.615625 3.78125 24.46875 3.3125 L 21.3125 6.46875 L 18.03125 6.46875 L 18.03125 3.125 L 21.15625 0.03125 C 20.699375 -0.106875 20.219375 -0.21875 19.71875 -0.21875 z";
	private String robotPath = "M 15.75 0.09375 C 15.69501 0.0951683 15.639844 0.11453532 15.59375 0.15625 C 15.479736 0.25943171 15.372009 0.27227839 15.34375 0.1875 C 15.33112 0.1496086 15.26277 0.1632054 15.15625 0.21875 C 15.06545 0.2660974 14.941067 0.3123338 14.875 0.3125 C 14.694387 0.31299861 14.59375 0.378668 14.59375 0.46875 C 14.59375 0.5324575 14.53469 0.5286338 14.34375 0.5 C 14.158641 0.472241 14.043093 0.4767454 13.875 0.5625 C 13.578611 0.71370609 13.510539 0.82060852 13.4375 1.1875 C 13.38307 1.4609382 13.36753 1.5260208 13.15625 1.6875 C 12.941836 1.8513737 12.900811 1.8592855 12.84375 1.78125 C 12.75166 1.6553093 12.720882 1.661879 12.53125 1.9375 C 12.361673 2.1839726 11.71999 2.625 11.53125 2.625 C 11.4832 2.625 11.393407 2.705464 11.34375 2.78125 C 11.250926 2.922917 10.896657 3.125 10.75 3.125 C 10.70463 3.125 10.579301 3.208531 10.5 3.3125 C 10.357995 3.4986784 10.023309 3.6875 9.8125 3.6875 C 9.753247 3.6875 9.688865 3.720308 9.65625 3.78125 C 9.5861658 3.912204 9.4308378 3.9256429 9.28125 3.8125 C 9.1290517 3.6973831 8.8374165 3.7195439 8.71875 3.84375 C 8.6240021 3.9429212 8.4824106 3.9961283 8.03125 4.0625 C 7.8696611 4.0862723 7.8244439 4.1260931 7.78125 4.25 C 7.7512212 4.3361396 7.6706287 4.4496271 7.59375 4.5 C 7.4753755 4.5775618 7.4556543 4.6021112 7.5 4.71875 C 7.528801 4.794501 7.5625 4.8777625 7.5625 4.90625 C 7.5625 4.993767 7.3986054 4.97302 7.34375 4.875 C 7.298185 4.793579 7.250332 4.778892 7.09375 4.84375 L 6.9375 4.90625 L 7.21875 5.1875 C 7.3739838 5.3302136 7.5 5.4803442 7.5 5.53125 C 7.5 5.5821553 7.4352813 5.6630863 7.34375 5.71875 C 7.1838769 5.8159744 7.1524632 5.8179808 7.25 5.90625 C 7.3058421 5.9567868 7.4178253 6 7.46875 6 C 7.6272813 6 7.544001 6.2073866 7.34375 6.34375 C 7.1925062 6.4467411 7.1502809 6.5052429 7.125 6.6875 C 7.074873 7.0488834 7.150712 7.2500031 7.4375 7.4375 C 7.5773216 7.528913 7.7326445 7.6822195 7.78125 7.78125 C 7.85071 7.9227702 7.9107979 7.9418435 8.03125 7.9375 L 8.1875 7.9375 L 8.0625 7.90625 C 7.9564163 7.8769868 7.9197536 7.8265133 7.90625 7.6875 C 7.896553 7.5876762 7.9012191 7.4499658 7.9375 7.40625 C 8.01382 7.31429 8.21875 7.320211 8.21875 7.40625 C 8.21875 7.438275 8.299602 7.486689 8.40625 7.53125 C 8.5128982 7.57581 8.59375 7.6457565 8.59375 7.6875 C 8.59375 7.729243 8.660389 7.7972385 8.71875 7.8125 C 8.77711 7.827762 8.8335506 7.897797 8.84375 7.96875 C 8.857252 8.062681 8.901146 8.1007929 9.0625 8.15625 C 9.5968047 8.3398893 9.636641 8.359868 9.875 8.84375 C 10.141105 9.3839588 10.216894 9.6577802 10.21875 10.15625 C 10.219703 10.41118 10.25846 10.686475 10.375 11.03125 C 10.46641 11.301685 10.53125 11.604566 10.53125 11.71875 C 10.53125 11.832934 10.56159 12.009161 10.59375 12.09375 C 10.742446 12.484851 10.448154 12.703009 9.78125 12.71875 C 9.3843248 12.72815 9.3216705 12.71435 8.90625 12.53125 C 8.1421093 12.194404 7.7492349 12.293558 7.5 12.875 C 7.3655103 13.188752 7.3591846 13.214739 7.53125 13.6875 C 7.628598 13.954971 7.7103098 14.106449 7.78125 14.125 C 7.9614201 14.17212 7.9094453 14.668882 7.71875 14.71875 C 7.647825 14.7373 7.59375 14.80626 7.59375 14.90625 C 7.59375 15.075995 7.7974749 15.375 7.90625 15.375 C 8.0580777 15.375 8.2172055 15.733929 8.125 15.875 C 8.075686 15.95045 7.4201735 16.09375 7.15625 16.09375 C 6.9574849 16.09375 6.9529724 16.1658 7.15625 16.25 C 7.2998888 16.3095 7.3267927 16.34232 7.34375 16.59375 C 7.363753 16.890353 7.297127 17.032818 7.125 17.03125 C 7.0027291 17.03015 6.9189734 16.96293 6.875 16.84375 C 6.8270981 16.713919 6.7545744 16.738007 6.71875 16.875 C 6.7009018 16.943252 6.6010454 16.979791 6.5 17 L 6.34375 17.0625 L 6.3125 17.5625 C 6.2924524 17.841285 6.2675424 18.204273 6.25 18.375 L 6.21875 18.6875 L 6.46875 18.75 C 6.6075555 18.78495 6.7692752 18.8125 6.84375 18.8125 C 7.0263456 18.8125 8.4858199 19.168386 8.625 19.25 C 8.685942 19.28574 8.8628809 19.368332 9 19.40625 C 9.3866787 19.51318 9.7804808 19.49911 9.96875 19.375 C 10.057999 19.31617 10.268525 19.208504 10.46875 19.15625 C 10.668974 19.104 10.875065 19.037448 10.90625 19 C 10.93744 18.96255 11.082345 18.92103 11.21875 18.90625 C 11.355155 18.89147 11.58241 18.84696 11.75 18.8125 C 11.91759 18.77804 12.166984 18.73545 12.28125 18.71875 C 12.456147 18.69319 12.46875 18.6761 12.46875 18.5625 C 12.46875 18.46013 12.45935 18.430975 12.34375 18.40625 C 12.03979 18.34123 11.943462 18.272912 11.875 18.0625 C 11.83716 17.946198 11.760121 17.778854 11.71875 17.71875 C 11.67738 17.65865 11.65625 17.566007 11.65625 17.5 C 11.65625 17.40676 11.61905 17.371947 11.46875 17.34375 C 11.320012 17.31585 11.247049 17.269 11.21875 17.15625 C 11.1631 16.934525 11.28787 16.546382 11.46875 16.375 C 11.55663 16.29174 11.669043 16.151902 11.6875 16.09375 C 11.71937 15.993349 12.18264 15.75 12.34375 15.75 C 12.38533 15.75 12.4326 15.72365 12.46875 15.6875 C 12.51945 15.6368 12.50446 15.60523 12.4375 15.53125 C 12.306109 15.386064 12.37826 15.205221 12.5625 15.15625 C 12.845095 15.08114 13.018641 14.84268 13.28125 14.21875 C 13.39649 13.944951 13.420946 13.84092 13.375 13.75 C 13.134745 13.274575 12.6875 11.450686 12.6875 10.90625 C 12.6875 10.778361 12.62503 10.459185 12.5 10.15625 C 12.382204 9.8708506 12.224976 9.4593191 12.15625 9.25 C 12.087525 9.0406803 11.979323 8.798904 11.9375 8.71875 C 11.895677 8.6385954 11.875166 8.5567846 11.875 8.53125 C 11.874834 8.5057154 11.796612 8.2794145 11.6875 8.03125 C 11.523282 7.6577442 11.474593 7.5509356 11.5 7.34375 C 11.516881 7.2060918 11.547509 6.9648546 11.5625 6.8125 L 11.59375 6.53125 L 11.53125 6.8125 C 11.47923 7.0258861 11.43425 7.0808154 11.34375 7.09375 C 11.25064 7.107057 11.169162 7.039415 10.96875 6.71875 C 10.82967 6.4962182 10.668157 6.2375375 10.59375 6.15625 C 10.519343 6.0749625 10.41144 5.9018997 10.375 5.75 C 10.332711 5.573718 10.255808 5.4324569 10.15625 5.34375 C 10.022703 5.2247589 9.998414 5.1583673 10.03125 5 C 10.06538 4.8353756 10.11116 4.8185105 10.28125 4.8125 C 10.386362 4.80879 10.497056 4.81641 10.53125 4.8125 C 10.56544 4.8086 10.60637 4.7102325 10.625 4.59375 C 10.65552 4.4028736 10.70595 4.3455616 11.09375 4.09375 C 11.330403 3.9400824 11.626078 3.7503028 11.75 3.6875 C 11.873922 3.6246978 11.991469 3.5479335 12.03125 3.5 C 12.07103 3.452066 12.132437 3.40625 12.15625 3.40625 C 12.18006 3.40625 12.34257 3.325398 12.53125 3.21875 C 12.789388 3.0728414 12.959108 3.03125 13.15625 3.03125 C 13.334028 3.03125 13.438006 3.003288 13.5 2.9375 C 13.74092 2.681833 13.919452 2.4321004 13.96875 2.28125 C 14.01244 2.1475741 14.05526 2.089074 14.21875 2.0625 C 14.330414 2.04435 14.475427 1.988019 14.53125 1.9375 C 14.60182 1.873632 14.70834 1.84375 14.9375 1.84375 C 15.27464 1.84375 15.473215 1.771736 15.625 1.5625 C 15.6816 1.484477 15.732958 1.473353 15.875 1.5 C 16.052856 1.533366 16.071887 1.539759 16.1875 1.28125 C 16.334744 0.95201392 16.326579 0.64835672 16.125 0.34375 C 16.010331 0.17047355 15.870989 0.09062867 15.75 0.09375 z M 19.71875 3.375 L 19.71875 17.75 L 22.6875 17.75 L 22.6875 12.125 L 25.84375 12.125 C 26.781241 12.125006 27.381501 12.287766 27.6875 12.59375 C 27.99348 12.899744 28.174469 13.408859 28.1875 14.125 L 28.1875 15.21875 C 28.20051 16.143231 28.329417 16.975261 28.53125 17.75 L 31.8125 17.75 L 31.8125 17.375 C 31.526028 17.270834 31.359362 17.037761 31.28125 16.71875 C 31.20962 16.393231 31.18749 15.953127 31.1875 15.40625 L 31.1875 14.84375 C 31.187487 13.652348 31.07681 12.778651 30.875 12.21875 C 30.673165 11.65886 30.15103 11.225267 29.34375 10.90625 C 30.834623 10.274747 31.593737 9.1523524 31.59375 7.53125 C 31.593737 6.2617303 31.17056 5.2422 30.34375 4.5 C 29.516916 3.7513161 28.411448 3.3750144 27.03125 3.375 L 19.71875 3.375 z M 9.34375 5.8125 C 9.390354 5.80254 9.4375 5.873831 9.4375 5.96875 C 9.4375 6.027894 9.406428 6.0625 9.375 6.0625 C 9.300223 6.0625 9.2347109 5.9190514 9.28125 5.84375 C 9.29431 5.822619 9.328215 5.815819 9.34375 5.8125 z M 22.6875 5.90625 L 26.46875 5.90625 C 27.87499 5.9062619 28.56249 6.5364696 28.5625 7.8125 C 28.56249 9.0950607 27.78124 9.750008 26.21875 9.75 L 22.6875 9.75 L 22.6875 5.90625 z M 9.625 6.1875 C 9.642724 6.17759 9.664647 6.1875 9.6875 6.1875 C 9.733206 6.1875 9.78125 6.204294 9.78125 6.25 C 9.78125 6.295706 9.733206 6.34375 9.6875 6.34375 C 9.641794 6.34375 9.59375 6.295706 9.59375 6.25 C 9.59375 6.227147 9.607276 6.197412 9.625 6.1875 z M 7.8125 13.4375 C 7.81702 13.436 7.839477 13.437749 7.84375 13.4375 C 7.873657 13.4357 7.875 13.45778 7.875 13.5 C 7.875 13.54824 7.829495 13.59375 7.78125 13.59375 C 7.683414 13.59375 7.709773 13.471742 7.8125 13.4375 z";

	private final static Logger LOGGER = LogManager.getLogger(MainViewController.class.getName());
	@FXML
	private StackPane stackPane;
	@FXML
	private GridPane gridPane;
	@FXML
	private ToolBar toolBarMenu;
	@FXML
	private Button alarm;
	@FXML
	private Button robot_speed;
	@FXML
	private Button set;
	@FXML
	private Button teach;
	@FXML
	private Button auto;
	@FXML
	private Button config;
	@FXML
	private HBox buttonBar;
	@FXML
	private HBox hBoxProcessMenuItems;
	public static StackPane parentStackPane;
	public static ButtonStyleChangingThread changingThread;
	public static AlarmListenThread alarmListenThread;
	private static StatusChangeThread robotStatusChangeThread;
	private List<Button> bts;
	private Parent setParent;
	private Parent teachParent;
	private Parent autoParent;
	private Parent configureParent;
	FXMLLoader fxmlLoader;
	SetViewController setViewController;
	Program program = null;


	public static boolean isCNCConn = false;
	public static boolean isRobotConn = false;

	public void Init() {
//		gridPane.setPrefSize(SizeManager.WIDTH, SizeManager.HEIGHT);
//		gridPane.setMinSize(SizeManager.WIDTH, SizeManager.HEIGHT);
//		gridPane.setMaxSize(SizeManager.WIDTH, SizeManager.HEIGHT);
		
		String programName = DBHandler.getInstance().getProgramName();
		if (programName != null) {
			program = DBHandler.getInstance().getProgramBuffer().get(programName);
		}
		stackPane.getChildren().add(RobotPopUpView.getInstance());
		stackPane.getChildren().add(AlarmView.getInstance());
		
		hBoxProcessMenuItems.setSpacing(-13);
		hBoxProcessMenuItems.setAlignment(Pos.CENTER);
		buttonBar.setAlignment(Pos.CENTER);		
		HBox.setHgrow(buttonBar, Priority.ALWAYS);
		HBox.setHgrow(hBoxProcessMenuItems, Priority.ALWAYS);
		buttonBar.setPadding(new Insets(0, 0, 0, 0));
		
		parentStackPane = stackPane;
		set.setGraphic(new Text("设置"));
		teach.setGraphic(new Text("示教"));
		auto.setGraphic(new Text("自动化"));
		bts = new ArrayList<Button>();
		bts.add(set);
		bts.add(teach);
		bts.add(auto);
		bts.add(config);

		alarmsShape = new SVGPath();
		alarmsShape.setContent(alarmsPath);
		alarmsShape.getStyleClass().add(CSS_CLASS_HEADER_BUTTON_SHAPE);
		alarm.setGraphic(alarmsShape);

		robotShape = new SVGPath();
		robotShape.setContent(robotPath);
		robotShape.getStyleClass().add(CSS_CLASS_HEADER_BUTTON_SHAPE);
		robot_speed.setGraphic(robotShape);

		adminShape = new SVGPath();
		adminShape.setContent(adminPath);
		adminShape.getStyleClass().add(CSS_CLASS_HEADER_BUTTON_SHAPE);
		config.setGraphic(adminShape);

		// 默认打开设置界面
		openSet();
		//setPrefWidthProperty(stackPane);
		startThrad();
		if(program != null && !program.isHasTeach()) {
			auto.setDisable(true);
		}
	}

	boolean running = true;
	private void startThrad() {
		// 报警监听
		changingThread = new ButtonStyleChangingThread(alarm, "", CSS_CLASS_ALARMS_PRESENT, 500);
		//ThreadManager.submit(changingThread);
		new Thread(changingThread).start();
		robotStatusChangeThread = new StatusChangeThread(alarm, 250, changingThread);
		new Thread(robotStatusChangeThread).start();
	}

	@FXML
	public void openSet() {
		isClicked(bts, set);
		RobotPopUpView.getInstance().setVisible(false);
		isSpeekViewOpen = true;
		AlarmView.getInstance().setVisible(false);
		isAlarmViewOpen = true;
		if (!gridPane.getChildren().contains(setParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/set/SetView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				setParent = fxmlLoader.load();
				setViewController = fxmlLoader.getController();
				// 中写的初始化方法
				setViewController.init(auto);
				gridPane.add(setParent, 0, 1, 2, 1);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			setViewController.init(auto);
			setDisVisible(1, gridPane, setParent);
		}
	}

	TeachMainViewController teachMainViewController;

	@FXML // 示教
	public void teachClick() {
		isClicked(bts, teach);
		RobotPopUpView.getInstance().setVisible(false);
		isSpeekViewOpen = true;
		AlarmView.getInstance().setVisible(false);
		isAlarmViewOpen = true;
		if (!gridPane.getChildren().contains(teachParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/teach/TeachMainView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				teachParent = fxmlLoader.load();
				teachMainViewController = fxmlLoader.getController();
				// 中写的初始化方法
				teachMainViewController.init(bts,auto);
				gridPane.add(teachParent, 0, 1, 2, 1);
				setDisVisible(1, gridPane, teachParent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			teachMainViewController.init(bts,auto);
			setDisVisible(1, gridPane, teachParent);
		}

	}

	public static AutoViewController autoViewController;

	@FXML // 自动化
	public void autoClick() {
		isClicked(bts, auto);
		RobotPopUpView.getInstance().setVisible(false);
		isSpeekViewOpen = true;
		AlarmView.getInstance().setVisible(false);
		isAlarmViewOpen = true;
		if (!gridPane.getChildren().contains(autoParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/auto/AutoView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				autoParent = fxmlLoader.load();
				autoViewController = fxmlLoader.getController();
				// 中写的初始化方法
				autoViewController.init(bts);
				gridPane.add(autoParent, 0, 1, 2, 1);
				setDisVisible(1, gridPane, autoParent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			autoViewController.init(bts);
			setDisVisible(1, gridPane, autoParent);
		}
	}

	ConfigureMainViewController configureMainViewController;

	@FXML // 配置
	public void configClick() {
		isClicked(bts, config);
		RobotPopUpView.getInstance().setVisible(false);
		isSpeekViewOpen = true;
		AlarmView.getInstance().setVisible(false);
		isAlarmViewOpen = true;
		if (!gridPane.getChildren().contains(configureParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/configure/ConfigureMainView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				configureParent = fxmlLoader.load();
				configureMainViewController = fxmlLoader.getController();
				// 中写的初始化方法
				configureMainViewController.init();
				gridPane.add(configureParent, 0, 1, 2, 1);
				setDisVisible(1, gridPane, configureParent);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			configureMainViewController.init();
			setDisVisible(1, gridPane, configureParent);
		}

	}

	boolean isAlarmViewOpen = true;

	@FXML // 机床，机器人连接提示
	public void alarmClick() {
		RobotPopUpView.getInstance().setVisible(false);
		isSpeekViewOpen = true;
		if (isAlarmViewOpen) {
			AlarmView.getInstance().setVisible(true);
			isAlarmViewOpen = false;
		} else {
			AlarmView.getInstance().setVisible(false);
			isAlarmViewOpen = true;
		}
	}

	boolean isSpeekViewOpen = true;

	@FXML // 机器人速度
	public void speedOnClick() {
		AlarmView.getInstance().setVisible(false);
		isAlarmViewOpen = true;
		if (isSpeekViewOpen) {
			RobotPopUpView.getInstance().setVisible(true);
			isSpeekViewOpen = false;
		} else {
			RobotPopUpView.getInstance().setVisible(false);
			isSpeekViewOpen = true;
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
