package pt.isel.tsma.exception.model.report;

import pt.isel.tsma.entity.model.shift.Type;
import pt.isel.tsma.exception.InvalidStateException;

import static pt.isel.tsma.exception.model.Code.INVALID_REPORT;

public class InvalidReportException extends InvalidStateException {

	public InvalidReportException(long calendarId, long memberId, Type shiftType) {
		super(INVALID_REPORT, "Invalid or not found report: calendarId=" + calendarId + ", memberId=" + memberId + ", shiftType=" + shiftType);
	}

}
