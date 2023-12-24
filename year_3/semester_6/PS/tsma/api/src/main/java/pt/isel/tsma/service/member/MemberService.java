package pt.isel.tsma.service.member;

import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;
import pt.isel.tsma.entity.dto.member.MemberCreateDTO;
import pt.isel.tsma.entity.dto.member.MemberGetDTO;
import pt.isel.tsma.entity.dto.member.MemberUpdateDTO;
import pt.isel.tsma.entity.model.Member;

import java.util.List;

@Transactional
public interface MemberService {

	Member createMember(MemberCreateDTO memberDto);

	@Transactional(readOnly = true)
	Member getMember(long memberId);

	@Transactional(readOnly = true)
	List<Member> getMembers(MemberGetDTO memberDto, Pageable page);

	Member updateMember(long memberId, MemberUpdateDTO memberDto);

	long deleteMember(long memberId);
}
