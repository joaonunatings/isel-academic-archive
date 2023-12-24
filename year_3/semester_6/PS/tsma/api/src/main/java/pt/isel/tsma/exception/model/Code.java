package pt.isel.tsma.exception.model;

import lombok.Getter;

@Getter
public enum Code {

	// Calendar codes: 1xxx
	CALENDAR_NOT_FOUND(1000),
	INVALID_CALENDAR(1001),
	MEMBER_ALREADY_IN_CALENDAR(1202),
	DUPLICATE_TITLE(1012),

	// Member codes: 2xxx
	MEMBER_NOT_FOUND(2000),
	INVALID_MEMBER(2001),
	INVALID_EMAIL(2011),
	INVALID_NAME(2021),
	DUPLICATE_EMAIL(2012),

	// Report codes: 3xxx
	REPORT_NOT_FOUND(3000),
	INVALID_REPORT(3001),

	// Shift codes: 4xxx
	SHIFT_NOT_FOUND(4000),
	INVALID_SHIFT(4001),

	// Graph errors: 5xxx
	GRAPH_SYNC_ERROR(5000),

	// Paging & sorting errors: 6xxx
	INVALID_PAGE_NUMBER(6000),
	INVALID_PAGE_SIZE(6001),
	INVALID_SORT(6002),

	// General errors 9xxx
	RESOURCE_NOT_FOUND(9000),
	INVALID_PARAMETER(9001),
	UNAUTHORIZED(9002),
	UNKNOWN_ERROR(9999);
	private final int value;

	Code(int value) {
		this.value = value;
	}
}
