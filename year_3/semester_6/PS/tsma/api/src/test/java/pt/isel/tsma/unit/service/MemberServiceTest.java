package pt.isel.tsma.unit.service;

import lombok.val;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.TestPropertySource;
import pt.isel.tsma.entity.dto.member.MemberCreateDTO;
import pt.isel.tsma.entity.model.Member;
import pt.isel.tsma.exception.model.member.DuplicateEmailException;
import pt.isel.tsma.exception.model.member.InvalidEmailException;
import pt.isel.tsma.exception.model.member.InvalidNameException;
import pt.isel.tsma.repository.MemberRepository;
import pt.isel.tsma.repository.ReportRepository;
import pt.isel.tsma.repository.ShiftRepository;
import pt.isel.tsma.repository.calendar.CalendarParametersRepository;
import pt.isel.tsma.repository.calendar.CalendarRepository;
import pt.isel.tsma.service.calendar.CalendarService;
import pt.isel.tsma.service.calendar.CalendarServiceImpl;
import pt.isel.tsma.service.member.MemberService;
import pt.isel.tsma.service.member.MemberServiceImpl;
import pt.isel.tsma.service.report.ReportService;
import pt.isel.tsma.service.report.ReportServiceImpl;
import pt.isel.tsma.service.shift.ShiftService;
import pt.isel.tsma.service.shift.ShiftServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@TestPropertySource(locations = "classpath:application-unit-test.properties")
@ExtendWith(MockitoExtension.class)
public class MemberServiceTest {

	@Mock
	private MemberRepository memberRepository;

	@Mock
	private CalendarRepository calendarRepository;

	@Mock
	private CalendarParametersRepository calendarParametersRepository;

	@Mock
	private ShiftRepository shiftRepository;

	@Mock
	private ReportRepository reportRepository;

	private MemberService memberService;

	@BeforeEach
	public void setup() {
		ReportService reportService = new ReportServiceImpl(reportRepository, shiftRepository);
		ShiftService shiftService = new ShiftServiceImpl(shiftRepository, reportService);
		CalendarService calendarService = new CalendarServiceImpl(calendarRepository, calendarParametersRepository, memberRepository, shiftService);
		memberService = new MemberServiceImpl(calendarRepository, memberRepository, calendarService);
	}

	@Nested
	@DisplayName("Tests for createMember")
	class CreateMember {

		private final MemberCreateDTO memberDto = new MemberCreateDTO("user", "user@email.com");
		private final Member member = new Member(1L, memberDto.getName(), memberDto.getEmail(), null, null, false);

		@AfterEach
		public void teardown() {
			assertAll(
				() -> verifyNoInteractions(calendarRepository),
				() -> verifyNoInteractions(shiftRepository),
				() -> verifyNoInteractions(reportRepository)
			);
		}

		@Test
		@DisplayName("Create member")
		void createMember() {
			when(memberRepository.existsByEmail(memberDto.getEmail())).thenReturn(false);
			when(memberRepository.save(any(Member.class))).thenReturn(member);

			val newMember = memberService.createMember(memberDto);

			assertAll(
				() -> verify(memberRepository).existsByEmail(memberDto.getEmail()),
				() -> verify(memberRepository).save(any(Member.class)),
				() -> assertEquals(member, newMember)
			);
		}

		@Test
		@DisplayName("Create member with no email")
		void createMemberWithNoEmail() {
			memberDto.setEmail(null);
			member.setEmail(null);
			when(memberRepository.save(any(Member.class))).thenReturn(member);

			val newMember = memberService.createMember(memberDto);

			assertAll(
				() -> verify(memberRepository, never()).existsByEmail(memberDto.getEmail()),
				() -> verify(memberRepository).save(any(Member.class)),
				() -> assertEquals(member, newMember)
			);
		}

		@Test
		@DisplayName("Create member with invalid name")
		void createMemberWithInvalidName() {
			memberDto.setName("");

			assertAll(
				() -> assertThrows(InvalidNameException.class, () -> memberService.createMember(memberDto)),
				() -> verifyNoInteractions(memberRepository)
			);
		}

		@Test
		@DisplayName("Create member with invalid email")
		void createMemberWithInvalidEmail() {
			val memberDtos = List.of(
				new MemberCreateDTO("User", "A"),
				new MemberCreateDTO("User", "A@"),
				new MemberCreateDTO("User", "@asd.com"),
				new MemberCreateDTO("User", "Asdas@@.com")
			);

			assertAll(
				() -> memberDtos.forEach(memberDto -> assertThrows(InvalidEmailException.class, () -> memberService.createMember(memberDto))),
				() -> verifyNoInteractions(memberRepository)
			);
		}

		@Test
		@DisplayName("Create member with duplicate email")
		void createMemberWithDuplicateEmail() {
			val email = memberDto.getEmail();
			when(memberRepository.existsByEmail(email)).thenReturn(true);

			assertAll(
				() -> assertThrows(DuplicateEmailException.class, () -> memberService.createMember(memberDto)),
				() -> verify(memberRepository).existsByEmail(email),
				() -> verify(memberRepository, never()).save(any(Member.class))
			);
		}
	}
}
