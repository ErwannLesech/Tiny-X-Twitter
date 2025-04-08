import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { fadeAnimation, scaleAnimation } from './post-modal.animations';
import { User, UserStateService } from '../../../services/user-state.service';
import { PostService, PostRequest } from '../../../services/post.service';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-post-modal',
  templateUrl: './post-modal.component.html',
  styleUrls: ['./post-modal.component.scss'],
  animations: [fadeAnimation, scaleAnimation],
  imports: [CommonModule, MatIconModule, FormsModule]
})
export class PostModalComponent implements OnInit {
  isModalOpen = false;
  loggedUser: User | null = null;
  newPostContent = '';
  isPosting = false;
  postError: string | null = null;
  
  constructor(
    private userStateService: UserStateService,
    private postService: PostService,
    private notificationService: NotificationService
  ) {}
  
  ngOnInit() {
    this.loggedUser = this.userStateService.getLoggedUser();
  }
  
  openModal() {
    this.isModalOpen = true;
    document.body.style.overflow = 'hidden';
    // Reset form when opening
    this.resetForm();
  }
  
  public show() {
    this.openModal();
  }
  
  closeModal() {
    this.isModalOpen = false;
    document.body.style.overflow = 'auto';
    // Reset the form state
    this.resetForm();
  }
  
  // Reset the form to initial state
  resetForm() {
    this.newPostContent = '';
    this.isPosting = false;
    this.postError = null;
  }
  
  // Handle Enter key press (without shift key)
  handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.createPost();
    }
  }
  
  // Create a new post
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
        this.notificationService.showSuccess('Post created successfully');
        // Close the modal after successful post
        this.closeModal();
      },
      error: (err) => {
        this.isPosting = false;
        this.postError = 'Failed to create post. Please try again.';
        console.error('Error creating post', err);
        this.notificationService.showError(this.postError);
      }
    });
  }
  
  // Close modal when clicking outside content area
  onOverlayClick(event: MouseEvent) {
    // Only close if the overlay itself was clicked (not its children)
    if ((event.target as HTMLElement).classList.contains('modal-overlay')) {
      this.closeModal();
    }
  }
  
  // Prevent event propagation to parent (overlay)
  onModalContentClick(event: MouseEvent) {
    event.stopPropagation();
  }
}