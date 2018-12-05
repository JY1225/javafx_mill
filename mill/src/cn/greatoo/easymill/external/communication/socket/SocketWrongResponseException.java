package cn.greatoo.easymill.external.communication.socket;

public class SocketWrongResponseException extends AbstractCommunicationException {

	private static final long serialVersionUID = 1L;

	private static final String WRONG_RESPONSE = "WrongResponseException.wrongResponse";

	private SocketConnection connection;
	private String command;
	private String response;

	public SocketWrongResponseException(final SocketConnection socketConnection, final String command,
			final String response) {
		this.connection = socketConnection;
		this.command = command;
		this.response = response;
	}

	public SocketConnection getConnection() {
		return connection;
	}

	@Override
	public String getMessage() {
		return "Wrong response from: " + connection.toString() + " - command was: " + command + " but reponse was: "
				+ response;
	}

	@Override
	public String getLocalizedMessage() {
		return "错误回应 " + connection.toString() + ".";
	}
}
