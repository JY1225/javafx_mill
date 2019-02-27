package cn.greatoo.easymill.ui.set;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.ui.general.NotificationBox;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.ui.main.MainViewController;
import cn.greatoo.easymill.util.TextInputControlListener;
import cn.greatoo.easymill.util.ThreadManager;
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
 * 设置默认菜单页（通用、保存、打开程序、新程序）
 * @author JY
 *
 */
public class SetMenuViewController extends Controller {
	protected SVGPath imagePath;
	protected SVGPath svgPauseLeft;
	private static final String SAVE_ICON = "M 10 0 C 4.4775 -5.7824116e-019 0 4.4787497 0 10 C 0 15.52375 4.4775 20 10 20 C 15.5225 20 20 15.52375 20 10 C 20 4.4787497 15.5225 0 10 0 z M 8.75 5 L 11.25 5 L 11.25 10.03125 L 14.96875 10.03125 L 10 15 L 5 10.03125 L 8.75 10.03125 L 8.75 5 z ";
	private static final String CONFIGURE_ICON = "M 2.5 0.0625 C 1.81125 0.0625 1.25 0.621875 1.25 1.3125 L 1.25 7.5625 L 0 7.5625 L 0 10.0625 L 1.25 10.0625 L 1.25 18.8125 C 1.25 19.503125 1.81125 20.0625 2.5 20.0625 C 3.193125 20.0625 3.75 19.503125 3.75 18.8125 L 3.75 10.0625 L 5 10.0625 L 5 7.5625 L 3.75 7.5625 L 3.75 1.3125 C 3.75 0.621875 3.193125 0.0625 2.5 0.0625 z M 10 0.0625 C 9.3112496 0.0625 8.75 0.621875 8.75 1.3125 L 8.75 12.5625 L 7.5 12.5625 L 7.5 15.0625 L 8.75 15.0625 L 8.75 18.8125 C 8.75 19.503125 9.3112496 20.0625 10 20.0625 C 10.693125 20.0625 11.25 19.503125 11.25 18.8125 L 11.25 15.0625 L 12.5 15.0625 L 12.5 12.5625 L 11.25 12.5625 L 11.25 1.3125 C 11.25 0.621875 10.693125 0.0625 10 0.0625 z M 17.5 0.0625 C 16.81125 0.0625 16.25 0.621875 16.25 1.3125 L 16.25 5.0625 L 15 5.0625 L 15 7.5625 L 16.25 7.5625 L 16.25 18.8125 C 16.25 19.503125 16.81125 20.0625 17.5 20.0625 C 18.193125 20.0625 18.75 19.503125 18.75 18.8125 L 18.75 7.5625 L 20 7.5625 L 20 5.0625 L 18.75 5.0625 L 18.75 1.3125 C 18.75 0.621875 18.193125 0.0625 17.5 0.0625 z";
	private static final String OPEN_ICON = "M 10 0 C 4.4775 0 0 4.4762499 0 10 C 0 15.52125 4.4775 20 10 20 C 15.5225 20 20 15.52125 20 10 C 20 4.4762499 15.5225 0 10 0 z M 10 5 L 14.96875 9.96875 L 11.25 9.96875 L 11.25 15 L 8.75 15 L 8.75 9.96875 L 5 9.96875 L 10 5 z ";
	static final String NEW_ICON = "M 2.5 0 L 2.5 20 L 17.5 20 L 17.5 6.25 L 11.25 0 L 2.5 0 z M 5 2.5 L 10 2.5 L 10 7.5 L 15 7.5 L 15 17.5 L 5 17.5 L 5 2.5 z";
	private static NotificationBox notificationBox;
	@FXML
	private StackPane stackPane;
	@FXML
	private VBox prosessVBox;
	@FXML
	private Button configuer;
	@FXML
	private Button save;
	@FXML
	private Button open;
	@FXML
	private Button new_pro;
	List<Button> bts;
	FXMLLoader fxmlLoader;
	private Parent generalParent;
	private Parent saveParent;
	private Parent openParent;
	private GridPane gridPane;
	private OpenViewController openViewController;
	private SaveViewController saveViewController;
	
	public void init(GridPane gridPane, SetViewController setViewController, Button auto) {
		this.gridPane = gridPane;
		bts = new ArrayList<Button>();
		bts.add(configuer);
		bts.add(save);
		bts.add(open);
		bts.add(new_pro);

		// 默认选择通用按钮
		isClicked(bts, configuer);
		openGeneralView();

		addMenuItem(prosessVBox,configuer, 0, CONFIGURE_ICON, "通用", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, configuer);
				openGeneralView();
			}
		});
		addMenuItem(prosessVBox,save, 1, SAVE_ICON, "保存", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, save);

				if (!gridPane.getChildren().contains(saveParent)) {
					try {
						URL location = getClass()
								.getResource("/cn/greatoo/easymill/ui/set/SaveView.fxml");
						fxmlLoader = new FXMLLoader();
						fxmlLoader.setLocation(location);
						fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
						saveParent = fxmlLoader.load();
						saveViewController = fxmlLoader.getController(); 
						// 中写的初始化方法
						saveViewController.init(generalViewController);
						gridPane.add(saveParent, 1, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					saveViewController.init(generalViewController);
					setDisVisible(2, 1, gridPane, saveParent);
				}

			}
		});
		addMenuItem(prosessVBox,open, 2, OPEN_ICON, "打开程序", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {
				isClicked(bts, open);

				if (!gridPane.getChildren().contains(openParent)) {
					try {
						URL location = getClass()
								.getResource("/cn/greatoo/easymill/ui/set/OpenView.fxml");
						fxmlLoader = new FXMLLoader();
						fxmlLoader.setLocation(location);
						fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
						openParent = fxmlLoader.load();
						openViewController = fxmlLoader.getController(); // 获取Controller的实例对?
						openViewController.init(setViewController,auto);
						gridPane.add(openParent, 1, 2);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} else {
					openViewController.init(setViewController,auto);
					setDisVisible(2, 1, gridPane, openParent);
				}
			}
		});
		addMenuItem(prosessVBox,new_pro, 3, NEW_ICON, "新程序", true, new EventHandler<ActionEvent>() {
			@Override
			public void handle(final ActionEvent event) {			
				newProcess(MainViewController.parentStackPane,generalViewController,saveViewController);
				
			}
		});
	}
	
	GeneralViewController generalViewController;
	protected void openGeneralView() {
		if (!gridPane.getChildren().contains(generalParent)) {
			try {
				URL location = getClass().getResource("/cn/greatoo/easymill/ui/set/GeneralView.fxml");
				fxmlLoader = new FXMLLoader();
				fxmlLoader.setLocation(location);
				fxmlLoader.setBuilderFactory(new JavaFXBuilderFactory());
				generalParent = fxmlLoader.load();
				generalViewController = fxmlLoader.getController(); 
				// 中写的初始化方法
				generalViewController.init();
				gridPane.add(generalParent, 1, 2);
				isClicked(bts, configuer);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			generalViewController.init();
			setDisVisible(2, 1, gridPane, generalParent);
		}
	}
	@FXML
	public void openConfig(MouseEvent event) {
		
	}
	
	@FXML
	public void openSave(MouseEvent event) {
		
	}
	
	@FXML
	public void openProcess(MouseEvent event) {
		
	}
	
	@FXML
	public void openNew(MouseEvent event) {
		
	}
	@Override
	public void setMessege(String mess) {
		// TODO Auto-generated method stub
		
	}
	
	public static NotificationBox getnotificationBox() {
		return notificationBox;
	}
	@Override
	public void setTextFieldListener(TextInputControlListener listener) {
		// TODO Auto-generated method stub
		
	}
}
