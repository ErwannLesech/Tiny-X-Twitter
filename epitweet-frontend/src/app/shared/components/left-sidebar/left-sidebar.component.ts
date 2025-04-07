import { CommonModule } from '@angular/common';
import { Component, EventEmitter, Input, Output } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { Router, RouterModule } from '@angular/router';
import { UserStateService } from '../../../services/user-state.service';

@Component({
  selector: 'app-left-sidebar',
  templateUrl: './left-sidebar.component.html',
  styleUrls: ['./left-sidebar.component.scss'],
  imports: [
    CommonModule,
    MatIconModule,
    RouterModule
  ],
})
export class LeftSidebarComponent {
  @Input() avatarUrl: string = '';
  @Input() userName: string = '';
  @Input() userTag: string = '';
  currentTheme: string = 'light'; // Default theme

  
  // @Output() postClick = new EventEmitter<void>();
  // @Output() logout = new EventEmitter<void>();

  constructor(
    private router: Router,
    private userStateService: UserStateService,
  ) {}

  ngOnInit() {
    // Retrieve the selected theme from localStorage
    const savedTheme = localStorage.getItem('theme');
    if (savedTheme) {
      this.currentTheme = savedTheme;
      document.documentElement.setAttribute('data-theme', this.currentTheme);
    }
  }

  isActive(route: string): boolean {
    return this.router.url.includes(route);
  }

  postClick() {
    // TODO: Implement post click logic
  }

  logout() {
    this.userStateService.logout();
    this.router.navigate(['/login']);
  }

  toggleTheme() {
    // Toggle between 'light' and 'dark'
    this.currentTheme = this.currentTheme === 'light' ? 'dark' : 'light';

    // Apply the theme to the <html> element
    document.documentElement.setAttribute('data-theme', this.currentTheme);

    // Save the selected theme in localStorage
    localStorage.setItem('theme', this.currentTheme);
  }
}