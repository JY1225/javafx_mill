package cn.greatoo.easymill.ui.set.cnc;

import java.util.ArrayList;
import java.util.List;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.entity.CNCSetting;
import cn.greatoo.easymill.entity.Clamping;
import cn.greatoo.easymill.ui.main.Controller;
import cn.greatoo.easymill.util.IconFlowSelector;
import cn.greatoo.easymill.util.TextInputControlListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;

public class CNCDeviceViewController  extends Controller {
	@FXML
	private GridPane gridPane;
	@FXML
	private Button LBt;
	@FXML
	private Button wBt;
	private IconFlowSelector ifsClampings;
	private static final double ICONFLOWSELECTOR_WIDTH = 530;
	private List<Button> bts;
	private String programName;	
	
	public void init() {
		bts = new ArrayList<Button>();
		bts.add(LBt);
		bts.add(wBt);
		
		ifsClampings = new IconFlowSelector(false);
        ifsClampings.setPrefWidth(ICONFLOWSELECTOR_WIDTH);
        gridPane.add(ifsClampings, 0, 2, 2, 1);
        programName = DBHandler.getInstance().getProgramName();      
        
        if(DBHandler.getInstance().getProgramBuffer().get(programName).getCncSetting().getClampingType() != null) {
        	if(DBHandler.getInstance().getProgramBuffer().get(programName).getCncSetting().getClampingType().equals(CNCSetting.ClampingType.LENGTH)) {
        		isClicked(bts, LBt);
        	}else {
        		isClicked(bts, wBt);
        	}
        }
        refresh();
	}
	@FXML
	public void LBtAction(ActionEvent event) {
		isClicked(bts, LBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getCncSetting().setClampingType(CNCSetting.ClampingType.LENGTH);
	}
	
	@FXML
	public void wBtAction(ActionEvent event) {
		isClicked(bts, wBt);
		DBHandler.getInstance().getProgramBuffer().get(programName).getCncSetting().setClampingType(CNCSetting.ClampingType.WIDTH);
	}
	@Override
	public void setMessege(String mess) {
		
	}
	public void refresh() {
		ifsClampings.clearItems();
		List<Clamping> clampings = DBHandler.getInstance().getClampBuffer();
		int itemIndex = 0;
		for (final Clamping clamping : clampings) {
			ifsClampings.addItem(itemIndex, clamping.getName(), clamping.getImageUrl(), new EventHandler<MouseEvent>() {
				@Override
				public void handle(final MouseEvent event) {
					changedGripper(clamping);					
				}
			});
			itemIndex++;
		}
		setSelectedGripper();
	}
	public void setSelectedGripper() {
		ifsClampings.deselectAll();
		ifsClampings.setSelected(DBHandler.getInstance().getClampBuffer().get(0).getName());		
	}
	
	public void changedGripper(final Clamping clamping) {
		ifsClampings.deselectAll();
		ifsClampings.setSelected(clamping.getName());		
		DBHandler.getInstance().getClampBuffer().get(0).setName(clamping.getName());
		System.out.println(clamping.getName());
	}
	@Override
	public void setTextFieldListener(TextInputControlListener listener) {
		// TODO Auto-generated method stub
		
	}
}
