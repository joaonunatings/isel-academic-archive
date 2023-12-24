package pt.isel.tsma.entity.dto.calendar;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import pt.isel.tsma.entity.model.shift.Type;

import java.util.List;

@Data
@NoArgsConstructor
public class SequenceDTO {

	private long memberId;

	@NonNull
	private List<Type> sequence;
}
