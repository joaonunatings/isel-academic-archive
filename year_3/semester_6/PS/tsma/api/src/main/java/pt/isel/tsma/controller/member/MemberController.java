package pt.isel.tsma.controller.member;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pt.isel.tsma.entity.dto.member.MemberCreateDTO;
import pt.isel.tsma.entity.dto.member.MemberDTO;
import pt.isel.tsma.entity.dto.member.MemberGetDTO;
import pt.isel.tsma.entity.dto.member.MemberUpdateDTO;

import java.util.List;

@RequestMapping("/members")
@Tag(name = "Member")
public interface MemberController {

	@PostMapping
	@ResponseStatus(value = HttpStatus.CREATED)
	@Operation(summary = "Create a new member")
	MemberDTO createMember(@RequestBody MemberCreateDTO memberDto);

	@GetMapping
	@Operation(summary = "Get members based on the given criteria")
	List<MemberDTO> getMembers(MemberGetDTO memberDto, @PageableDefault(sort = "name", size = 50) Pageable page);

	@PutMapping("/{memberId}")
	@Operation(summary = "Update an existing member")
	MemberDTO updateMember(@PathVariable long memberId, @RequestBody MemberUpdateDTO memberDto);

	@DeleteMapping("/{memberId}")
	@Operation(summary = "Delete an existing member")
	long deleteMember(@PathVariable long memberId);
}
