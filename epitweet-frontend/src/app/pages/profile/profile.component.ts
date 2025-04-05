import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Route, Router } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { UserService } from '../../services/user.service';
import { User, UserStateService } from '../../services/user-state.service';
import { Post, PostService } from '../../services/post.service';
import { catchError, map, Observable, throwError } from 'rxjs';
import { ProfileUpdateComponent } from "./profile-update/profile-update.component";
import { LeftSidebarComponent } from "../../shared/components/left-sidebar/left-sidebar.component";
import { RightSidebarComponent } from "../../shared/components/right-sidebar/right-sidebar.component";

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    RouterModule,
    ProfileUpdateComponent,
    LeftSidebarComponent,
    RightSidebarComponent
],
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.scss']
})
export class ProfileComponent implements OnInit {
  loggedUser: User | null = null;
  targetUser: User | null = null;

  posts: any[] = [];
  replies: any[] = [];
  activeTab: string = 'posts';
  isLoading = true;
  userError: string | null = null;
  postError: string | null = null;
  dropdownOpen: string | null = null; // Tracks which post's dropdown is open
  isPopupOpen: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private userStateService: UserStateService,
    private userService: UserService,
    private postService: PostService,
    private router: Router,
    private location: Location
  ) {}

  ngOnInit() {
    this.loggedUser = this.userStateService.getLoggedUser();

    this.route.params.subscribe(params => {
      const userTag = params['userTag'];
      this.handleRouteChange(userTag);
    });
  }


  private handleRouteChange(userTag: string) {
    // Reset state when route changes
    this.userError = null;
    this.postError = null;
    this.isLoading = true;
    
    // If no userTag specified or same as logged user, show logged user profile
    if (!userTag || userTag === this.loggedUser?.userTag) {
      this.targetUser = this.loggedUser;
      this.loadUserPosts(this.targetUser?.userId || '');
    } else {
      this.loadUserData(userTag).subscribe({
        next: (user) => {
          this.targetUser = user;
          this.loadUserPosts(user.userId);
        },
        error: (err) => {
          this.userError = 'User with tag '+userTag+' not found';
          this.isLoading = false;
        }
      });
    }
  }

  private loadUserData(userTag: string): Observable<User> {
    return this.userService.getUser(userTag).pipe(
      map(response => ({
        userId: response._id,
        userName: response.pseudo,
        userTag: response.tag,
        avatarUrl: response.profilePictureUrl,
        bannerUrl: response.profileBannerUrl,
        bio: response.profileDescription,
        followersCount: 0,
        followingCount: 0
      })),
      catchError(err => {
        console.error(err);
        return throwError(err);
      })
    );
  }

  private loadUserPosts(userId: any) {
    console.log('Loading user posts for user ID:', userId);
    this.isLoading = true;
    this.postService.getPosts(userId).subscribe({
      next: (allPosts: Post[]) => {
        this.posts = allPosts.filter(post => post.postType === 'post');
        this.replies = allPosts.filter(post => post.postType === 'reply');
        this.isLoading = false;
      },
      error: (err) => {
        this.postError = 'No posts found for this user';
        this.isLoading = false;
      }
    });
  }

  goBack() {
    if (window.history.length > 1) {
      this.location.back();
    } else {
      this.router.navigate(['/home']);
    }
  }

  setActiveTab(tab: string) {
    this.activeTab = tab;
  }

  toggleDropdown(postId: string, event: Event): void {
    event.stopPropagation(); // Prevent routing when clicking the dropdown
    this.dropdownOpen = this.dropdownOpen === postId ? null : postId;
  }

  onDeletePost(postId: string, event: Event): void {
    event.stopPropagation(); // Prevent routing when clicking the delete button
    this.postService.deletePost(postId).subscribe({
      next: () => {
        console.log(`Post ${postId} deleted successfully`);
        this.posts = this.posts.filter(post => post._id !== postId); // Remove the deleted post from the list
      },
      error: (err) => {
        console.error(`Error deleting post ${postId}:`, err);
      }
    });
  }

  openPopup() {
    this.isPopupOpen = true;
  }

  closePopup() {
    this.isPopupOpen = false;
  }

  public formatPostDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    
    // Calculate time difference in seconds
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    
    if (diffInSeconds < 60) {
      return `${diffInSeconds}s ago`;
    }
    
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) {
      return `${diffInMinutes}min ago`;
    }
    
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) {
      return `${diffInHours}h ago`;
    }
    
    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays < 7) {
      return `${diffInDays}j ago`;
    }
    
    // For older dates, show the actual date
    return date.toLocaleDateString('fr-FR', { 
      day: 'numeric', 
      month: 'short',
      year: diffInDays > 365 ? 'numeric' : undefined
    });
  }

  search(query: string) {
    if (query.trim()) {
      this.router.navigate(['/search'], { queryParams: { q: query } });
    }
  }

  onLikeClick(event: Event): void {
    event.stopPropagation();
    console.log('Like button clicked');
    // TODO : Implement like functionality
  }

  logout() {
    this.userStateService.logout();
    this.router.navigate(['/login']);
  }
}