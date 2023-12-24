package pt.isel.tsma.exception.model.report;

import pt.isel.tsma.exception.ResourceNotFoundException;

import static pt.isel.tsma.exception.model.Code.REPORT_NOT_FOUND;

public class ReportNotFoundException extends ResourceNotFoundException {

	public ReportNotFoundException(long reportId) {
		super(REPORT_NOT_FOUND, "Report #" + reportId + " not found");
	}
}
