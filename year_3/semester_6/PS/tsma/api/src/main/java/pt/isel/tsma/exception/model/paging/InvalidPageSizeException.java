package pt.isel.tsma.exception.model.paging;

import static pt.isel.tsma.exception.model.Code.INVALID_PAGE_SIZE;

public class InvalidPageSizeException extends InvalidPagingException {
	public InvalidPageSizeException(int pageSize) {
		super(INVALID_PAGE_SIZE, "Invalid page size: " + pageSize + ". Must be greater or equal than 0");
	}
}
