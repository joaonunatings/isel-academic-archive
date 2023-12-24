import {Component, OnInit} from '@angular/core';
import {MemberService} from "../../services/member.service";
import {Member} from "../../model/member";
import {UntypedFormControl, UntypedFormGroup, Validators} from "@angular/forms";
import {Utils} from "../../utils/utils";
import {MyErrorHandler} from "../../services/my-error-handler";
import {AlertService} from "../../services/alert.service";
import {AlertType} from "../alert/alert.component";

@Component({
  selector: 'app-members',
  templateUrl: './members.component.html',
  styleUrls: ['./members.component.css']
})
export class MembersComponent implements OnInit {

	public members : Member[] = []

	public membersForm = new UntypedFormGroup({
		memberName: new UntypedFormControl('', Validators.required),
		memberEmail: new UntypedFormControl('', Validators.required),
	})

	public updateMemberForm = new UntypedFormGroup({
		newMemberName: new UntypedFormControl('',),
		newMemberEmail: new UntypedFormControl(''),
	})

	constructor(private membersService : MemberService,
				private errorHandler: MyErrorHandler,
				private alertService: AlertService) {
		this.membersService.getAllMembers()
			.subscribe({
				next: (members) => {
					if (members && members.length > 0) this.members = members
				},
				error: err => {
					errorHandler.handleError(err)
				}
		})
		alertService.newAlert(AlertType.WARNING,
			'When a member is deleted, it will be removed from each calendar that contains it.',
			'Be careful when deleting members')
	}


	ngOnInit(): void {
	}

	/**
	 * To be called, when submitting in member creation form.
	 * Gets form value, calls API member service to create member with form values.
	 */
	onSubmitMember() {
		let form = this.membersForm.value
		this.membersService.createMember(new Member(0,form.memberName,form.memberEmail)).subscribe({
			next: (member) => {
				if (member != null) {
					this.members.push(member)
				}
			},
			error: err => {
				this.errorHandler.handleError(err)
			}
		})
	}

	/**
	 * To be called when a member update action is submitted via the update member form.
	 * Calls member service to update the values of the member name and/or email given in the form.
	 * @param m Member object to update for the interface model
	 */
	onSubmitUpdate(m: Member) {
		let form = this.updateMemberForm.value
		this.membersService.updateMember(m,form.newMemberName,form.newMemberEmail).subscribe({
			next: (updated) => {
				if (updated) {
					m.name = updated.name
					m.email = updated.email
				}
			},
			error: err => {
				this.errorHandler.handleError(err)
			}
		})
	}

	private lock : boolean = false

	/**
	 * Calls members service delete request using member id, if successful, remove that member from the view model.
	 * @param memberId Provided member id
	 */
	public deleteMember(memberId : number) {
		this.membersService.deleteMember(memberId).subscribe({
			next: () => {
				this.members.splice(this.findIndexOfMember(memberId),1)
			},
			error: err => {
				this.errorHandler.handleError(err)
			}
		})
		this.lock = false
	}

	/**
	 * Returns the index of the member, with given id, in the members list.
	 * @param id Provided member Id
	 */
	private findIndexOfMember(id : number) : number{
		return this.members.findIndex(elem => {
			 return elem.id == id
		})
	}

	showDeleteTooltip(toolId : number) {
		if (!this.lock) {
			Utils.getElementAndSetVisibility(`tooltip${toolId}`, 'visible', '1')
			this.lock = true
		}
	}

	hideDeleteTooltip(toolId : number) {
		Utils.getElementAndSetVisibility(`tooltip${toolId}`, 'hidden', '0')
		this.lock = false
	}

	showMemberForm() {
		Utils.getElementAndSetVisibility("members-form-container", 'visible', '1')
	}

	hideMemberForm() {
		Utils.getElementAndSetVisibility("members-form-container", 'hidden', '0')
	}

	private updateLock: boolean = false
	showUpdateForm(id: number) {
		if (!this.updateLock) {
			Utils.getElementAndSetVisibility(`update-tooltip-${id}`, 'visible', '1')
			this.updateLock = true
		}
	}

	hideUpdateForm(id: number) {
		Utils.getElementAndSetVisibility(`update-tooltip-${id}`, 'hidden','0')
		this.updateLock = false
	}

	copyText(text : string) {
		navigator.clipboard.writeText(text)
	}

}
