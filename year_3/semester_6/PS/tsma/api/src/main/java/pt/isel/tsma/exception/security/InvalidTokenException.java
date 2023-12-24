package pt.isel.tsma.exception.security;

public class InvalidTokenException extends SecurityException {
	public InvalidTokenException(String message) {
		super(message);
	}
}
