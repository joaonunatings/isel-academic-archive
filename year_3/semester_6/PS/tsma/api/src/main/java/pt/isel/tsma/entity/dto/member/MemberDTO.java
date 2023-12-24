package pt.isel.tsma.entity.dto.member;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@Data
public class MemberDTO implements IMemberDTO {

	private long id;

	@NonNull
	private String name;

	private String email;
}
