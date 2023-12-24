package pt.isel.tsma.entity.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.entity.model.shift.Type;

import javax.persistence.*;
import java.time.LocalDate;

@Entity(name = "report")
@Table(name = "report")
@Getter
@Setter
@NoArgsConstructor
public class Report {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(nullable = false)
	private Member member;

	@NonNull
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn
	private Calendar calendar;

	@NonNull
	@Column(nullable = false)
	private LocalDate startDate;

	@NonNull
	@Column(nullable = false)
	private LocalDate endDate;

	@NonNull
	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private Type shiftType = Type.REST;

	@NonNull
	private Long totalShifts = 0L;

	public Report(
		@NonNull Member member,
		@NonNull Calendar calendar,
		@NonNull LocalDate startDate,
		@NonNull LocalDate endDate,
		@NonNull Type shiftType,
		@NonNull Long totalShifts) {
		this.member = member;
		this.calendar = calendar;
		this.startDate = startDate;
		this.endDate = endDate;
		this.shiftType = shiftType;
		this.totalShifts = totalShifts;
	}
}
