import { Component, Input, Output, EventEmitter } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { User, UserStateService } from '../../../services/user-state.service';
import { Router } from '@angular/router';
import { NotificationService } from '../../../services/notification.service';

@Component({
  selector: 'app-profile-update',
  templateUrl: './profile-update.component.html',
  styleUrls: ['./profile-update.component.scss'],
  imports: [
    CommonModule,
    FormsModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule
  ],
})
export class ProfileUpdateComponent {
  @Input() userTag: string = '';
  @Output() close = new EventEmitter<void>();

  logedUser: User | null = null;
  userName: string = '';
  password: string = '';
  avatarUrl: string = '';
  bannerUrl: string = '';
  bio: string = '';

  constructor(
    private userService: UserService,
    private userStateService: UserStateService,
    private router: Router,
    private notification: NotificationService
  ) {}

  ngOnInit() {
    this.logedUser = this.userStateService.getLoggedUser();
    if (!this.logedUser) {
      this.router.navigate(['/login']);
      return;
    }
    this.userName = this.logedUser.userName || '';
    this.avatarUrl = this.logedUser.avatarUrl || '';
    this.bannerUrl = this.logedUser.bannerUrl || '';
    this.bio = this.logedUser.bio || '';
  }

  closePopup() {
    this.close.emit();
  }

  onSubmit() {
    if (!this.logedUser) return;

    const userRequest = {
      tag: this.logedUser.userTag,
      pseudo: this.userName,
      password: this.password || null,
      profilePictureUrl: this.avatarUrl,
      profileBannerUrl: this.bannerUrl,
      profileDescription: this.bio
    };

    this.userService.updateUser(userRequest).subscribe({
      next: () => {
          // First reset the logged user with the updated data
          const resetUser: User = {
            userId: this.logedUser!.userId,
            userName: this.userName,
            userTag: this.logedUser!.userTag,
            avatarUrl: this.avatarUrl,
            bannerUrl: this.bannerUrl,
            bio: this.bio,
            followersCount: this.logedUser!.followersCount,
            followingCount: this.logedUser!.followingCount
          };
      
          this.userStateService.setLoggedUser(resetUser);
          this.notification.showSuccess('Profile updated successfully!');
          this.router.navigate(['/profile', this.logedUser?.userTag]).then(() => {
            window.location.reload()
          });
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour du profil:', err);
        this.notification.showError('Failed to update profile');
      }
    });
  }

  onDelete(event: MouseEvent) {
    event.stopPropagation();
    event.preventDefault();

    if (confirm('Êtes-vous sûr de vouloir supprimer votre compte ?')) {
      this.userService.deleteUser(this.userTag).subscribe({
        next: () => {
          this.notification.showSuccess('Profile deleted successfully!');
          this.userStateService.logout();
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Erreur lors de la suppression du compte:', err);
          this.notification.showError('Failed to delete profile');
        }
      });
    }
  }
}