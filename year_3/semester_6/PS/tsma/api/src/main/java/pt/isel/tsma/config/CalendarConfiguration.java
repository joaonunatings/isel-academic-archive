package pt.isel.tsma.config;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import pt.isel.tsma.entity.dto.calendar.CalendarDTO;
import pt.isel.tsma.entity.model.calendar.CalendarParameters;
import pt.isel.tsma.entity.model.calendar.Parameters.ShiftDuration;
import pt.isel.tsma.entity.model.shift.Type;
import pt.isel.tsma.repository.calendar.CalendarParametersRepository;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static pt.isel.tsma.entity.model.shift.Type.*;

@Configuration
@Slf4j(topic = "tsma")
public class CalendarConfiguration {

	public static final List<String> VALID_SORT_PARAMS = List.of("id", "title", "startDate", "endDate");

	public static final HashMap<Type, ShiftDuration> DEFAULT_SHIFT_DURATIONS = new HashMap<>(Map.of(
		MORNING, new ShiftDuration(LocalTime.of(8, 0), LocalTime.of(16, 15)),
		AFTERNOON, new ShiftDuration(LocalTime.of(16, 0), LocalTime.of(0, 15)),
		MIDNIGHT, new ShiftDuration(LocalTime.of(0, 0), LocalTime.of(8, 15)),
		TRAINING, new ShiftDuration(LocalTime.of(9, 0), LocalTime.of(18, 0)),
		COORDINATOR, new ShiftDuration(LocalTime.of(9, 0), LocalTime.of(19, 0))
	));

	private static ModelMapper modelMapper;

	@Bean(name = "calendarModelMapper")
	public static ModelMapper modelMapper() {
		if (modelMapper != null) return modelMapper;
		modelMapper = new ModelMapper();

		modelMapper.typeMap(CalendarParameters.class, CalendarDTO.class).addMappings(
			mapper -> {
				mapper.map(calendar -> calendar.getCalendar().getId(), CalendarDTO::setId);
				mapper.map(calendar -> calendar.getCalendar().getTitle(), CalendarDTO::setTitle);
				mapper.map(calendar -> calendar.getCalendar().getDescription(), CalendarDTO::setDescription);
				mapper.map(calendar -> calendar.getCalendar().getStartDate(), CalendarDTO::setStartDate);
				mapper.map(calendar -> calendar.getCalendar().getEndDate(), CalendarDTO::setEndDate);
				mapper.map(calendar -> calendar.getParameters().getShiftDurations(), CalendarDTO::setShiftDurations);
				mapper.map(calendar -> calendar.getParameters().getSequences(), CalendarDTO::setSequences);
			}
		);

		return modelMapper;
	}

	@Bean
	@Profile("development")
	public ApplicationRunner parametersDatabaseInit(CalendarParametersRepository calendarParametersRepository) {
		return args -> {
			log.info("Initializing Calendar Parameters database...");
			calendarParametersRepository.deleteAll();
			log.info("Calendar Parameters database initialized");
		};
	}
}
