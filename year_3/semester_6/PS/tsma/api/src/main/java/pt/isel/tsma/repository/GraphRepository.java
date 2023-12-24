package pt.isel.tsma.repository;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.model.State;

import java.util.List;

@NoRepositoryBean
public interface GraphRepository<E, Id> {

	@Query("select e from #{#entityName} e where e.state = :state and e.deleted = :deleted")
	@Transactional(readOnly = true)
	List<E> findByState(State state, boolean deleted);

	@Query("update #{#entityName} e set e.state = :state where e.id = :id")
	@Transactional
	@Modifying
	void setState(Id id, State state);
}
