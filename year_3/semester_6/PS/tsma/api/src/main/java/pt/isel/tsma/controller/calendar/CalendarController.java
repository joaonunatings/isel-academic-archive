package pt.isel.tsma.controller.calendar;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import pt.isel.tsma.entity.dto.calendar.*;
import pt.isel.tsma.entity.dto.shift.ShiftDTO;
import pt.isel.tsma.entity.dto.shift.ShiftUpdateDTO;

import java.util.List;

@RequestMapping("/calendars")
@Tag(name = "Calendar")
public interface CalendarController {

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	@Operation(summary = "Create a new calendar")
	CalendarDTO createCalendar(@RequestBody CalendarCreateDTO calendarDto);

	@GetMapping("/{calendarId}")
	@Operation(summary = "Get a calendar by id")
	CalendarDTO getCalendar(@PathVariable long calendarId);

	@GetMapping
	@Operation(summary = "Get calendars based on the given criteria")
	List<CalendarSummaryDTO> getCalendars(
		CalendarGetDTO calendarDto,
		@PageableDefault(sort = "startDate", direction = Sort.Direction.DESC, size = 20) Pageable page);

	@PutMapping("/{calendarId}")
	@Operation(summary = "Update a calendar")
	CalendarDTO updateCalendar(@PathVariable long calendarId, @RequestBody CalendarUpdateDTO calendarDto);

	@PatchMapping("/{calendarId}/shifts")
	@Operation(summary = "Update shifts from a calendar")
	List<ShiftDTO> updateShifts(@PathVariable long calendarId, @RequestBody List<ShiftUpdateDTO> shiftDtos);

	@PutMapping("/{calendarId}/members/{memberId}")
	@Operation(summary = "Change a member from a calendar", description = "The old member must exist in the calendar and the new one must not")
	CalendarDTO updateMember(@PathVariable long calendarId, @PathVariable long memberId, @RequestBody long newMemberId);

	@PostMapping("/{calendarId}/members")
	@Operation(summary = "Add a member to a calendar", description = "The member must not exist in the calendar")
	CalendarDTO addMemberToCalendar(@PathVariable long calendarId, @RequestBody CalendarAddMemberDTO calendarDto);

	@DeleteMapping("/{calendarId}/members/{memberId}")
	@Operation(summary = "Remove a member from a calendar")
	CalendarDTO removeMemberFromCalendar(@PathVariable long calendarId, @PathVariable long memberId);

	@DeleteMapping("/{calendarId}")
	@Operation(summary = "Delete a calendar")
	long deleteCalendar(@PathVariable long calendarId);
}
