import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({ providedIn: 'root'})
export class UserService {
  private apiUrl = 'http://localhost:8081/api/users';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(
    private http: HttpClient
  ) {}

  createUser(userRequest: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/create`, userRequest, this.httpOptions)
      .pipe(
        catchError(this.handleCreationError)
      );
  }

  getUser(userTag: string): Observable<any> {
    this.httpOptions.headers = this.httpOptions.headers.set('userTag', userTag);
    return this.http.get(`${this.apiUrl}/getUser/`, this.httpOptions)
    .pipe(
      catchError(this.handleError)
    );
  }

  getUserById(userId: string): Observable<any> {
    this.httpOptions.headers = this.httpOptions.headers.set('userId', userId);
    return this.http.get(`${this.apiUrl}/getUserById/`, this.httpOptions)
    .pipe(
      catchError(this.handleError)
    );
  }

  authUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/auth`, userData)
    .pipe(
      catchError(this.handleAuthError)
    );
  }

  updateUser(userData: any): Observable<any> {
    return this.http.post(`${this.apiUrl}/uspdate`, userData)
    .pipe(
      catchError(this.handleError)
    );
  }

  private handleCreationError(error: HttpErrorResponse) {
    console.error('Error creating user:', error);
    return throwError(() => new Error('Error creating user, please try again later.'));
  }

  private handleAuthError(error: HttpErrorResponse) {
    console.error('Error auth user:', error);
    return throwError(() => new Error('Invalid credentials, please try again later.'));
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Error:', error);
    return throwError(() => new Error('Something went wrong; please try again later.'));
  }
}