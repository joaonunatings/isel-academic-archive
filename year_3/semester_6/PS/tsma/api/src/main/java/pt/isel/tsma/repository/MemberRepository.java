package pt.isel.tsma.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.model.Member;

@Repository
@Transactional(readOnly = true)
public interface MemberRepository extends SoftDeleteRepository<Member, Long>, JpaSpecificationExecutor<Member> {

	@Query("select case when (count(m) > 0) then true else false end from member m where m.email = :email and m.deleted = false")
	boolean existsByEmail(String email);

	@Query("select case when (count(m) > 0) then true else false end from member m where m <> :member and m.email = :email and m.deleted = false")
	boolean isDuplicateEmail(Member member, String email);
}
