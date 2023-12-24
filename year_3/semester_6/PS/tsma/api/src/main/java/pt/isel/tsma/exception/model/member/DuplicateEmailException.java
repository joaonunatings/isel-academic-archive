package pt.isel.tsma.exception.model.member;

import pt.isel.tsma.exception.InvalidParameterException;

import static pt.isel.tsma.exception.model.Code.DUPLICATE_EMAIL;

public class DuplicateEmailException extends InvalidParameterException {

	public DuplicateEmailException(String email, long memberId) {
		super(DUPLICATE_EMAIL, "Email " + email + " already being used by member #" + memberId);
	}

	public DuplicateEmailException(String email) {
		super(DUPLICATE_EMAIL, "Email " + email + " already being used");
	}
}
