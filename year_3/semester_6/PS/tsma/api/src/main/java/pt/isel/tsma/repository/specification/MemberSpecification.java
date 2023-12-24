package pt.isel.tsma.repository.specification;

import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.util.Utils;

import java.util.LinkedList;
import java.util.List;

@Data
public class MemberSpecification {

	private Specification<Member> spec = Specification.where(null);

	private List<Long> ids = new LinkedList<>();

	private List<String> emails = new LinkedList<>();

	private List<String> names = new LinkedList<>();

	public static Specification<Member> byId(long memberId) {
		return (root, query, cb) -> cb.equal(root.get("id"), memberId);
	}

	public static Specification<Member> byEmail(String email) {
		return (root, query, cb) -> cb.equal(root.get("email"), email);
	}

	public static Specification<Member> byName(String name) {
		return (root, query, cb) -> cb.equal(root.get("name"), name);
	}

	public Specification<Member> build() {
		Specification<Member> localSpec = Specification.where(null);

		if (ids != null) {
			Specification<Member> idSpec = Specification.where(null);
			for (Long id : ids) idSpec = idSpec.or(byId(id));
			localSpec = localSpec.and(idSpec);
		}

		if (emails != null) {
			Specification<Member> emailSpec = Specification.where(null);
			for (String email : emails) emailSpec = emailSpec.or(byEmail(email));
			localSpec = localSpec.and(emailSpec);
		}

		if (names != null) {
			Specification<Member> nameSpec = Specification.where(null);
			for (String name : names) nameSpec = nameSpec.or(byName(name));
			localSpec = localSpec.and(nameSpec);
		}

		localSpec = localSpec.and(Utils.byDeleted(false));

		setSpec(localSpec);
		return spec;
	}
}
