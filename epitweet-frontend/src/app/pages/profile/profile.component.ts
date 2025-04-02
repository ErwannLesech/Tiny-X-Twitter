import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { UserService } from '../../services/user.service';
import { User, UserStateService } from '../../services/user-state.service';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatFormFieldModule, 
    MatInputModule,
    RouterModule
  ],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  loggedUser: User | null = null;
  targetUser: any = {
    username: '',
    usertag: '',
    bio: '',
    followersCount: 0,
    followingCount: 0
  };

  posts: any[] = [];
  responses: any[] = [];
  isLoading = true;
  error: string | null = null;

  constructor(
    private route: ActivatedRoute,
    private userStateService: UserStateService
  ) {}

  ngOnInit() {
    this.route.params.subscribe(params => {
      const userTag = params['id'];
      this.loadUserData(userTag);
    });
    this.loggedUser = this.userStateService.getLoggedUser();
  }

  private loadUserData(userTag: string) {
    this.isLoading = true;
    this.targetUser = {
      username: userTag,
      usertag: userTag,
      bio: "Yo Coso",
      followersCount: 100,
      followingCount: 1
    };
    this.isLoading = false;
    // Fetch user profile
    // this.userService.getUserProfile(userTag).subscribe({
    //   next: (profileData) => {
    //     this.targetUser = {
    //       username: profileData.username,
    //       usertag: profileData.usertag,
    //       bio: profileData.bio,
    //       followersCount: profileData.followersCount,
    //       followingCount: profileData.followingCount
    //     };
        
    //     // Fetch user posts TODO
        
    //     // Fetch user responses TODO
    //   },
    //   error: (err) => {
    //     this.error = 'Failed to load user data';
    //     this.isLoading = false;
    //     console.error(err);
    //   }
    // });
  }
}