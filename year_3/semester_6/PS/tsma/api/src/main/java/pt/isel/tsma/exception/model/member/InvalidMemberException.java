package pt.isel.tsma.exception.model.member;

import pt.isel.tsma.exception.InvalidParameterException;

import static pt.isel.tsma.exception.model.Code.INVALID_MEMBER;

public class InvalidMemberException extends InvalidParameterException {

	public InvalidMemberException(long memberId) {
		super(INVALID_MEMBER, "Member #" + memberId + " not found or with invalid ID");
	}
}
