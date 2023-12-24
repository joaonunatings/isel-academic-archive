package pt.isel.tsma.entity.dto.member;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class MemberUpdateDTO implements IMemberDTO {

	private String name;

	private String email;
}
