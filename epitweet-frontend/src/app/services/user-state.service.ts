import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface User {
  username: string;
  userTag: string;
  avatarUrl?: string;
  bio: string;
  followersCount: number;
  followingCount: number;
}

@Injectable({ providedIn: 'root' })
export class UserStateService {
  private loggedUserSubject = new BehaviorSubject<User | null>(null);
  loggedUser$ = this.loggedUserSubject.asObservable();

  setLoggedUser(user: User) {
    this.loggedUserSubject.next(user);
  }

  getLoggedUser(): User | null {
    return this.loggedUserSubject.value;
  }
}