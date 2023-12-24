package pt.isel.tsma.entity.dto.calendar;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pt.isel.tsma.entity.dto.shift.ShiftDurationDTO;
import pt.isel.tsma.entity.model.shift.Type;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
public class CalendarDTO implements ICalendarDTO {

	private long id;

	@NonNull
	private String title;

	private String description;

	@NonNull
	private LocalDate startDate;

	@NonNull
	private LocalDate endDate;

	@NonNull
	private HashMap<Type, ShiftDurationDTO> shiftDurations;

	@NonNull
	private List<SequenceDTO> sequences;
}
