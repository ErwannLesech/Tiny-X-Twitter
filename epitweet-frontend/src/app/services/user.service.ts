import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root'})
export class UserService {
  private apiUrl = 'https://localhost:8081/api/users';

  constructor(
    private http: HttpClient
  ) {}

  createUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, userData);
  }

  getUserProfile(userTag: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/getUser/${userTag}`);
  }

  authUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth`, userData);
  }

  updateUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/uspdate`, userData);
  }
}