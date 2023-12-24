package pt.isel.tsma.entity.model;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import pt.isel.tsma.entity.model.shift.Shift;

import javax.persistence.*;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Entity(name = "member")
@Table(name = "member")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SQLDelete(sql = "UPDATE member m SET deleted = true WHERE m.id = ?")
public class Member {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id", nullable = false, unique = true)
	private Long id;

	@NonNull
	@Column(name = "name", nullable = false)
	private String name;

	@Column(name = "email")
	private String email;

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Report> reports = Collections.emptyList();

	@OneToMany(mappedBy = "member", cascade = CascadeType.ALL)
	private List<Shift> shifts = Collections.emptyList();

	@Column(name = "deleted", nullable = false)
	@NonNull
	private Boolean deleted = false;

	@Override
	public String toString() {
		return "Member #" + id + "\nName: " + name + "\nEmail: " + email;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Member member = (Member) o;
		return Objects.equals(id, member.id);
	}
}
