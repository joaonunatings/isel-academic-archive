package pt.isel.tsma.controller.graph;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("/graph")
@Tag(name = "Graph", description = "This is used to sync with Outlook using the Graph API")
@SecurityRequirement(name = "bearerAuth")
public interface GraphController {

	@PostMapping("/sync")
	@Operation(summary = "Sync with Outlook")
	void sync();
}
