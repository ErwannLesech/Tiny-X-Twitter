import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { User, UserStateService } from '../../services/user-state.service';
import { UserService } from '../../services/user.service';

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
  userName: string = '';
  password: string = '';
  errorMessage: string | null = null;
  userRequest: any = {};
  isLoading: boolean = false;
  user: User | null = null;

  constructor(
    private router: Router,
    private userStateService: UserStateService,
    private userService: UserService
  ) {}

  onSubmit(): void {
    if (!this.userName || !this.password) {
      this.errorMessage = 'Veuillez remplir tous les champs';
      return;
    }
    this.errorMessage = null;
    
    this.userRequest = {
      tag: this.userName.toLowerCase(),
      pseudo: this.userName,
      password: this.password
    };


    this.userService.authUser(this.userRequest).subscribe({
        next: (responseAuth: any) => {
          console.log('User authentifiate successfully');
          this.userService.getUser(this.userRequest.tag).subscribe({
            next: (response: any) => {
              this.isLoading = false;
              this.user = {
                userId: response._id,
                userName: response.pseudo,
                userTag: response.tag,
                avatarUrl: "",
                bio: "",
                followersCount: 0,
                followingCount: 0
              }
              this.userStateService.setLoggedUser(this.user);
              console.log('User:', this.user);
              this.router.navigate(['/home']);
              this.errorMessage = null;
            },
            error: (error: any) => {
              this.isLoading = false;
              this.errorMessage = error;
            }
          });
        },
        error: (error: any) => {
          this.isLoading = false;
          this.errorMessage = error;
        }
    });
  }
}