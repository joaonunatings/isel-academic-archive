package pt.isel.tsma.service.graph;

import org.springframework.transaction.annotation.Transactional;

public interface GraphService {

	@Transactional
	void sync();
}
