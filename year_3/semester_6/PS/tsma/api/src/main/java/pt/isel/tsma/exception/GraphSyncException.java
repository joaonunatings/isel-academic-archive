package pt.isel.tsma.exception;

import static pt.isel.tsma.exception.model.Code.GRAPH_SYNC_ERROR;

public class GraphSyncException extends InvalidStateException {
	public GraphSyncException(Exception e) {
		super(e, GRAPH_SYNC_ERROR, "Error while syncing with Outlook");
	}
}
