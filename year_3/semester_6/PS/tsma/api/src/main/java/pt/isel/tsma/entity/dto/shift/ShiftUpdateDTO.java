package pt.isel.tsma.entity.dto.shift;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pt.isel.tsma.entity.model.shift.Type;

import java.time.LocalDate;
import java.time.LocalTime;

@NoArgsConstructor
@Data
public class ShiftUpdateDTO implements IShiftDTO {

	@NonNull
	private Long memberId;

	@NonNull
	private LocalDate date;

	@NonNull
	private Type type;

	private LocalTime startTime;

	private LocalTime endTime;

	public ShiftUpdateDTO(@NonNull Long memberId, @NonNull LocalDate date, @NonNull Type type) {
		this.memberId = memberId;
		this.date = date;
		this.type = type;
	}
}
