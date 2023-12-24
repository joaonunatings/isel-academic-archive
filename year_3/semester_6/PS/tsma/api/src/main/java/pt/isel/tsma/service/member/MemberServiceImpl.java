package pt.isel.tsma.service.member;

import lombok.AllArgsConstructor;
import lombok.experimental.ExtensionMethod;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import pt.isel.tsma.config.MemberConfiguration;
import pt.isel.tsma.entity.dto.member.MemberCreateDTO;
import pt.isel.tsma.entity.dto.member.MemberGetDTO;
import pt.isel.tsma.entity.dto.member.MemberUpdateDTO;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.exception.model.member.DuplicateEmailException;
import pt.isel.tsma.exception.model.member.InvalidEmailException;
import pt.isel.tsma.exception.model.member.InvalidNameException;
import pt.isel.tsma.exception.model.member.MemberNotFoundException;
import pt.isel.tsma.repository.MemberRepository;
import pt.isel.tsma.repository.calendar.CalendarRepository;
import pt.isel.tsma.repository.specification.MemberSpecification;
import pt.isel.tsma.service.calendar.CalendarService;

import java.util.List;

import static pt.isel.tsma.util.Utils.*;

@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))
@ExtensionMethod({StringExtensions.class, ListExtensions.class})
@Slf4j(topic = "tsma")
@CacheConfig(cacheNames = {"members"})
public class MemberServiceImpl implements MemberService {
	private final CalendarRepository calendarRepository;
	private final MemberRepository memberRepository;
	private final CalendarService calendarService;
	private final ModelMapper mapper = MemberConfiguration.modelMapper();

	@Override
	@CacheEvict(allEntries = true)
	public Member createMember(MemberCreateDTO memberDto) {
		log.debug("Creating member...");
		val name = memberDto.getName();
		val email = memberDto.getEmail();

		if (name.isNullOrEmpty()) throw new InvalidNameException(name);
		if (!email.isNullOrEmpty()) {
			if (!isValidEmail(email)) throw new InvalidEmailException(email);

			if (memberRepository.existsByEmail(email)) throw new DuplicateEmailException(email);
		}

		val member = memberRepository.save(mapper.map(memberDto, Member.class));
		log.debug("Created member #" + member.getId());
		return member;
	}

	@Override
	@Cacheable
	public Member getMember(long memberId) {
		log.debug("Getting member #" + memberId + "...");
		val member = memberRepository.findById(memberId).orElseThrow(() -> new MemberNotFoundException(memberId));
		log.debug("Found member #" + memberId);
		return member;
	}

	@Override
	@Cacheable
	public List<Member> getMembers(MemberGetDTO memberDto, Pageable page) {
		log.debug("Getting members...");
		val specBuilder = new MemberSpecification();
		val validSortParams = MemberConfiguration.VALID_SORT_PARAMS;
		specBuilder.setIds(memberDto.getIds());
		specBuilder.setNames(memberDto.getNames());
		specBuilder.setEmails(memberDto.getEmails());
		validatePage(page, validSortParams);

		val members = memberRepository.findAll(specBuilder.build(), page).getContent();

		log.debug("Found " + members.size() + " members");
		return members;
	}

	@Override
	@CacheEvict(allEntries = true)
	public Member updateMember(long memberId, MemberUpdateDTO memberDto) {
		log.debug("Updating member #" + memberId + "...");
		val memberToUpdate = getMember(memberId);
		var newEmail = memberDto.getEmail();
		if (newEmail.isNullOrEmpty()) {
			newEmail = memberToUpdate.getEmail();
		} else {
			if (!isValidEmail(newEmail)) throw new InvalidEmailException(newEmail);
			if (memberRepository.isDuplicateEmail(memberToUpdate, newEmail))
				throw new DuplicateEmailException(newEmail, memberId);
		}

		var newName = memberDto.getName();
		if (newName.isNullOrEmpty()) newName = memberToUpdate.getName();

		memberToUpdate.setName(newName);
		memberToUpdate.setEmail(newEmail);
		val updatedMember = memberRepository.save(memberToUpdate);
		log.debug("Updated member #" + memberId);

		return updatedMember;
	}

	@Override
	@CacheEvict(allEntries = true)
	public long deleteMember(long memberId) {
		log.debug("Deleting member #" + memberId + "...");
		val member = getMember(memberId);

		val calendars = calendarRepository.findByMember(member);
		for (val calendar : calendars) {
			calendarService.removeMemberFromCalendar(calendar.getId(), memberId);
		}

		memberRepository.delete(member);
		log.debug("Deleted member #" + memberId);
		return memberId;
	}
}
