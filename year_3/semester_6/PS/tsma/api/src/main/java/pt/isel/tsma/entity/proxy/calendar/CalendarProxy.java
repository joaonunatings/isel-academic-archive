package pt.isel.tsma.entity.proxy.calendar;

import com.microsoft.graph.models.Calendar;
import lombok.Data;
import lombok.NoArgsConstructor;
import pt.isel.tsma.entity.model.State;
import pt.isel.tsma.entity.proxy.Proxy;
import pt.isel.tsma.repository.calendar.CalendarRepository;

@Data
@NoArgsConstructor
public abstract class CalendarProxy implements Proxy<Calendar, CalendarRepository> {

	private pt.isel.tsma.entity.model.calendar.Calendar calendar;

	@Override
	public Calendar build() {
		return new Calendar();
	}

	@Override
	public void save(CalendarRepository repository) {
		repository.save(this.calendar);
		repository.setState(this.calendar.getId(), State.SYNCED);
	}
}
