package pt.isel.tsma.controller.shift;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import pt.isel.tsma.entity.dto.shift.ShiftDTO;
import pt.isel.tsma.entity.dto.shift.ShiftGetDTO;

import java.util.List;

@RequestMapping("/shifts")
@Tag(name = "Shift")
public interface ShiftController {

	@GetMapping
	@Operation(summary = "Get shifts based on the given criteria")
	List<ShiftDTO> getShifts(
		ShiftGetDTO shiftDto,
		@PageableDefault(sort = {"calendar", "member", "date"}, size = 100) Pageable page);
}
