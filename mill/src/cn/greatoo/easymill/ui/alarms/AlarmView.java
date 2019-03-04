package cn.greatoo.easymill.ui.alarms;

import cn.greatoo.easymill.cnc.CNCMachine;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.robot.FanucRobot;
import cn.greatoo.easymill.util.UIConstants;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class AlarmView extends StackPane {
	public static AlarmView INSTANCE = null;
	private VBox vBoxMenuItems1;

	public  Button roboConnBt, cncConnBt, resetRobot, resetCNC;

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

		resetRobot = new Button();
		resetRobot.setGraphic(new Text("重置 ROBOT"));
		resetRobot.setPrefSize(WIDTH * 1.38, BUTTON_HEIGHT);
		resetRobot.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);	
		resetRobot.getStyleClass().add("pop-up-first");
		resetRobot.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {//61
				try {
					if(FanucRobot.getInstance(null,0,null) != null && FanucRobot.getInstance(null,0,null).isConnected()) {
						FanucRobot.getInstance(null,0,null).reset();
					}
				} catch (AbstractCommunicationException | InterruptedException e) {
					
				}
			}
		});
		
		resetCNC = new Button();
		resetCNC.setGraphic(new Text("重置 CNC"));
		resetCNC.setPrefSize(WIDTH * 1.38, BUTTON_HEIGHT);
		resetCNC.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);	
		resetCNC.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent arg0) {//WW19;01;1024;
				try {
					if(CNCMachine.getInstance(null,null,null) != null && CNCMachine.getInstance(null,null,null).isConnected()) {
						CNCMachine.getInstance(null,null,null).reset();
					}
				} catch (AbstractCommunicationException | InterruptedException e) {
					
				}
			}
		}); 
		
		roboConnBt = new Button();
		roboConnBt.setGraphic(new Text(roboConn));
		roboConnBt.setPrefSize(WIDTH * 1.38, BUTTON_HEIGHT);
		roboConnBt.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);	
		
		cncConnBt = new Button();
		cncConnBt.setGraphic(new Text(cncConn));
		cncConnBt.setPrefSize(WIDTH * 1.38, BUTTON_HEIGHT);
		cncConnBt.getStyleClass().add(CSS_CLASS_POPUP_BUTTON);
				
		cncConnBt.setDisable(true);
		roboConnBt.setDisable(true);
		
		vBoxMenuItems1.getChildren().add(resetRobot);
		vBoxMenuItems1.getChildren().add(resetCNC);
		vBoxMenuItems1.getChildren().add(roboConnBt);
		vBoxMenuItems1.getChildren().add(cncConnBt);
		
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
