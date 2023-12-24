package pt.isel.tsma.repository;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.model.shift.Shift;

import java.time.LocalDate;

@Repository
@Transactional
public interface ShiftRepository extends
	SoftDeleteRepository<Shift, Shift.ShiftId>,
	GraphRepository<Shift, Shift.ShiftId>,
	JpaSpecificationExecutor<Shift> {

	@Modifying
	@Query(value = "DELETE FROM shift s WHERE s.deleted = true AND s.calendar_id = :calendarId AND s.date = :date AND s.member_id = :memberId", nativeQuery = true)
	void permanentDelete(Long calendarId, Long memberId, LocalDate date);

	@Modifying
	@Query(value = "DELETE FROM shift WHERE deleted = true AND calendar_id = :calendarId", nativeQuery = true)
	void permanentDeleteByCalendar(Long calendarId);
}
