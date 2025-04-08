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
import { SocialService } from '../../services/social.service';
import { HttpParams } from '@angular/common/http';

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
  likedPosts: any[] = [];
  activeTab: string = 'posts';
  isLoading = true;
  userError: string | null = null;
  postError: string | null = null;
  dropdownOpen: string | null = null;
  isPopupOpen: boolean = false;
  isFollowing: boolean = false;
  isBlocked: boolean = false;

  constructor(
    private route: ActivatedRoute,
    private userStateService: UserStateService,
    private userService: UserService,
    private postService: PostService,
    private router: Router,
    private location: Location,
    private socialService: SocialService
  ) {}

  ngOnInit() {
    this.loggedUser = this.userStateService.getLoggedUser();

    this.route.params.subscribe(params => {
      const userTag = params['userTag'];
      this.handleRouteChange(userTag);
    });
  }


  private handleRouteChange(userTag: string) {
    this.userError = null;
    this.postError = null;
    this.isLoading = true;
    
    if (!userTag || userTag === this.loggedUser?.userTag) {
      this.targetUser = this.loggedUser;
      this.loadUserPosts(this.targetUser?.userId || '');
      this.loadSocialData(this.targetUser?.userId || '');
    } else {
      this.loadUserData(userTag).subscribe({
        next: (user) => {
          this.targetUser = user;
          this.loadUserPosts(user.userId);
          this.loadSocialData(user.userId);
          this.checkFollowStatus();
          this.checkBlockStatus();
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
        // Trier les posts du plus récent au plus ancien
        this.posts = allPosts
          .filter(post => post.postType === 'post')
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        
        // Trier les réponses du plus récent au plus ancien
        this.replies = allPosts
          .filter(post => post.postType === 'reply')
          .sort((a, b) => new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime());
        
        console.log('Loaded posts:', this.posts);
        console.log('Loaded replies:', this.replies);
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
    event.stopPropagation();
    this.postService.deletePost(postId).subscribe({
      next: () => {
        console.log(`Post ${postId} deleted successfully`);
        // Remove from posts array if it exists there
        this.posts = this.posts.filter(post => post._id !== postId);
        // Remove from replies array if it exists there
        this.replies = this.replies.filter(reply => reply._id !== postId);
        // Remove from liked posts array if it exists there
        this.likedPosts = this.likedPosts.filter(post => post._id !== postId);
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

  // Social Linked functions

  private loadSocialData(userId: string) {
    this.socialService.getFollowers(userId).subscribe({
      next: (followers) => {
        if (this.targetUser) {
          this.targetUser.followersCount = followers.length;
          
        }
      }
    });

    this.socialService.getFollowing(userId).subscribe({
      next: (following) => {
        if (this.targetUser) {
          this.targetUser.followingCount = following.length;
        }
      }
    });
  }

  private checkFollowStatus() {
    if (!this.loggedUser || !this.targetUser) return;
    
    this.socialService.getFollowing(this.loggedUser.userId).subscribe({
      next: (following) => {
        this.isFollowing = following.some((id: string) => id === this.targetUser?.userId);
      }
    });
  }

  private checkBlockStatus() {
    if (!this.loggedUser || !this.targetUser) return;
    
    this.socialService.getBlockedUsers(this.loggedUser.userId).subscribe({
      next: (blockedUsers) => {
        this.isBlocked = blockedUsers.some((id: string) => id === this.targetUser?.userId);
      }
    });
  }

  toggleFollow() {
    if (!this.loggedUser || !this.targetUser) return;
    
    const followRequest = {
      followUnfollow: !this.isFollowing,
      userFollowedId: this.targetUser.userId,
      userFollowId: this.loggedUser.userId
    };
    
    this.socialService.followUser(followRequest).subscribe({
      next: () => {
        this.isFollowing = !this.isFollowing;
        if (this.isFollowing) {
          this.targetUser!.followersCount++;
        } else {
          this.targetUser!.followersCount--;
        }
      },
      error: (err) => console.error('Error toggling follow:', err)
    });
  }

  toggleBlock() {
    if (!this.loggedUser || !this.targetUser) return;
    
    const blockRequest = {
      blockUnblock: !this.isBlocked,
      userBlockedId: this.targetUser.userId,
      userBlockId: this.loggedUser.userId
    };
    
    this.socialService.blockUser(blockRequest).subscribe({
      next: () => {
        this.isBlocked = !this.isBlocked;
      },
      error: (err) => console.error('Error toggling block:', err)
    });
  }

  loadLikedPosts() {
    if (!this.targetUser) return;
    
    this.isLoading = true;
    this.postService.getUserLikedPosts(this.targetUser.userId).subscribe({
      next: (posts) => {
        this.likedPosts = posts || [];
        this.isLoading = false;
      },
      error: (err) => {
        console.error('Error loading liked posts:', err);
        this.isLoading = false;
      }
    });
  }

  onLikeClick(post: Post, event: Event): void {
    event.stopPropagation();
    if (!this.loggedUser) return;
  
    // Determine the new like state
    const newLikeState = !post.isLiked;
    
    // Prepare the request
    const likeRequest = {
      likeUnlike: newLikeState,
      postId: post._id,
      userId: this.loggedUser.userId
    };
    
    // Update UI optimistically
    post.isLiked = newLikeState;
    post.likes = newLikeState ? (post.likes || 0) + 1 : Math.max(0, (post.likes || 1) - 1);

    // Update the like status in posts and replies lists if the post exists there
    this.posts = this.posts.map(p => p._id === post._id ? { ...p, isLiked: newLikeState, likes: post.likes } : p);
    this.replies = this.replies.map(r => r._id === post._id ? { ...r, isLiked: newLikeState, likes: post.likes } : r);
  
    // Send the request
    this.socialService.likePost(likeRequest).subscribe({
      next: () => {
        // If we're unliking and we're on the likes tab, reload the likes list
        if (!newLikeState && this.activeTab === 'likes') {
          this.loadLikedPosts();
        }
      },
      error: (err) => {
        console.error('Error toggling like:', err);
        // Revert UI changes if the request fails
        post.isLiked = !newLikeState;
        post.likes = newLikeState ? 
          Math.max(0, (post.likes || 1) - 1) : 
          (post.likes || 0) + 1;

        // Revert the like status in posts and replies lists
        this.posts = this.posts.map(p => p._id === post._id ? { ...p, isLiked: post.isLiked, likes: post.likes } : p);
        this.replies = this.replies.map(r => r._id === post._id ? { ...r, isLiked: post.isLiked, likes: post.likes } : r);
      }
    });
  }
}