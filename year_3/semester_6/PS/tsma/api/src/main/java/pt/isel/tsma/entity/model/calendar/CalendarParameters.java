package pt.isel.tsma.entity.model.calendar;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;

@Data
@AllArgsConstructor
public class CalendarParameters {

	@NonNull
	Calendar calendar;

	@NonNull
	Parameters parameters;
}
