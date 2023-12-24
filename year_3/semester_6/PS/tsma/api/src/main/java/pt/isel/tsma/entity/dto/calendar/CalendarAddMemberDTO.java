package pt.isel.tsma.entity.dto.calendar;

import lombok.Data;
import lombok.NoArgsConstructor;
import pt.isel.tsma.entity.model.shift.Type;

import java.util.List;

@Data
@NoArgsConstructor
public class CalendarAddMemberDTO implements ICalendarDTO {

	private long memberId;

	private List<Type> sequence;
}
