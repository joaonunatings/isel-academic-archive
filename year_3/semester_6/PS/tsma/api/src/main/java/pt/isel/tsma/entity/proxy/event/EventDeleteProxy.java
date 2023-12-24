package pt.isel.tsma.entity.proxy.event;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class EventDeleteProxy extends EventProxy {

	@NonNull String calendarGraphId;
	@NonNull String graphId;
}
