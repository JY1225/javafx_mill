package cn.greatoo.easymill.external.communication.socket;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cn.greatoo.easymill.entity.Coordinates;
import cn.greatoo.easymill.util.RobotConstants;

public class RobotSocketCommunication extends ExternalSocketCommunication {

	private StringBuffer command;
	//private AbstractRobot robot;
	private static Logger logger = LogManager.getLogger(RobotSocketCommunication.class.getName());
		
	public RobotSocketCommunication(final SocketConnection socketConnection) {
		super(socketConnection);
		this.command = new StringBuffer();
	}

	public synchronized void writeValues(final int commandId, final int ackId, final int timeout, final List<String> values) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		command = new StringBuffer();
		command.append(commandId);
		command.append(";");
		for (String value : values) {
			command.append(value);
			command.append(";");
		}
		getExternalCommunicationThread().clearIncommingBuffer();
		getExternalCommunicationThread().writeString(command.toString());
		awaitResponse(ackId + ";", timeout);
		System.out.println("write to robot : "+command.toString());
	}
	
	public synchronized void writeCommand(final int commandId, final int ackId, final int timeout) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		writeValues(commandId, ackId, timeout, new ArrayList<String>());
	}
	
	public synchronized void writeValue(final int commandId, final int ackId, final int timeout, final String value) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
		List<String> values = new ArrayList<String>();
		values.add(value);
		writeValues(commandId, ackId, timeout, values);
	}
	
	public synchronized List<String> readValues(final int commandId, final int ackId, final int timeout) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
	    getExternalCommunicationThread().writeString(commandId + ";");
	    String responseString = awaitResponse(ackId + ";", timeout);
	    return parseResult(responseString.substring((ackId + ";").length()));
	}

	public synchronized List<String> readValues(final int commandId, final int responseId, final int timeout, final List<String> values) throws SocketDisconnectedException, SocketResponseTimedOutException, InterruptedException, SocketWrongResponseException {
	    command = new StringBuffer();
	    command.append(commandId);
	    command.append(";");
	    for (String value : values) {
	        command.append(value);
	        command.append(";");
	    }
	    getExternalCommunicationThread().clearIncommingBuffer();
	    getExternalCommunicationThread().writeString(command.toString());
	    return parseResult(awaitResponse(responseId + ";", timeout));
	}
	
	public List<String> parseResult(final String response) {
		String[] values = response.split(";");
		return new ArrayList<String>(Arrays.asList(values));
	}
	
	public synchronized Coordinates getPosition(final int waitTimeout) throws SocketResponseTimedOutException, SocketDisconnectedException, InterruptedException, SocketWrongResponseException {
		long currentTime = System.currentTimeMillis();
		boolean timeout = false;
		while (!timeout) {
			if (System.currentTimeMillis() - currentTime >= waitTimeout) {
				timeout = true;
				break;
			}
			List<String> positionValues = readValues(RobotConstants.COMMAND_ASK_POSITION, RobotConstants.RESPONSE_ASK_POSITION, getDefaultWaitTimeout());
			logger.debug("Position values: [" + positionValues + "].");
			float x = Float.parseFloat(positionValues.get(0));
			float y = Float.parseFloat(positionValues.get(1));
			float z = Float.parseFloat(positionValues.get(2));
			float w = Float.parseFloat(positionValues.get(3));
			float p = Float.parseFloat(positionValues.get(4));
			float r = Float.parseFloat(positionValues.get(5));
			return new Coordinates(null,null,x, y, z, w, p, r);
		}
		return null;
	}


	@Override
	public void iOExceptionOccured(final IOException e) {
		// this exception is already logged, and the robot was disconnected as a result
		// TODO handle this error in more detail if needed
	}

	@Override
	public String toString() {
		return "Fanuc robot communication: " + getExternalCommunicationThread().getSocketConnection().toString();
	}

	@Override
	public void connected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub
		
	}
}
