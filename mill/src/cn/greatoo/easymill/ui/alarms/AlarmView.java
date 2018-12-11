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
	private VBox vBoxMenuItems2;

	private Button cncConnBt;
	private Button roboConnBt;

	private static final int BUTTON_HEIGHT = UIConstants.BUTTON_HEIGHT + 5;
	private static final int WIDTH = BUTTON_HEIGHT * 4;

	private static final String CSS_CLASS_POPUP_BUTTON = "pop-up-btn";
	private static final String CSS_CLASS_POPUP = "popup";

	private static String cncConn = "连接异常  CNC1";
	public static String roboConn = "连接异常  ROBO1";

	
	private AlarmView() {
		this.setVisible(false);
		this.getStyleClass().add(CSS_CLASS_POPUP);
		build();
		this.setTranslateX(0);
		this.setTranslateY(43);
	}

	protected void build() {
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
		
		vBoxMenuItems1.getChildren().add(cncConnBt);
		vBoxMenuItems1.getChildren().add(roboConnBt);		
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
