package pt.isel.tsma.exception.model.paging;

import java.util.List;

import static pt.isel.tsma.exception.model.Code.INVALID_SORT;

public class InvalidSortException extends InvalidPagingException {
	public InvalidSortException(String sort, List<String> validSorts) {
		super(INVALID_SORT, "Invalid sort parameter: " + sort + ". Valid sort parameters: " + validSorts);
	}
}

