package pt.isel.tsma.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.entity.model.Report;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.entity.model.shift.Type;

import java.util.Optional;

@Repository
public interface ReportRepository extends JpaRepository<Report, Long>, JpaSpecificationExecutor<Report> {

	@Transactional(readOnly = true)
	Optional<Report> findByCalendarAndMemberAndShiftType(Calendar calendar, Member member, Type shiftType);
}
