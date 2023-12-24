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
public class EventCreateProxy extends EventProxy {

	@NonNull
	private String calendarGraphId;

	@NonNull
	private String subject;

	@NonNull
	private String category;

	@NonNull
	private LocalDate date;

	private LocalTime startTime;

	private LocalTime endTime;

	public void setGraphId(String graphId) {
		this.getShift().setGraphId(graphId);
	}

	@Override
	public Event build() {
		val event = super.build();
		event.subject = this.subject;
		event.categories = List.of(category);
		return GraphUtils.addStartEnd(event, date, startTime, endTime);
	}
}
