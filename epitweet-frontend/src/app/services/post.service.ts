import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, forkJoin, map, Observable, of, switchMap, throwError } from 'rxjs';
import { User, UserStateService } from './user-state.service';
import { UserService } from './user.service';
import { SocialService } from './social.service';
export interface Post {
  _id: string | '';
  userId: string | '';
  postType: string | '';
  content: string | '';
  mediaUrl: string | '';
  parentId: string | '';
  createdAt: string | '';
  updatedAt: string | '';
  user: User | null;
  likes: number | 0;
  likeUsersIds: string[] | [];
  replies: number | 0;
  repliesIds: string[] | [];
  reposts: number | 0;
  isLiked: boolean | false;
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
    private userService: UserService,
    private socialService: SocialService,
    private userStateService: UserStateService
  ) {}

  getPosts(userId: any): Observable<Post[]> {
    this.httpOptions.headers = this.httpOptions.headers.set('userId', userId);
    return this.http.get<Post[]>(`${this.apiUrl}/getPosts`, this.httpOptions).pipe(
      switchMap((posts: Post[]) => {
        if (!posts || posts.length === 0) {
          return of([]);
        }

        const loggedUser = this.userStateService.getLoggedUser();
        if (!loggedUser) {
          return of([]);
        }
        // Then get like status for all posts
        const postObservables = posts.map((post) => 
          this.socialService.getLikeUsers(post._id).pipe(
            switchMap((likeUsersIds: string[]) => {
              const updatedPost = {
                ...post,
                likes: likeUsersIds.length,
                likeUsersIds: likeUsersIds,
                isLiked: likeUsersIds.includes(loggedUser.userId)
              };
              return this.getReplies(post._id).pipe(
                map((replies: Post[]) => ({
                  ...updatedPost,
                  replies: replies.length,
                  repliesIds: replies.map((reply) => reply._id)
                })),
                catchError(() => of({
                  ...updatedPost,
                  replies: 0,
                  repliesIds: []
                }))
              );
            }),
            catchError(() => of({
              ...post,
              likes: 0,
              likeUsersIds: [],
              isLiked: false,
              replies: 0,
              repliesIds: []
            }))
          )
        );

        return forkJoin(postObservables).pipe(
          map((flattenedPosts) => flattenedPosts.flat())
        );
      }),
      catchError(this.handleGetPostsError)
    );
  }

  getPostById(postId: string): Observable<Post | null> {
    return this.http.get<Post>(`${this.apiUrl}/getPost/${postId}`, this.httpOptions).pipe(
      switchMap((post: Post) => {
        // If we have a logged in user, check if they liked this post
        const loggedUser = this.userStateService.getLoggedUser();
        if (!loggedUser) {
          return of({...post, isLiked: false});
        }
        
        return this.socialService.getLikeUsers(post._id).pipe(
          map((likeUsersIds: string[]) => {
            return {
              ...post,
              likes: likeUsersIds.length,
              likeUsersIds: likeUsersIds,
              isLiked: likeUsersIds.includes(loggedUser.userId)
            };
          })
        );
      }),
      switchMap((post: Post) =>
        this.userService.getUserById(post.userId).pipe(
          map((userResponse) => {
            const postUser = {
              userId: post.userId,
              userName: userResponse.pseudo,
              userTag: userResponse.tag,
              avatarUrl: userResponse.profilePictureUrl || "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png",
              bannerUrl: userResponse.profileBannerUrl || "https://t3.ftcdn.net/jpg/04/67/96/14/360_F_467961418_UnS1ZAwAqbvVVMKExxqUNi0MUFTEJI83.jpg",
              bio: userResponse.profileDescription || "Ceci est la bio de @" + userResponse.tag,
              followersCount: userResponse.followersCount || 0,
              followingCount: userResponse.followingCount || 0
            };
            return {
              ...post,
              user: postUser
            };
          }),
          catchError((err) => {
            console.error('Error fetching user:', err);
            return of(null);
          })
        )
      ),
      switchMap((post: Post | null) => {
        if (!post) {
          return of(null);
        }
        return this.getReplies(post._id).pipe(
          map((replies: Post[]) => ({
            ...post,
            replies: replies.length,
            repliesIds: replies.map((reply) => reply._id)
          })),
          catchError(() => of({
            ...post,
            replies: 0,
            repliesIds: []
          }))
        );
      }),
      catchError(this.handleGetPostsError)
    );
  }

  // Get a user's liked posts
  getUserLikedPosts(userId: string): Observable<Post[] | null> {
    return this.socialService.getUserLikedPostsIds(userId).pipe(
      switchMap((postIds: string[]) => {
        if (!postIds || postIds.length === 0) {
          return of([]);
        }
        
        // Fetch details for each post
        const postObservables = postIds.map((postId: string) => 
          this.getPostById(postId).pipe(
            catchError(() => of(null)) // Handle case where post might not be found
          )
        );
        
        return forkJoin(postObservables).pipe(
          map((posts: (Post | null)[]) => posts.filter((post: Post | null) => post !== null) as Post[])
        );
      }),
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