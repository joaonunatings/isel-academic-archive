export class Errors {

	public static httpErrors = {
		'e_Calendar_NotFound_404': Errors.httpError(404,'Calendar was not found.'),
		'e_Calendar_Invalid_400': Errors.httpError(400,'Calendar is invalid.'),
		'e_Calendar_ReplicatedMember_400': Errors.httpError(400,'Calendar can not have duplicate members.'),
		'e_Calendar_DuplicateTitle_400': Errors.httpError(400,'Calendar title must be unique.'),
		'e_Member_NotFound_404': Errors.httpError(404,'Member was not found.'),
		'e_Member_Invalid_400': Errors.httpError(400,'Invalid Member.'),
		'e_Member_InvalidEmail_400': Errors.httpError(400,'Member email is not valid.'),
		'e_Member_InvalidName_400': Errors.httpError(400,'Member name is not valid.'),
		'e_Member_DuplicatedEmail_400': Errors.httpError(404,'Member email already in use.'),
		'e_Report_NotFound_404': Errors.httpError(404,'Report was not found.'),
		'e_Report_Invalid_400': Errors.httpError(400,'Report is invalid.'),
		'e_Shift_NotFound_404': Errors.httpError(404,'Shift was not found.'),
		'e_Shift_Invalid_400': Errors.httpError(400,'Shift is invalid.'),
		'e_Graph_Error_500': Errors.httpError(500,'Could not sync export with graph.'),
		'e_Server_Error_500': Errors.httpError(500,'Internal Server Error.')
	}

	private static httpError(status: number, body: string) {
		return {
			'status': status,
			'body':body
		}
	}




}
