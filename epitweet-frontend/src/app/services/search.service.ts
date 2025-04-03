import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';

@Injectable({ providedIn: 'root'})
export class SearchService {
  private apiUrl = 'http://localhost:8083/api/search';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(
    private http: HttpClient
  ) {}

  searchPostsByContent(request: string): Observable<any> {
    const encodedRequest = encodeURIComponent(request);
    return this.http.post(`${this.apiUrl}/searchPosts/${encodedRequest}`, this.httpOptions)
      .pipe(
        catchError(this.handleSearchError)
      );
  }

  private handleSearchError(error: HttpErrorResponse) {
    if (error.status == 404) {
      return throwError(() => error.error);
    }
    console.error('Error searching posts', error);
    return throwError(() => new Error('Error searching posts.'));
  }
}