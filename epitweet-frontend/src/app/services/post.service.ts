import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, map, Observable, of, switchMap, throwError } from 'rxjs';
import { User } from './user-state.service';
import { UserService } from './user.service';
export interface Post {
  _id: string | '';
  userId: string | '';
  postType: string | '';
  content: string | '';
  mediaUrl: string | '';
  createdAt: string | '';
  updatedAt: string | '';
  user: User | null;
  likes: number | 0;
  replies: number | 0;
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
    private http: HttpClient,
    private userService: UserService
  ) {}

  getPosts(userId: any): Observable<any> {
    this.httpOptions.headers = this.httpOptions.headers.set('userId', userId);
    return this.http.get(`${this.apiUrl}/getPosts`, this.httpOptions)
      .pipe(
        catchError(this.handleGetPostsError)
      );
  }

  getPostById(postId: string): Observable<Post | null> {
    return this.http.get<Post>(`${this.apiUrl}/getPost/${postId}`, this.httpOptions).pipe(
      catchError(this.handleGetPostsError),
      switchMap((post: Post) =>
        this.userService.getUserById(post.userId).pipe(
          map((userResponse) => {
            const postUser = {
              userId: post.userId,
              userName: userResponse.pseudo,
              userTag: userResponse.tag,
              avatarUrl: userResponse.profilePictureUrl || "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png",
              bannerUrl: userResponse.profileBannerUrl || "https://t3.ftcdn.net/jpg/04/67/96/14/360_F_467961418_UnS1ZAwAqbvVVMKExxqUNi0MUFTEJI83.jpg",
              bio: userResponse.profileDescription  || "Ceci est la bio de @" + userResponse.tag,
              followersCount: userResponse.followersCount || 0,
              followingCount: userResponse.followingCount || 0
            };
            post.user = postUser;
            return post;
          }),
          catchError((err) => {
            console.error('Error fetching user:', err);
            return of(null);
          })
        )
      )
    );
  }
  

  createPost(userId: any, postRequest: any): Observable<any> {
    this.httpOptions.headers = this.httpOptions.headers.set('userId', userId);
    return this.http.post(`${this.apiUrl}/createPost`, postRequest, this.httpOptions)
      .pipe(
        catchError(this.handleCreatePostError)
      );
  }

  deletePost(postId: any): Observable<any> {
    return this.http.delete(`${this.apiUrl}/deletePost/${postId}`, this.httpOptions)
      .pipe(
        catchError(this.handleDeletePostError)
      );
  }

  getReplies(postId: string) {
    return this.http.get<any[]>(`${this.apiUrl}/getPostReply/${postId}`);
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

  private handleDeletePostError(error: HttpErrorResponse) {
    console.error('Error deleting post', error);
    return throwError(() => new Error('Error deleting post.'));
  }
}