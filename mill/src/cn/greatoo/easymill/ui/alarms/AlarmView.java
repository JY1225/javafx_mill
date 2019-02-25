package cn.greatoo.easymill.ui.alarms;

import cn.greatoo.easymill.util.UIConstants;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AlarmView extends StackPane {
	public static AlarmView INSTANCE = null;
	private VBox vBoxMenuItems1;

	public  Button cncConnBt;
	public  Button roboConnBt;

	private static final int BUTTON_HEIGHT = UIConstants.BUTTON_HEIGHT + 5;
	private static final int WIDTH = BUTTON_HEIGHT * 4;

	private static final String CSS_CLASS_POPUP_BUTTON = "pop-up-btn";
	//private static final String CSS_CLASS_POPUP = "popup";

	public static String cncConn = "连接异常  CNC";
	public static String roboConn = "连接异常  ROBOT";
	
	public AlarmView() {
		this.setVisible(false);
		build();
		this.setTranslateX(0);
		this.setTranslateY(43);
	}

	public void build() {
		HBox hbox = new HBox();

		vBoxMenuItems1 = new VBox();

		hbox.setSpacing(-1);
		hbox.getChildren().addAll(vBoxMenuItems1);

		this.getChildren().add(hbox);

		cncConnBt = new Button();
		cncConnBt.setGraphic(new Text(cncConn));
		cncConnBt.setPrefSize(WIDTH * 1.38, BUTTON_HEIGHT);
		cncConnBt.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
		cncConnBt.getStyleClass().add("pop-up-first");

		roboConnBt = new Button();
		roboConnBt.setGraphic(new Text(roboConn));
		roboConnBt.setPrefSize(WIDTH * 1.38, BUTTON_HEIGHT);
		roboConnBt.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);		
		cncConnBt.setDisable(true);
		roboConnBt.setDisable(true);
		vBoxMenuItems1.getChildren().add(cncConnBt);
		vBoxMenuItems1.getChildren().add(roboConnBt);
		
	}

	public void isCNCConn(boolean conn) {
		if(conn) {
			cncConn = "连接正常  CNC";
		}else {
			cncConn = "连接异常  CNC";
		}
		cncConnBt.setGraphic(new Text(cncConn));
	}
	public void isRoboConn(boolean conn) {
		if(conn) {
			roboConn = "连接正常  ROBOT";
		}else {
			roboConn = "连接异常  ROBOT";
		}	
		roboConnBt.setGraphic(new Text(roboConn));
	}
	/**
	 * 获取实例
	 * 
	 * @return AlarmView
	 */
	public static AlarmView getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new AlarmView();
		}
		return INSTANCE;
	}

}
