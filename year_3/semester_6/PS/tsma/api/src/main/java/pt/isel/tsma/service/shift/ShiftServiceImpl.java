package pt.isel.tsma.service.shift;

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
import pt.isel.tsma.config.ShiftConfiguration;
import pt.isel.tsma.entity.dto.shift.ShiftGetDTO;
import pt.isel.tsma.entity.dto.shift.ShiftUpdateDTO;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.entity.model.calendar.CalendarParameters;
import pt.isel.tsma.entity.model.calendar.Sequence;
import pt.isel.tsma.entity.model.shift.Shift;
import pt.isel.tsma.exception.InvalidStateException;
import pt.isel.tsma.exception.model.shift.InvalidShiftException;
import pt.isel.tsma.repository.ShiftRepository;
import pt.isel.tsma.repository.specification.ShiftSpecification;
import pt.isel.tsma.service.report.ReportService;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static pt.isel.tsma.util.Utils.validatePage;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@Slf4j(topic = "tsma")
@CacheConfig(cacheNames = {"shifts"})
public class ShiftServiceImpl implements ShiftService {

	private final ShiftRepository shiftRepository;
	private final ReportService reportService;

	@Override
	@CacheEvict(allEntries = true)
	public List<Shift> createShifts(CalendarParameters calendarParameters, List<Sequence> sequences) {
		val calendar = calendarParameters.getCalendar();
		val parameters = calendarParameters.getParameters();
		log.debug("Creating shifts for calendar " + calendar.getId());
		val shifts = new LinkedList<Shift>();
		val calendarDays = calendar.getEndDate().toEpochDay() - calendar.getStartDate().toEpochDay();
		val members = new ArrayList<Member>(sequences.size());

		for (val sequence : sequences) {
			val types = sequence.getSequence();
			members.add(sequence.getMember());
			for (int day = 0; day < calendarDays; day++) {
				val type = types.get(day % types.size());
				val date = calendar.getStartDate().plusDays(day);
				val shift = new Shift(calendar, sequence.getMember(), date, type);
				if (parameters.getShiftDurations().containsKey(type)) {
					shift.setStartTime(parameters.getShiftDurations().get(type).getStart());
					shift.setEndTime(parameters.getShiftDurations().get(type).getEnd());
				}
				shifts.add(shift);
			}
		}

		val newShifts = shiftRepository.saveAll(shifts);
		log.debug("Created " + newShifts.size() + " shifts for calendar " + calendar.getId());

		reportService.createReports(calendar, members, newShifts);

		return newShifts;
	}

	@Override
	@Cacheable
	public List<Shift> getShifts(ShiftGetDTO shiftDto, Pageable page) {
		log.debug("Getting shifts...");
		val specBuilder = new ShiftSpecification();
		specBuilder.setCalendarIds(shiftDto.getCalendars());
		specBuilder.setMemberIds(shiftDto.getMembers());
		specBuilder.setStartDate(shiftDto.getStartDate());
		specBuilder.setEndDate(shiftDto.getEndDate());
		specBuilder.setTypes(shiftDto.getTypes());
		validatePage(page, ShiftConfiguration.VALID_SORT_PARAMS);

		// TODO: Fix while is not possible to use 'date' as sorting (only 'id.date' or 'id_date' is allowed)
		var localSort = Sort.unsorted();
		for (val sort : page.getSort()) {
			var property = sort.getProperty();
			if (sort.getProperty().equals("date")) property = "id.date";
			localSort = localSort.and(Sort.by(sort.getDirection(), property));
		}
		val localPage = PageRequest.of(page.getPageNumber(), page.getPageSize(), localSort);

		val shifts = shiftRepository.findAll(specBuilder.build(), localPage).getContent();
		log.debug("Found " + shifts.size() + " shifts");
		return shifts;
	}

	@Override
	@CacheEvict(allEntries = true)
	public List<Shift> updateShifts(CalendarParameters calendarParameters, List<ShiftUpdateDTO> shiftDtos) {
		log.debug("Updating shifts...");
		val oldShifts = new ArrayList<Shift>(shiftDtos.size());
		val shiftsToUpdate = new ArrayList<Shift>(shiftDtos.size());
		val calendar = calendarParameters.getCalendar();
		val parameters = calendarParameters.getParameters();

		for (val shiftDto : shiftDtos) {
			val shiftId = new Shift.ShiftId(calendar.getId(), shiftDto.getMemberId(), shiftDto.getDate());
			val shiftToUpdate = shiftRepository.findById(shiftId).orElseThrow(() -> new InvalidShiftException(shiftId));

			try {
				val oldShift = shiftToUpdate.clone();
				oldShifts.add(oldShift);
			} catch (CloneNotSupportedException e) {
				throw new InvalidStateException("Could not update shift");
			}

			if (parameters.getShiftDurations().containsKey(shiftDto.getType())) {
				shiftToUpdate.setStartTime(parameters.getShiftDurations().get(shiftDto.getType()).getStart());
				shiftToUpdate.setEndTime(parameters.getShiftDurations().get(shiftDto.getType()).getEnd());
			} else {
				shiftToUpdate.setStartTime(null);
				shiftToUpdate.setEndTime(null);
			}
			shiftToUpdate.setType(shiftDto.getType());
			shiftsToUpdate.add(shiftToUpdate);
		}

		val updatedShifts = shiftRepository.saveAll(shiftsToUpdate);
		log.debug("Updated " + updatedShifts.size() + " shifts");
		reportService.updateReports(oldShifts, updatedShifts);

		return updatedShifts;
	}

	@Override
	@CacheEvict(allEntries = true)
	public void deleteShifts(Calendar calendar, Member member) {
		log.debug("Deleting member #" + member.getId() + " shifts in calendar #" + calendar.getId());
		val calendarDays = calendar.getEndDate().toEpochDay() - calendar.getStartDate().toEpochDay();
		val shiftDto = new ShiftGetDTO(calendar.getId(), member.getId());
		val page = PageRequest.of(0, (int) calendarDays, Sort.unsorted());
		val shifts = getShifts(shiftDto, page);

		shiftRepository.deleteAll(shifts);
		log.debug("Deleted " + shifts.size() + " shifts for calendar #" + calendar.getId() + " and member #" + member.getId());

		reportService.deleteReports(calendar, member);
	}
}
