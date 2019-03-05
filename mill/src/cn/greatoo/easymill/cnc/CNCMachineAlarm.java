package cn.greatoo.easymill.cnc;

import java.util.HashSet;
import java.util.Set;

import cn.greatoo.easymill.util.Translator;

public class CNCMachineAlarm {

	public static final int MACHINE = 0;
	public static final int DOOR1_NOT_OPEN = 5;
	public static final int DOOR2_NOT_OPEN = 6;
	public static final int DOOR1_NOT_CLOSED = 7;
	public static final int DOOR2_NOT_CLOSED = 8;
	public static final int CLAMP1_NOT_OPEN = 9;
	public static final int CLAMP2_NOT_OPEN = 10;
	public static final int CLAMP1_NOT_CLOSED = 11;
	public static final int CLAMP2_NOT_CLOSED = 12;
	public static final int WA1_PUT = 13;
	public static final int WA2_PUT = 14;
	public static final int WA1_PICK = 15;
	public static final int WA2_PICK = 16;
	public static final int WA1_CYCLUS_START = 17;
	public static final int WA2_CYCLUS_START = 18;
	public static final int WA1_CLAMP = 19;
	public static final int WA2_CLAMP = 20;
	public static final int WA1_UNCLAMP = 21;
	public static final int WA2_UNCLAMP = 22;
	public static final int MULTIPLE_IPC_REQUESTS = 23;
	
	public static final int CYCLE_NOT_STARTED_TIMEOUT = 101;
	public static final int CYCLE_END_TIMEOUT = 102;
	public static final int PREPARE_PICK_TIMEOUT = 103;
	public static final int PREPARE_PUT_TIMEOUT = 104;
	public static final int UNCLAMP_TIMEOUT = 105;
	public static final int CLAMP_TIMEOUT = 106;
	
	private static final int DEFAULT_PRIORITY = 5;
		
	private int id;
	
	public CNCMachineAlarm(final int id) {
		this.id = id;
	}
	
	public String getLocalizedMessage() {
		return Translator.getTranslation("CNCMachineAlarm." + id);
	}
	
	public String getMessage() {
		return "CNC Machine alarm: id = " + id;
	}
	
	//TODO implement priorities, for now: all the same
	public int getPriority() {
		return DEFAULT_PRIORITY;
	}
	
	public int getId() {
		return id;
	}
	
	@Override
	public int hashCode() {
		return id;
	}
	
	@Override
	public boolean equals(final Object o) {
		if (o instanceof CNCMachineAlarm) {
			if (((CNCMachineAlarm) o).getId() == id) {
				return true;
			}
		}
		return false;
	}
	
	public String toString() {
		return "CNCMachineAlarm: " + id;
	}
	
	//TODO - language file starting from CNCMachineAlarm.1000 - should only be used for the new type of devInt v2
	public static Set<CNCMachineAlarm> parseCNCAlarms(final CNCMachineAlarm timeout, final int... alarmReg) {
		Set<CNCMachineAlarm> alarms = new HashSet<CNCMachineAlarm>();
		int i = 0;
		int j = 1000;
		for(int[] possibleAlarmsRegs: CNCMachineConstantsDevIntv.ERROR_REGS_ARRAY) {
			int alarmRegCNC = alarmReg[i];
			for(int possibleError: possibleAlarmsRegs) {
				if((alarmRegCNC & possibleError) > 0) {
					alarms.add(new CNCMachineAlarm(j));
				}
				j++;
			}
			i++;
		}
		if(timeout != null) {
			alarms.add(timeout);
		}
		return alarms;
	}
}
