import { Injectable } from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {environment} from "../../environments/environment";
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
export class GraphSyncService {

	private readonly graphSyncUrl: string

	constructor(private http: HttpClient) {
		this.graphSyncUrl = environment.apiUrl + 'graph/sync'
	}

	public syncWithGraph() : Observable<any>{
		return this.http.post<any>(this.graphSyncUrl,'')
	}
}
