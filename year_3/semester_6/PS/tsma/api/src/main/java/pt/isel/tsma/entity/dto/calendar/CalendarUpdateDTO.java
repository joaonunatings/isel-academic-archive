package pt.isel.tsma.entity.dto.calendar;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.isel.tsma.entity.model.calendar.Parameters;
import pt.isel.tsma.entity.model.shift.Type;

import java.util.HashMap;

import static java.util.Collections.emptyMap;

@Data
@NoArgsConstructor
public class CalendarUpdateDTO implements ICalendarDTO {

	private String title;

	private String description;

	private HashMap<Type, Parameters.ShiftDuration> shiftDurations = new HashMap<>(emptyMap());
}
