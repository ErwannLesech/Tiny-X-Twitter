import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, Observable, throwError } from 'rxjs';
import { User } from './user-state.service';

export interface Post {
  _id: number;
  userId: string;
  postType: string;
  content: string;
  mediaUrl: string;
  createdAt: string;
  updatedAt: string;
  user: User;
  likes: number;
  replies: number;
}

export interface PostRequest {
  postType: string;
  content: string;
  mediaUrl: string;
  parentId: string | null;
}

@Injectable({ providedIn: 'root'})
export class PostService {
  private apiUrl = 'http://localhost:8082/api/posts';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(
    private http: HttpClient
  ) {}

  getPosts(userId: any): Observable<any> {
    this.httpOptions.headers = this.httpOptions.headers.set('userId', userId);
    return this.http.get(`${this.apiUrl}/getPosts`, this.httpOptions)
      .pipe(
        catchError(this.handleGetPostsError)
      );
  }

  getPostById(postId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/getPost/${postId}`, this.httpOptions)
      .pipe(
        catchError(this.handleGetPostsError)
      );
  }
  

  createPost(userId: any, postRequest: any): Observable<any> {
    this.httpOptions.headers = this.httpOptions.headers.set('userId', userId);
    return this.http.post(`${this.apiUrl}/createPost`, postRequest, this.httpOptions)
      .pipe(
        catchError(this.handleCreatePostError)
      );
  }

  //TODO : add updatePost and deletePost methods

  private handleGetPostsError(error: HttpErrorResponse) {
    console.error('Error geting posts', error);
    return throwError(() => new Error('Error getting posts.'));
  }

  private handleCreatePostError(error: HttpErrorResponse) {
    console.error('Error creating post', error);
    return throwError(() => new Error('Error creating post.'));
  }
}