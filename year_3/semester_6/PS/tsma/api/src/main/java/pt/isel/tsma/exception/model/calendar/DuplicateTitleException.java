package pt.isel.tsma.exception.model.calendar;

import pt.isel.tsma.exception.InvalidParameterException;

import static pt.isel.tsma.exception.model.Code.DUPLICATE_TITLE;

public class DuplicateTitleException extends InvalidParameterException {

	public DuplicateTitleException(String title) {
		super(DUPLICATE_TITLE, "Calendar with title [" + title + "] already exists");
	}
}
