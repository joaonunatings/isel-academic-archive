package pt.isel.tsma.entity.proxy.calendar;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CalendarDeleteProxy extends CalendarProxy {

	@NonNull
	private String graphId;
}

