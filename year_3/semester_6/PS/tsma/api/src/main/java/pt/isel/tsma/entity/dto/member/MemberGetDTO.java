package pt.isel.tsma.entity.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

import static java.util.Collections.emptyList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MemberGetDTO implements IMemberDTO {

	private List<Long> ids = emptyList();

	private List<String> names = emptyList();

	private List<String> emails = emptyList();
}
