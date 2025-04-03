import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export interface User {
  userId: number;
  userName: string;
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
    localStorage.setItem('loggedUser', JSON.stringify(user));
  }

  getLoggedUser(): User | null {
    if (this.loggedUserSubject.value !== null) {
      return this.loggedUserSubject.value;
    }
    return localStorage.getItem('loggedUser') ? JSON.parse(localStorage.getItem('loggedUser')!) : null;
  }
}