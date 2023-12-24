package pt.isel.tsma.exception.model.member;

import pt.isel.tsma.exception.InvalidParameterException;

import static pt.isel.tsma.exception.model.Code.INVALID_EMAIL;

public class InvalidEmailException extends InvalidParameterException {

	public InvalidEmailException(String email) {
		super(INVALID_EMAIL, "Invalid email: " + email);
	}
}
