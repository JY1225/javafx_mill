package cn.greatoo.easymill.cnc;

import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.external.communication.socket.AbstractCommunicationException;
import cn.greatoo.easymill.external.communication.socket.CNCSocketCommunication;
import cn.greatoo.easymill.external.communication.socket.SocketDisconnectedException;
import cn.greatoo.easymill.external.communication.socket.SocketResponseTimedOutException;
import cn.greatoo.easymill.external.communication.socket.SocketWrongResponseException;
import cn.greatoo.easymill.process.StatusChangedEvent.Mode;
import cn.greatoo.easymill.util.Clamping;
import cn.greatoo.easymill.util.Clamping.Type;
import cn.greatoo.easymill.util.Coordinates;
import cn.greatoo.easymill.workpiece.WorkPiece;
import eu.robojob.millassist.external.device.AbstractDeviceActionSettings;
import eu.robojob.millassist.external.device.ClampingManner;
import eu.robojob.millassist.external.device.DeviceInterventionSettings;
import eu.robojob.millassist.external.device.DevicePickSettings;
import eu.robojob.millassist.external.device.DevicePutSettings;
import eu.robojob.millassist.external.device.EFixtureType;
import eu.robojob.millassist.external.device.processing.ProcessingDeviceStartCyclusSettings;
import eu.robojob.millassist.process.ProcessFlow;

public class CNCMillingMachineDevIntv extends AbstractCNCMachine {
	
	private CNCSocketCommunication cncMachineCommunication;

	private static final int PREPARE_PUT_TIMEOUT = 2 * 60 * 1000;
	private static final int PREPARE_PICK_TIMEOUT = 2 * 60 * 1000;
	private static final int CLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int UNCLAMP_TIMEOUT = 1 * 60 * 1000;
	private static final int START_CYCLE_TIMEOUT = 3 * 60 * 1000;
	private static final int SLEEP_TIME_AFTER_RESET = 500;
	private static final int OPERATOR_RQST_BLUE_LAMP_VAL = 5;
	private static final int FINISH_BLUE_LAMP_VAL = 10;
	
	private static Logger logger = LogManager.getLogger(CNCMillingMachineDevIntv.class.getName());
	
	public CNCMillingMachineDevIntv(final CNCSocketCommunication socketConnection, final MCodeAdapter mCodeAdapter, final EWayOfOperating wayOfOperating) {
		super(socketConnection, mCodeAdapter,wayOfOperating);
		this.cncMachineCommunication = socketConnection;		
	}
	
	@Override
	public CNCSocketCommunication getCNCSocketCommunication() {
		return this.cncMachineCommunication;
	}
	
	@Override
	public void updateStatusAndAlarms() throws InterruptedException, SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException {
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
		setAlarms(CNCMachineAlarm.parseCNCAlarms(getCncMachineTimeout(), alarmReg1, alarmReg2, alarmReg3, alarmReg4, alarmReg5, alarmReg6));
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			// read robot service 
			int robotServiceInputs = (cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv.STATUS_SLOT_1, 1)).get(0);
			getMCodeAdapter().updateRobotServiceInputsDevIntv(robotServiceInputs);
		}
	}
	
	@Override
	public void reset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		command = command | CNCMachineConstantsDevIntv.IPC_RESET_CMD;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.IPC_COMMAND, registers);
		setCncMachineTimeout(null);
	}
	
	@Override
	public void nCReset() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, DeviceActionException, SocketWrongResponseException {
		int command = 0;
		resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_NC_RESET_OK);
		int maxResetTime = cncMachineCommunication.readRegisters(CNCMachineConstantsDevIntv.PAR_MACHINE_MAX_NC_RESET_TIME, 1).get(0);
		command = command | CNCMachineConstantsDevIntv.IPC_NC_RESET_CMD;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.IPC_COMMAND, registers);
		try {
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_NC_RESET_OK, maxResetTime);
		} catch (DeviceActionException e) {
			e.printStackTrace();
		}
		Thread.sleep(SLEEP_TIME_AFTER_RESET);
	}

	@Override
	public void powerOff() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		// Direct power off command
		command = command | CNCMachineConstantsDevIntv.OUT_MACHINE_POWER_OFF;
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.OUTPUT_SLOT_1, registers);
		// normally no more commands after this, so multiple IPC requests problem can't occur
	}

	@Override
	public void indicateAllProcessed() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, DeviceActionException, SocketWrongResponseException {
		// Still something todo?
		nCReset();
		int[] registers = {FINISH_BLUE_LAMP_VAL};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.PAR_MACHINE_BLUE_LAMP, registers);
	}

	@Override
	public void indicateOperatorRequested(final boolean requested) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		int command = 0;
		if (requested) {
			command = OPERATOR_RQST_BLUE_LAMP_VAL;
		}
		int[] registers = {command};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.PAR_MACHINE_BLUE_LAMP, registers);
	}
	
	@Override
	public void clearIndications() throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		// reset blue lamp
		indicateOperatorRequested(false);
	}
	
	@Override
	public void prepareForProcess()  throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		// check work area
		for(WorkAreaManager waManager: getWorkAreaManagers()) {
			waManager.resetNbPossibleWPPerClamping(getWayOfOperating().getNbOfSides());
			waManager.setMaxClampingsToUse(getWayOfOperating().getNbOfSides() * waManager.getNbActiveClampingsEachSide());
		}
		//FIXME review! potential problems with reset in double processflow execution
		//只对机床clear
		clearIndications();		
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			// wait half a second
			Thread.sleep(500);
			
			int selectionCommand = 0;
			int zoneNr = this.getWorkAreaManagers().get(0).getZone().getZoneNr();
			if(zoneNr == 1) {
				selectionCommand = selectionCommand |  CNCMachineConstantsDevIntv.ZONE1_SELECT;
			} else if (zoneNr == 2) {
				selectionCommand = selectionCommand |  CNCMachineConstantsDevIntv.ZONE2_SELECT;
			} else {
				throw new IllegalArgumentException("Unknown zone number: " + zoneNr);
			}
			int startCommand = 0;
			startCommand = startCommand | CNCMachineConstantsDevIntv.IPC_START_CNC_CMD;
			
			int[] registers = {selectionCommand, startCommand};
			cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers);
		}
	}

	
	@Override
	public void prepareForPick(final DevicePickSettings pickSettings, final int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}

		// if way of operation is m codes, await unloading m code!
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
			waitForMCodes(processId, mCodeUnLoad);
		}
		
		if (pickSettings.getMachineAirblow()) {
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
		fixSelectCommand = fixSelectCommand | selectZone(pickSettings);
		fixSelectCommand = fixSelectCommand | selectWorkArea(pickSettings);
		fixSelectCommand = fixSelectCommand | selectFixture(pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true, pickSettings.getWorkArea().getSequenceNb()).getFixtureType());
		int command2 = 0 | CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PICK_CMD;
		
		int[] registers = {fixSelectCommand, command2};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers);

		boolean pickReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PICK_OK, PREPARE_PICK_TIMEOUT);
		if(!pickReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PICK_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PICK_OK);
			setCncMachineTimeout(null);
		}
		monitorPickThread.startExecution(pickSettings.getStep().getRobot());
	}

	@Override
	public void prepareForPut(final DevicePutSettings putSettings, final int processId) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
		// check a valid workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}		
		// if way of operation is m codes, await loading m code!
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			int mCodeLoad = getMCodeIndex(putSettings.getWorkArea(), true);
			waitForMCodes(processId, mCodeLoad);
		}
		
		if (putSettings.getMachineAirblow()) {
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
		    if (putSettings.isClampingPressureLow()) {
		        clampingPressure = CNCMachineConstantsDevIntv.PRESSURE_LEVEL_SELECT_LOW;
		    } else {
		        clampingPressure = CNCMachineConstantsDevIntv.PRESSURE_LEVEL_SELECT_HIGH;
		    }
		}
		int[] pressureValue = {clampingPressure};
	    cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.PRESSURE_LEVEL_SELECT, pressureValue);

		
		// Create prepare for put command
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(putSettings);
		fixSelectCommand = fixSelectCommand | selectWorkArea(putSettings);
		fixSelectCommand = fixSelectCommand | selectFixture(putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false, putSettings.getWorkArea().getSequenceNb()).getFixtureType());
		int command2 = 0 | CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PUT_CMD;
		int[] registers = {fixSelectCommand, command2};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers);
		
		boolean putReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PUT_OK, PREPARE_PUT_TIMEOUT);
		if(!putReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.PREPARE_PUT_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_PREPARE_FOR_PUT_OK);
			setCncMachineTimeout(null);
		}
		monitorPutThread.startExecution(putSettings.getStep().getRobot());
	}

	@Override
	public void releasePiece(final DevicePickSettings pickSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
        monitorPickThread.stopMonitoring();
	    // check a valid workarea is selected 
		if (!getWorkAreaNames().contains(pickSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + pickSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_UNCLAMP_OK);
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(pickSettings);
		fixSelectCommand = fixSelectCommand | selectWorkArea(pickSettings);
		fixSelectCommand = fixSelectCommand | selectFixture(pickSettings.getWorkArea().getWorkAreaManager().getActiveClamping(true, pickSettings.getWorkArea().getSequenceNb()).getFixtureType());
		int actionCommand = 0 | CNCMachineConstantsDevIntv.IPC_UNCLAMP_CMD;
		
		int[] registers = {fixSelectCommand, actionCommand};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers);
		
		boolean unclampReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_UNCLAMP_OK, UNCLAMP_TIMEOUT);
		if(!unclampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_UNCLAMP_OK);
			setCncMachineTimeout(null);
		}
	}

	@Override
	public void grabPiece(final DevicePutSettings putSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
	      monitorPutThread.stopMonitoring();
	    // check a valid workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_CLAMP_OK);
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZone(putSettings);
		fixSelectCommand = fixSelectCommand | selectWorkArea(putSettings);
		fixSelectCommand = fixSelectCommand | selectFixture(putSettings.getWorkArea().getWorkAreaManager().getActiveClamping(false, putSettings.getWorkArea().getSequenceNb()).getFixtureType());
		int actionCommand = 0 | CNCMachineConstantsDevIntv.IPC_CLAMP_CMD;
		
		int[] registers = {fixSelectCommand, actionCommand};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers);
		
		boolean clampReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_CLAMP_OK, CLAMP_TIMEOUT);
		if(!clampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.CLAMP_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_CLAMP_OK);
			setCncMachineTimeout(null);
		}
	}

	@Override
	public boolean canPut(final DevicePutSettings putSettings) throws InterruptedException, DeviceActionException {
		// check first workarea is selected 
		if (!getWorkAreaNames().contains(putSettings.getWorkArea().getName())) {
			throw new IllegalArgumentException("Unknown workarea: " + putSettings.getWorkArea().getName() + " valid workareas are: " + getWorkAreaNames());
		}
		if ((getWayOfOperating() == EWayOfOperating.M_CODES) || (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD)) {
			int mCodeLoad = getMCodeIndex(putSettings.getWorkArea(), true);
			return getMCodeAdapter().isMCodeActive(mCodeLoad);
		}
		return true;
	}
	
	// this is not taken into account on the Machine-side for now
	@Override
	public boolean canPick(final DevicePickSettings pickSettings) throws AbstractCommunicationException {
		return true;
	}
	
	@Override
	public boolean canIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, DeviceActionException {
		return true;
	}

	@Override
	public void prepareForIntervention(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException, InterruptedException {
		indicateOperatorRequested(true);
	}
	
	@Override 
	public void pickFinished(final DevicePickSettings pickSettings, final int processId) throws AbstractCommunicationException, InterruptedException, DeviceActionException {
		if (getWayOfOperating() == EWayOfOperating.M_CODES) {
			if (isLastWorkPiece(pickSettings)) {
				// last work piece: send reset in stead of finishing m code
				nCReset();
			} else {
				int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
				finishMCode(processId, mCodeUnLoad);
				Thread.sleep(500);
			}
		} else if (getWayOfOperating() == EWayOfOperating.M_CODES_DUAL_LOAD) {
			if (isLastWorkPiece(pickSettings)) {
				// last work piece: send reset in stead of finishing m code
				unclampAfterFinish(pickSettings.getWorkArea().getWorkAreaManager());
				// thread.sleep(500) after every request to the CNC machine - otherwise we could get the error of multiple IPC request (CNCMachine.Alarm 23)
				Thread.sleep(500);
				nCReset();
				// also finish m code if still active after nc reset
				Thread.sleep(500);
				// also finish m code if still active after nc reset
				for (int activeMCode : getMCodeAdapter().getActiveMCodes()) {
					finishMCode(processId, activeMCode);
					Thread.sleep(500);
				}
			} else {
				int mCodeUnLoad = getMCodeIndex(pickSettings.getWorkArea(), false);
				finishMCode(processId, mCodeUnLoad);
				Thread.sleep(500);
				int nbActiveClampings = getNbClampingsInUse(processId);
				// We are going to put the piece that we have just picked back to the stacker ( +1), so in fact we have finished getFinishedAmount + 1.
				// There are maximum nbActiveClampings workPieces still in the flow. 
				if ((pickSettings.getStep().getProcessFlow().getFinishedAmount() + 1 + nbActiveClampings == pickSettings.getStep().getProcessFlow().getTotalAmount()) &&
						(pickSettings.getStep().getProcessFlow().getType() != ProcessFlow.Type.CONTINUOUS)) {
					if (!pickSettings.getStep().getRobotSettings().getWorkPiece().getType().equals(WorkPiece.Type.HALF_FINISHED)) {
						int mCodeLoad = getMCodeIndex(pickSettings.getWorkArea(), true);
						// last but one work piece: no upcoming put, but we wait for the upcoming LOAD M-code and confirm it
						mCodeLoad = getNxtMCode(mCodeLoad, pickSettings.getStep().getProcessFlow().getNbCNCInFlow());
						mCodeLoad = getNxtMCode(mCodeLoad, pickSettings.getStep().getProcessFlow().getNbCNCInFlow());
						waitForMCodes(processId, mCodeLoad);
						finishMCode(processId, mCodeLoad);
						Thread.sleep(500);
					}
				}
			}
		}
	}
	
	@Override 
	public void putFinished(final DevicePutSettings putSettings) throws AbstractCommunicationException, InterruptedException {}
	
	// these are not taken into account by the machine for now...
	@Override public void interventionFinished(final DeviceInterventionSettings interventionSettings) throws AbstractCommunicationException { }
	@Override public void prepareForStartCyclus(final ProcessingDeviceStartCyclusSettings startCylusSettings) throws AbstractCommunicationException, DeviceActionException, InterruptedException {
	    if (startCylusSettings.getWorkNumber() > 0) {
	        logger.debug("Prepare for process program id " + startCylusSettings.getWorkNumber());
	        int command = startCylusSettings.getWorkNumber();
	        int[] registers = {command};
	        cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.PAR_MACHINE_WORKNUMBER, registers);
	        resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_WORKNB_OK);
	        
	        int wkNbSearchCmd = 0;
	        wkNbSearchCmd = wkNbSearchCmd | CNCMachineConstantsDevIntv.IPC_WORKNB_CMD;
	        int[] registerValue = {wkNbSearchCmd};
	        cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.IPC_COMMAND, registerValue);
	        
	        boolean wkNbReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_WORKNB_OK, START_CYCLE_TIMEOUT);
	        if(!wkNbReady) {
	            // TODO
	            setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
	            
	            waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_WORKNB_OK);
	            setCncMachineTimeout(null);
	        }
	    }
	}

	@Override
	public Coordinates getLocationOrientation(final SimpleWorkArea workArea, final ClampingManner clampType) {
		Coordinates c = new Coordinates(workArea.getWorkAreaManager().getActiveClamping(true, workArea.getSequenceNb()).getRelativePosition());
		if (clampType.getType() == Type.LENGTH) {
			if (clampType.isChanged())  {
				c.setR(c.getR() + getClampingWidthR());
			} else {
				c.setR(c.getR());
			}
		} else {
			if (clampType.isChanged()) {
				c.setR(c.getR());
			} else {
				c.setR(c.getR() + getClampingWidthR());
			}
		}
		return c;
	}
	
	private int selectZone(final AbstractDeviceActionSettings<?> deviceActionSettings) throws IllegalArgumentException {
		return selectZoneByWorkArea(deviceActionSettings.getWorkArea().getWorkAreaManager());
	}
	
	private int selectZoneByWorkArea(final WorkAreaManager workAreaManager) {
		int command = 0;
		if(workAreaManager.getZone().getZoneNr() == 1) {
			command = command | CNCMachineConstantsDevIntv.ZONE1_SELECT;
		} else if (workAreaManager.getZone().getZoneNr() == 2) {
			command = command | CNCMachineConstantsDevIntv.ZONE2_SELECT;
		} else {
			throw new IllegalArgumentException("Unknown zone number: " + workAreaManager.getZone().getZoneNr());
		}
		return command;
	}
	
	private int selectWorkArea(final AbstractDeviceActionSettings<?> deviceActionSettings) throws IllegalArgumentException {
		return selectWorkAreaByWorkArea(deviceActionSettings.getWorkArea().getWorkAreaManager());
	}
	
	private int selectWorkAreaByWorkArea(final WorkAreaManager workAreaManager) {
		int command = 0;
		if(workAreaManager.getWorkAreaNr() == 1) {
			command = command | CNCMachineConstantsDevIntv.WA1_SELECT;
		} else if (workAreaManager.getWorkAreaNr() == 2) {
			command = command | CNCMachineConstantsDevIntv.WA2_SELECT;
		} else {
			throw new IllegalArgumentException("Unknown workarea number: " + workAreaManager.getWorkAreaNr());
		}
		return command;
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
	
	private int selectAllFixtures(final WorkAreaManager workArea) {
		int command = 0;
		for (Clamping clamping: workArea.getClampings()) {
			command = command | selectFixture(clamping.getFixtureType());
		}
		return command;
	}
	
	private void resetStatusValue(final int registerNr, final int value) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, DeviceActionException, SocketWrongResponseException {
		// Read current status from register
		int currentStatus = cncMachineCommunication.readRegisters(registerNr, 1).get(0);
		// Check whether the value is still high (bitwise AND operation)
		if((currentStatus & value) > 0) {
			// Exclusive OR operation 
			int resultValue = currentStatus ^ value;
			int[] registerValue = {resultValue};
			cncMachineCommunication.writeRegisters(registerNr, registerValue);
			waitForStatusGoneDevIntv2(registerNr, value);
		}
	}
	
	private void resetValue(final int registerNr, final int value) throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException {
		int currentStatus = cncMachineCommunication.readRegisters(registerNr, 1).get(0);
		// Check whether the value is still high (bitwise AND operation)
		if((currentStatus & value) > 0) {
			// Exclusive OR operation 
			int resultValue = currentStatus ^ value;
			int[] registerValue = {resultValue};
			cncMachineCommunication.writeRegisters(registerNr, registerValue);
		}
	}
	
	private void setValue(final int registerNr, final int value) throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException {
		int currentStatus = cncMachineCommunication.readRegisters(registerNr, 1).get(0);
		// Check whether the value is still high (bitwise AND operation)
		if((currentStatus & value) == 0) {
			// bitwise OR operation 
			int resultValue = currentStatus | value;
			int[] registerValue = {resultValue};
			cncMachineCommunication.writeRegisters(registerNr, registerValue);
		}
	}

	@Override
	public boolean isConnected() {
		return cncMachineCommunication.isConnected();
	}
	
	@Override
	public void disconnect() {
		cncMachineCommunication.disconnect();
	}
	
	@Override
	public String toString() {
		return "CNCMillingMachine: " + getName();
	}

	@Override
	public boolean isUsingNewDevInt() {
		return true;
	}
	
	private void unclampAfterFinish(final WorkAreaManager workArea) 
			throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException, DeviceActionException {
		resetStatusValue(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_UNCLAMP_OK);
		
		int fixSelectCommand = 0;
		fixSelectCommand = fixSelectCommand | selectZoneByWorkArea(workArea);
		fixSelectCommand = fixSelectCommand | selectWorkAreaByWorkArea(workArea);
		fixSelectCommand = fixSelectCommand | selectAllFixtures(workArea);
		int actionCommand = 0 | CNCMachineConstantsDevIntv.IPC_UNCLAMP_CMD;
		
		int[] registers2 = {fixSelectCommand, actionCommand};
		cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.ZONE_WA_FIX_SELECT, registers2);
		
		boolean clampReady = waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_UNCLAMP_OK, UNCLAMP_TIMEOUT);
		if(!clampReady) {
			setCncMachineTimeout(new CNCMachineAlarm(CNCMachineAlarm.UNCLAMP_TIMEOUT));
			waitForStatusDevIntv2(CNCMachineConstantsDevIntv.IPC_OK, CNCMachineConstantsDevIntv.IPC_UNCLAMP_OK);
			setCncMachineTimeout(null);
		}
	}
	

	
	/**
	 * Finish the m-code given in case it is configured to be finished
	 * 
	 * @param mCodeIndex
	 * @throws SocketResponseTimedOutException
	 * @throws SocketDisconnectedException
	 * @throws SocketWrongResponseException
	 * @throws InterruptedException
	 * @throws DeviceActionException
	 */
	private void finishMCode(final int processId, final int mCodeIndex) 
			throws SocketResponseTimedOutException, SocketDisconnectedException, SocketWrongResponseException, InterruptedException, DeviceActionException {
		Set<Integer> robotServiceOutputs = getMCodeAdapter().getGenericMCode(mCodeIndex).getRobotServiceOutputsUsed();
		int command = 0;
		if (robotServiceOutputs.contains(0)) {
			logger.info("PRC[" + processId + "] FINISH M CODE " + mCodeIndex);
			command = command | CNCMachineConstantsDevIntv.IPC_MC_FINISH_CMD;
			int[] registers = {command};
			cncMachineCommunication.writeRegisters(CNCMachineConstantsDevIntv.IPC_COMMAND, registers);
			waitForNoMCode(processId, mCodeIndex);
		}
	}
	
	/**
	 * Check whether the piece that needs to be picked from the machine is the last work piece of the flow
	 * 
	 * @param 	pickSettings
	 * @return	- false in case the processflow is continuous. This means that we do not have a fixed amount
	 * 			of pieces to do, so we will never reach the last one.
	 * 			- true in case the work piece that needs to be picked is the last one of the process executor
	 * 			(E.g. if one reversal unit is part of the flow, the last work piece of the executor is the one
	 * 			that has been processed after reversal) AND we are in teach mode OR it is the last one of 
	 * 			the entire process
	 * 			- false otherwise
	 */
	private boolean isLastWorkPiece(final DevicePickSettings pickSettings) {
		if (pickSettings.getStep().getProcessFlow().getType().equals(ProcessFlow.Type.CONTINUOUS)) {
			return false;
		}
		//laatste bewerking (workPieceType.FINISHED) - da gaat nog altijd normaal... de laatste zou niet mogen veranderen 
		if (pickSettings.getStep().getRobotSettings().getWorkPiece().getType().equals(WorkPiece.Type.FINISHED)) {
			//final piece of the flow (amount)
			if (pickSettings.getStep().getProcessFlow().getFinishedAmount() == pickSettings.getStep().getProcessFlow().getTotalAmount() - 1) {
				return true;
			} else if (pickSettings.getStep().getProcessFlow().getMode().equals(Mode.TEACH)) {
				return true;
			}
		}
		return false;
	}
	
	private int getNbClampingsInUse(final int processId) {
		int result = 0;
		for (Zone zone: getZones()) {
			for (WorkAreaManager workArea: zone.getWorkAreaManagers()) {
				if (workArea.isInUse()) {
					result += workArea.getMaxNbClampingOtherProcessThread(processId);
				}
			}
		}
		return result;
	}
	
    @Override
    public void stopMonitoringMotionEnablingThreads() {
        monitorPickThread.stopMonitoring();
        monitorPutThread.stopMonitoring();
    }
}
