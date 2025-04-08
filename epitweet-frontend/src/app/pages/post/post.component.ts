import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { CommonModule, Location } from '@angular/common';
import { PostRequest, PostService } from '../../services/post.service';
import { UserStateService } from '../../services/user-state.service';
import { User } from '../../services/user-state.service';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { FormsModule } from '@angular/forms';
import { UserService } from '../../services/user.service';
import { LeftSidebarComponent } from "../../shared/components/left-sidebar/left-sidebar.component";
import { RightSidebarComponent } from "../../shared/components/right-sidebar/right-sidebar.component";
import { NotificationService } from '../../services/notification.service';
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
    RouterModule,
    MatFormFieldModule,
    MatInputModule,
    LeftSidebarComponent,
    RightSidebarComponent,
    GifSelectorComponent,
    EmojiSelectorComponent
  ],
  standalone: true
})
export class PostComponent implements OnInit {
  post: any = null;
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
    private notificationService: NotificationService
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
  
    this.postService.getPostById(postId).subscribe({
      next: (post) => {
        if (post) {
          this.post = post;

          this.postService.getReplies(postId).subscribe({
            next: (replies) => {
              this.post.comments = replies;
              this.isLoading = false;
            },
            error: (err) => {
              console.error('Failed to load replies:', err);
              this.post.comments = [];
              this.isLoading = false;
            }
          });
  
        } else {
          this.error = 'Failed to load post';
          this.isLoading = false;
        }
      },
      error: (err) => {
        console.error('Error loading post:', err);
        this.error = 'Failed to load post';
        this.isLoading = false;
      }
    });
  }

  postReply() {
    if ((!this.replyContent.trim() && !this.selectedGifUrl) || !this.loggedUser) return;

    const postRequest: PostRequest = {
      postType: 'reply',
      content: this.replyContent.trim(),
      mediaUrl: this.selectedGifUrl || '',
      parentId: this.post._id
    };

    this.postService.createPost(this.loggedUser.userId, postRequest).subscribe({
      next: (reply) => {
        this.post.comments = this.post.comments || [];
        this.post.comments.unshift(reply);
        this.replyContent = '';
        this.selectedGifUrl = null;
        this.notificationService.showSuccess('Reply created successfully')
      },
      error: (err) => {
        console.error('Failed to post reply', err);
        this.notificationService.showError('Error creating reply')
      }
    });
  }
  
  toggleGifSelector() {
    this.showGifSelector = !this.showGifSelector;
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
    this.replyContent = this.replyContent.substring(0, start) + emoji + this.replyContent.substring(end);
    
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

  goBack() {
    this.location.back();
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
}