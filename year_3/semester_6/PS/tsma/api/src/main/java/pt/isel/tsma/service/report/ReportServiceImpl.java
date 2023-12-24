package pt.isel.tsma.service.report;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pt.isel.tsma.config.ReportConfiguration;
import pt.isel.tsma.entity.dto.report.ReportGetDTO;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.entity.model.Report;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.entity.model.shift.Shift;
import pt.isel.tsma.entity.model.shift.Type;
import pt.isel.tsma.exception.model.report.InvalidReportException;
import pt.isel.tsma.repository.ReportRepository;
import pt.isel.tsma.repository.ShiftRepository;
import pt.isel.tsma.repository.specification.ReportSpecification;
import pt.isel.tsma.repository.specification.ShiftSpecification;

import java.util.LinkedList;
import java.util.List;

import static pt.isel.tsma.util.Utils.validatePage;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j(topic = "tsma")
@CacheConfig(cacheNames = {"reports"})
public class ReportServiceImpl implements ReportService {

	private final ReportRepository reportRepository;
	private final ShiftRepository shiftRepository;

	@Override
	@CacheEvict(allEntries = true)
	public List<Report> createReports(Calendar calendar, List<Member> members, List<Shift> shifts) {
		log.debug("Creating reports...");
		val reports = new LinkedList<Report>();

		ShiftSpecification spec = new ShiftSpecification();
		for (var member : members) {
			for (var shiftType : Type.values()) {
				spec.setCalendarIds(List.of(calendar.getId()));
				spec.setMemberIds(List.of(member.getId()));
				spec.setTypes(List.of(shiftType));
				long totalShifts = shiftRepository.count(spec.build());
				val report = new Report(member, calendar, calendar.getStartDate(), calendar.getEndDate(), shiftType, totalShifts);
				reports.add(report);
			}
		}

		val newReports = reportRepository.saveAll(reports);
		log.debug("Created " + reports.size() + " reports");
		return newReports;
	}

	@Override
	@Cacheable
	public List<Report> getReports(ReportGetDTO reportDto, Pageable page) {
		log.debug("Getting reports...");
		val specBuilder = new ReportSpecification();
		specBuilder.setIds(reportDto.getIds());
		specBuilder.setCalendarIds(reportDto.getCalendars());
		specBuilder.setMemberIds(reportDto.getMembers());
		specBuilder.setStartDate(reportDto.getStartDate());
		specBuilder.setEndDate(reportDto.getEndDate());
		specBuilder.setShiftTypes(reportDto.getShiftTypes());
		validatePage(page, ReportConfiguration.VALID_SORT_PARAMS);

		val reports = reportRepository.findAll(specBuilder.build(), page).getContent();
		log.debug("Found " + reports.size() + " reports");
		return reports;
	}

	@Override
	@CacheEvict(allEntries = true)
	public void updateReports(List<Shift> oldShifts, List<Shift> updatedShifts) {
		log.debug("Updating " + (oldShifts.size() + updatedShifts.size()) + " reports...");
		for (int i = 0; i < oldShifts.size(); i++) {
			val oldShift = oldShifts.get(i);
			val updatedShift = updatedShifts.get(i);
			val oldShiftReport = reportRepository
				.findByCalendarAndMemberAndShiftType(oldShift.getCalendar(), oldShift.getMember(), oldShift.getType())
				.orElseThrow(() -> new InvalidReportException(oldShift.getCalendar().getId(), oldShift.getMember().getId(), oldShift.getType()));
			val updatedShiftReport = reportRepository
				.findByCalendarAndMemberAndShiftType(updatedShift.getCalendar(), updatedShift.getMember(), updatedShift.getType())
				.orElseThrow(() -> new InvalidReportException(updatedShift.getCalendar().getId(), updatedShift.getMember().getId(), updatedShift.getType()));
			oldShiftReport.setTotalShifts(oldShiftReport.getTotalShifts() - 1);
			updatedShiftReport.setTotalShifts(updatedShiftReport.getTotalShifts() + 1);
			reportRepository.save(oldShiftReport);
			reportRepository.save(updatedShiftReport);
		}
		log.debug("Updated " + (oldShifts.size() + updatedShifts.size()) + " reports");
	}

	@Override
	@CacheEvict(allEntries = true)
	public void deleteReports(Calendar calendar, Member member) {
		log.debug("Deleting reports...");
		val calendarDays = calendar.getEndDate().toEpochDay() - calendar.getStartDate().toEpochDay();
		val reportDto = new ReportGetDTO(calendar.getId(), member.getId());
		val page = PageRequest.of(0, (int) calendarDays, Sort.unsorted());
		val reports = getReports(reportDto, page);

		reportRepository.deleteAll(reports);
		log.debug("Deleted " + reports.size() + " reports");
	}
}
