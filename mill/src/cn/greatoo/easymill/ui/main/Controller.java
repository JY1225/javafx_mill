package cn.greatoo.easymill.ui.main;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.process.StatusChangedEvent;
import cn.greatoo.easymill.ui.general.NotificationBox;
import cn.greatoo.easymill.ui.general.dialog.AbstractDialogView;
import cn.greatoo.easymill.ui.general.dialog.ConfirmationDialogPresenter;
import cn.greatoo.easymill.ui.general.dialog.ConfirmationDialogView;
import cn.greatoo.easymill.util.TextInputControlListener;
import cn.greatoo.easymill.util.ThreadManager;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;

public abstract class Controller extends Pane implements TextInputControlListener{
	private static String arrowRightPath = "M 1.6875 0 L 0 1.65625 L 3.375 5 L 0.0625 8.3125 L 1.71875 10 L 6.65625 5.0625 L 6.5625 4.96875 L 6.625 4.90625 L 1.6875 0 z ";
	protected SVGPath imagePath;
	protected SVGPath svgPauseLeft;
	private static NotificationBox notificationBox = new NotificationBox();
	private static final int BUTTON_WIDTH = 209;
	private static final int BUTTON_HEIGHT = 43;

	private static final int ICON_WIDTH = 20;
	private static final int ICON_MARGIN = 6;
	private static final int ICON_ARROW_WIDTH = 10;
	
	private static final String CSS_CLASS_MENU_ICON = "menu-icon";
	private static final String CSS_CLASS_LEFT_MENU_ITEM_LABEL = "left-menu-item-label";
	private static final String CSS_CLASS_LEFT_MENU_ITEM_PANEL = "left-menu-item-panel";
	private static final String CSS_CLASS_LEFT_MENU_BUTTON = "left-menu-button";
	
	private static final String CSS_CLASS_FORM_BUTTON_ICON = "form-button-icon";
	protected static final String CSS_CLASS_FORM_BUTTON_LABEL = "form-button-label";
	private static final String CSS_CLASS_FORM_BUTTON_PANEL = "form-button-panel";
	
	protected static final String CSS_CLASS_FORM_BUTTON = "form-button";
	protected static final String CSS_CLASS_SAVE_BUTTON = "save-btn";
	protected static final String CSS_CLASS_DELETE_BUTTON = "delete-btn";
	static final String NEW_ICON = "M 2.5 0 L 2.5 20 L 17.5 20 L 17.5 6.25 L 11.25 0 L 2.5 0 z M 5 2.5 L 10 2.5 L 10 7.5 L 15 7.5 L 15 17.5 L 5 17.5 L 5 2.5 z";
	 
	private static final String CSS_CLASS_BTN_SELECTED = "selected";
	
	protected void buildAlarmHBox(GridPane grid, int x, int y, int col, int row) {
		grid.getChildren().remove(notificationBox);
		grid.add(notificationBox, x, y, col, row);
		hideNotification();
	}
	
	public void showNotification(final String notification, NotificationBox.Type type) {
		notificationBox.showNotification(notification, type);
		notificationBox.setVisible(true);
		notificationBox.setManaged(true);
	}
	
	public void hideNotification() {
		notificationBox.setVisible(false);
		notificationBox.setManaged(false);
	}
	
	public void setTextFieldListener(final TextInputControlListener listener) {
		this.setTextFieldListener(listener);
	}

	// 按钮被选中，背景变颜色
		public void isClicked(List<Button> bts,Button button) {
			for (int i = 0; i < bts.size(); i++) {
				if (bts.get(i) == button ) {
					if(!button.getStyleClass().contains(CSS_CLASS_BTN_SELECTED)) {
						button.getStyleClass().add(CSS_CLASS_BTN_SELECTED);
						
					}
					
				} else {
					if (bts.get(i).getStyleClass().contains(CSS_CLASS_BTN_SELECTED)) {
						bts.get(i).getStyleClass().remove(CSS_CLASS_BTN_SELECTED);
					}
				}
			}
		}
		// 按钮连续被选中，背景变颜色
		public void isDisSelect(List<Button> bts,Button button) {
			for (int i = 0; i < bts.size(); i++) {
				if (bts.get(i) == button ) {
					bts.get(i).getStyleClass().remove(CSS_CLASS_BTN_SELECTED);
				}
			}
		}
		public void setDisable(List<Button> bts,boolean isDisable) {			
			for (int i = 0; i < bts.size(); i++) {
				if(isDisable) {
					bts.get(i).setDisable(true);				
				}else {
					bts.get(i).setDisable(false);
				}
			}
		}
		public static void showNotificationOverlay(StackPane stackPane,final String title, final String message) {
			ThreadManager.submit(new Thread() {
				@Override
				public void run() {
					askConfirmation(stackPane,title, message);				
				}
			});
		}
		
		//private ProcessFlow activeProcessFlow;
		
		@SuppressWarnings("unused")
		public void newProcess(StackPane stackPane) {		
			if(false/*activeProcessFlow.hasChangesSinceLastSave()*/) {
				ThreadManager.submit(new Thread() {
					@Override
					public void run() {
						if (askConfirmation(stackPane,"未保存数据", "当前程序包含未保存数据，如果继续，未保存的数据将会丢失。")) {
							createNewProcess();
						}
					}
				});
			} else {
				ThreadManager.submit(new Thread() {
					@Override
					public void run() {
						if (askConfirmation(stackPane,"新程序", "是否要创建新程序")) {
							createNewProcess();
						}
					}
				});
			}
		}
		
		boolean isConfirm = false;
		public boolean askConfirm(StackPane stackPane,String title, String mess) {	
			ThreadManager.submit(new Thread() {
				@Override
				public void run() {
					isConfirm =  askConfirmation(stackPane,title,mess);
				}
			});
			return isConfirm;
			
		}
		public void createNewProcess() {
			Platform.runLater(new Thread() {
				@Override
				public void run() {
					DBHandler.getInstance().setProgramName(null);
				}
			});
		}

		
		@SuppressWarnings("static-access")
		public void setDisVisible (final int row, GridPane gridPane,Node parent) {
		    ObservableList<Node> childrens = gridPane.getChildren();
		    
		    for (Node node : childrens) {
		    	try {
		    	int r = gridPane.getRowIndex(node);				
		        if(r == row ) {
		        	if(node != parent) {
						node.setVisible(false);
					}else
						node.setVisible(true);
		        }		   
		    }catch (Exception e) {
		    	
		    }
		    }
		}
		
		@SuppressWarnings("static-access")
		public void setDisVisible (final int row, final int column, GridPane gridPane,Node parent) {
		    ObservableList<Node> childrens = gridPane.getChildren();
		    
		    for (Node node : childrens) {
		    	try {
		    	int r = gridPane.getRowIndex(node);
				int c = gridPane.getColumnIndex(node);
		        if(r == row && c == column) {
		        	if(node != parent) {
						node.setVisible(false);
					}else
						node.setVisible(true);
		        }		   
		    }catch (Exception e) {
		    	
		    }
		    }
		}

		public void setAllDisVisible (final int row, final int column, GridPane gridPane) {
		    ObservableList<Node> childrens = gridPane.getChildren();
		    
		    for (Node node : childrens) {
		    	try {
		    	int r = gridPane.getRowIndex(node);
				int c = gridPane.getColumnIndex(node);
		        if(r == row && c == column) {		        
					node.setVisible(false);					
		        }		   
		    }catch (Exception e) {
		    	
		    }
		    }
		}
		
		public static boolean askConfirmation(StackPane stackPane,final String title, final String message) {
			final ConfirmationDialogView view = new ConfirmationDialogView(title, message);
			ConfirmationDialogPresenter confirmationDialogPresenter = new ConfirmationDialogPresenter(view);
			Platform.runLater(new Thread() {
				@Override
				public void run() {
					showDialog(stackPane,view);
				}
			});
			boolean returnValue = false;
			try {
				returnValue = confirmationDialogPresenter.getResult();
			} catch (InterruptedException e) {
				
				e.printStackTrace();
			}
			Platform.runLater(new Thread() {
				@Override
				public void run() {
					hideDialog(stackPane);
				}
			});
			return returnValue;
		}
		
		public static void showDialog(StackPane stackPane,final AbstractDialogView<?> dialog) {
			stackPane.getChildren().add(dialog);
			
		}
		
		public static void hideDialog(StackPane stackPane) {
			List<Node> toRemove = new ArrayList<Node>();
			for (Node node : stackPane.getChildren()) {
				if (node instanceof AbstractDialogView<?>) {
					toRemove.add(node);
				}
			}
			stackPane.getChildren().removeAll(toRemove);
		}
		protected void addMenuItem(VBox prosessVBox,Button button, final int index, final String iconPath, final String text,
				final boolean isRightNav, final EventHandler<ActionEvent> clickedEventHandler) {
			prosessVBox.setPadding(new Insets(1, 1, 0, 0));
			HBox hbox = new HBox();
			StackPane iconPane = new StackPane();
			SVGPath icon = new SVGPath();
			icon.setContent(iconPath);
			icon.getStyleClass().add(CSS_CLASS_MENU_ICON);
			hbox.setAlignment(Pos.CENTER);
			iconPane.getChildren().add(icon);
			iconPane.setPrefSize(ICON_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
			hbox.getChildren().add(iconPane);
			Label label = new Label(text);
			label.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_LABEL);
			label.setPrefSize(BUTTON_WIDTH - 2 * ICON_WIDTH - 4 * ICON_MARGIN, BUTTON_HEIGHT);
			// Added for test purposes
			label.setLabelFor(button);
			hbox.getChildren().add(label);
			HBox.setHgrow(label, Priority.ALWAYS);
			StackPane arrowPane = new StackPane();
			arrowPane.setPrefSize(ICON_ARROW_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
			if (isRightNav) {
				SVGPath rightArrow = new SVGPath();
				rightArrow.setContent(arrowRightPath);
				arrowPane.getChildren().add(rightArrow);
				rightArrow.getStyleClass().add(CSS_CLASS_MENU_ICON);
			}
			hbox.getChildren().add(arrowPane);
			hbox.setPrefSize(BUTTON_WIDTH, BUTTON_HEIGHT);
			hbox.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_PANEL);
			button.setGraphic(hbox);
			button.setOnAction(clickedEventHandler);
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BUTTON);
		}
		
		protected void addMenuItem(VBox prosessVBox,Button button, final int index, final String text,
				final boolean isRightNav, final EventHandler<ActionEvent> clickedEventHandler) {
			prosessVBox.setPadding(new Insets(1, 1, 0, 0));
			HBox hbox = new HBox();
			StackPane iconPane = new StackPane();
			SVGPath icon = new SVGPath();
			icon.setContent("");
			icon.getStyleClass().add(CSS_CLASS_MENU_ICON);
			hbox.setAlignment(Pos.CENTER);
			iconPane.getChildren().add(icon);
			iconPane.setPrefSize(ICON_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
			hbox.getChildren().add(iconPane);
			Label label = new Label(text);
			label.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_LABEL);
			label.setPrefSize(BUTTON_WIDTH - 2 * ICON_WIDTH - 4 * ICON_MARGIN, BUTTON_HEIGHT);
			// Added for test purposes
			label.setLabelFor(button);
			hbox.getChildren().add(label);
			HBox.setHgrow(label, Priority.ALWAYS);
			StackPane arrowPane = new StackPane();
			arrowPane.setPrefSize(ICON_ARROW_WIDTH + 2 * ICON_MARGIN, BUTTON_HEIGHT);
			if (isRightNav) {
				SVGPath rightArrow = new SVGPath();
				rightArrow.setContent(arrowRightPath);
				arrowPane.getChildren().add(rightArrow);
				rightArrow.getStyleClass().add(CSS_CLASS_MENU_ICON);
			}
			hbox.getChildren().add(arrowPane);
			hbox.setPrefSize(155, 50);
			hbox.getStyleClass().add(CSS_CLASS_LEFT_MENU_ITEM_PANEL);
			button.setGraphic(hbox);
			button.setOnAction(clickedEventHandler);
			button.getStyleClass().add(CSS_CLASS_LEFT_MENU_BUTTON);
		}
		
		
		public static Button createButton(final String iconPath, final String iconClass, final String text, final double width, final double height, final EventHandler<ActionEvent> handler, final double iconWidth) {
			Button button = new Button();
			HBox hbox = new HBox();
			StackPane iconPane = new StackPane();
			SVGPath icon = new SVGPath();
			icon.setContent(iconPath);
			icon.getStyleClass().addAll(CSS_CLASS_FORM_BUTTON_ICON, iconClass);
			hbox.setAlignment(Pos.CENTER_LEFT);
			iconPane.getChildren().add(icon);
			iconPane.setPrefSize(iconWidth, height);
			HBox.setMargin(iconPane, new Insets(0, 0, 0, ICON_MARGIN));
			hbox.getChildren().add(iconPane);
			Label label = new Label(text);
			label.getStyleClass().add(CSS_CLASS_FORM_BUTTON_LABEL);
			label.setPrefSize(width - iconWidth - 3 * ICON_MARGIN, height);
			label.setAlignment(Pos.CENTER);
			hbox.getChildren().add(label);
			HBox.setHgrow(label, Priority.ALWAYS);
			hbox.setPrefSize(width, height);
			hbox.setMinSize(width, height);
			hbox.setMaxSize(width, height);
			hbox.getStyleClass().add(CSS_CLASS_FORM_BUTTON_PANEL);
			button.setOnAction(handler);
			button.setGraphic(hbox);
			button.setPrefSize(width, height);
			button.setMinSize(width, height);
			button.setMaxSize(width, height);
			button.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
			return button;
		}
		
		public static Button createButton(final String iconPath, final String iconClass, final String text, final double width, final double height, final EventHandler<ActionEvent> handler) {
			return createButton(iconPath, iconClass, text, width, height, handler, ICON_WIDTH);
		}
		
		public static Button createButton(final String text, final double width, final double height, final EventHandler<ActionEvent> handler) {
			Button button = new Button();
			HBox hbox = new HBox();
			hbox.setAlignment(Pos.CENTER_LEFT);
			Label label = new Label(text);
			label.getStyleClass().add(CSS_CLASS_FORM_BUTTON_LABEL);
			label.setPrefSize(width, height);
			label.setAlignment(Pos.CENTER);
			hbox.getChildren().add(label);
			HBox.setHgrow(label, Priority.ALWAYS);
			hbox.setPrefSize(width, height);
			hbox.setMinSize(width, height);
			hbox.setMaxSize(width, height);
			hbox.getStyleClass().add(CSS_CLASS_FORM_BUTTON_PANEL);
			button.setPrefSize(width, height);
			button.setMinSize(width, height);
			button.setMaxSize(width, height);
			button.setOnAction(handler);
			button.setGraphic(hbox);
			button.getStyleClass().add(CSS_CLASS_FORM_BUTTON);
			return button;
		}
		
		static String mes = null;
		public void statusChanged(final StatusChangedEvent e) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
						switch (e.getStatusId()) {							
							case StatusChangedEvent.STARTED:
								mes = "启动程序。";
								setMessege("启动程序。");								
								break;
							case StatusChangedEvent.PICK_FROM_TABLE:
								mes = "从料架下料。";
								setMessege("从料架下料。");								
								break;
							case StatusChangedEvent.PUT_TO_CNC:
								mes = "给机床上料。";
								setMessege("给机床上料。");
								break;
							case StatusChangedEvent.EXECUTE_TEACHED:
								break;
							case StatusChangedEvent.CNC_PROCESSING:
								mes = "机床加工中。";
								setMessege("机床加工中。");
								break;
							case StatusChangedEvent.PICK_FROM_CNC:
								mes = "从机床下料。";
								setMessege("从机床下料。");
								break;
							case StatusChangedEvent.PUT_TO_TABLE:
								mes = "给料架上料。";
								setMessege("给料架上料。");
								break;
							case StatusChangedEvent.ENDED:
								mes = "成功示教。";
								setMessege("成功示教。");								
								break;
							case StatusChangedEvent.TEACHING_NEEDED://请把机器人示教至正确位置。
								mes = "请把机器人示教至正确位置。";
								setMessege("请把机器人示教至正确位置。");
								break;
							case StatusChangedEvent.TEACHING_FINISHED://位置示教正确，可继续执行
								mes = "位置示教正确，可继续执行。";
								setMessege("位置示教正确，可继续执行。");
								break;
							case StatusChangedEvent.FINISHED:
								mes = "加工完成。";
								setMessege("加工完成。");
								break;	
							
							default:
								throw new IllegalArgumentException("Unknown status id: " + e.getStatusId());
						}				
				}
			});
		}
		public void statusChanged(String wIndex) {
			Platform.runLater(new Runnable() {
				@Override public void run() {
					mes = wIndex;
					setMessege(wIndex);	
				}
			});
		}
		public abstract void setMessege(String mess);
		
		public static String getMessege() {
			return mes;
			
		}
		
		@Override
		public void textFieldFocussed(TextInputControl textInputControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void textFieldLostFocus(TextInputControl textInputControl) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void closeKeyboard() {
			// TODO Auto-generated method stub
			
		}
}
