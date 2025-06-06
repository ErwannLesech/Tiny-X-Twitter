import { Injectable } from '@angular/core';
import { BehaviorSubject, map, switchMap } from 'rxjs';
import { UserService } from './user.service';
import { SocialService } from './social.service';

export interface User {
  userId: string;
  userName: string;
  userTag: string;
  avatarUrl: string;
  bannerUrl: string;
  bio: string;
  followersCount: number;
  followingCount: number;
  likedPosts?: string[];
}

@Injectable({ providedIn: 'root' })
export class UserStateService {
  private loggedUserSubject = new BehaviorSubject<User | null>(null);
  loggedUser$ = this.loggedUserSubject.asObservable();

  constructor(
    private userService: UserService,
    private socialService: SocialService
  ) {}

  setLoggedUser(user: User) {
    this.loggedUserSubject.next(user);
    localStorage.setItem('loggedUser', JSON.stringify(user));
  }

  getLoggedUser(): User | null {
    if (this.loggedUserSubject.value !== null) {
      return this.loggedUserSubject.value;
    }
    return localStorage.getItem('loggedUser') ? JSON.parse(localStorage.getItem('loggedUser')!) : null;
  }

  resetLoggedUser(user: User) {
    this.userService.getUserById(user.userId).pipe(
      switchMap((response) => {
        const updatedUser: User = {
          userId: response._id,
          userName: response.pseudo,
          userTag: response.tag,
          avatarUrl: response.profilePictureUrl || "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png",
          bannerUrl: response.profileBannerUrl || "https://t3.ftcdn.net/jpg/04/67/96/14/360_F_467961418_UnS1ZAwAqbvVVMKExxqUNi0MUFTEJI83.jpg",
          bio: response.profileDescription || "Ceci est la bio de @" + response.tag,
          followersCount: response.followersCount || 0,
          followingCount: response.followingCount || 0
        };
        
        // Get liked posts for the user
        return this.socialService.getUserLikedPostsIds(user.userId).pipe(
          map((likedPostIds: string[]) => {
            updatedUser.likedPosts = likedPostIds;
            return updatedUser;
          })
        );
      })
    ).subscribe({
      next: (updatedUser) => {
        this.loggedUserSubject.next(updatedUser);
        localStorage.setItem('loggedUser', JSON.stringify(updatedUser));
      },
      error: (err) => {
        console.error('Error fetching user data:', err);
      }
    });
  }

  logout() {
    this.loggedUserSubject.next(null);
    localStorage.removeItem('loggedUser');
  }
}
