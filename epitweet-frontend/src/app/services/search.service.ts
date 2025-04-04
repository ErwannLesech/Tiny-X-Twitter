import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse, HttpHeaders } from '@angular/common/http';
import { catchError, forkJoin, map, Observable, of, switchMap, throwError } from 'rxjs';
import { UserService } from './user.service';
import { Post } from './post.service';

@Injectable({ providedIn: 'root' })
export class SearchService {
  private apiUrl = 'http://localhost:8083/api/search';
  private httpOptions = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json'
    })
  };

  constructor(
    private http: HttpClient,
    private userService: UserService
  ) {}

  searchPostsByContent(request: string): Observable<Post[]> {
    const encodedRequest = encodeURIComponent(request);
    return this.http.post<Post[]>(`${this.apiUrl}/searchPosts/${encodedRequest}`, this.httpOptions).pipe(
      catchError(this.handleSearchError),
      switchMap((posts: Post[]) => {
        const postsWithUsers$ = posts.map(post =>
          this.userService.getUserById(post.userId).pipe(
            map(userResponse => {
              post.user = {
                userId: post.userId,
                userName: userResponse.pseudo,
                userTag: userResponse.tag,
                avatarUrl: userResponse.profilePictureUrl || "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png",
                bannerUrl: userResponse.profileBannerUrl || "https://t3.ftcdn.net/jpg/04/67/96/14/360_F_467961418_UnS1ZAwAqbvVVMKExxqUNi0MUFTEJI83.jpg",
                bio: userResponse.profileDescription  || "Ceci est la bio de @" + userResponse.tag,
                followersCount: userResponse.followersCount || 0,
                followingCount: userResponse.followingCount || 0
              };
              return post;
            }),
            catchError(err => {
              console.error('Error fetching user:', err);
              return of(post); // Return the post without user data if an error occurs
            })
          )
        );
        return forkJoin(postsWithUsers$); // Combine all post observables into one
      })
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