package pt.isel.tsma.repository;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@NoRepositoryBean
@Transactional(readOnly = true)
public interface SoftDeleteRepository<E, ID> extends JpaRepository<E, ID> {

	@Override
	@Query("select e from #{#entityName} e where e.deleted = false")
	@NonNull
	List<E> findAll();

	@Override
	@Query("select e from #{#entityName} e where e.id = ?1 and e.deleted = false")
	@NonNull
	Optional<E> findById(@NonNull ID id);

	@Query("select e from #{#entityName} e where e.deleted = true")
	List<E> findDeleted();

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM #{#entityName} WHERE deleted = true", nativeQuery = true)
	void permanentDelete();
}
