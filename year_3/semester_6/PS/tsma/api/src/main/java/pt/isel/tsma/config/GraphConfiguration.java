package pt.isel.tsma.config;

import com.microsoft.graph.models.OutlookCategory;
import lombok.val;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.spi.MappingContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.entity.model.shift.Shift;
import pt.isel.tsma.entity.model.shift.Type;
import pt.isel.tsma.entity.proxy.calendar.CalendarCreateProxy;
import pt.isel.tsma.entity.proxy.calendar.CalendarUpdateProxy;
import pt.isel.tsma.entity.proxy.event.EventCreateProxy;
import pt.isel.tsma.entity.proxy.event.EventDeleteProxy;
import pt.isel.tsma.entity.proxy.event.EventUpdateProxy;

import java.util.HashMap;

import static pt.isel.tsma.entity.model.shift.Type.*;
import static pt.isel.tsma.util.GraphUtils.createCategory;

@Configuration
public class GraphConfiguration {

	public static final HashMap<Type, OutlookCategory> CATEGORIES = new HashMap<>() {
		{
			put(REST, createCategory("REST", "Gray"));
			put(MORNING, createCategory("MORNING", "Brown"));
			put(AFTERNOON, createCategory("AFTERNOON", "DarkOrange"));
			put(MIDNIGHT, createCategory("MIDNIGHT", "Green"));
			put(OFF, createCategory("OFF", "Gray"));
			put(TRAINING, createCategory("TRAINING", "Steel"));
			put(HOLIDAY, createCategory("HOLIDAY", "Olive"));
			put(PATCHING, createCategory("PATCHING", "Steel"));
			put(COORDINATOR, createCategory("COORDINATOR", "Orange"));
			put(SICK, createCategory("SICK", "DarkBlue"));
			put(MISSED, createCategory("MISSED", "Teal"));
			put(HOME, createCategory("HOME", "Purple"));
		}
	};

	private static ModelMapper modelMapper;

	@Bean(name = "graphModelMapper")
	public static ModelMapper modelMapper() {
		if (modelMapper != null) return modelMapper;
		modelMapper = new ModelMapper();

		val config = modelMapper.getConfiguration();
		config.setPreferNestedProperties(false);
		config.setSkipNullEnabled(true);

		modelMapper.typeMap(Calendar.class, CalendarCreateProxy.class).addMapping(Calendar::getTitle, CalendarCreateProxy::setName);

		modelMapper.typeMap(Calendar.class, CalendarUpdateProxy.class).addMapping(Calendar::getTitle, CalendarUpdateProxy::setName);

		val typeConverter = new Converter<Type, String>() {
			@Override
			public String convert(MappingContext<Type, String> mappingContext) {
				return mappingContext.getSource().toString();
			}
		};

		modelMapper.typeMap(Shift.class, EventCreateProxy.class).addMappings(
			mapper -> {
				mapper.map(shift -> shift.getCalendar().getGraphId(), EventCreateProxy::setCalendarGraphId);
				mapper.map(shift -> shift.getMember().getEmail(), EventCreateProxy::setSubject);
				mapper.using(typeConverter).map(Shift::getType, EventCreateProxy::setCategory);
			}
		);

		modelMapper.typeMap(Shift.class, EventUpdateProxy.class).addMappings(
			mapper -> {
				mapper.map(shift -> shift.getCalendar().getGraphId(), EventUpdateProxy::setCalendarGraphId);
				mapper.map(shift -> shift.getMember().getEmail(), EventUpdateProxy::setSubject);
				mapper.using(typeConverter).map(Shift::getType, EventUpdateProxy::setCategory);
			}
		);

		modelMapper.typeMap(Shift.class, EventDeleteProxy.class)
			.addMapping(shift -> shift.getCalendar().getGraphId(), EventDeleteProxy::setCalendarGraphId);

		return modelMapper;
	}
}
