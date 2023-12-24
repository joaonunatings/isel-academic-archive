package pt.isel.tsma.exception.security;

public class MissingTokenException extends SecurityException {

	public MissingTokenException() {
		super("No authorization token provided");
	}

	public MissingTokenException(String message) {
		super(message);
	}
}
