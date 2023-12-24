package pt.isel.tsma.exception.model.calendar;

import pt.isel.tsma.exception.InvalidParameterException;

import static pt.isel.tsma.exception.model.Code.INVALID_CALENDAR;

public class InvalidCalendarException extends InvalidParameterException {

	public InvalidCalendarException(long calendarId) {
		super(INVALID_CALENDAR, "Calendar #" + calendarId + " not found or with invalid ID");
	}
}
