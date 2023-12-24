package pt.isel.tsma.service.calendar;

import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import pt.isel.tsma.config.CalendarConfiguration;
import pt.isel.tsma.entity.dto.calendar.CalendarAddMemberDTO;
import pt.isel.tsma.entity.dto.calendar.CalendarCreateDTO;
import pt.isel.tsma.entity.dto.calendar.CalendarGetDTO;
import pt.isel.tsma.entity.dto.calendar.CalendarUpdateDTO;
import pt.isel.tsma.entity.dto.shift.ShiftGetDTO;
import pt.isel.tsma.entity.dto.shift.ShiftUpdateDTO;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.entity.model.calendar.CalendarParameters;
import pt.isel.tsma.entity.model.calendar.Parameters;
import pt.isel.tsma.entity.model.calendar.Sequence;
import pt.isel.tsma.entity.model.shift.Shift;
import pt.isel.tsma.exception.InvalidStateException;
import pt.isel.tsma.exception.model.calendar.CalendarNotFoundException;
import pt.isel.tsma.exception.model.calendar.DuplicateTitleException;
import pt.isel.tsma.exception.model.calendar.InvalidDateIntervalException;
import pt.isel.tsma.exception.model.calendar.MemberAlreadyInCalendarException;
import pt.isel.tsma.exception.model.member.InvalidMemberException;
import pt.isel.tsma.exception.model.member.MemberNotFoundException;
import pt.isel.tsma.repository.MemberRepository;
import pt.isel.tsma.repository.calendar.CalendarParametersRepository;
import pt.isel.tsma.repository.calendar.CalendarRepository;
import pt.isel.tsma.repository.specification.CalendarSpecification;
import pt.isel.tsma.service.shift.ShiftService;
import pt.isel.tsma.util.Utils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static pt.isel.tsma.util.Utils.validatePage;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ExtensionMethod({Utils.StringExtensions.class, Utils.ListExtensions.class, Utils.MapExtensions.class})
@Slf4j(topic = "tsma")
@CacheConfig(cacheNames = {"calendars"})
public class CalendarServiceImpl implements CalendarService {

	private final CalendarRepository calendarRepository;
	private final CalendarParametersRepository calendarParametersRepository;
	private final MemberRepository memberRepository;
	private final ShiftService shiftService;
	private final ModelMapper mapper = CalendarConfiguration.modelMapper();

	@Override
	@CacheEvict(allEntries = true)
	public CalendarParameters createCalendar(CalendarCreateDTO calendarDto) {
		log.debug("Creating calendar...");
		var calendar = mapper.map(calendarDto, Calendar.class);

		if (calendarRepository.existsByTitle(calendar.getTitle()))
			throw new DuplicateTitleException(calendar.getTitle());

		if (calendarDto.getStartDate() == null) calendar.setStartDate(LocalDate.now());

		val sequences = new ArrayList<Sequence>(calendarDto.getSequences().size());
		var maxSequenceSize = 0;
		for (var sequence : calendarDto.getSequences()) {
			val member = memberRepository.findById(sequence.getMemberId()).orElseThrow(() -> new InvalidMemberException(sequence.getMemberId()));
			sequences.add(new Sequence(member, sequence.getSequence()));
			maxSequenceSize = Math.max(maxSequenceSize, sequence.getSequence().size());
		}
		val calendarDays = calendar.getEndDate().toEpochDay() - calendar.getStartDate().toEpochDay();
		if (calendarDays <= 0) throw new InvalidDateIntervalException(calendar.getStartDate(), calendar.getEndDate());
		if (calendarDays < maxSequenceSize) throw new InvalidDateIntervalException(calendarDays, maxSequenceSize);

		calendar = calendarRepository.save(calendar);

		val shiftDurations = CalendarConfiguration.DEFAULT_SHIFT_DURATIONS;
		shiftDurations.putAll(new HashMap<>(calendarDto.getShiftDurations().mapMap(Parameters.ShiftDuration.class, mapper)));
		val parameters = calendarParametersRepository.save(
			new Parameters(calendar.getId(), shiftDurations, calendarDto.getSequences().mapList(Parameters.Sequence.class, mapper)));

		val calendarParameters = new CalendarParameters(calendar, parameters);
		shiftService.createShifts(calendarParameters, sequences);
		log.debug("Calendar created");

		return calendarParameters;
	}

	@Override
	@Cacheable
	public CalendarParameters getCalendar(long calendarId) {
		log.debug("Getting calendar #" + calendarId + "...");
		val calendar = calendarRepository
			.findById(calendarId)
			.orElseThrow(() -> new CalendarNotFoundException(calendarId));
		val parameters = calendarParametersRepository
			.findByCalendarId(calendarId)
			.orElseThrow(() -> new CalendarNotFoundException(calendarId));
		log.debug("Calendar #" + calendarId + " fetched");

		return new CalendarParameters(calendar, parameters);
	}

	@Override
	@Cacheable
	public List<Calendar> getCalendars(CalendarGetDTO calendarDto, Pageable page) {
		log.debug("Getting calendars...");
		val spec = new CalendarSpecification();
		spec.setIds(calendarDto.getIds());
		spec.setTitles(calendarDto.getTitles());
		spec.setStartDate(calendarDto.getStartDate());
		spec.setEndDate(calendarDto.getEndDate());
		validatePage(page, CalendarConfiguration.VALID_SORT_PARAMS);

		val calendars = calendarRepository.findAll(spec.build(), page).getContent();
		log.debug("Found " + calendars.size() + " calendars");
		return calendars;
	}

	@Override
	@CacheEvict(allEntries = true)
	public CalendarParameters updateCalendar(long calendarId, CalendarUpdateDTO calendarDto) {
		log.debug("Updating calendar #" + calendarId);
		val calendarParameters = getCalendar(calendarId);
		val calendarToUpdate = calendarParameters.getCalendar();
		val parametersToUpdate = calendarParameters.getParameters();

		var newTitle = calendarDto.getTitle();
		if (newTitle.isNullOrEmpty()) {
			newTitle = calendarToUpdate.getTitle();
		}
		calendarToUpdate.setTitle(newTitle);

		var newDescription = calendarDto.getDescription();
		if (newDescription.isNullOrEmpty()) newDescription = calendarToUpdate.getDescription();
		calendarToUpdate.setDescription(newDescription);

		val newShiftDurations = parametersToUpdate.getShiftDurations();
		newShiftDurations.putAll(calendarDto.getShiftDurations().mapMap(Parameters.ShiftDuration.class, mapper));
		parametersToUpdate.setShiftDurations(newShiftDurations);

		calendarParameters.setCalendar(calendarRepository.save(calendarToUpdate));
		calendarParameters.setParameters(calendarParametersRepository.save(parametersToUpdate));
		log.debug("Calendar #" + calendarId + " updated");

		if (calendarDto.getShiftDurations().size() > 0) {
			val updatedTypes = new ArrayList<>(calendarDto.getShiftDurations().keySet());
			val shiftGetDto = new ShiftGetDTO(List.of(calendarId), emptyList(), null, null, updatedTypes);
			val page = PageRequest.of(0, calendarToUpdate.getShifts().size(), Sort.unsorted());
			val shifts = shiftService.getShifts(shiftGetDto, page);
			val shiftDtos = shifts
				.stream()
				.map(shift -> {
					val shiftDto = new ShiftUpdateDTO(shift.getMember().getId(), shift.getDate(), shift.getType());
					if (updatedTypes.contains(shift.getType())) {
						shiftDto.setStartTime(calendarDto.getShiftDurations().get(shift.getType()).getStart());
						shiftDto.setEndTime(calendarDto.getShiftDurations().get(shift.getType()).getEnd());
					}
					return shiftDto;
				})
				.collect(Collectors.toList());
			shiftService.updateShifts(calendarParameters, shiftDtos);
		}

		return calendarParameters;
	}

	@Override
	@CacheEvict(allEntries = true)
	public List<Shift> updateShifts(long calendarId, List<ShiftUpdateDTO> shiftDtos) {
		log.debug("Updating " + shiftDtos.size() + " shifts...");
		val calendarParameters = getCalendar(calendarId);
		val shifts = shiftService.updateShifts(calendarParameters, shiftDtos);
		log.debug("Updated " + shiftDtos.size() + " shifts");
		return shifts;
	}

	@Override
	@CacheEvict(allEntries = true)
	public CalendarParameters updateMember(long calendarId, long oldMemberId, long newMemberId) {
		log.debug("Exchanging member #" + oldMemberId + " with #" + newMemberId + " in calendar #" + calendarId);
		val calendarParameters = getCalendar(calendarId);
		val parameters = calendarParameters.getParameters();
		val calendar = calendarParameters.getCalendar();
		val oldMember = memberRepository.findById(oldMemberId).orElseThrow(() -> new MemberNotFoundException(oldMemberId));
		if (!calendarRepository.memberExists(calendar, oldMember)) throw new MemberNotFoundException(oldMemberId);
		val newMember = memberRepository.findById(newMemberId).orElseThrow(() -> new InvalidMemberException(newMemberId));
		if (calendarRepository.memberExists(calendar, newMember))
			throw new MemberAlreadyInCalendarException(newMemberId, calendarId);

		val parameterSequence = parameters
			.getSequences()
			.stream()
			.filter(s -> s.getMemberId() == oldMemberId)
			.findFirst()
			.orElseThrow(() -> new InvalidStateException("Old member sequence not found"));
		shiftService.deleteShifts(calendar, oldMember);
		val sequence = new Sequence(newMember, parameterSequence.getSequence());
		shiftService.createShifts(calendarParameters, List.of(sequence));

		parameterSequence.setMemberId(newMemberId);
		calendarParametersRepository.save(parameters);
		log.debug("Member #" + oldMemberId + " exchanged with #" + newMemberId + " in calendar #" + calendarId);

		return calendarParameters;
	}

	@Override
	@CacheEvict(allEntries = true)
	public CalendarParameters addMemberToCalendar(long calendarId, CalendarAddMemberDTO calendarDto) {
		log.debug("Adding member #" + calendarDto.getMemberId() + " to calendar #" + calendarId);
		val calendarParameters = getCalendar(calendarId);
		val calendar = calendarParameters.getCalendar();
		val parameters = calendarParameters.getParameters();
		val calendarDays = calendar.getEndDate().toEpochDay() - calendar.getStartDate().toEpochDay();

		val sequenceDays = calendarDto.getSequence().size();
		if (sequenceDays > calendarDays) throw new InvalidDateIntervalException(calendarDays, sequenceDays);
		val member = memberRepository
			.findById(calendarDto.getMemberId())
			.orElseThrow(() -> new InvalidMemberException(calendarDto.getMemberId()));
		if (calendarRepository.memberExists(calendar, member))
			throw new MemberAlreadyInCalendarException(calendarDto.getMemberId(), calendarId);

		parameters.getSequences().add(mapper.map(calendarDto, Parameters.Sequence.class));
		val sequence = new Sequence(member, calendarDto.getSequence());

		shiftService.createShifts(calendarParameters, List.of(sequence));

		calendarParameters.setParameters(calendarParametersRepository.save(parameters));
		calendarParameters.setCalendar(calendarRepository.save(calendar));
		log.debug("Member #" + calendarDto.getMemberId() + " added to calendar #" + calendarId);

		return calendarParameters;
	}

	@Override
	@CacheEvict(allEntries = true)
	public CalendarParameters removeMemberFromCalendar(long calendarId, long memberId) {
		log.debug("Removing member #" + memberId + " from calendar #" + calendarId);
		val calendarParameters = getCalendar(calendarId);
		val calendar = calendarParameters.getCalendar();
		val parameters = calendarParameters.getParameters();
		val member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
		if (!calendarRepository.memberExists(calendar, member)) throw new MemberNotFoundException(memberId);

		parameters.getSequences().removeIf(sequence -> sequence.getMemberId() == memberId);
		shiftService.deleteShifts(calendar, member);
		calendarParameters.setParameters(calendarParametersRepository.save(parameters));
		log.debug("Member #" + memberId + " removed from calendar #" + calendarId);

		return calendarParameters;
	}

	@Override
	@CacheEvict(allEntries = true)
	public long deleteCalendar(long calendarId) {
		log.debug("Deleting calendar #" + calendarId);
		val calendarParameters = getCalendar(calendarId);
		val calendar = calendarParameters.getCalendar();

		calendarParametersRepository.delete(calendarParameters.getParameters());
		calendarRepository.delete(calendar);
		log.debug("Calendar #" + calendarId + " deleted");

		return calendarId;
	}
}
