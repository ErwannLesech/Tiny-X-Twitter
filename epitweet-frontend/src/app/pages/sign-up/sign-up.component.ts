import { Component } from '@angular/core';
import { Router, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatCardModule } from '@angular/material/card';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { UserService } from '../../services/user.service';
import { User, UserStateService } from '../../services/user-state.service';

@Component({
  selector: 'app-sign-up',
  templateUrl: './sign-up.component.html',
  styleUrls: ['./sign-up.component.scss'],
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
export class SignUpComponent {
  userRequest: any = {};
  userName: string = '';
  email: string = '';
  password: string = '';
  confirmPassword: string = '';
  errorMessage: string | null = null;
  isLoading: boolean = false;
  user: User | null = null;

  constructor(
    private router: Router,
    private userService: UserService,
    private userStateService: UserStateService
  ) {}

  onSubmit(): void {
    if (!this.userName || !this.password || !this.confirmPassword) {
      this.errorMessage = 'Veuillez remplir tous les champs';
      return;
    }
    else if (this.password !== this.confirmPassword) {
      this.errorMessage = 'Mot de passe et Confirmation de mot de passe non identiques';
      return;
    }
    this.isLoading = true;
    this.errorMessage = null;
    this.userRequest = {
      tag: this.userName.toLowerCase(),
      pseudo: this.userName,
      password: this.password,
      profilePictureUrl: "https://static-00.iconduck.com/assets.00/profile-default-icon-2048x2045-u3j7s5nj.png",
      profileBannerUrl: "https://t3.ftcdn.net/jpg/04/67/96/14/360_F_467961418_UnS1ZAwAqbvVVMKExxqUNi0MUFTEJI83.jpg",
      profileDescription: "Ceci est la bio de @" + this.userName.toLowerCase(),
    };


    this.userService.createUser(this.userRequest).subscribe({
        next: (response: any) => {
          this.isLoading = false;
          console.log('User created successfully:', response);
          this.user = {
            userId: response._id,
            userName: response.pseudo,
            userTag: response.tag,
            avatarUrl: response.profilePictureUrl,
            bannerUrl: response.profileBannerUrl,
            bio: response.profileDescription,
            followersCount: 0,
            followingCount: 0
          }
          this.userStateService.setLoggedUser(this.user);
          this.router.navigate(['/home']);
          this.errorMessage = null;
        },
        error: (error: any) => {
          this.isLoading = false;
          this.errorMessage = error;
        }
    });
  }
}