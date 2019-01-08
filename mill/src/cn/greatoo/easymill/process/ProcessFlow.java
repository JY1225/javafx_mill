package cn.greatoo.easymill.process;

import java.sql.Timestamp;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.device.ClampingManner;
import cn.greatoo.easymill.util.PropertyManager;
import cn.greatoo.easymill.util.PropertyManager.Setting;

public class ProcessFlow {
			
	private Integer finishedAmount;

	private int id;
	
	private String name;
	private Timestamp creation;
	private Timestamp lastOpened;
	
	private boolean hasChangesSinceLastSave;
	
	
	private static Logger logger = LogManager.getLogger(ProcessFlow.class.getName());
	
	public static final int WORKPIECE_0_ID = 0;
	public static final int WORKPIECE_1_ID = 1;
	public static final int WORKPIECE_2_ID = 2;
		
	private ClampingManner clampingManner;
	private boolean isSingleCycle;

	public ProcessFlow(final String name, final ClampingManner clampingManner, final Timestamp creation, final Timestamp lastOpened) {
		this.name = name;
		this.clampingManner = clampingManner;

		this.creation = creation;
		this.lastOpened = lastOpened;
		this.hasChangesSinceLastSave = false;
	}

	public ProcessFlow(final String name,final Timestamp creation, final Timestamp lastOpened) {
		this(name, new ClampingManner(), creation, lastOpened);
	}
	

	public void setId(final int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}
	
	public Timestamp getCreation() {
		return creation;
	}

	public void setCreation(final Timestamp creation) {
		this.creation = creation;
	}

	public Timestamp getLastOpened() {
		return lastOpened;
	}

	public void setLastOpened(final Timestamp lastOpened) {
		this.lastOpened = lastOpened;
	}


	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	
//	public void addStep(final int index, final AbstractProcessStep newStep) {
//		//Adds the newStep and shift all the other steps that follow to the right (@see List.add(index, Object))
//		processSteps.add(index, newStep);
//		newStep.setProcessFlow(this);
//	}
//	
//	public void addStep(final AbstractProcessStep newStep) {
//		processSteps.add(newStep);
//		newStep.setProcessFlow(this);
//	}
	
	
//	public void removeStep(final AbstractProcessStep step) {
//		processSteps.remove(step);
//		initialize();		// always initialize after updates to the process flow
//	}
//	
//	public void removeSteps(final List<AbstractProcessStep> steps) {
//		processSteps.removeAll(steps);
//		initialize();		// always initialize after updates to the process flow
//	}
//	
//	public void addStepAfter(final AbstractProcessStep step, final AbstractProcessStep newStep) {
//		if (processSteps.indexOf(step) == -1) {
//			throw new IllegalArgumentException("Could not find step: [" + step + "].");
//		} else {
//			processSteps.add(processSteps.indexOf(step) + 1, newStep);
//			newStep.setProcessFlow(this);
//		}
//		initialize();		// always initialize after updates to the process flow
//	}
//	
//	public void addStepBefore(final AbstractProcessStep step, final AbstractProcessStep newStep) {
//		if (processSteps.indexOf(step) == -1) {
//			throw new IllegalArgumentException("Could not find this step");
//		} else {
//			processSteps.add(processSteps.indexOf(step), newStep);
//			newStep.setProcessFlow(this);
//		}
//		initialize();		// always initialize after updates to the process flow
//	}
//	
	


	public ClampingManner getClampingType() {
		return clampingManner;
	}

	public void setClampingType(final ClampingManner clampingType) {
		this.clampingManner = clampingType;
	}
	
	public String toString() {
		return "ProcessFlow: " + getName();
	}
	
	public boolean hasSingleCycleSetting() {
		return PropertyManager.hasSettingValue(Setting.SINGLE_CYCLE, "true");
	}
	
	public void setSingleCycle(boolean isSingleCycle) {
		this.isSingleCycle = isSingleCycle;
	}
	
	public boolean isSingleCycle() {
		return this.isSingleCycle;
	}
	
	
	//In case this function is called by the activeProcessFlow, the activeProcessFlow will be changed to the one in the argument. (Actually this should be managed by the ProcessFlowManager)
	public void loadFromOtherProcessFlow(final ProcessFlow processFlow) {

		this.id = processFlow.getId();
		this.name = processFlow.getName();
		this.creation = processFlow.getCreation();
		this.lastOpened = new Timestamp(System.currentTimeMillis());
		this.isSingleCycle = processFlow.isSingleCycle();
		this.clampingManner.setType(processFlow.getClampingType().getType());
	}

	public void initialize() {
		// TODO Auto-generated method stub
		
	}
	
	public boolean hasBasicStackPlateForFinishedPieces() {
//        for(AbstractProcessStep processStep: processSteps) {
//            if(processStep instanceof PutStep) {
//                if(((PutStep) processStep).getDevice().getType().equals(EDeviceGroup.BASIC_STACK_PLATE)) {
//                    return true;
//                }
//            }
//        }
        return false;
    }

}
