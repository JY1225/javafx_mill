package cn.greatoo.easymill.ui.set.table.load;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.entity.Program;
import cn.greatoo.easymill.entity.Stacker;
import cn.greatoo.easymill.entity.StackingPosition;
import cn.greatoo.easymill.process.WorkPiecePositions;
import cn.greatoo.easymill.ui.set.table.load.StudPosition.StudType;
import cn.greatoo.easymill.ui.shape.IDrawableObject;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import cn.greatoo.easymill.util.SizeManager;

/**
 * 布局页面Controller
 *
 */
public class LayoutViewController {	
	@FXML
	private VBox vbox;
	private GridPane gpContents;
	private Pane layoutPane;
	private Rectangle stackPlate;
	private StackPane spRoot;

	private Scale scale;
	private Translate translate;

	private List<Circle> holes;
	private List<Circle> studs;
	private List<Text> horizontalLabels;
	private List<Text> verticalLabels;

	private static final float TXT_WIDTH = 40;
	private static final float TXT_HEIGHT = 15;
	private static final float EXTRA_PADDING = 25;

	private float width;
	private float length;
	private String programName;
	private Program program;
	private Stacker stack;
	private static final String CORNER_PATH = "m 2.75,0 -0.09375,0.03125 -0.125,0 L 2.40625,0.0625 2.3125,0.09375 2.1875,0.125 2.09375,0.15625 1.96875,0.1875 1.875,0.21875 1.75,0.25 1.65625,0.3125 1.5625,0.375 1.46875,0.40625 1.34375,0.46875 1.25,0.5625 1.1875,0.625 1.0625,0.6875 1,0.75 0.90625,0.84375 0.84375,0.9375 0.75,1 0.65625,1.09375 0.59375,1.1875 0.53125,1.28125 0.46875,1.375 0.40625,1.46875 0.34375,1.5625 0.3125,1.6875 0.25,1.78125 0.1875,1.875 0.15625,2 0.125,2.09375 0.09375,2.21875 0.0625,2.3125 l -0.03125,0.125 0,0.09375 L 0,2.65625 0,2.78125 0,2.875 0,3 l 0,38 0,0.28125 0,0.25 0.03125,0.28125 0.03125,0.25 0.0625,0.28125 0.03125,0.25 0.09375,0.28125 0.0625,0.25 0.09375,0.25 0.09375,0.25 0.09375,0.25 0.125,0.25 0.125,0.21875 0.125,0.25 0.15625,0.21875 0.125,0.21875 0.15625,0.21875 0.1875,0.21875 0.15625,0.1875 0.1875,0.21875 0.1875,0.1875 0.21875,0.15625 0.1875,0.1875 0.21875,0.15625 0.21875,0.1875 0.21875,0.125 0.21875,0.15625 0.25,0.125 0.21875,0.125 0.25,0.125 0.25,0.09375 0.25,0.09375 0.25,0.09375 0.25,0.0625 0.25,0.0625 0.28125,0.0625 0.25,0.0625 L 6.1875,47.96875 6.46875,48 6.71875,48 7,48 l 54.5,0 0.125,0 0.09375,0 0.125,0 0.125,-0.03125 0.09375,-0.03125 0.125,0 0.09375,-0.03125 0.125,-0.03125 0.09375,-0.03125 0.125,-0.0625 0.09375,-0.03125 0.09375,-0.0625 0.125,-0.03125 0.09375,-0.0625 0.09375,-0.0625 0.09375,-0.0625 0.09375,-0.0625 L 63.40625,47.3125 63.5,47.25 63.5625,47.15625 63.65625,47.09375 63.75,47 63.8125,46.90625 63.875,46.8125 63.9375,46.75 64,46.625 l 0.0625,-0.09375 0.0625,-0.09375 0.0625,-0.09375 0.03125,-0.125 0.0625,-0.09375 0.03125,-0.09375 0.03125,-0.125 0.03125,-0.09375 0.03125,-0.125 0.03125,-0.09375 0.03125,-0.125 0,-0.125 L 64.5,45.25 l 0,-0.125 0,-0.125 0,-11 0,-0.03125 0,-0.03125 0,-0.03125 0,-0.0625 -0.03125,-0.03125 0,-0.03125 0,-0.03125 -0.03125,-0.03125 0,-0.0625 0,-0.03125 -0.03125,-0.03125 -0.03125,-0.03125 0,-0.03125 -0.03125,-0.03125 0,-0.03125 L 64.3125,33.4375 64.28125,33.40625 64.25,33.375 64.25,33.34375 64.21875,33.3125 64.1875,33.28125 64.15625,33.25 64.125,33.21875 l -0.03125,0 -0.03125,-0.03125 -0.03125,-0.03125 -0.03125,0 -0.03125,-0.03125 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.0625,-0.03125 -0.03125,0 -0.03125,0 -0.03125,0 L 63.625,33 63.5625,33 63.53125,33 63.5,33 l -47,0 0,0.09375 -0.03125,0.0625 0,0.09375 0,0.0625 -0.03125,0.09375 -0.03125,0.0625 -0.03125,0.09375 -0.03125,0.0625 -0.03125,0.0625 -0.03125,0.09375 -0.03125,0.0625 -0.0625,0.0625 -0.03125,0.0625 -0.0625,0.0625 -0.0625,0.0625 -0.0625,0.03125 -0.0625,0.0625 -0.0625,0.0625 -0.0625,0.03125 -0.0625,0.03125 -0.09375,0.0625 -0.0625,0.03125 -0.0625,0 -0.09375,0.03125 -0.0625,0.03125 -0.09375,0 -0.0625,0.03125 -0.09375,0 -0.09375,0 -0.0625,0 -0.09375,0 -0.0625,0 -0.09375,-0.03125 -0.0625,0 L 14.53125,34.4375 14.46875,34.40625 14.375,34.375 14.3125,34.34375 14.25,34.3125 14.1875,34.25 14.125,34.21875 14.0625,34.1875 14,34.125 13.9375,34.0625 13.875,34 13.8125,33.9375 13.78125,33.875 13.71875,33.8125 13.6875,33.75 13.65625,33.6875 13.625,33.59375 13.59375,33.53125 13.5625,33.4375 13.53125,33.375 l 0,-0.0625 -0.03125,-0.09375 0,-0.0625 0,-0.09375 0,-0.09375 0,-0.0625 0,-0.09375 0,-0.0625 0.03125,-0.09375 0.03125,-0.09375 0,-0.0625 0.03125,-0.0625 0.03125,-0.09375 0.0625,-0.0625 0.03125,-0.0625 0.03125,-0.0625 0.0625,-0.0625 0.03125,-0.0625 0.0625,-0.0625 0.0625,-0.0625 0.0625,-0.0625 0.0625,-0.03125 0.0625,-0.0625 0.0625,-0.03125 0.09375,-0.03125 0.0625,-0.03125 0.0625,-0.0625 0.09375,0 0.0625,-0.03125 0.0625,-0.03125 0.09375,0 0.0625,-0.03125 0.09375,0 L 15,31.5 15,1 15,0.96875 15,0.90625 15,0.875 14.96875,0.84375 l 0,-0.03125 0,-0.03125 L 14.9375,0.75 l 0,-0.0625 0,-0.03125 -0.03125,-0.03125 0,-0.03125 L 14.875,0.5625 14.875,0.53125 14.84375,0.5 14.8125,0.46875 14.8125,0.4375 14.78125,0.40625 14.75,0.375 14.75,0.34375 14.71875,0.3125 14.6875,0.28125 14.65625,0.25 14.625,0.21875 l -0.03125,0 L 14.5625,0.1875 14.53125,0.15625 14.5,0.15625 14.46875,0.125 14.4375,0.09375 l -0.03125,0 -0.03125,-0.03125 -0.0625,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 L 14.125,0 14.09375,0 14.0625,0 14.03125,0 14,0 3,0 2.875,0 2.75,0 z m 4.59375,10.75 0.28125,0 0.25,0.03125 0.28125,0.03125 0.25,0.03125 0.25,0.0625 0.28125,0.0625 0.25,0.09375 0.25,0.09375 0.25,0.09375 0.21875,0.125 0.25,0.125 0.21875,0.15625 0.1875,0.15625 0.21875,0.1875 0.1875,0.15625 0.1875,0.21875 0.1875,0.1875 L 11.5,12.78125 11.65625,13 l 0.125,0.21875 0.15625,0.21875 0.09375,0.25 0.125,0.25 0.0625,0.25 0.09375,0.25 0.0625,0.25 0.0625,0.28125 0.03125,0.25 0,0.28125 0.03125,0.25 -0.03125,0.28125 0,0.25 -0.03125,0.28125 -0.0625,0.25 -0.0625,0.25 -0.09375,0.25 -0.0625,0.25 -0.125,0.25 -0.09375,0.25 -0.15625,0.21875 -0.125,0.25 L 11.5,18.75 l -0.15625,0.1875 -0.1875,0.21875 -0.1875,0.1875 L 10.78125,19.5 10.5625,19.6875 10.375,19.84375 10.15625,20 l -0.25,0.125 -0.21875,0.125 -0.25,0.125 -0.25,0.09375 -0.25,0.0625 -0.28125,0.0625 -0.25,0.0625 -0.25,0.0625 L 7.875,20.75 l -0.25,0 -0.28125,0 -0.25,0 -0.28125,-0.03125 -0.25,-0.0625 -0.25,-0.0625 -0.28125,-0.0625 -0.25,-0.0625 L 5.5625,20.375 5.3125,20.25 5.0625,20.125 4.84375,20 4.625,19.84375 4.40625,19.6875 4.1875,19.5 4,19.34375 3.8125,19.15625 3.65625,18.9375 3.46875,18.75 3.3125,18.53125 l -0.125,-0.25 -0.125,-0.21875 -0.125,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.0625,-0.25 -0.03125,-0.25 -0.0625,-0.28125 0,-0.25 0,-0.28125 0,-0.25 0,-0.28125 0.0625,-0.25 0.03125,-0.28125 0.0625,-0.25 0.09375,-0.25 0.09375,-0.25 0.09375,-0.25 0.125,-0.25 0.125,-0.21875 L 3.3125,13 3.46875,12.78125 3.65625,12.5625 3.8125,12.375 4,12.15625 4.1875,12 4.40625,11.8125 4.625,11.65625 4.84375,11.5 5.0625,11.375 l 0.25,-0.125 0.25,-0.09375 0.21875,-0.09375 0.25,-0.09375 0.28125,-0.0625 0.25,-0.0625 0.25,-0.03125 0.28125,-0.03125 0.25,-0.03125 z m 0,24.75 0.28125,0 0.25,0.03125 0.28125,0.03125 0.25,0.03125 0.25,0.0625 0.28125,0.0625 0.25,0.09375 0.25,0.09375 0.25,0.09375 0.21875,0.125 0.25,0.125 0.21875,0.15625 0.1875,0.15625 0.21875,0.1875 0.1875,0.1875 0.1875,0.1875 0.1875,0.1875 0.15625,0.21875 0.15625,0.21875 0.125,0.21875 0.15625,0.21875 0.09375,0.25 0.125,0.25 0.0625,0.25 0.09375,0.25 0.0625,0.25 0.0625,0.28125 0.03125,0.25 0,0.28125 0.03125,0.25 -0.03125,0.28125 0,0.25 -0.03125,0.28125 -0.0625,0.25 -0.0625,0.25 -0.09375,0.25 -0.0625,0.25 -0.125,0.25 -0.09375,0.25 -0.15625,0.25 -0.125,0.21875 L 11.5,43.5 l -0.15625,0.1875 -0.1875,0.21875 -0.1875,0.1875 L 10.78125,44.25 10.5625,44.4375 10.375,44.59375 10.15625,44.75 9.90625,44.875 9.6875,45 l -0.25,0.125 -0.25,0.09375 -0.25,0.09375 -0.28125,0.0625 -0.25,0.03125 -0.25,0.0625 L 7.875,45.5 l -0.25,0 -0.28125,0 -0.25,0 L 6.8125,45.46875 6.5625,45.40625 6.3125,45.375 6.03125,45.3125 5.78125,45.21875 5.5625,45.125 5.3125,45 5.0625,44.875 4.84375,44.75 4.625,44.59375 4.40625,44.4375 4.1875,44.25 4,44.09375 3.8125,43.90625 3.65625,43.6875 3.46875,43.5 3.3125,43.28125 3.1875,43.0625 l -0.125,-0.25 -0.125,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.0625,-0.25 -0.03125,-0.25 -0.0625,-0.28125 0,-0.25 0,-0.28125 0,-0.25 0,-0.28125 0.0625,-0.25 0.03125,-0.28125 0.0625,-0.25 0.09375,-0.25 0.09375,-0.25 0.09375,-0.25 0.125,-0.25 0.125,-0.21875 L 3.3125,37.75 3.46875,37.53125 3.65625,37.3125 3.8125,37.125 4,36.9375 4.1875,36.75 4.40625,36.5625 4.625,36.40625 4.84375,36.25 5.0625,36.125 5.3125,36 l 0.25,-0.09375 0.21875,-0.09375 0.25,-0.09375 0.28125,-0.0625 0.25,-0.0625 0.25,-0.03125 0.28125,-0.03125 0.25,-0.03125 z m 24.6875,0 0.25,0 0.28125,0 0.25,0.03125 0.25,0.03125 0.25,0.0625 0.28125,0.0625 0.21875,0.0625 0.25,0.09375 0.25,0.125 0.21875,0.09375 0.25,0.125 0.21875,0.125 0.1875,0.15625 0.21875,0.15625 0.1875,0.1875 0.1875,0.15625 0.1875,0.21875 0.03125,0 0,0.03125 0.03125,0.03125 0.03125,0 0,0.03125 0.03125,0 0.03125,0.03125 0.03125,0.03125 0.03125,0 0.03125,0.03125 0.03125,0 0,0.03125 0.03125,0 0.03125,0 0.03125,0.03125 0.03125,0 0.03125,0.03125 0.03125,0 0.03125,0 0.03125,0 0.03125,0.03125 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 1.3125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,-0.03125 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,-0.03125 0.03125,-0.03125 0.03125,0 0.03125,0 L 38.5,37.375 l 0.03125,0 0,-0.03125 0.03125,0 0.03125,-0.03125 0.03125,0 0.03125,-0.03125 0.03125,-0.03125 0.03125,0 0,-0.03125 0.03125,-0.03125 0.03125,0 0.15625,-0.21875 0.1875,-0.15625 0.21875,-0.1875 0.1875,-0.15625 0.21875,-0.15625 0.21875,-0.125 0.21875,-0.125 0.25,-0.09375 0.21875,-0.125 0.25,-0.09375 0.25,-0.0625 0.25,-0.0625 0.25,-0.0625 0.25,-0.03125 0.28125,-0.03125 0.25,0 0.25,0 0.28125,0.03125 0.25,0.03125 0.25,0.03125 0.25,0.0625 0.25,0.0625 0.25,0.09375 0.25,0.09375 0.21875,0.125 0.25,0.09375 0.21875,0.125 0.21875,0.15625 0.1875,0.15625 0.21875,0.15625 0.1875,0.1875 0.15625,0.1875 0.1875,0.1875 0.15625,0.1875 0.15625,0.21875 0.15625,0.21875 0.125,0.25 L 47,38.375 l 0.125,0.25 0.09375,0.21875 0.0625,0.25 0.0625,0.25 0.0625,0.25 0.0625,0.25 0,0.28125 0.03125,0.25 0,0.25 -0.03125,0.28125 0,0.25 -0.0625,0.25 -0.0625,0.25 -0.0625,0.25 -0.0625,0.25 -0.09375,0.25 -0.125,0.25 -0.09375,0.21875 -0.125,0.21875 -0.15625,0.21875 -0.15625,0.21875 -0.15625,0.1875 -0.1875,0.1875 -0.15625,0.1875 -0.1875,0.1875 -0.21875,0.15625 -0.1875,0.15625 -0.21875,0.15625 -0.21875,0.125 -0.25,0.125 -0.21875,0.09375 -0.25,0.09375 -0.25,0.09375 -0.25,0.0625 -0.25,0.0625 -0.25,0.0625 -0.25,0 -0.28125,0.03125 -0.25,0 -0.25,0 -0.28125,-0.03125 -0.25,-0.03125 -0.25,-0.0625 -0.25,-0.0625 -0.25,-0.0625 -0.25,-0.09375 -0.21875,-0.09375 -0.25,-0.125 -0.21875,-0.125 L 39.75,44.6875 39.53125,44.53125 39.34375,44.375 39.125,44.21875 38.9375,44.03125 38.78125,43.84375 38.75,43.8125 l -0.03125,0 0,-0.03125 -0.03125,-0.03125 -0.03125,0 0,-0.03125 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,-0.03125 0,-0.03125 -0.03125,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0 -1.3125,0 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0.03125 -0.03125,0.03125 -0.03125,0 -0.03125,0.03125 L 36.0625,43.75 36.03125,43.78125 36,43.8125 l -0.03125,0.03125 -0.1875,0.1875 -0.1875,0.1875 L 35.40625,44.375 35.1875,44.53125 35,44.6875 l -0.21875,0.125 -0.25,0.125 -0.21875,0.125 -0.25,0.09375 -0.25,0.09375 -0.21875,0.0625 -0.28125,0.0625 -0.25,0.0625 -0.25,0.03125 -0.25,0.03125 -0.28125,0 -0.25,0 -0.25,-0.03125 -0.25,0 -0.25,-0.0625 L 31,45.34375 30.75,45.28125 30.53125,45.1875 30.28125,45.09375 30.03125,45 29.8125,44.875 29.59375,44.75 29.375,44.59375 29.15625,44.4375 l -0.1875,-0.15625 -0.1875,-0.1875 -0.1875,-0.1875 -0.1875,-0.1875 -0.15625,-0.1875 -0.15625,-0.21875 -0.125,-0.21875 -0.125,-0.21875 -0.125,-0.21875 -0.09375,-0.25 -0.09375,-0.25 -0.09375,-0.25 -0.0625,-0.25 -0.0625,-0.25 -0.03125,-0.25 -0.03125,-0.25 0,-0.28125 0,-0.25 0,-0.25 0.03125,-0.28125 0.03125,-0.25 0.0625,-0.25 0.0625,-0.25 0.09375,-0.25 0.09375,-0.21875 0.09375,-0.25 0.125,-0.21875 0.125,-0.25 0.125,-0.21875 0.15625,-0.21875 0.15625,-0.1875 0.1875,-0.1875 0.1875,-0.1875 0.1875,-0.1875 0.1875,-0.15625 0.21875,-0.15625 0.21875,-0.15625 0.21875,-0.125 0.21875,-0.09375 0.25,-0.125 0.25,-0.09375 0.21875,-0.09375 0.25,-0.0625 0.28125,-0.0625 0.25,-0.03125 0.25,-0.03125 0.25,-0.03125 z";
	private static final String CORNER_PATH_LEFT = "m 50.375,0 -0.0625,0.03125 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.0625,0 -0.03125,0.03125 -0.03125,0 L 50.03125,0.125 50,0.15625 l -0.03125,0 -0.03125,0.03125 -0.03125,0.03125 -0.03125,0 L 49.84375,0.25 49.8125,0.28125 49.78125,0.3125 49.75,0.34375 l 0,0.03125 -0.03125,0.03125 -0.03125,0.03125 0,0.03125 L 49.65625,0.5 49.625,0.53125 l 0,0.03125 -0.03125,0.03125 0,0.03125 -0.03125,0.03125 0,0.03125 0,0.0625 -0.03125,0.03125 0,0.03125 0,0.03125 L 49.5,0.875 l 0,0.03125 0,0.0625 0,0.03125 0,30.5 0.09375,0 0.09375,0 0.0625,0.03125 0.09375,0 0.0625,0.03125 0.0625,0.03125 0.09375,0 0.0625,0.0625 0.0625,0.03125 0.09375,0.03125 0.0625,0.03125 0.0625,0.0625 0.0625,0.03125 0.0625,0.0625 0.0625,0.0625 0.0625,0.0625 0.03125,0.0625 0.0625,0.0625 0.03125,0.0625 0.03125,0.0625 0.0625,0.0625 0.03125,0.09375 0.03125,0.0625 0,0.0625 0.03125,0.09375 L 51,32.75 l 0,0.0625 0,0.09375 0,0.0625 0,0.09375 0,0.09375 0,0.0625 -0.03125,0.09375 0,0.0625 -0.03125,0.0625 -0.03125,0.09375 L 50.875,33.59375 50.84375,33.6875 50.8125,33.75 50.78125,33.8125 50.71875,33.875 50.6875,33.9375 50.625,34 50.5625,34.0625 50.5,34.125 50.4375,34.1875 50.375,34.21875 50.3125,34.25 50.25,34.3125 50.1875,34.34375 50.125,34.375 l -0.09375,0.03125 -0.0625,0.03125 -0.09375,0.03125 -0.0625,0 -0.09375,0.03125 -0.0625,0 -0.09375,0 -0.0625,0 -0.09375,0 -0.09375,0 -0.0625,-0.03125 -0.09375,0 L 49.09375,34.4375 49,34.40625 l -0.0625,0 -0.0625,-0.03125 -0.09375,-0.0625 -0.0625,-0.03125 -0.0625,-0.03125 -0.0625,-0.0625 -0.0625,-0.0625 -0.0625,-0.03125 -0.0625,-0.0625 -0.0625,-0.0625 L 48.3125,33.90625 48.25,33.84375 48.21875,33.78125 48.1875,33.6875 48.15625,33.625 48.125,33.5625 48.09375,33.46875 48.0625,33.40625 48.03125,33.3125 l 0,-0.0625 0,-0.09375 L 48,33.09375 48,33 1,33 0.96875,33 0.9375,33 0.875,33 l -0.03125,0.03125 -0.03125,0 -0.03125,0 -0.03125,0 -0.0625,0.03125 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0 L 0.53125,33.125 0.5,33.15625 l -0.03125,0 -0.03125,0.03125 -0.03125,0.03125 -0.03125,0 L 0.34375,33.25 0.3125,33.28125 0.28125,33.3125 0.25,33.34375 l 0,0.03125 -0.03125,0.03125 -0.03125,0.03125 -0.03125,0.03125 0,0.03125 -0.03125,0.03125 0,0.03125 -0.03125,0.03125 -0.03125,0.03125 0,0.03125 0,0.0625 -0.03125,0.03125 0,0.03125 0,0.03125 L 0,33.84375 0,33.90625 0,33.9375 0,33.96875 0,34 l 0,11 0,0.125 0,0.125 0.03125,0.09375 0,0.125 0.03125,0.125 0.03125,0.09375 0.03125,0.125 0.03125,0.09375 0.03125,0.125 0.03125,0.09375 0.0625,0.09375 0.03125,0.125 L 0.375,46.4375 0.4375,46.53125 0.5,46.625 0.5625,46.75 0.625,46.8125 0.6875,46.90625 0.75,47 0.84375,47.09375 0.9375,47.15625 1,47.25 l 0.09375,0.0625 0.09375,0.09375 0.09375,0.0625 0.09375,0.0625 0.09375,0.0625 0.09375,0.0625 0.125,0.03125 L 1.78125,47.75 1.875,47.78125 2,47.84375 2.09375,47.875 l 0.125,0.03125 0.09375,0.03125 0.125,0 0.09375,0.03125 0.125,0.03125 0.125,0 0.09375,0 0.125,0 54.5,0 0.28125,0 0.25,0 0.28125,-0.03125 0.28125,-0.03125 0.25,-0.0625 0.28125,-0.0625 0.25,-0.0625 0.25,-0.0625 0.25,-0.09375 0.25,-0.09375 0.25,-0.09375 0.25,-0.125 0.21875,-0.125 0.25,-0.125 0.21875,-0.15625 0.21875,-0.125 0.21875,-0.1875 0.21875,-0.15625 0.1875,-0.1875 0.21875,-0.15625 0.1875,-0.1875 0.1875,-0.21875 0.15625,-0.1875 0.1875,-0.21875 0.15625,-0.21875 0.125,-0.21875 0.15625,-0.21875 0.125,-0.25 0.125,-0.21875 0.125,-0.25 0.09375,-0.25 0.09375,-0.25 0.09375,-0.25 0.0625,-0.25 0.09375,-0.28125 0.03125,-0.25 0.0625,-0.28125 0.03125,-0.25 0.03125,-0.28125 0,-0.25 0,-0.28125 0,-38 0,-0.125 0,-0.09375 0,-0.125 -0.03125,-0.125 0,-0.09375 L 64.4375,2.3125 64.40625,2.21875 64.375,2.09375 64.34375,2 64.3125,1.875 64.25,1.78125 64.1875,1.6875 64.15625,1.5625 64.09375,1.46875 64.03125,1.375 63.96875,1.28125 63.90625,1.1875 63.84375,1.09375 63.75,1 63.65625,0.9375 63.59375,0.84375 63.5,0.75 63.4375,0.6875 63.3125,0.625 63.25,0.5625 63.15625,0.46875 63.03125,0.40625 62.9375,0.375 62.84375,0.3125 62.75,0.25 62.625,0.21875 62.53125,0.1875 62.40625,0.15625 62.3125,0.125 62.1875,0.09375 62.09375,0.0625 l -0.125,-0.03125 -0.125,0 L 61.75,0 61.625,0 61.5,0 50.5,0 50.46875,0 50.4375,0 50.40625,0 50.375,0 z m 6.5,10.75 0.28125,0 0.25,0.03125 0.28125,0.03125 0.25,0.03125 0.25,0.0625 0.28125,0.0625 0.25,0.09375 0.21875,0.09375 0.25,0.09375 0.25,0.125 0.21875,0.125 0.21875,0.15625 0.21875,0.15625 L 60.3125,12 60.5,12.15625 l 0.1875,0.21875 0.15625,0.1875 0.1875,0.21875 L 61.1875,13 l 0.125,0.21875 0.125,0.21875 0.125,0.25 0.09375,0.25 0.09375,0.25 0.09375,0.25 0.0625,0.25 0.03125,0.28125 0.0625,0.25 0,0.28125 0,0.25 0,0.28125 0,0.25 -0.0625,0.28125 -0.03125,0.25 -0.0625,0.25 -0.09375,0.25 -0.09375,0.25 -0.09375,0.25 -0.125,0.25 -0.125,0.21875 -0.125,0.25 L 61.03125,18.75 60.84375,18.9375 60.6875,19.15625 60.5,19.34375 60.3125,19.5 60.09375,19.6875 59.875,19.84375 59.65625,20 l -0.21875,0.125 -0.25,0.125 -0.25,0.125 -0.21875,0.09375 -0.25,0.0625 -0.28125,0.0625 -0.25,0.0625 -0.25,0.0625 -0.28125,0.03125 -0.25,0 -0.28125,0 -0.25,0 -0.28125,-0.03125 -0.25,-0.0625 -0.25,-0.0625 -0.28125,-0.0625 -0.25,-0.0625 -0.25,-0.09375 -0.25,-0.125 L 54.59375,20.125 54.34375,20 54.125,19.84375 53.9375,19.6875 53.71875,19.5 53.53125,19.34375 53.34375,19.15625 53.15625,18.9375 53,18.75 l -0.15625,-0.21875 -0.125,-0.25 -0.15625,-0.21875 -0.09375,-0.25 -0.125,-0.25 -0.0625,-0.25 -0.09375,-0.25 -0.0625,-0.25 -0.0625,-0.25 -0.03125,-0.28125 0,-0.25 L 52,15.75 l 0.03125,-0.25 0,-0.28125 0.03125,-0.25 0.0625,-0.28125 0.0625,-0.25 0.09375,-0.25 0.0625,-0.25 0.125,-0.25 0.09375,-0.25 0.15625,-0.21875 L 52.84375,13 53,12.78125 53.15625,12.5625 53.34375,12.375 53.53125,12.15625 53.71875,12 53.9375,11.8125 54.125,11.65625 54.34375,11.5 l 0.25,-0.125 0.21875,-0.125 0.25,-0.09375 0.25,-0.09375 0.25,-0.09375 0.28125,-0.0625 0.25,-0.0625 0.25,-0.03125 0.28125,-0.03125 0.25,-0.03125 z m -35.0625,24.75 0.25,0 0.25,0 0.28125,0.03125 0.25,0.03125 0.25,0.0625 0.25,0.0625 0.25,0.0625 0.25,0.09375 0.21875,0.125 0.25,0.09375 0.21875,0.125 0.21875,0.125 0.21875,0.15625 0.1875,0.15625 0.21875,0.1875 0.1875,0.15625 0.15625,0.21875 0.03125,0 0.03125,0.03125 0,0.03125 0.03125,0 0.03125,0.03125 0.03125,0.03125 0.03125,0 0.03125,0.03125 0.03125,0 0,0.03125 0.03125,0 0.03125,0.03125 0.03125,0 0.03125,0 0.03125,0.03125 0.03125,0.03125 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0.03125 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 1.3125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 0.03125,0 L 28,37.46875 l 0.03125,0 0.03125,0 0.03125,0 0.03125,-0.03125 0.03125,0 0.03125,-0.03125 0.03125,0 0.03125,0 0,-0.03125 0.03125,0 0.03125,-0.03125 0.03125,0 0.03125,-0.03125 0.03125,-0.03125 0.03125,0 0,-0.03125 0.03125,0 0.03125,-0.03125 0,-0.03125 0.03125,0 0.1875,-0.21875 0.1875,-0.15625 0.1875,-0.1875 0.21875,-0.15625 0.1875,-0.15625 0.21875,-0.125 0.25,-0.125 0.21875,-0.09375 0.25,-0.125 0.25,-0.09375 0.21875,-0.0625 0.28125,-0.0625 0.25,-0.0625 0.25,-0.03125 0.25,-0.03125 0.28125,0 0.25,0 0.25,0.03125 0.25,0.03125 0.25,0.03125 0.28125,0.0625 0.25,0.0625 0.21875,0.09375 0.25,0.09375 0.25,0.125 0.21875,0.09375 0.21875,0.125 0.21875,0.15625 0.21875,0.15625 0.1875,0.15625 0.1875,0.1875 0.1875,0.1875 0.1875,0.1875 0.15625,0.1875 0.15625,0.21875 0.125,0.21875 0.125,0.25 0.125,0.21875 0.09375,0.25 0.09375,0.21875 0.09375,0.25 0.0625,0.25 0.0625,0.25 0.03125,0.25 0.03125,0.28125 0,0.25 0,0.25 0,0.28125 -0.03125,0.25 -0.03125,0.25 -0.0625,0.25 -0.0625,0.25 -0.09375,0.25 -0.09375,0.25 -0.09375,0.25 -0.125,0.21875 -0.125,0.21875 -0.125,0.21875 -0.15625,0.21875 -0.15625,0.1875 -0.1875,0.1875 -0.1875,0.1875 -0.1875,0.1875 -0.1875,0.15625 L 35.125,44.59375 34.90625,44.75 34.6875,44.875 34.46875,45 l -0.25,0.09375 -0.25,0.09375 -0.21875,0.09375 -0.25,0.0625 -0.28125,0.0625 -0.25,0.0625 -0.25,0 -0.25,0.03125 -0.25,0 -0.28125,0 -0.25,-0.03125 -0.25,-0.03125 -0.25,-0.0625 -0.28125,-0.0625 -0.21875,-0.0625 -0.25,-0.09375 -0.25,-0.09375 -0.21875,-0.125 -0.25,-0.125 L 29.5,44.6875 29.3125,44.53125 29.09375,44.375 l -0.1875,-0.15625 -0.1875,-0.1875 -0.1875,-0.1875 L 28.5,43.8125 28.46875,43.78125 28.4375,43.75 28.40625,43.71875 28.375,43.6875 l -0.03125,0 -0.03125,-0.03125 -0.03125,-0.03125 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,-0.03125 -0.03125,0 -0.03125,0 L 27.9375,43.5 l -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0 -1.3125,0 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0.03125 -0.03125,0 -0.03125,0 0,0.03125 -0.03125,0.03125 -0.03125,0 -0.03125,0 -0.03125,0.03125 0,0.03125 -0.03125,0 -0.03125,0.03125 0,0.03125 -0.03125,0 -0.03125,0.03125 -0.15625,0.1875 -0.1875,0.1875 -0.21875,0.15625 -0.1875,0.15625 -0.21875,0.15625 -0.21875,0.125 -0.21875,0.125 -0.25,0.125 -0.21875,0.09375 -0.25,0.09375 -0.25,0.0625 -0.25,0.0625 -0.25,0.0625 -0.25,0.03125 -0.28125,0.03125 -0.25,0 -0.25,0 -0.28125,-0.03125 -0.25,0 -0.25,-0.0625 -0.25,-0.0625 -0.25,-0.0625 -0.25,-0.09375 -0.25,-0.09375 L 19.8125,45 19.5625,44.875 19.34375,44.75 19.125,44.59375 18.9375,44.4375 18.71875,44.28125 18.53125,44.09375 18.375,43.90625 18.1875,43.71875 18.03125,43.53125 17.875,43.3125 17.71875,43.09375 17.59375,42.875 17.5,42.65625 l -0.125,-0.25 -0.09375,-0.25 -0.0625,-0.25 -0.0625,-0.25 -0.0625,-0.25 -0.0625,-0.25 0,-0.25 L 17,40.625 l 0,-0.25 0.03125,-0.25 0,-0.28125 0.0625,-0.25 0.0625,-0.25 0.0625,-0.25 0.0625,-0.25 0.09375,-0.21875 0.125,-0.25 0.09375,-0.21875 0.125,-0.25 0.15625,-0.21875 0.15625,-0.21875 0.15625,-0.1875 0.1875,-0.1875 0.15625,-0.1875 0.1875,-0.1875 0.21875,-0.15625 0.1875,-0.15625 0.21875,-0.15625 0.21875,-0.125 0.25,-0.09375 0.21875,-0.125 0.25,-0.09375 0.25,-0.09375 0.25,-0.0625 0.25,-0.0625 0.25,-0.03125 0.25,-0.03125 L 21.8125,35.5 z m 35.0625,0 0.28125,0 0.25,0.03125 0.28125,0.03125 0.25,0.03125 0.25,0.0625 0.28125,0.0625 0.25,0.09375 0.21875,0.09375 0.25,0.09375 0.25,0.125 0.21875,0.125 0.21875,0.15625 0.21875,0.15625 0.21875,0.1875 0.1875,0.1875 0.1875,0.1875 0.15625,0.1875 0.1875,0.21875 0.15625,0.21875 0.125,0.21875 0.125,0.21875 0.125,0.25 0.09375,0.25 0.09375,0.25 0.09375,0.25 0.0625,0.25 0.03125,0.28125 0.0625,0.25 0,0.28125 0,0.25 0,0.28125 0,0.25 -0.0625,0.28125 -0.03125,0.25 -0.0625,0.25 -0.09375,0.25 -0.09375,0.25 -0.09375,0.25 -0.125,0.25 -0.125,0.25 -0.125,0.21875 L 61.03125,43.5 60.84375,43.6875 60.6875,43.90625 60.5,44.09375 60.3125,44.25 60.09375,44.4375 59.875,44.59375 59.65625,44.75 59.4375,44.875 59.1875,45 l -0.25,0.125 -0.21875,0.09375 -0.25,0.09375 -0.28125,0.0625 -0.25,0.03125 -0.25,0.0625 -0.28125,0.03125 -0.25,0 -0.28125,0 -0.25,0 -0.28125,-0.03125 -0.25,-0.0625 -0.25,-0.03125 -0.28125,-0.0625 -0.25,-0.09375 L 55.0625,45.125 54.8125,45 54.59375,44.875 54.34375,44.75 54.125,44.59375 53.9375,44.4375 53.71875,44.25 53.53125,44.09375 53.34375,43.90625 53.15625,43.6875 53,43.5 l -0.15625,-0.21875 -0.125,-0.21875 -0.15625,-0.25 -0.09375,-0.25 -0.125,-0.25 -0.0625,-0.25 -0.09375,-0.25 -0.0625,-0.25 -0.0625,-0.25 -0.03125,-0.28125 0,-0.25 L 52,40.5 l 0.03125,-0.25 0,-0.28125 0.03125,-0.25 0.0625,-0.28125 0.0625,-0.25 0.09375,-0.25 0.0625,-0.25 0.125,-0.25 0.09375,-0.25 0.15625,-0.21875 L 52.84375,37.75 53,37.53125 l 0.15625,-0.21875 0.1875,-0.1875 0.1875,-0.1875 0.1875,-0.1875 0.21875,-0.1875 0.1875,-0.15625 0.21875,-0.15625 0.25,-0.125 0.21875,-0.125 0.25,-0.09375 0.25,-0.09375 0.25,-0.09375 0.28125,-0.0625 0.25,-0.0625 0.25,-0.03125 L 56.625,35.53125 56.875,35.5 z";

	private static final String CSS_CLASS_STACKER_TEXT = "stacker-text";
	private static final String CSS_CLASS_STACKPLATE = "stackplate";
	private static final String CSS_CLASS_LINE = "line";
	private static final String CSS_CLASS_HOLE = "hole";
	private static final String CSS_CLASS_NORMALSTUD = "normal-stud";
	private static final String CSS_CLASS_CORNERSHAPE = "corner-shape";
	private static final String CSS_CLASS_AMOUNT = "amount-text";
	private StudPosition[][] studPositions;
	private WorkPiecePositions workPiecePositions;
	
	public void init(GridPane setProsessPane) {
		this.programName = DBHandler.getInstance().getProgramName();
		this.program = DBHandler.getInstance().getProgramBuffer().get(programName);
		workPiecePositions = new WorkPiecePositions(program);
		workPiecePositions.initStackingPositions(program.getRawWorkPiece());
		stack = DBHandler.getInstance().getStatckerBuffer().get(0);
		holes = new ArrayList<Circle>();
		studs = new ArrayList<Circle>();
		horizontalLabels = new ArrayList<Text>();
		verticalLabels = new ArrayList<Text>();
		length = stack.getHorizontalPadding() + (stack.getHorizontalHoleAmount() - 1) * stack.getHorizontalHoleDistance() + stack.getHorizontalPadding();
		width = stack.getVerticalPaddingBottom() + (stack.getVerticalHoleAmount() - 1) * stack.getVerticalHoleDistance() + stack.getVerticalPaddingTop();
		build();

	}

	public void build() {
		Platform.runLater(new Thread() {
			@Override
			public void run() {
				holes.clear();
				studs.clear();
				horizontalLabels.clear();
				verticalLabels.clear();				
				gpContents = new GridPane();
				if (layoutPane != null) {
					layoutPane.getChildren().clear();
				} else {
					layoutPane = new Pane();
					scale = new Scale(1.0, 1.0, 0, 0);
					translate = new Translate(0.0, 0.0);
					layoutPane.getTransforms().addAll(translate, scale);
				}

				if (spRoot == null) {
					spRoot = new StackPane();
					spRoot.getChildren().add(layoutPane);
					gpContents.add(spRoot, 0, 0);
				}

				// add plate
				stackPlate = new Rectangle(0, 0, length, width);
				stackPlate.getStyleClass().add(CSS_CLASS_STACKPLATE);
				layoutPane.getChildren().add(stackPlate);
			
				vbox.getChildren().add(gpContents);
				gpContents.setAlignment(Pos.CENTER);
				initStuds();
				
				if (workPiecePositions.stackingPositionList.size() > 0) {                
					configureStuds();
					configureWorkPieces();
				}
				updateScale();
			}
		});
	}

	private void initStuds() {   	
        int index = 1;
        initStudPositions();
        for (StudPosition[] horizontalPositions : studPositions) {
            Text txt = new Text(""  + (char) ('A' + (index - 1)));
            txt.setX(-10);
            txt.setY(width - horizontalPositions[0].getCenterPosition().getY() + TXT_HEIGHT / 2);
            txt.setWrappingWidth(TXT_WIDTH);
            txt.getStyleClass().add(CSS_CLASS_STACKER_TEXT);
            layoutPane.getChildren().add(txt);
            verticalLabels.add(txt);
            int index2 = 1;
            for (StudPosition pos : horizontalPositions) {
                if (index == 1) {
                    if (index2 % 2 == 0) {
                        Text txt2 = new Text("" + index2);
                        txt2.setX(pos.getCenterPosition().getX() - TXT_WIDTH / 2);
                        txt2.setY(width - TXT_HEIGHT / 8);
                        txt2.setWrappingWidth(TXT_WIDTH);
                        txt2.getStyleClass().add(CSS_CLASS_STACKER_TEXT);
                        layoutPane.getChildren().add(txt2);
                        horizontalLabels.add(txt2);
                    } else {
                        Text txt2 = new Text("\u00B7");
                        txt2.setX(pos.getCenterPosition().getX() - TXT_WIDTH / 2);
                        txt2.setY(width - TXT_HEIGHT / 8);
                        txt2.setWrappingWidth(TXT_WIDTH);
                        txt2.getStyleClass().add(CSS_CLASS_STACKER_TEXT);
                        layoutPane.getChildren().add(txt2);
                        horizontalLabels.add(txt2);
                        // draw line
                        Path path = new Path();
                        MoveTo moveTo = new MoveTo();
                        moveTo.setX(pos.getCenterPosition().getX());
                        moveTo.setY(width - pos.getCenterPosition().getY());
                        LineTo lineTo = new LineTo();
                        lineTo.setX(pos.getCenterPosition().getX());
                        lineTo.setY(width - studPositions[studPositions.length - 1][0].getCenterPosition().getY());
                        path.getElements().add(moveTo);
                        path.getElements().add(lineTo);
                        path.getStyleClass().add(CSS_CLASS_LINE);
                        layoutPane.getChildren().add(path);
                    }
                    index2++;
                }
                Circle hole = new Circle(pos.getCenterPosition().getX(), width - pos.getCenterPosition().getY(), stack.getHoleDiameter() / 2);
                holes.add(hole);
                hole.getStyleClass().add(CSS_CLASS_HOLE);
                layoutPane.getChildren().add(hole);
            }
            index++;
        }
    }
	
	private void configureStuds() {
		for (StudPosition[] horizontalPositions : studPositions) {
			for (StudPosition pos : horizontalPositions) {
				if (pos.getStudType() == StudType.NORMAL) {
					Circle circle = new Circle(pos.getCenterPosition().getX(), width - pos.getCenterPosition().getY(),
							stack.getHoleDiameter() / 2);
					circle.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle);
					layoutPane.getChildren().add(circle);
				} else if (pos.getStudType() == StudType.HORIZONTAL_CORNER) {
					SVGPath corner = new SVGPath();
					corner.setContent(CORNER_PATH);
					corner.getStyleClass().add(CSS_CLASS_CORNERSHAPE);
					Translate tr = new Translate();
					tr.setX(pos.getCenterPosition().getX() - 7.5);
					tr.setY(width - pos.getCenterPosition().getY() - 40.5);
					corner.getTransforms().add(tr);
					layoutPane.getChildren().add(corner);
					// add first stud
					Circle circle = new Circle(pos.getCenterPosition().getX(), width - pos.getCenterPosition().getY(),
							stack.getHoleDiameter() / 2);
					circle.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle);
					layoutPane.getChildren().add(circle);
					// add second stud
					Circle circle2 = new Circle(pos.getCenterPosition().getX() + 2,
							width - pos.getCenterPosition().getY(), stack.getHoleDiameter() / 2);
					circle2.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle2);
					layoutPane.getChildren().add(circle2);
				} else if (pos.getStudType() == StudType.HORIZONTAL_CORNER_LEFT) {
					SVGPath corner = new SVGPath();
					corner.setContent(CORNER_PATH_LEFT);
					corner.getStyleClass().add(CSS_CLASS_CORNERSHAPE);
					Translate tr = new Translate();
					tr.setX(pos.getCenterPosition().getX() - 64.5 + 7.5);
					tr.setY(width - pos.getCenterPosition().getY() - 40.5);
					corner.getTransforms().add(tr);
					layoutPane.getChildren().add(corner);
					// add first stud
					Circle circle = new Circle(pos.getCenterPosition().getX(), width - pos.getCenterPosition().getY(),
							stack.getHoleDiameter() / 2);
					circle.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle);
					layoutPane.getChildren().add(circle);
					// add second stud
					Circle circle2 = new Circle(pos.getCenterPosition().getX() - 2,
							width - pos.getCenterPosition().getY(), stack.getHoleDiameter() / 2);
					circle2.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle2);
					layoutPane.getChildren().add(circle2);
				} else if (pos.getStudType() == StudType.TILTED_CORNER
						|| pos.getStudType() == StudType.TILTED_CORNER_RIGHT) {
					SVGPath corner = new SVGPath();
					corner.setContent(CORNER_PATH);
					corner.getStyleClass().add(CSS_CLASS_CORNERSHAPE);
					Rotate rt = new Rotate(-45, 7.5, 15.751);
					Translate tr = new Translate();
					tr.setX(pos.getCenterPosition().getX() - 7.5);
					tr.setY(width - pos.getCenterPosition().getY() - 40.5 + 24.749);
					corner.getTransforms().addAll(tr);
					if (pos.getStudType() == StudType.TILTED_CORNER_RIGHT) {
						rt.setPivotY(15.751);
						rt.setPivotX(7.5);
						corner.getTransforms().add(Translate.scale(-1, 1));
						corner.getTransforms().add(new Translate(-2 - 2, 0));
					}
					corner.getTransforms().add(rt);
					layoutPane.getChildren().add(corner);
					// add first stud
					Circle circle = new Circle(pos.getCenterPosition().getX(), width - pos.getCenterPosition().getY(),
							stack.getHoleDiameter() / 2);
					circle.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle);
					layoutPane.getChildren().add(circle);
					// add second stud
					Circle circle2 = new Circle(pos.getCenterPosition().getX() + 2,
							width - pos.getCenterPosition().getY(), stack.getHoleDiameter() / 2);
					circle2.getStyleClass().add(CSS_CLASS_NORMALSTUD);
					studs.add(circle2);
					layoutPane.getChildren().add(circle2);
				}
			}
		}
	}
	
	private void initStudPositions() {
		int verticalHoleAmount = stack.getVerticalHoleAmount();
		int horizontalHoleAmount = stack.getHorizontalHoleAmount();		
		this.studPositions = new StudPosition[verticalHoleAmount][horizontalHoleAmount];
		for (int i = 0; i < verticalHoleAmount; i++) {//verticalHoleAmount=7
			for (int j = 0; j < horizontalHoleAmount; j++) {
				//Calculate coordinates using the origin from the stacker
				float x = j * stack.getHorizontalHoleDistance() + stack.getHorizontalPadding();
				float y = i * stack.getVerticalHoleDistance() + stack.getVerticalPaddingBottom();
				studPositions[i][j] = new StudPosition(j, i, x, y, StudType.NONE);
			}
		}
	}
	
    private synchronized void configureWorkPieces() {
        List<StackingPosition> stackingPositions = workPiecePositions.stackingPositionList;
        for (int i = 0;i < program.getAmount();i++) {
        	if(stackingPositions.get(i).getAmount() > 0) {
                IDrawableObject workPieceRepre = program.getRawWorkPiece().getRepresentation();
                Shape workPiece = workPieceRepre.createShape();
                Pane markerPane = new Pane();
                markerPane.getChildren().add(workPiece);
                if (workPieceRepre.needsMarkers()) {
                    Rectangle marker = createMarker(program, workPieceRepre);
                    markerPane.getChildren().add(marker);
                }
                //LayoutX - the origin of the piece (left bottom corner)
                markerPane.setLayoutX(stackingPositions.get(i).getCoordinates().getX() - workPieceRepre.getXCorrection());
                //LayoutY - the origin of the piece (left bottom corner)
                markerPane.setLayoutY(width - stackingPositions.get(i).getCoordinates().getY() - workPieceRepre.getYCorrection());
                
                markerPane.setRotate(program.getOrientation()*-1);
                
                layoutPane.getChildren().add(markerPane);
                Text txtAmount = new Text(stackingPositions.get(i).getAmount() + "");
                txtAmount.getStyleClass().add(CSS_CLASS_AMOUNT);
                txtAmount.setX(stackingPositions.get(i).getCoordinates().getX() - txtAmount.getBoundsInParent().getWidth()/2);
                txtAmount.setY(width - stackingPositions.get(i).getCoordinates().getY() + txtAmount.getBoundsInParent().getHeight()/2);
                layoutPane.getChildren().add(txtAmount);  
        	}
        }
    }

    private Rectangle createMarker(Program stackingPosition, IDrawableObject workPiece) {
        Rectangle marker = null;
        if (stackingPosition.getOrientation() == 0) {
            marker = workPiece.createMarker(false);
            if (stack.getHorizontalR() < 0) {
                marker.setTranslateX(workPiece.getXTranslationMarker());
            } else {
                marker.setTranslateX(5);
            }
        } else if (stackingPosition.getOrientation() == 90) {
            marker = workPiece.createMarker(true);
            if (stack.getHorizontalR() < 0) {
                marker.setTranslateY(workPiece.getYTranslationMarker());
            } else {
                marker.setTranslateY(5);
            }
        } else {
            float deltaR = stack.getTiltedR() - stack.getHorizontalR();
            if (deltaR > 0) {
                    marker = workPiece.createMarker(false);
                    if (stack.getTiltedR() > 0 && stackingPosition.getOrientation() < 90) {
                        marker.setTranslateX(5);
                    } else {
                        marker.setTranslateX(workPiece.getXTranslationMarker());
                    }
            } else {
                    marker = workPiece.createMarker(true);
                    if (stack.getTiltedR() > 0 && stackingPosition.getOrientation() < 90) {
                        marker.setTranslateY(5);
                    } else {
                        marker.setTranslateY(workPiece.getYTranslationMarker());
                    }
                }
        }
        return marker;
    }

	private void updateScale() {
		if (layoutPane != null) {
            double scaleVal = Math.min((SizeManager.HEIGHT_BOTTOM  - 2*EXTRA_PADDING ) / (layoutPane.getBoundsInLocal().getHeight()), 
                    (SizeManager.WIDTH - SizeManager.WIDTH_BOTTOM_LEFT - 2*EXTRA_PADDING) / (layoutPane.getBoundsInLocal().getWidth()));
            scaleVal = Math.min(scaleVal, 1.0);
            scale.setX(scaleVal);
            scale.setY(scaleVal);
            if (scaleVal != 1) {
                updateTranslate();
            }
        }
	}

	private void updateTranslate() {
        translate.setX(((SizeManager.WIDTH - SizeManager.WIDTH_BOTTOM_LEFT) - layoutPane.getBoundsInParent().getWidth()) / 2);
        translate.setY((SizeManager.HEIGHT_BOTTOM - layoutPane.getBoundsInParent().getHeight()) / 2);
    }
}
