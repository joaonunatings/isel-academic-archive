package pt.isel.tsma.entity.proxy.event;

import com.microsoft.graph.models.Event;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pt.isel.tsma.entity.model.State;
import pt.isel.tsma.entity.proxy.Proxy;
import pt.isel.tsma.repository.ShiftRepository;

@Data
@NoArgsConstructor
public abstract class EventProxy implements Proxy<Event, ShiftRepository> {

	@NonNull
	private pt.isel.tsma.entity.model.shift.Shift shift;

	@Override
	public Event build() {
		return new Event();
	}

	@Override
	public void save(ShiftRepository repository) {
		repository.save(this.shift);
		repository.setState(this.shift.getId(), State.SYNCED);
	}
}
