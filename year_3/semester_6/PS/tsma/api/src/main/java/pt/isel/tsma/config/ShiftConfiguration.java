package pt.isel.tsma.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.isel.tsma.entity.dto.shift.ShiftDTO;
import pt.isel.tsma.entity.dto.shift.ShiftUpdateDTO;
import pt.isel.tsma.entity.model.shift.Shift;

import java.time.LocalDate;
import java.util.List;

@Configuration
public class ShiftConfiguration {

	public static final List<String> VALID_SORT_PARAMS = List.of("calendar", "member", "date", "type");

	private static ModelMapper modelMapper;

	@Bean(name = "shiftModelMapper")
	public static ModelMapper modelMapper() {
		if (modelMapper != null) return modelMapper;
		modelMapper = new ModelMapper();

		modelMapper.typeMap(Shift.class, ShiftDTO.class).addMappings(
			mapper -> {
				mapper.map(shift -> shift.getMember().getId(), ShiftDTO::setMemberId);
				mapper.map(shift -> shift.getCalendar().getId(), ShiftDTO::setCalendarId);
				mapper.map(Shift::getDate, ShiftDTO::setDate);
				mapper.map(Shift::getType, ShiftDTO::setType);
			});

		modelMapper.typeMap(ShiftUpdateDTO.class, Shift.class).addMappings(
			mapper -> mapper.map(ShiftUpdateDTO::getDate, (shift, date) -> shift.getId().setDate((LocalDate) date)));
		modelMapper.typeMap(Shift.class, ShiftUpdateDTO.class).addMappings(
			mapper -> mapper.map(Shift::getDate, ShiftUpdateDTO::setDate));

		return modelMapper;
	}
}
