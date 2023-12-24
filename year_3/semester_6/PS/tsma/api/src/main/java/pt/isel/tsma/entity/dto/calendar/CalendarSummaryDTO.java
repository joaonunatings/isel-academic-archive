package pt.isel.tsma.entity.dto.calendar;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CalendarSummaryDTO implements ICalendarDTO {

	private long id;

	@NonNull
	private String title;

	private String description;

	@NonNull
	private LocalDate startDate;

	@NonNull
	private LocalDate endDate;
}
