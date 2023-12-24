package pt.isel.tsma.repository.calendar;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.repository.GraphRepository;
import pt.isel.tsma.repository.SoftDeleteRepository;

import java.util.List;

@Repository
@Transactional(readOnly = true)
public interface CalendarRepository extends
	SoftDeleteRepository<Calendar, Long>,
	GraphRepository<Calendar, Long>,
	JpaSpecificationExecutor<Calendar> {

	@Query("select case when (count(s) > 0) then true else false end from shift s where s.calendar = :calendar and s.member = :member and s.deleted = false")
	boolean memberExists(Calendar calendar, Member member);

	@Query("select distinct c from calendar c, shift s where c.id = s.calendar.id and s.member = :member and c.deleted = false and s.deleted = false")
	List<Calendar> findByMember(Member member);

	boolean existsByTitle(String title);

	@Modifying
	@Transactional
	@Query(value = "DELETE FROM calendar WHERE deleted = true AND id = :calendarId", nativeQuery = true)
	void permanentDelete(Long calendarId);
}
