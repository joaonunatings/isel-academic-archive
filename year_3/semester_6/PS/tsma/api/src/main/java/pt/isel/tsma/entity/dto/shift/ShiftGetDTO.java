package pt.isel.tsma.entity.dto.shift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import pt.isel.tsma.entity.model.shift.Type;

import java.time.LocalDate;
import java.util.List;

import static java.util.Collections.emptyList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftGetDTO implements IShiftDTO {

	List<Long> calendars = emptyList();

	List<Long> members = emptyList();

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	LocalDate endDate;

	List<Type> types = emptyList();

	public ShiftGetDTO(long calendarId, long memberId) {
		this.calendars = List.of(calendarId);
		this.members = List.of(memberId);
	}
}
