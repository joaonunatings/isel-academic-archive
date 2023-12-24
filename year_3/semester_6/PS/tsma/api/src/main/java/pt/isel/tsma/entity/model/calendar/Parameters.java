package pt.isel.tsma.entity.model.calendar;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import pt.isel.tsma.config.CalendarConfiguration;
import pt.isel.tsma.entity.model.shift.Type;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;

@Data
@NoArgsConstructor
@Document(collection = "calendarparameters")
public class Parameters {

	@Id
	private String id;

	@NonNull
	private Long calendarId;

	@NonNull
	private HashMap<Type, ShiftDuration> shiftDurations = CalendarConfiguration.DEFAULT_SHIFT_DURATIONS;

	@NonNull
	private List<Sequence> sequences;

	public Parameters(@NonNull Long calendarId, @NonNull HashMap<Type, ShiftDuration> shiftDurations, @NonNull List<Sequence> sequences) {
		this.calendarId = calendarId;
		this.shiftDurations = shiftDurations;
		this.sequences = sequences;
	}

	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class ShiftDuration {

		@NonNull
		private LocalTime start;

		@NonNull
		private LocalTime end;
	}

	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class Sequence {

		@NonNull
		private Long memberId;

		@NonNull
		private List<Type> sequence;
	}
}
