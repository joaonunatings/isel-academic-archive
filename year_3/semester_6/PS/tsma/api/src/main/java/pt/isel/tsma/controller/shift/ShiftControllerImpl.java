package pt.isel.tsma.controller.shift;

import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.tsma.config.ShiftConfiguration;
import pt.isel.tsma.entity.dto.shift.ShiftDTO;
import pt.isel.tsma.entity.dto.shift.ShiftGetDTO;
import pt.isel.tsma.service.shift.ShiftService;
import pt.isel.tsma.util.Utils;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ExtensionMethod(Utils.ListExtensions.class)
public class ShiftControllerImpl implements ShiftController {

	private final ShiftService shiftService;
	private final ModelMapper mapper = ShiftConfiguration.modelMapper();

	@Override
	public List<ShiftDTO> getShifts(ShiftGetDTO shiftDto, Pageable page) {
		val shifts = shiftService.getShifts(shiftDto, page);
		return shifts.mapList(ShiftDTO.class, mapper);
	}
}
