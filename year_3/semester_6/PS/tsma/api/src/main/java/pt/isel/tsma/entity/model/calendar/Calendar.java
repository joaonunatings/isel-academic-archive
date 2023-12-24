package pt.isel.tsma.entity.model.calendar;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.hibernate.annotations.SQLDelete;
import pt.isel.tsma.entity.model.Report;
import pt.isel.tsma.entity.model.State;
import pt.isel.tsma.entity.model.shift.Shift;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Entity(name = "calendar")
@Table(name = "calendar")
@Getter
@Setter
@NoArgsConstructor
@SQLDelete(sql = "UPDATE calendar SET deleted = true, state = CASE WHEN state = 'CREATED' THEN 'SYNCED' ELSE 'NOT_SYNCED' END WHERE id = ?")
public class Calendar {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@Column(nullable = false)
	@NonNull
	private String title;

	@Column(length = 1024)
	private String description;

	private LocalDate startDate;

	@NonNull
	private LocalDate endDate;

	@OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL)
	@NonNull
	private List<Shift> shifts;

	@OneToMany(mappedBy = "calendar", cascade = CascadeType.ALL)
	private List<Report> reports;

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
	public void updateStateOnUpdate() {
		if (state == State.SYNCED) state = State.NOT_SYNCED;    // Entity was updated, set SYNC status to NOT_SYNC
	}

	@Override
	public boolean equals(Object other) {
		if (this == other) return true;
		if (other == null || getClass() != other.getClass()) return false;
		Calendar that = (Calendar) other;
		return Objects.equals(id, that.id);
	}
}
