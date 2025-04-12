import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { FormsModule } from '@angular/forms';
import { fadeAnimation, scaleAnimation } from './post-modal.animations';
import { User, UserStateService } from '../../../services/user-state.service';
import { PostService, PostRequest } from '../../../services/post.service';
import { NotificationService } from '../../../services/notification.service';
import { GifSelectorComponent } from '../../../shared/components/gif-selector/gif-selector.component';
import { EmojiSelectorComponent } from '../../../shared/components/emoji-selector/emoji-selector.component';

@Component({
  selector: 'app-post-modal',
  templateUrl: './post-modal.component.html',
  styleUrls: ['./post-modal.component.scss'],
  animations: [fadeAnimation, scaleAnimation],
  imports: [CommonModule, MatIconModule, FormsModule, GifSelectorComponent, EmojiSelectorComponent],
  standalone: true
})
export class PostModalComponent implements OnInit {
  isModalOpen = false;
  loggedUser: User | null = null;
  newPostContent = '';
  isPosting = false;
  postError: string | null = null;
  selectedGifUrl: string | null = null;
  showGifSelector: boolean = false;
  showEmojiSelector: boolean = false;
  
  @ViewChild('postTextarea') postTextarea!: ElementRef;
  
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
    this.selectedGifUrl = null;
    this.showGifSelector = false;
    this.showEmojiSelector = false;
  }
  
  // Handle Enter key press (without shift key)
  handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.createPost();
    }
  }
  
  // Toggle GIF selector
  toggleGifSelector() {
    this.showGifSelector = !this.showGifSelector;
    if (this.showGifSelector) {
      this.showEmojiSelector = false;
    }
  }
  
  // Toggle Emoji selector
  toggleEmojiSelector() {
    this.showEmojiSelector = !this.showEmojiSelector;
    if (this.showEmojiSelector) {
      this.showGifSelector = false;
    }
  }
  
  // Handle GIF selection
  onGifSelected(gifUrl: string) {
    this.selectedGifUrl = gifUrl;
    this.showGifSelector = false;
  }
  
  // Handle Emoji selection
  onEmojiSelected(emoji: string) {
    // Insérer l'emoji à la position du curseur
    const textArea = this.postTextarea.nativeElement;
    const start = textArea.selectionStart;
    const end = textArea.selectionEnd;
    
    // Concaténer le texte avant l'emoji, l'emoji, puis le texte après
    this.newPostContent = this.newPostContent.substring(0, start) + emoji + this.newPostContent.substring(end);
    
    // Replacer le curseur juste après l'emoji inséré
    setTimeout(() => {
      textArea.selectionStart = textArea.selectionEnd = start + emoji.length;
      textArea.focus();
    }, 0);
    
    this.showEmojiSelector = false;
  }
  
  // Remove selected GIF
  removeSelectedGif() {
    this.selectedGifUrl = null;
  }
  
  // Create a new post
  createPost() {
    if ((!this.newPostContent.trim() && !this.selectedGifUrl) || !this.loggedUser?.userId) {
      return;
    }
    
    this.isPosting = true;
    this.postError = null;
    
    const postRequest: PostRequest = {
      postType: 'post',
      content: this.newPostContent,
      mediaUrl: this.selectedGifUrl || '',
      parentId: null // This would be used for replies
    };
    
    this.postService.createPost(this.loggedUser.userId, postRequest).subscribe({
      next: (response) => {
        this.newPostContent = '';
        this.selectedGifUrl = null;
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