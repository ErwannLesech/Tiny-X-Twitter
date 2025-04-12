import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, forkJoin, map, Observable, of, switchMap, throwError } from 'rxjs';
import { UserService } from './user.service';
import { Post } from './post.service';
import { UserStateService } from './user-state.service';
import { SocialService } from './social.service';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private apiUrl = '/api/search';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(
    private http: HttpClient,
    private userService: UserService,
    private userStateService: UserStateService,
    private socialService: SocialService
  ) {}

  searchPostsByContent(request: string): Observable<Post[]> {
    const encodedRequest = encodeURIComponent(request);
    return this.http.post<Post[]>(`${this.apiUrl}/searchPosts/${encodedRequest}`, this.httpOptions).pipe(
      switchMap((posts: Post[]) => {
        if (!posts || posts.length === 0) {
          return of([]);
        }
  
        const loggedUser = this.userStateService.getLoggedUser();
        
        // First get all post authors
        const userObservables = posts.map(post => 
          this.userService.getUserById(post.userId).pipe(
            catchError(() => of(null)) // Handle case where user might not be found
          )
        );
  
        return forkJoin(userObservables).pipe(
          switchMap((users) => {
            // Then get like status for all posts
            const postObservables = posts.map((post, index) => {
              const postUser = users[index] ? {
                userId: post.userId,
                userName: users[index].pseudo,
                userTag: users[index].tag,
                avatarUrl: users[index].profilePictureUrl || "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png",
                bannerUrl: users[index].profileBannerUrl || "https://t3.ftcdn.net/jpg/04/67/96/14/360_F_467961418_UnS1ZAwAqbvVVMKExxqUNi0MUFTEJI83.jpg",
                bio: users[index].profileDescription || "Ceci est la bio de @" + users[index].tag,
                followersCount: users[index].followersCount || 0,
                followingCount: users[index].followingCount || 0
              } : null;
  
              if (!loggedUser) {
                return of({
                  ...post,
                  user: postUser,
                  isLiked: false
                });
              }
  
              return this.socialService.getLikeUsers(post._id).pipe(
                map((likeUsersIds: string[]) => ({
                  ...post,
                  user: postUser,
                  likes: likeUsersIds.length,
                  likeUsersIds: likeUsersIds,
                  isLiked: likeUsersIds.includes(loggedUser.userId)
                })),
                catchError(() => of({
                  ...post,
                  user: postUser,
                  likes: 0,
                  likeUsersIds: [],
                  isLiked: false
                }))
              );
            });
  
            return forkJoin(postObservables);
          })
        );
      }),
      catchError(this.handleSearchError)
    );
  }

  private handleSearchError(error: HttpErrorResponse) {
    if (error.status === 404) {
      return throwError(() => error.error);
    }
    console.error('Error searching posts', error);
    return throwError(() => new Error('Error searching posts.'));
  }
}
