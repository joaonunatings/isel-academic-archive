package pt.isel.tsma.service.shift;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.dto.shift.ShiftGetDTO;
import pt.isel.tsma.entity.dto.shift.ShiftUpdateDTO;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.entity.model.calendar.CalendarParameters;
import pt.isel.tsma.entity.model.calendar.Sequence;
import pt.isel.tsma.entity.model.shift.Shift;

import java.util.List;

@Transactional
public interface ShiftService {

	@SuppressWarnings("UnusedReturnValue")
	List<Shift> createShifts(CalendarParameters calendarParameters, List<Sequence> sequences);

	@Transactional(readOnly = true)
	List<Shift> getShifts(ShiftGetDTO shiftDto, Pageable page);

	List<Shift> updateShifts(CalendarParameters calendarParameters, List<ShiftUpdateDTO> shiftDtos);

	void deleteShifts(Calendar calendar, Member member);
}
