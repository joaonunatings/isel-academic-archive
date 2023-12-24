package pt.isel.tsma.controller.report;

import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.tsma.config.ReportConfiguration;
import pt.isel.tsma.entity.dto.report.ReportDTO;
import pt.isel.tsma.entity.dto.report.ReportGetDTO;
import pt.isel.tsma.service.report.ReportService;
import pt.isel.tsma.util.Utils;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ExtensionMethod(Utils.ListExtensions.class)
public class ReportControllerImpl implements ReportController {

	private final ReportService reportService;
	private final ModelMapper mapper = ReportConfiguration.modelMapper();

	@Override
	public List<ReportDTO> getReports(ReportGetDTO reportDto, Pageable page) {
		val reports = reportService.getReports(reportDto, page);
		return reports.mapList(ReportDTO.class, mapper);
	}
}
