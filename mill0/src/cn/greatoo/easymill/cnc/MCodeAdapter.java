package cn.greatoo.easymill.cnc;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MCodeAdapter {
	private int id;
	private List<GenericMCode> genericMCodes;
	private List<String> robotServiceInputNames;
	private List<String> robotServiceOutputNames;
	private Set<Integer> activeRobotServiceInputs;
	private Set<Integer> activeMCodes;
	
	public MCodeAdapter(int id, final List<GenericMCode> genericMCodes, final List<String> robotServiceInputNames,
			final List<String> robotServiceOutputNames) {
		this.id = id;
		this.genericMCodes = genericMCodes;
		this.robotServiceInputNames = robotServiceInputNames;
		this.robotServiceOutputNames = robotServiceOutputNames;
		this.activeRobotServiceInputs = new HashSet<Integer>();
		this.activeMCodes = new HashSet<Integer>();
	}
	public MCodeAdapter(final List<GenericMCode> genericMCodes, final List<String> robotServiceInputNames,
			final List<String> robotServiceOutputNames) {
		this.genericMCodes = genericMCodes;
		this.robotServiceInputNames = robotServiceInputNames;
		this.robotServiceOutputNames = robotServiceOutputNames;
		this.activeRobotServiceInputs = new HashSet<Integer>();
		this.activeMCodes = new HashSet<Integer>();
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<String> getRobotServiceInputNames() {
		return robotServiceInputNames;
	}

	public void setRobotServiceInputNames(final List<String> robotServiceInputNames) {
		this.robotServiceInputNames = robotServiceInputNames;
	}

	public List<String> getRobotServiceOutputNames() {
		return robotServiceOutputNames;
	}

	public void setRobotServiceOutputNames(final List<String> robotServiceOutputNames) {
		this.robotServiceOutputNames = robotServiceOutputNames;
	}

	
	public synchronized void updateRobotServiceInputsDevIntv(final int serviceRegisterValue) {
		this.activeRobotServiceInputs.clear();
		if ((serviceRegisterValue & CNCMachineConstantsDevIntv.R_MACHINE_M_CODE_1) > 0) {
			activeRobotServiceInputs.add(0);
		}
		if ((serviceRegisterValue & CNCMachineConstantsDevIntv.R_MACHINE_M_CODE_2) > 0) {
			activeRobotServiceInputs.add(1);
		}
		if ((serviceRegisterValue & CNCMachineConstantsDevIntv.R_MACHINE_M_CODE_3) > 0) {
			activeRobotServiceInputs.add(2);
		}
		if ((serviceRegisterValue & CNCMachineConstantsDevIntv.R_MACHINE_M_CODE_4) > 0) {
			activeRobotServiceInputs.add(3);
		}
		this.activeMCodes.clear();
		for (int i = 0; i < genericMCodes.size(); i++) {
			GenericMCode mCode = genericMCodes.get(i);
			if (mCode.isActive(activeRobotServiceInputs)) {
				this.activeMCodes.add(i);
			}
		}
	}
	
	public synchronized boolean isMCodeActive(final int mCodeIndex) {
		if (activeMCodes.contains(mCodeIndex)) {
			return true;
		}
		return false;
	}
	
	public synchronized Set<Integer> getActiveMCodes() {
		return activeMCodes;
	}
	
	public GenericMCode getGenericMCode(final int index) {
		return genericMCodes.get(index);
	}

	public List<GenericMCode> getGenericMCodes() {
		return genericMCodes;
	}

	public void setGenericMCodes(final List<GenericMCode> genericMCodes) {
		this.genericMCodes = genericMCodes;
	}
	
}
