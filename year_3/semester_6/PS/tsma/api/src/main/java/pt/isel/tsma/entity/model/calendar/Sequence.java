package pt.isel.tsma.entity.model.calendar;

import lombok.Data;
import lombok.NonNull;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.entity.model.shift.Type;

import java.util.List;

@Data
public class Sequence {

	@NonNull
	private Member member;

	@NonNull
	private List<Type> sequence;
}
