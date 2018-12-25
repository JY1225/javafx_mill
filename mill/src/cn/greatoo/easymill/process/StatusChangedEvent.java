package cn.greatoo.easymill.process;

public class StatusChangedEvent {

	public enum Mode {
		CONFIG, 	// The initial mode, the ProcessFlow-data has not been checked. 
		READY, 		// The ProcessFlow is ready to be executed.
		TEACH, 		// The ProcessFlow is being executed in 'teach mode'.
		AUTO, 		// The ProcessFlow is being executed in 'auto mode'.
		PAUSED, 	// The Execution of ProcessFlow was paused.
		STOPPED, 	// The Execution of ProcessFlow was stopped.
		FINISHED	// The Execution of ProcessFlow has finished.
	}
	private Mode mode;
	public static final int INACTIVE = 0;
	
	public static final int STARTED = 1;
	public static final int PREPARE_DEVICE = 2;
	public static final int EXECUTE_TEACHED = 3;
	public static final int EXECUTE_NORMAL = 4;
	public static final int INTERVENTION_READY = 5;
	public static final int PROCESSING_STARTED = 6;
	public static final int ENDED = 10;
	
	public static final int TEACHING_NEEDED = 21;
	public static final int TEACHING_FINISHED = 22;
	
	public static final int PREPARE = 30;
	public static final int FINISHED = 31;
	
	private int statusId;
	
	private int processId;//0-pick;1-put;2-intervention
	/**
	 * processId
	 * 0-下料;1-上料;2-中断
	 * @param processId
	 */
	public StatusChangedEvent(final int statusId, final int processId,Mode mode) {
		this.statusId = statusId;
		this.processId = processId;
		this.mode = mode;
	}
	
	public int getStatusId() {
		return statusId;
	}
	
	public int getProcessId() {
		return processId;
	}
	public synchronized Mode getMode() {
		return mode;
	}

	public synchronized void setMode(final Mode mode) {
		if (mode != this.mode) { 
			this.mode = mode;
		}
	}
}
