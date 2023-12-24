package pt.isel.tsma.entity.proxy.calendar;

import com.microsoft.graph.models.Calendar;
import lombok.*;


@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CalendarCreateProxy extends CalendarProxy {

	@NonNull
	private String name;

	public void setGraphId(String graphId) {
		this.getCalendar().setGraphId(graphId);
	}

	@Override
	public Calendar build() {
		val calendar = super.build();
		calendar.name = name;
		return calendar;
	}
}
