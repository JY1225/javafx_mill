package cn.greatoo.easymill.ui.set.cnc;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.entity.Process;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;

public class CNCDeviceViewController  extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private Button LBt;
	@FXML
	private Button wBt;
	private IconFlowSelector ifsClamping;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	List<Button> bts;
	public static Clamping clamping = new Clamping();
	
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(LBt);
		bts.add(wBt);
		isClicked(bts, LBt);
		ifsClamping = new IconFlowSelector(false);
        ifsClamping.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        gridPane.add(ifsClamping, 0, 2, 2, 1);
        
        clamping.setClampingType(Clamping.ClampingType.LENGTH);
	}
	@FXML
	public void LBtAction(ActionEvent event) {
		isClicked(bts, LBt);
		clamping.setClampingType(Clamping.ClampingType.LENGTH);
	}
	
	@FXML
	public void wBtAction(ActionEvent event) {
		isClicked(bts, wBt);
		clamping.setClampingType(Clamping.ClampingType.WIDTH);
	}
	@Override
	public void setMessege(String mess) {
		
	}
	
}
