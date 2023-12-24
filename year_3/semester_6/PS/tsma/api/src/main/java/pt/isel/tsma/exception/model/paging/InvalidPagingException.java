package pt.isel.tsma.exception.model.paging;

import pt.isel.tsma.exception.InvalidParameterException;
import pt.isel.tsma.exception.model.Code;

public class InvalidPagingException extends InvalidParameterException {
	public InvalidPagingException(Code code, String message) {
		super(code, message);
	}
}
