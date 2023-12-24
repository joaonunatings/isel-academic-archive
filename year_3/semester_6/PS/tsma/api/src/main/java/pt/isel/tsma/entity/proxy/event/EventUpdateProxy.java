package pt.isel.tsma.entity.proxy.event;

import com.microsoft.graph.models.Event;
import lombok.*;
import pt.isel.tsma.util.GraphUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EventUpdateProxy extends EventProxy {

	private @NonNull String calendarGraphId;

	private @NonNull String graphId;

	private @NonNull String subject;

	private @NonNull String category;

	private @NonNull LocalDate date;

	private @NonNull LocalTime startTime;

	private @NonNull LocalTime endTime;

	@Override
	public Event build() {
		val event = super.build();
		event.subject = this.subject;
		event.categories = List.of(category);
		return GraphUtils.addStartEnd(event, date, startTime, endTime);
	}
}
