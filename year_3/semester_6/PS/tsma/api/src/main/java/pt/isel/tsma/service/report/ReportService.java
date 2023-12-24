package pt.isel.tsma.service.report;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.dto.report.ReportGetDTO;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.entity.model.Report;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.entity.model.shift.Shift;

import java.util.List;


@Transactional
public interface ReportService {

	@SuppressWarnings("UnusedReturnValue")
	List<Report> createReports(Calendar calendar, List<Member> members, List<Shift> shifts);

	@Transactional(readOnly = true)
	List<Report> getReports(ReportGetDTO reportDto, Pageable page);

	void updateReports(List<Shift> oldShifts, List<Shift> updatedShifts);

	void deleteReports(Calendar calendar, Member member);
}
