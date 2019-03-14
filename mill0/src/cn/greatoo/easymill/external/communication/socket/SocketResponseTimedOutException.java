package cn.greatoo.easymill.external.communication.socket;

public class SocketResponseTimedOutException extends AbstractCommunicationException {

	private static final long serialVersionUID = 1L;

	private static final String RESPONSE_TIMED_OUT_FROM = "ResponseTimedOutException.responseTimedOutFrom";

	private SocketConnection socketConnection;
	private String command;

	public SocketResponseTimedOutException(final SocketConnection socketConnection, final String command) {
		this.socketConnection = socketConnection;
		this.command = command;
	}

	public SocketConnection getConnection() {
		return socketConnection;
	}

	@Override
	public String getMessage() {
		return "Waiting for a response from " + socketConnection + " timed out.";
	}

	@Override
	public String getLocalizedMessage() {
		return "等待响应时超�? " + socketConnection + " - " + command;
	}
}
