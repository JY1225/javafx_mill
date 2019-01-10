package cn.greatoo.easymill.cnc;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.db.util.DBHandler;
import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.external.communication.socket.CNCSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.SocketDisconnectedException;
import cn.greatoo.easymill.external.communication.socket.SocketResponseTimedOutException;
import cn.greatoo.easymill.external.communication.socket.SocketWrongResponseException;

public class CNCMachine extends AbstractCNCMachine {

	private CNCSocketCommunication cncMachineCommunication;
	public static CNCMachine INSTANCE = null;
	private static final int PREPARE_PUT_TIMEOUT = 2 * 60 * 1000;
	private static final int PREPARE_PICK_TIMEOUT = 2 * 60 * 1000;
	private static final int CLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int UNCLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int START_CYCLE_TIMEOUT = 3 * 60 * 1000;
	private static final int SLEEP_TIME_AFTER_RESET = 500;
	private static final int OPERATOR_RQST_BLUE_LAMP_VAL = 5;
	private static final int FINISH_BLUE_LAMP_VAL = 10;

	private static Logger logger = LogManager.getLogger(CNCMachine.class.getName());

	public CNCMachine(final CNCSocketCommunication socketConnection, final MCodeAdapter mCodeAdapter,
			final EWayOfOperating wayOfOperating) {
		super(socketConnection, mCodeAdapter, wayOfOperating);
		this.cncMachineCommunication = socketConnection;
	}
	
	public static CNCMachine getInstance(final CNCSocketCommunication socketConnection,final MCodeAdapter mCodeAdapter,
			final EWayOfOperating wayOfOperating) {
		if (INSTANCE == null) {
			INSTANCE = new CNCMachine(socketConnection,mCodeAdapter,wayOfOperating);
		}
		return INSTANCE;
	}
	
	@Override
	public CNCSocketCommunication getCNCSocketCommunication() {
		return this.cncMachineCommunication;
	}

	@Override
	public void updateStatusAndAlarms() throws InterruptedException, SocketResponseTimedOutException,
			SocketDisconnectedException, SocketWrongResponseException {
		List<Integer> statusInts = (cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv.STATUS_SLOT_1, 2));
		setStatus(statusInts.get(0), CNCMachineConstantsDevIntv.STATUS_SLOT_1);
		setStatus(statusInts.get(1), CNCMachineConstantsDevIntv.STATUS_SLOT_2);
		statusInts = (cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv.IPC_BUSY, 3));
		setStatus(statusInts.get(0), CNCMachineConstantsDevIntv.IPC_BUSY);
		setStatus(statusInts.get(1), CNCMachineConstantsDevIntv.IPC_ERROR);
		setStatus(statusInts.get(2), CNCMachineConstantsDevIntv.IPC_OK);
		List<Integer> alarmInts = cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv.ERROR_REG_1, 6);
		int alarmReg1 = alarmInts.get(0);
		int alarmReg2 = alarmInts.get(1);
		int alarmReg3 = alarmInts.get(2);
		int alarmReg4 = alarmInts.get(3);
		int alarmReg5 = alarmInts.get(4);
		int alarmReg6 = alarmInts.get(5);
		setAlarms(CNCMachineAlarm.parseCNCAlarms(getCncMachineTimeout(), alarmReg1, alarmReg2, alarmReg3, alarmReg4,
				alarmReg5, alarmReg6));
		if ((getWayOfOperating() == EWayOfOperating.M_CODES)
				|| (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			// read robot service
			int robotServiceInputs = (cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv.STATUS_SLOT_1,
					1)).get(0);
			getMCodeAdapter().updateRobotServiceInputsDevIntv(robotServiceInputs);
		}
	}

	@SuppressWarnings("unused")
	public void finishMCode(final int processId, final int mCodeIndex) throws SocketResponseTimedOutException,
			SocketDisconnectedException, SocketWrongResponseException, InterruptedException, DeviceActionException {
		Set<Integer> robotServiceOutputs = getMCodeAdapter().getGenericMCode(mCodeIndex).getRobotServiceOutputsUsed();
		int command = 0;
		if (robotServiceOutputs.contains(0)) {
			logger.info("PRC[" + processId + "] FINISH M CODE " + mCodeIndex);
			command = command | CNCMachineConstantsDevIntv.IPC_MC_FINISH_CMD;
			int[] registers = { command };
			cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.IPC_COMMAND, registers);
			waitForNoMCode(processId, mCodeIndex);
		}
	}
	public void prepareForIntervention() throws AbstractCommunicationException, InterruptedException {
		indicateOperatorRequested(true);
	}
	@Override
	public void reset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException,
			SocketWrongResponseException {
		int command = 0;
		command = command | CNCMachineConstantsDevIntv.IPC_RESET_CMD;
		int[] registers = { command };
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.IPC_COMMAND, registers);
		setCncMachineTimeout(null);
	}

	@Override
	public void nCReset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException,
			DeviceActionException, SocketWrongResponseException {
		int command = 0;
		resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_NC_RESET_OK);
		int maxResetTime = cncMachineCommunication
				.readRegisters(CNCMachineConstantsDevIntv.PAR_MACHINE_MAX_NC_RESET_TIME, 1).get(0);
		command = command | CNCMachineConstantsDevIntv.IPC_NC_RESET_CMD;
		int[] registers = { command };
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.IPC_COMMAND, registers);
		try {
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_NC_RESET_OK,
					maxResetTime);
		} catch (DeviceActionException e) {
			e.printStackTrace();
		}
		Thread.sleep(SLEEP_TIME_AFTER_RESET);
	}

	@Override
	public void powerOff() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException,
			SocketWrongResponseException {
		int command = 0;
		// Direct power off command
		command = command | CNCMachineConstantsDevIntv.OUT_MACHINE_POWER_OFF;
		int[] registers = { command };
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.OUTPUT_SLOT_1, registers);
		// normally no more commands after this, so multiple IPC requests problem can't
		// occur
	}

	@Override
	public void indicateAllProcessed() throws SocketResponseTimedOutException, SocketDisconnectedException,
			InterruptedException, DeviceActionException, SocketWrongResponseException {
		// Still something todo?
		nCReset();
		int[] registers = { FINISH_BLUE_LAMP_VAL };
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.PAR_MACHINE_BLUE_LAMP, registers);
	}

	@Override
	public void indicateOperatorRequested(final boolean requested) throws SocketResponseTimedOutException,
			SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		if (requested) {
			command = OPERATOR_RQST_BLUE_LAMP_VAL;
		}
		int[] registers = { command };
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.PAR_MACHINE_BLUE_LAMP, registers);
	}

	@Override
	public void clearIndications() throws SocketResponseTimedOutException, SocketDisconnectedException,
			InterruptedException, SocketWrongResponseException {
		// reset blue lamp
		indicateOperatorRequested(false);
	}

	public void prepareForProcess(int zoneNr) throws SocketResponseTimedOutException, SocketDisconnectedException,
			InterruptedException, SocketWrongResponseException {
		// check work area
		// 只对机床clear
		clearIndications();
		if ((getWayOfOperating() == EWayOfOperating.M_CODES)
				|| (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			// wait half a second
			Thread.sleep(500);

			int selectionCommand = 0;
			if (zoneNr == 1) {
				selectionCommand = selectionCommand | CNCMachineConstantsDevIntv.ZONE1_SELECT;
			} else if (zoneNr == 2) {
				selectionCommand = selectionCommand | CNCMachineConstantsDevIntv.ZONE2_SELECT;
			} else {
				throw new IllegalArgumentException("Unknown zone number: " + zoneNr);
			}
			int startCommand = 0;
			startCommand = startCommand | CNCMachineConstantsDevIntv.IPC_START_CNC_CMD;

			int[] registers = { selectionCommand, startCommand };
			cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers);
		}
	}

	/**
	 * processId 0-teach, 1-auto
	 *
	 */
	public void prepareForPick(boolean isAirblow,final int processId,final int mCodeUnLoad)
			throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			//int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
			waitForMCodes(processId, mCodeUnLoad);
		}
		
		if (isAirblow) {
			logger.debug("Set machine airblow for pick");
			setValue(CNCMachineConstantsDevIntv.CONFIG_5, CNCMachineConstantsDevIntv.CFG_FIX_1_AIRBLOW_PICK);
			setValue(CNCMachineConstantsDevIntv.CONFIG_6, CNCMachineConstantsDevIntv.CFG_FIX_2_AIRBLOW_PICK);
			setValue(CNCMachineConstantsDevIntv.CONFIG_7, CNCMachineConstantsDevIntv.CFG_FIX_3_AIRBLOW_PICK);
			setValue(CNCMachineConstantsDevIntv.CONFIG_8, CNCMachineConstantsDevIntv.CFG_FIX_4_AIRBLOW_PICK);
		} else {
			logger.debug("Reset machine airblow for pick");
			resetValue(CNCMachineConstantsDevIntv.CONFIG_5, CNCMachineConstantsDevIntv.CFG_FIX_1_AIRBLOW_PICK);
			resetValue(CNCMachineConstantsDevIntv.CONFIG_6, CNCMachineConstantsDevIntv.CFG_FIX_2_AIRBLOW_PICK);
			resetValue(CNCMachineConstantsDevIntv.CONFIG_7, CNCMachineConstantsDevIntv.CFG_FIX_3_AIRBLOW_PICK);
			resetValue(CNCMachineConstantsDevIntv.CONFIG_8, CNCMachineConstantsDevIntv.CFG_FIX_4_AIRBLOW_PICK);
		}

		resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PICK_OK);

		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(1);
		fixSelectCommand = fixSelectCommand | selectWorkArea(1);
		fixSelectCommand = fixSelectCommand | selectFixture(EFixtureType.FIXTURE_1);
		int command2 = 0 | CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PICK_CMD;
		
		int[] register = {fixSelectCommand, command2};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, register);

		int[] values = new int[2];
		values[0] = 37;
		values[1] = 1;

		boolean pickReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK,
				CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PICK_OK, PREPARE_PICK_TIMEOUT);
		if (!pickReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PICK_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK,
					CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PICK_OK);
			setCncMachineTimeout(null);
		}
	}

	public void prepareForPut(boolean isAirblow, final int processId,final int mCodeLoad)
			throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected

		// if way of operation is m codes, await loading m code!
		if ((getWayOfOperating() == EWayOfOperating.M_CODES)
				|| (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			//int mCodeLoad = getMCodeIndex(putSettings.getWorkArea(), true);
			 waitForMCodes(processId, mCodeLoad);
		}

		if (isAirblow) {
			logger.debug("Set machine airblow for put");
			setValue(CNCMachineConstantsDevIntv.CONFIG_5, CNCMachineConstantsDevIntv.CFG_FIX_1_AIRBLOW_PUT);
			setValue(CNCMachineConstantsDevIntv.CONFIG_6, CNCMachineConstantsDevIntv.CFG_FIX_2_AIRBLOW_PUT);
			setValue(CNCMachineConstantsDevIntv.CONFIG_7, CNCMachineConstantsDevIntv.CFG_FIX_3_AIRBLOW_PUT);
			setValue(CNCMachineConstantsDevIntv.CONFIG_8, CNCMachineConstantsDevIntv.CFG_FIX_4_AIRBLOW_PUT);
		} else {
			logger.debug("Reset machine airblow for put");
			resetValue(CNCMachineConstantsDevIntv.CONFIG_5, CNCMachineConstantsDevIntv.CFG_FIX_1_AIRBLOW_PUT);
			resetValue(CNCMachineConstantsDevIntv.CONFIG_6, CNCMachineConstantsDevIntv.CFG_FIX_2_AIRBLOW_PUT);
			resetValue(CNCMachineConstantsDevIntv.CONFIG_7, CNCMachineConstantsDevIntv.CFG_FIX_3_AIRBLOW_PUT);
			resetValue(CNCMachineConstantsDevIntv.CONFIG_8, CNCMachineConstantsDevIntv.CFG_FIX_4_AIRBLOW_PUT);
		}

		resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PUT_OK);

		// Set pressure selection value
		int clampingPressure = CNCMachineConstantsDevIntv.PRESSURE_LEVEL_SELECT_DEFAULT;
		if (isClampingPressureSelectable()) {
			if (true) {
				clampingPressure = CNCMachineConstantsDevIntv.PRESSURE_LEVEL_SELECT_LOW;
			} else {
				clampingPressure = CNCMachineConstantsDevIntv.PRESSURE_LEVEL_SELECT_HIGH;
			}
		}
		int[] pressureValue = { clampingPressure };
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.PRESSURE_LEVEL_SELECT, pressureValue);

		// Create prepare for put command
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(1);
		fixSelectCommand = fixSelectCommand | selectWorkArea(1);
		fixSelectCommand = fixSelectCommand | selectFixture(EFixtureType.FIXTURE_1);
		int command2 = 0 | CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PUT_CMD;
		int[] registers = { fixSelectCommand, command2 };
		System.out.println("1-registers: "+registers);
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers);

		boolean putReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK,
				CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PUT_OK, PREPARE_PUT_TIMEOUT);
		if (!putReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PUT_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PUT_OK);
			setCncMachineTimeout(null);
		}
	}
	
	private int selectZone(int i) {
		int command = 0;
		int zn = DBHandler.getInstance().getZoneNr(i);
		if(zn == 1) {
			command = command | CNCMachineConstantsDevIntv.ZONE1_SELECT;
		} else if (zn == 2) {
			command = command | CNCMachineConstantsDevIntv.ZONE2_SELECT;
		} else {
			throw new IllegalArgumentException("Unknown zone number: " + DBHandler.getInstance().getZoneNr(i));
		}
		return command;
	}
	
	int workAreaNr;
	private int selectWorkArea(int i) {	
		int command = 0;
		int wa = DBHandler.getInstance().getWorkArea(i);
		switch (wa) {
		case 3:
			this.workAreaNr = 1;
			break;
		case 4:
			this.workAreaNr = 2;
			break;
		default:
			this.workAreaNr = 0;
			break;
		}
		if(workAreaNr == 1) {
			command = command | CNCMachineConstantsDevIntv.WA1_SELECT;
		} else if (workAreaNr == 2) {
			command = command | CNCMachineConstantsDevIntv.WA2_SELECT;
		} else {
			throw new IllegalArgumentException("Unknown workarea number: " + DBHandler.getInstance().getWorkArea(i));
		}
		
		return command;
	}

	public boolean canPut(int mCodeLoad) throws InterruptedException, DeviceActionException {
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {			
			return getMCodeAdapter().isMCodeActive(mCodeLoad);
		}
		return true;
	}
	
	public void putFinished() throws AbstractCommunicationException, InterruptedException {
	}

	public void releasePiece()
			throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected

		resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_UNCLAMP_OK);

		//cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers);
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(1);
		fixSelectCommand = fixSelectCommand | selectWorkArea(1);
		fixSelectCommand = fixSelectCommand | selectFixture(EFixtureType.FIXTURE_1);
		int actionCommand = 0 | CNCMachineConstantsDevIntv.IPC_UNCLAMP_CMD;
		
		int[] register = {fixSelectCommand, actionCommand};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, register);
		
		boolean unclampReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK,
				CNCMachineConstantsDevIntv.IPC_UNCLAMP_OK, UNCLAMP_TIMEOUT);
		if (!unclampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_UNCLAMP_OK);
			setCncMachineTimeout(null);
		}
	}

	public void grabPiece() throws AbstractCommunicationException, DeviceActionException, InterruptedException {

		resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_CLAMP_OK);
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(1);
		fixSelectCommand = fixSelectCommand | selectWorkArea(1);
		fixSelectCommand = fixSelectCommand | selectFixture(EFixtureType.FIXTURE_1);
		int actionCommand = 0 | CNCMachineConstantsDevIntv.IPC_CLAMP_CMD;

		int[] registers = { fixSelectCommand, actionCommand };
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers);

		boolean clampReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK,
				CNCMachineConstantsDevIntv.IPC_CLAMP_OK, CLAMP_TIMEOUT);
		if (!clampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CLAMP_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_CLAMP_OK);
			setCncMachineTimeout(null);
		}
	}

	//
	public void pickFinished(final int processId,boolean isLastWorkPiece)
			throws AbstractCommunicationException, InterruptedException, DeviceActionException {
		if (getWayOfOperating() == EWayOfOperating.M_CODES) {
			if (isLastWorkPiece) { // last work piece: send reset in stead of finishing m code
				nCReset();
			} else {
				int mCodeUnLoad = 1;
				finishMCode(processId, mCodeUnLoad);
				Thread.sleep(500);
			}
		} 
	}

	private void resetStatusValue(final int registerNr, final int value) throws SocketResponseTimedOutException,
			SocketDisconnectedException, InterruptedException, DeviceActionException, SocketWrongResponseException {
		// Read current status from register
		int currentStatus = cncMachineCommunication.readRegisters(registerNr, 1).get(0);
		// Check whether the value is still high (bitwise AND operation)
		if ((currentStatus & value) > 0) {
			// Exclusive OR operation
			int resultValue = currentStatus ^ value;
			int[] registerValue = { resultValue };
			cncMachineCommunication.writeRegisters(registerNr, registerValue);
			waitForStatusGoneDevIntv2(registerNr, value);
		}
	}

	private void resetValue(final int registerNr, final int value) throws SocketResponseTimedOutException,
			SocketDisconnectedException, SocketWrongResponseException, InterruptedException {
		int currentStatus = cncMachineCommunication.readRegisters(registerNr, 1).get(0);
		// Check whether the value is still high (bitwise AND operation)
		if ((currentStatus & value) > 0) {
			// Exclusive OR operation
			int resultValue = currentStatus ^ value;
			int[] registerValue = { resultValue };
			cncMachineCommunication.writeRegisters(registerNr, registerValue);
		}
	}

	private void setValue(final int registerNr, final int value) throws SocketResponseTimedOutException,
			SocketDisconnectedException, SocketWrongResponseException, InterruptedException {
		int currentStatus = cncMachineCommunication.readRegisters(registerNr, 1).get(0);
		// Check whether the value is still high (bitwise AND operation)
		if ((currentStatus & value) == 0) {
			// bitwise OR operation
			int resultValue = currentStatus | value;
			int[] registerValue = { resultValue };
			cncMachineCommunication.writeRegisters(registerNr, registerValue);
		}
	}

	private int selectFixture(final EFixtureType fixtureType) throws IllegalArgumentException {
		int command = 0;
		switch(fixtureType) {
		case FIXTURE_1:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_1;
			break;
		case FIXTURE_2:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_2;
			break;
		case FIXTURE_3:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_3;
			break;
		case FIXTURE_4:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_4;
			break;	
		case FIXTURE_1_2:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_2;
			break;	
		case FIXTURE_1_3:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_3;
			break;	
		case FIXTURE_1_4:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_4;
			break;	
		case FIXTURE_2_3:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_2;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_3;
			break;	
		case FIXTURE_2_4:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_2;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_4;
			break;
		case FIXTURE_3_4:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_3;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_4;
			break;
		case FIXTURE_1_2_3:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_2;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_3;
			break;
		case FIXTURE_1_2_3_4:
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_1;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_2;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_3;
			command = command | CNCMachineConstantsDevIntv.FIX_SELECT_4;
			break;
		default:
			break;
		}
		return command;
	}
	
	@Override
	public void disconnect() {
		cncMachineCommunication.disconnect();
	}

	 @Override
	    public boolean isConnected() {
	        return cncMachineCommunication.isConnected();
	    }

	@Override
	public boolean isUsingNewDevInt() {
		return true;
	}

	@Override
	public void stopMonitoringMotionEnablingThreads() {
		// TODO Auto-generated method stub

	}

}
