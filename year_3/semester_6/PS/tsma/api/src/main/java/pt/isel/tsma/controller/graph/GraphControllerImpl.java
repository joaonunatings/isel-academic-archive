package pt.isel.tsma.controller.graph;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.tsma.service.graph.GraphService;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class GraphControllerImpl implements GraphController {

	private final GraphService graphService;

	@Override
	public void sync() {
		graphService.sync();
	}
}
