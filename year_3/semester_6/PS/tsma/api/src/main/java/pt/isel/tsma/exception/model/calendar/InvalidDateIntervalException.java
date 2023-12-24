package pt.isel.tsma.exception.model.calendar;

import pt.isel.tsma.exception.InvalidParameterException;

import java.time.LocalDate;

import static pt.isel.tsma.exception.model.Code.INVALID_CALENDAR;

public class InvalidDateIntervalException extends InvalidParameterException {
	public InvalidDateIntervalException(long calendarDays, int sequenceDays) {
		super(INVALID_CALENDAR, "The calendar days (" + calendarDays + ") must be greater than the sequence days (" + sequenceDays + ")");
	}

	public InvalidDateIntervalException(LocalDate startDate, LocalDate endDate) {
		super(INVALID_CALENDAR, "The start date (" + startDate + ") must be before the end date (" + endDate + ")");
	}
}
