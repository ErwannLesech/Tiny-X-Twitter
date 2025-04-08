import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { SearchService } from '../../services/search.service';
import { Post } from '../../services/post.service';
import { UserStateService } from '../../services/user-state.service';
import { User } from '../../services/user-state.service';
import { CommonModule, Location } from '@angular/common';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RouterModule } from '@angular/router';
import { LeftSidebarComponent } from "../../shared/components/left-sidebar/left-sidebar.component";
import { RightSidebarComponent } from "../../shared/components/right-sidebar/right-sidebar.component";
import { SocialService } from '../../services/social.service';

@Component({
  selector: 'app-search-results',
  standalone: true,
  imports: [
    CommonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
    RouterModule,
    LeftSidebarComponent,
    RightSidebarComponent
],
  templateUrl: './search-results.component.html',
  styleUrls: ['./search-results.component.scss']
})
export class SearchResultsComponent implements OnInit {
  searchQuery: string = '';
  searchResults: Post[] = [];
  isLoading: boolean = false;
  error: string | null = null;
  loggedUser: User | null = null;

  constructor(
    private route: ActivatedRoute,
    private searchService: SearchService,
    private userStateService: UserStateService,
    private socialService: SocialService,
    private router: Router,
    private location: Location
  ) {}

  ngOnInit() {
    this.loggedUser = this.userStateService.getLoggedUser();
    this.route.queryParams.subscribe(params => {
      this.searchQuery = params['q'] || '';
      if (this.searchQuery) {
        this.performSearch(this.searchQuery);
      }
    });
  }

  performSearch(query: string) {
    this.isLoading = true;
    this.error = null;
    this.searchService.searchPostsByContent(query).subscribe({
      next: (results) => {
        this.searchResults = results;
        this.isLoading = false;
      },
      error: (err) => {
        this.error = err;
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

  formatPostDate(dateString: string): string {
    const date = new Date(dateString);
    const now = new Date();
    
    const diffInSeconds = Math.floor((now.getTime() - date.getTime()) / 1000);
    
    if (diffInSeconds < 60) {
      return `${diffInSeconds}s`;
    }
    
    const diffInMinutes = Math.floor(diffInSeconds / 60);
    if (diffInMinutes < 60) {
      return `${diffInMinutes}min`;
    }
    
    const diffInHours = Math.floor(diffInMinutes / 60);
    if (diffInHours < 24) {
      return `${diffInHours}h`;
    }
    
    const diffInDays = Math.floor(diffInHours / 24);
    if (diffInDays < 7) {
      return `${diffInDays}j`;
    }
    
    return date.toLocaleDateString('fr-FR', { 
      day: 'numeric', 
      month: 'short',
      year: diffInDays > 365 ? 'numeric' : undefined
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
  
  onCommentClick(event: Event): void {
    // TODO: Add comment logic here
  }
}