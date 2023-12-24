package pt.isel.tsma.service.calendar;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.dto.calendar.CalendarAddMemberDTO;
import pt.isel.tsma.entity.dto.calendar.CalendarCreateDTO;
import pt.isel.tsma.entity.dto.calendar.CalendarGetDTO;
import pt.isel.tsma.entity.dto.calendar.CalendarUpdateDTO;
import pt.isel.tsma.entity.dto.shift.ShiftUpdateDTO;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.entity.model.calendar.CalendarParameters;
import pt.isel.tsma.entity.model.shift.Shift;

import java.util.List;

@Transactional
public interface CalendarService {

	CalendarParameters createCalendar(CalendarCreateDTO calendarDto);

	@Transactional(readOnly = true)
	CalendarParameters getCalendar(long calendarId);

	@Transactional(readOnly = true)
	List<Calendar> getCalendars(CalendarGetDTO calendarDto, Pageable page);

	CalendarParameters updateCalendar(long calendarId, CalendarUpdateDTO calendarDto);

	List<Shift> updateShifts(long calendarId, List<ShiftUpdateDTO> shiftDtos);

	CalendarParameters updateMember(long calendarId, long oldMemberId, long newMemberId);

	CalendarParameters addMemberToCalendar(long calendarId, CalendarAddMemberDTO calendarDto);

	CalendarParameters removeMemberFromCalendar(long calendarId, long memberId);

	long deleteCalendar(long calendarId);
}
