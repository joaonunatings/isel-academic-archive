package pt.isel.tsma.exception.model.calendar;

import pt.isel.tsma.exception.InvalidParameterException;

import static pt.isel.tsma.exception.model.Code.MEMBER_ALREADY_IN_CALENDAR;

public class MemberAlreadyInCalendarException extends InvalidParameterException {
	public MemberAlreadyInCalendarException(long memberId, long calendarId) {
		super(MEMBER_ALREADY_IN_CALENDAR, "Member #" + memberId + " is already in calendar #" + calendarId);
	}
}
