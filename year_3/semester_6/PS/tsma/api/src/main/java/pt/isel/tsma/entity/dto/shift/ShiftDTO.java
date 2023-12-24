package pt.isel.tsma.entity.dto.shift;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pt.isel.tsma.entity.model.shift.Type;

import java.time.LocalDate;

@NoArgsConstructor
@Data
public class ShiftDTO implements IShiftDTO {

	@NonNull
	LocalDate date;
	private long calendarId;
	private long memberId;
	@NonNull
	private Type type;
}
