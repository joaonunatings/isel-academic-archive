package pt.isel.tsma.controller.report;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.isel.tsma.entity.dto.report.ReportDTO;
import pt.isel.tsma.entity.dto.report.ReportGetDTO;

import java.util.List;

@RequestMapping("/reports")
@Tag(name = "Report")
public interface ReportController {

	@GetMapping
	@Operation(summary = "Get reports based on the given criteria")
	List<ReportDTO> getReports(
		ReportGetDTO reportDto,
		@PageableDefault(sort = {"member", "shiftType"}, size = 100) Pageable page);
}
