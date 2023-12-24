package pt.isel.tsma.repository.specification;

import lombok.Data;
import org.springframework.data.jpa.domain.Specification;
import pt.isel.tsma.entity.model.calendar.Calendar;
import pt.isel.tsma.util.Utils;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

@Data
public class CalendarSpecification {

	private Specification<Calendar> spec = Specification.where(null);

	private List<Long> ids = new LinkedList<>();

	private List<String> titles = new LinkedList<>();

	private LocalDate startDate;

	private LocalDate endDate;

	public static Specification<Calendar> byId(long calendarId) {
		return (root, query, cb) -> cb.equal(root.get("id"), calendarId);
	}

	public static Specification<Calendar> byTitle(String title) {
		return (root, query, cb) -> cb.equal(root.get("title"), title);
	}

	public static Specification<Calendar> byStartDate(LocalDate startDate) {
		return (root, query, cb) -> cb.greaterThanOrEqualTo(root.get("startDate"), startDate);
	}

	public static Specification<Calendar> byEndDate(LocalDate endDate) {
		return (root, query, cb) -> cb.lessThanOrEqualTo(root.get("endDate"), endDate);
	}

	public Specification<Calendar> build() {
		Specification<Calendar> localSpec = Specification.where(null);

		if (ids != null) {
			Specification<Calendar> idSpec = Specification.where(null);
			for (Long id : ids) idSpec = idSpec.or(byId(id));
			localSpec = localSpec.and(idSpec);
		}

		if (titles != null) {
			Specification<Calendar> titleSpec = Specification.where(null);
			for (String title : titles) titleSpec = titleSpec.or(byTitle(title));
			localSpec = localSpec.and(titleSpec);
		}

		if (startDate != null) localSpec = localSpec.and(byStartDate(startDate));
		if (endDate != null) localSpec = localSpec.and(byEndDate(endDate));

		localSpec = localSpec.and(Utils.byDeleted(false));

		setSpec(localSpec);
		return localSpec;
	}
}
