package pt.isel.tsma.entity.dto.calendar;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

@Data
@NoArgsConstructor
public class CalendarGetDTO implements ICalendarDTO {

	private List<Long> ids = emptyList();

	private List<String> titles = emptyList();

	private LocalDate startDate;

	private LocalDate endDate;
}
