package pt.isel.tsma.exception.model.member;

import pt.isel.tsma.exception.ResourceNotFoundException;

import static pt.isel.tsma.exception.model.Code.MEMBER_NOT_FOUND;

public class MemberNotFoundException extends ResourceNotFoundException {

	public MemberNotFoundException(long memberId) {
		super(MEMBER_NOT_FOUND, "Member #" + memberId + " not found");
	}
}
