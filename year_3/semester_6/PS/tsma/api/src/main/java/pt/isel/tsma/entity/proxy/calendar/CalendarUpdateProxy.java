package pt.isel.tsma.entity.proxy.calendar;

import com.microsoft.graph.models.Calendar;
import lombok.*;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class CalendarUpdateProxy extends CalendarProxy {

	@NonNull
	private String graphId;

	@NonNull
	private String name;

	@Override
	public Calendar build() {
		val calendar = super.build();
		calendar.name = this.name;
		return calendar;
	}
}
