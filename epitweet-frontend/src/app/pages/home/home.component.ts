import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { User, UserStateService } from '../../services/user-state.service';
import { PostService, PostRequest, Post } from '../../services/post.service';
import { HomeTimelineService } from '../../services/home-timeline.service';
import { FormsModule } from '@angular/forms';
import { LeftSidebarComponent } from "../../shared/components/left-sidebar/left-sidebar.component";
import { RightSidebarComponent } from "../../shared/components/right-sidebar/right-sidebar.component";
import { SocialService } from '../../services/social.service';
import { NotificationService } from '../../services/notification.service';
import { GifSelectorComponent } from '../../shared/components/gif-selector/gif-selector.component';
import { EmojiSelectorComponent } from '../../shared/components/emoji-selector/emoji-selector.component';


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
    RightSidebarComponent,
    GifSelectorComponent,
    EmojiSelectorComponent
  ],
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit {
  loggedUser: User | null = null;
  newPostContent: string = '';
  isPosting: boolean = false;
  postError: string | null = null;
  selectedGifUrl: string | null = null;
  showGifSelector: boolean = false;
  showEmojiSelector: boolean = false;
  posts: Post[] = [];

  @ViewChild('postTextarea') postTextarea!: ElementRef;

  constructor(
    private userStateService: UserStateService,
    private postService: PostService,
    private socialService: SocialService,
    private notificationService: NotificationService,
    private homeTimelineService: HomeTimelineService,
  ) {}

  ngOnInit() {
    this.loggedUser = this.userStateService.getLoggedUser();
    if (this.loggedUser) {
      this.loadHomeTimeline();
    }
  }

  loadHomeTimeline() {
    if (!this.loggedUser?.userId) return;
    
    this.homeTimelineService.getHomeTimeline(this.loggedUser.userId).subscribe({
      next: (posts) => {
        this.posts = posts;
      },
      error: (err) => {
        console.error('Error loading home timeline', err);
        this.notificationService.showError('Failed to load timeline');
      }
    });
  }

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
        this.notificationService.showSuccess('Post created successfully')
        this.loadHomeTimeline(); // Reload timeline after posting
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
        next: (response) => {
          console.log('Like toggled successfully', response);
          this.loadHomeTimeline(); // Reload timeline after posting
        },
        error: (err) => {
          console.error('Error toggling like:', err);
          // Revert UI changes if the request fails
          post.isLiked = !newLikeState;
          post.likes = newLikeState ? 
            Math.max(0, (post.likes || 1) - 1) : 
            (post.likes || 0) + 1;
          this.notificationService.showError("you can't to like this post");
        }
      });
    }
  
  toggleGifSelector() {
    this.showGifSelector = !this.showGifSelector;
    if (this.showGifSelector) {
      this.showEmojiSelector = false;
    }
  }
  
  toggleEmojiSelector() {
    this.showEmojiSelector = !this.showEmojiSelector;
    if (this.showEmojiSelector) {
      this.showGifSelector = false;
    }
  }
  
  onGifSelected(gifUrl: string) {
    this.selectedGifUrl = gifUrl;
    this.showGifSelector = false;
  }
  
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
  
  removeSelectedGif() {
    this.selectedGifUrl = null;
  }
}