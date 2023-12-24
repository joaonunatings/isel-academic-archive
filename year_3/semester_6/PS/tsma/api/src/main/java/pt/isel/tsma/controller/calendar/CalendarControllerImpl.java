package pt.isel.tsma.controller.calendar;

import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.tsma.config.CalendarConfiguration;
import pt.isel.tsma.entity.dto.calendar.*;
import pt.isel.tsma.entity.dto.shift.ShiftDTO;
import pt.isel.tsma.entity.dto.shift.ShiftUpdateDTO;
import pt.isel.tsma.service.calendar.CalendarService;
import pt.isel.tsma.util.Utils;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ExtensionMethod(Utils.ListExtensions.class)
public class CalendarControllerImpl implements CalendarController {

	private final CalendarService calendarService;
	private final ModelMapper mapper = CalendarConfiguration.modelMapper();

	@Override
	public CalendarDTO createCalendar(CalendarCreateDTO calendarDto) {
		val calendar = calendarService.createCalendar(calendarDto);
		return mapper.map(calendar, CalendarDTO.class);
	}

	@Override
	public CalendarDTO getCalendar(long calendarId) {
		val calendar = calendarService.getCalendar(calendarId);
		return mapper.map(calendar, CalendarDTO.class);
	}

	@Override
	public List<CalendarSummaryDTO> getCalendars(CalendarGetDTO calendarDto, Pageable page) {
		val calendars = calendarService.getCalendars(calendarDto, page);
		return calendars.mapList(CalendarSummaryDTO.class, mapper);
	}

	@Override
	public CalendarDTO updateCalendar(long calendarId, CalendarUpdateDTO calendarDto) {
		val calendar = calendarService.updateCalendar(calendarId, calendarDto);
		return mapper.map(calendar, CalendarDTO.class);
	}

	@Override
	public List<ShiftDTO> updateShifts(long calendarId, List<ShiftUpdateDTO> shiftDtos) {
		val shifts = calendarService.updateShifts(calendarId, shiftDtos);
		return shifts.mapList(ShiftDTO.class, mapper);
	}

	@Override
	public CalendarDTO updateMember(long calendarId, long memberId, long newMemberId) {
		val calendar = calendarService.updateMember(calendarId, memberId, newMemberId);
		return mapper.map(calendar, CalendarDTO.class);
	}

	@Override
	public CalendarDTO addMemberToCalendar(long calendarId, CalendarAddMemberDTO calendarDto) {
		val calendar = calendarService.addMemberToCalendar(calendarId, calendarDto);
		return mapper.map(calendar, CalendarDTO.class);
	}

	@Override
	public CalendarDTO removeMemberFromCalendar(long calendarId, long memberId) {
		val calendar = calendarService.removeMemberFromCalendar(calendarId, memberId);
		return mapper.map(calendar, CalendarDTO.class);
	}

	@Override
	public long deleteCalendar(long calendarId) {
		return calendarService.deleteCalendar(calendarId);
	}
}
