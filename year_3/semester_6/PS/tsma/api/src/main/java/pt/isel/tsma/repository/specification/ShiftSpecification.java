package pt.isel.tsma.repository.specification;

import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import pt.isel.tsma.entity.model.shift.Shift;
import pt.isel.tsma.entity.model.shift.Type;
import pt.isel.tsma.util.Utils;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Data
public class ShiftSpecification {

	private Specification<Shift> spec = Specification.where(null);

	private List<Long> calendarIds = new LinkedList<>();

	private List<Long> memberIds = new LinkedList<>();

	private LocalDate startDate = null;

	private LocalDate endDate = null;

	private List<Type> types = new LinkedList<>();

	public static Specification<Shift> byCalendarId(long calendarId) {
		return (root, query, cb) -> cb.equal(root.get("calendar").get("id"), calendarId);
	}

	public static Specification<Shift> byMemberId(long memberId) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("member").get("id"), memberId);
	}

	public static Specification<Shift> byStartDate(LocalDate startDate) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("id").get("date"), startDate);
	}

	public static Specification<Shift> byEndDate(LocalDate endDate) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("id").get("date"), endDate);
	}

	public static Specification<Shift> byType(Type type) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("type"), type);
	}

	public Specification<Shift> build() {
		Specification<Shift> localSpec = Specification.where(null);

		if (calendarIds != null) {
			Specification<Shift> calendarIdSpec = Specification.where(null);
			for (Long calendarId : calendarIds) calendarIdSpec = calendarIdSpec.or(byCalendarId(calendarId));
			localSpec = localSpec.and(calendarIdSpec);
		}

		if (memberIds != null) {
			Specification<Shift> memberIdSpec = Specification.where(null);
			for (Long memberId : memberIds) memberIdSpec = memberIdSpec.or(byMemberId(memberId));
			localSpec = localSpec.and(memberIdSpec);
		}

		if (startDate != null) {
			localSpec = localSpec.and(byStartDate(startDate));
		}

		if (endDate != null) {
			localSpec = localSpec.and(byEndDate(endDate));
		}

		if (types != null) {
			Specification<Shift> typeSpec = Specification.where(null);
			for (Type type : types) typeSpec = typeSpec.or(byType(type));
			localSpec = localSpec.and(typeSpec);
		}

		localSpec = localSpec.and(Utils.byDeleted(false));

		setSpec(localSpec);
		return spec;
	}
}
