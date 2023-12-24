import {TestBed, waitForAsync} from '@angular/core/testing';

import { MemberService } from './member.service';
import {HttpClient} from "@angular/common/http";
import {Member} from "../model/member";
import {Observable, of} from "rxjs";

describe('MemberService', () => {
	let memberService: MemberService;
	let httpClientSpy: jasmine.SpyObj<HttpClient>

	beforeEach(() => {
		const spy = jasmine.createSpyObj('HttpClient', ['get','post','delete','put'])
		TestBed.configureTestingModule({
			providers: [
				MemberService,
				{ provide: HttpClient, useValue: spy }
			]
		});
		memberService = TestBed.inject(MemberService);
		httpClientSpy = TestBed.inject(HttpClient) as jasmine.SpyObj<HttpClient>
	});

	it('should be created', () => {
		expect(memberService).toBeTruthy();
	});


	it('should return expected member', function (done: DoneFn) {
		const expectedMember = new Member(1, 'name', 'some@mail.com')

		httpClientSpy.get.and.returnValue(of([expectedMember]))

		memberService.getMember(1)
			.subscribe({
				next: (member) => {
					expect(member.length)
						.withContext('expected 1 member')
						.toBe(1)
					expect(member[0])
						.withContext('expected member')
						.toEqual(expectedMember)
					done()
				},
				error: () => {
					done.fail('Failed to get member by id')
				}
			})
		expect(httpClientSpy.get.calls.count())
			.withContext('one call')
			.toBe(1)
	});

	it('should return expected created member', function (done: DoneFn) {
		const expectedName = 'MemberTest'
		const expectedEmail = 'member.test@mail.com'
		const member = new Member(1, expectedName, expectedEmail)

		httpClientSpy.post.and.returnValue(of(member))

		memberService.createMember(member)
			.subscribe({
				next: (m) => {
					expect(m)
						.withContext('expected member')
						.toEqual(member)
					done()
				},
				error: () => {
					done.fail('Failed to create expected member')
				}
			})
		expect(httpClientSpy.post.calls.count())
			.withContext('one call')
			.toBe(1)
	});

	xit('should delete member by id', function (done: DoneFn) {

	});

	it('should update member information in separated calls', function (done: DoneFn) {
		const expectedName = 'MemberTest'
		const expectedEmail = 'member.test@mail.com'
		const member = new Member(1, 'name', 'some@email.com')
		const expectedMember = new Member(1,expectedName,'some@email.com')
		const expectedMemberFinal = new Member(1,expectedName,expectedEmail)

		httpClientSpy.put.and.returnValue(of(expectedMember))

		memberService.updateMember(member,expectedName,"")
			.subscribe({
				next: (updatedMember) => {
					expect(updatedMember)
						.withContext('expected name update')
						.toEqual(expectedMember)

					httpClientSpy.put.and.returnValue(of(expectedMemberFinal))
					memberService.updateMember(updatedMember,"",expectedEmail)
						.subscribe({
							next: (finalMember) => {
								expect(finalMember)
									.withContext('expected email update')
									.toEqual(expectedMemberFinal)
								done()
							},
							error: () => {
								done.fail('Failed to update member email')
							}
						})
				},
				error: () => {
					done.fail('Failed to update member name')
				}
			})
		expect(httpClientSpy.put.calls.count())
			.withContext('two calls')
			.toBe(2)
	});

});
