package pt.isel.tsma.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.isel.tsma.entity.dto.report.ReportGetDTO;
import pt.isel.tsma.entity.model.Report;

import java.util.List;

@Configuration
public class ReportConfiguration {

	public static List<String> VALID_SORT_PARAMS = List.of("id", "member", "calendar", "startDate", "endDate", "shiftType", "totalShifts");
	private static ModelMapper modelMapper;

	@Bean(name = "reportModelMapper")
	public static ModelMapper modelMapper() {
		if (modelMapper != null) return modelMapper;

		modelMapper = new ModelMapper();

		PropertyMap<ReportGetDTO, Report> getPropertyMap = new PropertyMap<>() {
			@Override
			protected void configure() {
				skip().setId(0L);
			}
		};
		modelMapper.addMappings(getPropertyMap);

		return modelMapper;
	}
}
