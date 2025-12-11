// api.service.ts
import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  // Endpoint que devuelve { totalUsers: number, totalRecords: number, usersByRole: {role:string,count:number}[] }
  getDashboardStats(): Observable<any> {
    return this.http.get('/api/dashboard/stats');
  }
}
