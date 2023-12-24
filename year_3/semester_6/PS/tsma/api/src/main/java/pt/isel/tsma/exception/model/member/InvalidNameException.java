package pt.isel.tsma.exception.model.member;

import pt.isel.tsma.exception.InvalidParameterException;

import static pt.isel.tsma.exception.model.Code.INVALID_NAME;

public class InvalidNameException extends InvalidParameterException {

	public InvalidNameException(String name) {
		super(INVALID_NAME, "Invalid name: " + name);
	}
}
