import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { UserStateService } from '../../services/user-state.service';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss'],
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    FormsModule,
    MatCardModule,
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule
  ]
})
export class LoginComponent {
  username: string = '';
  password: string = '';

  constructor(
    private router: Router,
    private userStateService: UserStateService
  ) {}

  onSubmit(): void {
    this.userStateService.setLoggedUser({
      username: this.username,
      userTag: this.username.toLowerCase(),
      avatarUrl: `../avatars/${this.username}.svg`,
      bio: '',
      followersCount: 0,
      followingCount: 0
    });
    this.router.navigate(['/home']);
  }
}