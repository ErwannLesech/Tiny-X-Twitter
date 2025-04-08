import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import { Post, PostRequest, PostService } from '../../services/post.service';
import { UserStateService } from '../../services/user-state.service';
import { User } from '../../services/user-state.service';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { LeftSidebarComponent } from "../../shared/components/left-sidebar/left-sidebar.component";
import { RightSidebarComponent } from "../../shared/components/right-sidebar/right-sidebar.component";
import { SocialService } from '../../services/social.service';
import { NotificationService } from '../../services/notification.service';
import { forkJoin, of } from 'rxjs';
import { catchError, map, switchMap } from 'rxjs/operators';
import { UserService } from '../../services/user.service';
import { GifSelectorComponent } from '../../shared/components/gif-selector/gif-selector.component';
import { EmojiSelectorComponent } from '../../shared/components/emoji-selector/emoji-selector.component';

@Component({
  selector: 'app-post',
  templateUrl: './post.component.html',
  styleUrls: ['./post.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    LeftSidebarComponent,
    RightSidebarComponent,
    GifSelectorComponent,
    EmojiSelectorComponent,
    RouterModule
  ],
  standalone: true
})
export class PostComponent implements OnInit {
  post: Post | null = null;
  comments: Post[] = [];
  replyContent: string = '';
  loggedUser: User | null = null;
  isLoading = true;
  error: string | null = null;
  selectedGifUrl: string | null = null;
  showGifSelector: boolean = false;
  showEmojiSelector: boolean = false;
  @ViewChild('postTextarea') postTextarea!: ElementRef;

  constructor(
    private route: ActivatedRoute,
    private postService: PostService,
    private userStateService: UserStateService,
    private router: Router,
    private location: Location,
    private socialService: SocialService,
    private notificationService: NotificationService,
    private userService: UserService
  ) {}

  ngOnInit() {
    this.loggedUser = this.userStateService.getLoggedUser();
    const postId = this.route.snapshot.paramMap.get('postId');
    
    if (postId) {
      this.loadPost(postId);
    } else {
      this.error = 'Post not found';
      this.isLoading = false;
    }
  }

  loadPost(postId: string) {
    this.isLoading = true;
    this.error = null;
    this.post = null;
    this.comments = [];
  
    this.postService.getPostById(postId).pipe(
      switchMap(post => {
        if (!post) {
          this.error = 'Post not found';
          this.isLoading = false;
          return of(null);
        }
  
        // Check if current user liked this post
        if (this.loggedUser && post.likeUsersIds) {
          post.isLiked = post.likeUsersIds.includes(this.loggedUser.userId);
        } else {
          post.isLiked = false;
        }
        
        this.post = post;
        
        // Load replies if there are any
        if (post.repliesIds && post.repliesIds.length > 0) {
          return forkJoin(
            post.repliesIds.map(replyId => 
              this.postService.getPostById(replyId).pipe(
                catchError(() => of(null)) // Ignore errors for individual replies
              )
            )
          ).pipe(
            map(replies => {
              // Filter out null replies and sort by date (newest first)
              this.comments = replies.filter(reply => reply !== null) as Post[];
              this.comments.sort((a, b) => 
                new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime()
              );
              return post;
            })
          );
        }
        
        return of(post);
      }),
      catchError(err => {
        console.error('Error loading post:', err);
        this.error = 'Failed to load post';
        this.isLoading = false;
        return of(null);
      })
    ).subscribe(() => {
      this.isLoading = false;
    });
  }

  postReply() {
    if ((!this.replyContent.trim() && !this.selectedGifUrl) || !this.loggedUser || !this.post) return;
  
    const postRequest: PostRequest = {
      postType: 'reply',
      content: this.replyContent.trim(),
      mediaUrl: this.selectedGifUrl || '',
      parentId: this.post._id
    };
  
    this.postService.createPost(this.loggedUser.userId, postRequest).subscribe({
      next: (reply) => {
        if (!this.post) return;
        
        // Get the full reply post with user info
        this.postService.getPostById(reply._id).subscribe(fullReply => {
          if (fullReply) {
            // Update the replies count and add to comments array
            this.post!.replies = (this.post!.replies || 0) + 1;
            this.post!.repliesIds = [...(this.post!.repliesIds || []), reply._id];
            this.comments = [fullReply, ...this.comments]; // Add new reply at the beginning
          }
        });
  
        this.replyContent = '';
        this.selectedGifUrl = null;
        this.notificationService.showSuccess('Reply created successfully');
      },
      error: (err) => {
        console.error('Failed to post reply', err);
        this.notificationService.showError('Error creating reply, the user may have blocked you !');
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
    const textArea = this.postTextarea.nativeElement;
    const start = textArea.selectionStart;
    const end = textArea.selectionEnd;
    
    this.replyContent = this.replyContent.substring(0, start) + emoji + this.replyContent.substring(end);
    
    setTimeout(() => {
      textArea.selectionStart = textArea.selectionEnd = start + emoji.length;
      textArea.focus();
    }, 0);
    
    this.showEmojiSelector = false;
  }
  
  removeSelectedGif() {
    this.selectedGifUrl = null;
  }

  viewReplyAsPost(replyId: string) {
    this.router.navigate(['/post', replyId]).then(() => {
      this.loadPost(replyId);
    });
  }

  goBack() {
    if (this.post?.parentId) {
      // If this is a reply, navigate to its parent post
      this.router.navigate(['/post', this.post.parentId]).then(() => {
        window.location.reload(); // Force reload to ensure fresh data
      });
    } else {
      // If this is a top-level post, go back in history
      this.router.navigate(['/home']).then(() => {
        window.location.reload(); // Force reload to ensure fresh data
      });
    }
  }
  
  public formatPostDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    
    if (diffInSeconds < 60) return `${diffInSeconds}s ago`;
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) return `${diffInMinutes}min ago`;
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) return `${diffInHours}h ago`;
    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays < 7) return `${diffInDays}j ago`;
    
    return date.toLocaleDateString('fr-FR', { 
      day: 'numeric', 
      month: 'short',
      year: diffInDays > 365 ? 'numeric' : undefined
    });
  }

  onLikeClick(post: Post, event: Event): void {
    event.stopPropagation();
    if (!this.loggedUser) return;
  
    const newLikeState = !post.isLiked;
    const likeRequest = {
      likeUnlike: newLikeState,
      postId: post._id,
      userId: this.loggedUser.userId
    };
    
    // Optimistic update
    post.isLiked = newLikeState;
    post.likes = newLikeState ? (post.likes || 0) + 1 : Math.max(0, (post.likes || 1) - 1);
    
    if (newLikeState) {
      post.likeUsersIds = [...(post.likeUsersIds || []), this.loggedUser.userId];
    } else {
      post.likeUsersIds = (post.likeUsersIds || []).filter(id => id !== this.loggedUser?.userId);
    }
  
    this.socialService.likePost(likeRequest).subscribe({
      error: (err) => {
        console.error('Error toggling like:', err);
        // Revert on error
        post.isLiked = !newLikeState;
        post.likes = newLikeState ? 
          Math.max(0, (post.likes || 1) - 1): 
          (post.likes || 0) + 1;
          
        if (newLikeState) {
          post.likeUsersIds = (post.likeUsersIds || []).filter(id => id !== this.loggedUser?.userId);
        } else {
          post.likeUsersIds = [...(post.likeUsersIds || []), this.loggedUser?.userId || ''];
        }
      }
    });
  }
}