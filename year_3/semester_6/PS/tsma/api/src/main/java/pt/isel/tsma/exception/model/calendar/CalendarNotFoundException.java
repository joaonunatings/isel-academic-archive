package pt.isel.tsma.exception.model.calendar;

import pt.isel.tsma.exception.ResourceNotFoundException;

import static pt.isel.tsma.exception.model.Code.CALENDAR_NOT_FOUND;

public class CalendarNotFoundException extends ResourceNotFoundException {
	public CalendarNotFoundException(Long calendarId) {
		super(CALENDAR_NOT_FOUND, "Calendar #" + calendarId + " not found");
	}
}
