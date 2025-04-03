import { Component, OnInit, ViewChild, ElementRef } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { User, UserStateService } from '../../services/user-state.service';
import { PostService, PostRequest } from '../../services/post.service';
import { FormsModule } from '@angular/forms';

@Component({
  selector: 'app-home',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    RouterModule,
    FormsModule
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
    private router: Router,
    private userStateService: UserStateService,
    private postService: PostService
  ) {}

  ngOnInit() {
    this.loggedUser = this.userStateService.getLoggedUser();
  }

  search(query: string) {
    if (query.trim()) {
      this.router.navigate(['/search'], { queryParams: { q: query } });
    }
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
      },
      error: (err) => {
        this.isPosting = false;
        this.postError = 'Failed to create post. Please try again.';
        console.error('Error creating post', err);
      }
    });
  }

  handleKeyDown(event: KeyboardEvent) {
    if (event.key === 'Enter' && !event.shiftKey) {
      event.preventDefault();
      this.createPost();
    }
  }
}