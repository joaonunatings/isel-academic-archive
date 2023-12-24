import { Injectable } from '@angular/core';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Member } from '../model/member'
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {Utils} from "../utils/utils";

@Injectable({
  providedIn: 'root'
})
export class MemberService {

    private readonly membersUrl: string

    constructor(private http: HttpClient) {
		this.membersUrl = environment.apiUrl + 'members'
    }

	public getMembers(ids?:number[], sortBy?:string[], pageSize?:number, pageNumber?:number) : Observable<Member[]> {
		let url = Utils.getURLWithQueryParametersOf(this.membersUrl,ids,sortBy,pageSize,pageNumber)
		return this.http.get<Member[]>(url.toString())
	}

	public getAllMembers() : Observable<Member[]> {
		return this.getMembers(undefined, ['id,asc'])
	}

	public getMember(id: number) : Observable<Member[]> {
		return this.getMembers([id])
	}

	public createMember(member: Member) : Observable<Member> {
		return this.http.post<Member>(this.membersUrl, {'name':member.name, 'email':member.email})
	}

	public deleteMember(id: number) : Observable<string> {
		let url = this.membersUrl
		return this.http.delete<string>(url.concat(`/${id}`))
	}

	public updateMember(member: Member, newName: string, newEmail: string) : Observable<Member> {
		let url = this.membersUrl
		let body: any = {}
		if(newName != "") body.name = newName
		if(newEmail != "") body.email = newEmail
		return this.http.put<Member>(url.concat(`/${member.id}`),body)
	}
}
