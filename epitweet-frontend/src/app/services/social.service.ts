import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, forkJoin, map, Observable, of, switchMap, throwError } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class SocialService {
  private apiUrl = '/api/social';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(
    private http: HttpClient
  ) {}

  // Follow/Unfollow a user
  followUser(FollowUnfollowRequest: { followUnfollow: boolean, userFollowedId: string, userFollowId: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/follow`, FollowUnfollowRequest, this.httpOptions)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get users that a user follows
  getFollowing(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/getFollows/${userId}`, this.httpOptions)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get followers of a user
  getFollowers(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/getFollowers/${userId}`, this.httpOptions)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Block/Unblock a user
  blockUser(BlockUnblockRequest: { blockUnblock: boolean, userBlockedId: string, userBlockId: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/block`, BlockUnblockRequest, this.httpOptions)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get blocked users of a user
  getBlockedUsers(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/getBlocked/${userId}`, this.httpOptions)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get users who blocked a user
  getUsersWhoBlocked(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/getBlock/${userId}`, this.httpOptions)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Like/Unlike a post
  likePost(AppreciationRequest: { likeUnlike: boolean, postId: string, userId: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/like`, AppreciationRequest, this.httpOptions)
      .pipe(
        catchError(this.handleError)
      );
  }

  // Get users who liked a post
  getLikeUsers(postId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/getLikeUsers/${postId}`, this.httpOptions)
      .pipe(
        catchError(this.handleError)
      );
  }

  getUserLikedPostsIds(userId: string): Observable<any> {
    return this.http.get(`${this.apiUrl}/getLikedPosts/${userId}`, this.httpOptions).pipe(
      catchError(this.handleError)
    );
  }

  private handleError(error: HttpErrorResponse) {
    console.error('Social Service Error:', error);
    return throwError(() => new Error('Something went wrong with the social service; please try again later.'));
  }
}
