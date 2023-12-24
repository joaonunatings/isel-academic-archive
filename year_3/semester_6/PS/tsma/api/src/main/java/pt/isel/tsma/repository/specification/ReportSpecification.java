package pt.isel.tsma.repository.specification;

import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import pt.isel.tsma.entity.model.Report;
import pt.isel.tsma.entity.model.shift.Type;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Data
public class ReportSpecification {

	private Specification<Report> spec = Specification.where(null);

	private List<Long> ids = new LinkedList<>();

	private List<Long> calendarIds = new LinkedList<>();

	private List<Long> memberIds = new LinkedList<>();

	private LocalDate startDate;

	private LocalDate endDate;

	private List<Type> shiftTypes = new LinkedList<>();

	public static Specification<Report> byId(long reportId) {
		return (root, query, cb) -> cb.equal(root.get("id"), reportId);
	}

	public static Specification<Report> byCalendarId(long calendarId) {
		return (root, query, cb) -> cb.equal(root.get("calendar").get("id"), calendarId);
	}

	public static Specification<Report> byMemberId(long memberId) {
		return (root, query, cb) -> cb.equal(root.get("member").get("id"), memberId);
	}

	public static Specification<Report> byStartDate(LocalDate startDate) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.greaterThanOrEqualTo(root.get("startDate"), startDate);
	}

	public static Specification<Report> byEndDate(LocalDate endDate) {
		return (root, query, criteriaBuilder) -> criteriaBuilder.lessThanOrEqualTo(root.get("endDate"), endDate);
	}

	public static Specification<Report> byShiftType(Type shiftType) {
		return (root, query, cb) -> cb.equal(root.get("shiftType"), shiftType);
	}

	public Specification<Report> build() {
		Specification<Report> localSpec = Specification.where(null);

		if (ids != null) {
			Specification<Report> idSpec = Specification.where(null);
			for (Long id : ids) idSpec = idSpec.or(byId(id));
			localSpec = localSpec.and(idSpec);
		}

		if (calendarIds != null) {
			Specification<Report> calendarIdSpec = Specification.where(null);
			for (Long calendarId : calendarIds) calendarIdSpec = calendarIdSpec.or(byCalendarId(calendarId));
			localSpec = localSpec.and(calendarIdSpec);
		}

		if (memberIds != null) {
			Specification<Report> memberIdSpec = Specification.where(null);
			for (Long memberId : memberIds) memberIdSpec = memberIdSpec.or(byMemberId(memberId));
			localSpec = localSpec.and(memberIdSpec);
		}

		if (startDate != null) {
			localSpec = localSpec.and(byStartDate(startDate));
		}

		if (endDate != null) {
			localSpec = localSpec.and(byEndDate(endDate));
		}

		if (shiftTypes != null) {
			Specification<Report> shiftTypeSpec = Specification.where(null);
			for (Type type : shiftTypes) shiftTypeSpec = shiftTypeSpec.or(byShiftType(type));
			localSpec = localSpec.and(shiftTypeSpec);
		}

		setSpec(localSpec);
		return localSpec;
	}
}
