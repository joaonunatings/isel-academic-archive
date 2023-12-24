package pt.isel.tsma.entity.model.shift;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.entity.model.State;
import pt.isel.tsma.entity.model.calendar.Calendar;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalTime;

@Entity(name = "shift")
@Table(name = "shift")
@Getter
@Setter
@NoArgsConstructor
// Only change this in case of entity redefinition. The order is important (calendar -> date -> member)
@SQLDelete(sql = "UPDATE shift s SET deleted = true, state = CASE WHEN state = 'CREATED' THEN 'SYNCED' ELSE 'NOT_SYNCED' END WHERE s.calendar_id = ? AND s.date = ? AND s.member_id = ?")
public class Shift implements Cloneable {

	@EmbeddedId
	@NonNull
	private ShiftId id;

	@NonNull
	@ManyToOne(optional = false)
	@MapsId("calendarId")
	private Calendar calendar;

	@NonNull
	@ManyToOne(optional = false)
	@MapsId("memberId")
	private Member member;

	@Column(name = "start_time")
	private LocalTime startTime;

	@Column(name = "end_time")
	private LocalTime endTime;

	@NonNull
	@Column(name = "type", nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private Type type;

	@Column(name = "graph_id")
	private String graphId;

	@Column(name = "deleted", nullable = false)
	@NonNull
	private Boolean deleted = false;

	@Column(name = "state", nullable = false)
	@NonNull
	@Enumerated(EnumType.STRING)
	private State state = State.CREATED;

	@PreUpdate
	public void updateSyncStatus() {
		if (state == State.SYNCED) state = State.NOT_SYNCED;    // Entity was updated, set SYNC status to NOT_SYNC
	}

	public Shift(@NonNull Calendar calendar, @NonNull Member member, @NonNull LocalDate date, @NonNull Type type) {
		this.calendar = calendar;
		this.member = member;
		this.type = type;
		this.id = new ShiftId(calendar.getId(), member.getId(), date);
	}

	public LocalDate getDate() {
		return this.id.getDate();
	}

	@Override
	public Shift clone() throws CloneNotSupportedException {
		Shift clone = (Shift) super.clone();
		val id = new ShiftId(this.calendar.getId(), this.member.getId(), this.id.getDate());
		clone.setId(id);
		clone.setType(this.type);
		return clone;
	}

	@Embeddable
	@Data
	@NoArgsConstructor
	@RequiredArgsConstructor
	public static class ShiftId implements Serializable {

		@NonNull
		@Column(name = "calendar_id", nullable = false)
		private Long calendarId;

		@NonNull
		@Column(name = "member_id", nullable = false)
		private Long memberId;

		@NonNull
		@Column(name = "date", nullable = false)
		private LocalDate date;

		@Override
		public String toString() {
			return "(calendarId=" + calendarId + ", memberId=" + memberId + ", date=" + date + ")";
		}
	}
}
