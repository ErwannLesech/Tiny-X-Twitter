import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, forkJoin, map, Observable, of, switchMap, throwError } from 'rxjs';
import { User, UserStateService } from './user-state.service';
import { UserService } from './user.service';
import { SocialService } from './social.service';
import { PostService, Post } from './post.service';

interface HomeTimelineResponse {
  userId: string;
  timeline: HomeTimelinePost[];
}

interface HomeTimelinePost {
  userId: string;
  postId: string;
  type: string; // Assuming EntryType is a string in TypeScript
  postOrLikeTime: string; // Instant is converted to string in JSON
}

@Injectable({ providedIn: 'root' })
export class HomeTimelineService {
  private apiUrl = '/api/timeline/home';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(
    private http: HttpClient,
    private userService: UserService,
    private socialService: SocialService,
    private userStateService: UserStateService,
    private postService: PostService
  ) {}

  getHomeTimeline(userId: string): Observable<Post[]> {
    this.httpOptions.headers = this.httpOptions.headers.set('userId', userId);
    return this.http.get<HomeTimelineResponse>(`${this.apiUrl}/${userId}`, this.httpOptions).pipe(
      switchMap((response: HomeTimelineResponse) => {
        console.log('Timeline response', response);

        if (!response.timeline || response.timeline.length === 0) {
          return of([]);
        }

        // Get unique post IDs from the timeline
        const postIds = response.timeline.map(item => item.postId);
        
        // Fetch full post details for each post ID
        const postObservables = postIds.map(postId => 
          this.postService.getPostById(postId).pipe(
            catchError(() => of(null)) // Skip posts that fail to load
          )
        );

        return forkJoin(postObservables).pipe(
          map(posts => posts.filter(post => post !== null) as Post[]),
          switchMap(posts => this.enrichPostsWithSocialData(posts))
        );
      }),
      catchError(this.handleGetTimelineError)
    );
  }

  private enrichPostsWithSocialData(posts: Post[]): Observable<Post[]> {
    if (!posts || posts.length === 0) {
      return of([]);
    }

    const loggedUser = this.userStateService.getLoggedUser();
    
    const enrichedPosts$ = posts.map(post => 
      this.socialService.getLikeUsers(post._id).pipe(
        catchError(() => of([])),
        switchMap((likeUsersIds: string[]) => {
          const isLiked = loggedUser ? likeUsersIds.includes(loggedUser.userId) : false;
          const updatedPost = {
            ...post,
            likes: likeUsersIds.length,
            likeUsersIds: likeUsersIds,
            isLiked: isLiked
          };
          return this.getReplies(post._id).pipe(
            catchError(() => of([])),
            map((replies: Post[]) => ({
              ...updatedPost,
              replies: replies.length,
              repliesIds: replies.map(reply => reply._id)
            }))
          );
        })
      )
    );

    return forkJoin(enrichedPosts$).pipe(
      switchMap(posts => this.enrichPostsWithUserData(posts))
    );
  }

  private enrichPostsWithUserData(posts: Post[]): Observable<Post[]> {
    const enrichedPosts$ = posts.map(post => 
      this.userService.getUserById(post.userId).pipe(
        map(userResponse => ({
          ...post,
          user: {
            userId: post.userId,
            userName: userResponse.pseudo,
            userTag: userResponse.tag,
            avatarUrl: userResponse.profilePictureUrl || "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png",
            bannerUrl: userResponse.profileBannerUrl || "https://t3.ftcdn.net/jpg/04/67/96/14/360_F_467961418_UnS1ZAwAqbvVVMKExxqUNi0MUFTEJI83.jpg",
            bio: userResponse.profileDescription || "Ceci est la bio de @" + userResponse.tag,
            followersCount: userResponse.followersCount || 0,
            followingCount: userResponse.followingCount || 0
          }
        })),
        catchError(() => of(post)) // Continue with post even if user fetch fails
      )
    );
    
    return forkJoin(enrichedPosts$);
  }

  private getReplies(postId: string): Observable<Post[]> {
    return this.http.get<Post[]>(`/api/posts/getPostReply/${postId}`).pipe(
      catchError(() => of([]))
    );
  }

  private handleGetTimelineError(error: HttpErrorResponse) {
    console.error('Error getting home timeline', error);
    return throwError(() => new Error('Error getting home timeline.'));
  }
}