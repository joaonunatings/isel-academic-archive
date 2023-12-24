package pt.isel.tsma.exception.model.paging;

import static pt.isel.tsma.exception.model.Code.INVALID_PAGE_NUMBER;

public class InvalidPageNoException extends InvalidPagingException {
	public InvalidPageNoException(int pageNo) {
		super(INVALID_PAGE_NUMBER, "Invalid page number: " + pageNo + ". Must be greater or equal than 0");
	}
}
