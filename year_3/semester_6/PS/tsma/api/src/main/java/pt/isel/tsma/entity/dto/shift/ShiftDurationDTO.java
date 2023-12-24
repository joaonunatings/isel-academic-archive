package pt.isel.tsma.entity.dto.shift;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShiftDurationDTO {

	@NonNull
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime start;

	@NonNull
	@DateTimeFormat(pattern = "HH:mm")
	private LocalTime end;
}
