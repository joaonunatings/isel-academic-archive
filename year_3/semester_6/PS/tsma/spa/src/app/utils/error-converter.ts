import {Errors} from './errors'

const API_TO_ERRORS : any = {
	'1000': Errors.httpErrors.e_Calendar_NotFound_404,
	'1001': Errors.httpErrors.e_Calendar_Invalid_400,
	'1012': Errors.httpErrors.e_Calendar_DuplicateTitle_400,
	'1202': Errors.httpErrors.e_Calendar_ReplicatedMember_400,
	'2000': Errors.httpErrors.e_Member_NotFound_404,
	'2001': Errors.httpErrors.e_Member_Invalid_400,
	'2011': Errors.httpErrors.e_Member_InvalidEmail_400,
	'2012': Errors.httpErrors.e_Member_DuplicatedEmail_400,
	'2021': Errors.httpErrors.e_Member_InvalidName_400,
	'3000': Errors.httpErrors.e_Report_NotFound_404,
	'3001': Errors.httpErrors.e_Report_Invalid_400,
	'4000': Errors.httpErrors.e_Shift_NotFound_404,
	'4001': Errors.httpErrors.e_Shift_Invalid_400,
	'5000': Errors.httpErrors.e_Graph_Error_500,
}


export class ErrorConverter {

	public static convertError(apiError: any) {
		return API_TO_ERRORS[apiError.code] || Errors.httpErrors.e_Server_Error_500
	}

}

