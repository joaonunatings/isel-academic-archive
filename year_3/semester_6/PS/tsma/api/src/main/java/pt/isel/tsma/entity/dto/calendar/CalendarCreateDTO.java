package pt.isel.tsma.entity.dto.calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pt.isel.tsma.entity.dto.shift.ShiftDurationDTO;
import pt.isel.tsma.entity.model.shift.Type;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;

import static java.util.Collections.emptyMap;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CalendarCreateDTO implements ICalendarDTO {

	@NonNull
	private String title;

	private String description;

	private LocalDate startDate;

	@NonNull
	private LocalDate endDate;

	@NonNull
	private List<SequenceDTO> sequences;

	private HashMap<Type, ShiftDurationDTO> shiftDurations = new HashMap<>(emptyMap());
}


