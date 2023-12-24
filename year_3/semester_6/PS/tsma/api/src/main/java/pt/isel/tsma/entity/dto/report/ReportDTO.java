package pt.isel.tsma.entity.dto.report;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pt.isel.tsma.entity.model.shift.Type;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class ReportDTO implements IReportDTO {

	private long id;

	private long memberId;

	private long calendarId;

	@NonNull
	private LocalDate startDate;

	@NonNull
	private LocalDate endDate;

	@NonNull
	private Type shiftType;

	private int totalShifts;
}
