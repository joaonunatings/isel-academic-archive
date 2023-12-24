package pt.isel.tsma.repository.calendar;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.model.calendar.Parameters;

import java.util.Optional;


@Repository
public interface CalendarParametersRepository extends MongoRepository<Parameters, String> {

	@Transactional(readOnly = true)
	Optional<Parameters> findByCalendarId(long calendarId);
}
