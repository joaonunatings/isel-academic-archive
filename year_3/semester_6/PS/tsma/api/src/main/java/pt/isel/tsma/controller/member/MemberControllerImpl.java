package pt.isel.tsma.controller.member;

import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RestController;
import pt.isel.tsma.config.MemberConfiguration;
import pt.isel.tsma.entity.dto.member.MemberCreateDTO;
import pt.isel.tsma.entity.dto.member.MemberDTO;
import pt.isel.tsma.entity.dto.member.MemberGetDTO;
import pt.isel.tsma.entity.dto.member.MemberUpdateDTO;
import pt.isel.tsma.service.member.MemberService;
import pt.isel.tsma.util.Utils;

import java.util.List;

@RestController
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ExtensionMethod(Utils.ListExtensions.class)
public class MemberControllerImpl implements MemberController {

	private final MemberService memberService;
	private final ModelMapper mapper = MemberConfiguration.modelMapper();

	@Override
	public MemberDTO createMember(MemberCreateDTO memberDto) {
		return mapper.map(memberService.createMember(memberDto), MemberDTO.class);
	}

	@Override
	public List<MemberDTO> getMembers(MemberGetDTO memberDto, Pageable page) {
		val members = memberService.getMembers(memberDto, page);
		return members.mapList(MemberDTO.class, mapper);
	}

	@Override
	public MemberDTO updateMember(long memberId, MemberUpdateDTO memberDto) {
		return mapper.map(memberService.updateMember(memberId, memberDto), MemberDTO.class);
	}

	@Override
	public long deleteMember(long memberId) {
		return memberService.deleteMember(memberId);
	}
}
