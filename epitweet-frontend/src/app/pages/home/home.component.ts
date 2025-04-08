import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { User, UserStateService } from '../../services/user-state.service';
import { PostService, PostRequest, Post } from '../../services/post.service';
import { FormsModule } from '@angular/forms';
import { LeftSidebarComponent } from "../../shared/components/left-sidebar/left-sidebar.component";
import { RightSidebarComponent } from "../../shared/components/right-sidebar/right-sidebar.component";
import { SocialService } from '../../services/social.service';
import { NotificationService } from '../../services/notification.service';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    RouterModule,
    FormsModule,
    LeftSidebarComponent,
    RightSidebarComponent
],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  loggedUser: User | null = null;
  newPostContent: string = '';
  isPosting: boolean = false;
  postError: string | null = null;

  @ViewChild('postTextarea') postTextarea!: ElementRef;

  constructor(
    private userStateService: UserStateService,
    private postService: PostService,
    private socialService: SocialService,
    private notificationService: NotificationService
  ) {}

  ngOnInit() {
    this.loggedUser = this.userStateService.getLoggedUser();
  }

  createPost() {
    if (!this.newPostContent.trim() || !this.loggedUser?.userId) {
      return;
    }

    this.isPosting = true;
    this.postError = null;

    const postRequest: PostRequest = {
      postType: 'post',
      content: this.newPostContent,
      mediaUrl: '',
      parentId: null // This would be used for replies
    };

    this.postService.createPost(this.loggedUser.userId, postRequest).subscribe({
      next: (response) => {
        this.newPostContent = '';
        this.isPosting = false;
        console.log('Post created successfully', response);
        this.notificationService.showSuccess('Post created successfully')
      },
      error: (err) => {
        this.isPosting = false;
        this.postError = 'Failed to create post. Please try again.';
        console.error('Error creating post', err);
        this.notificationService.showError(this.postError)
      }
    });
  }

  handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.createPost();
    }
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
    
      // Send the request
      this.socialService.likePost(likeRequest).subscribe({
        error: (err) => {
          console.error('Error toggling like:', err);
          // Revert UI changes if the request fails
          post.isLiked = !newLikeState;
          post.likes = newLikeState ? 
            Math.max(0, (post.likes || 1) - 1) : 
            (post.likes || 0) + 1;
        }
      });
    }
}