package pt.isel.tsma.entity.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class MemberCreateDTO implements IMemberDTO {

	@NonNull
	private String name;

	private String email;
}
