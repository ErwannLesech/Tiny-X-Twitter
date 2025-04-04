import { Component, Input, Output, EventEmitter } from '@angular/core';
import { UserService } from '../../../services/user.service';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { MatIconModule } from '@angular/material/icon';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { User, UserStateService } from '../../../services/user-state.service';
import { Router } from '@angular/router';

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

  constructor(
    private userService: UserService,
    private userStateService: UserStateService,
    private router: Router
  ) {}

  ngOnInit() {
    this.logedUser = this.userStateService.getLoggedUser();
  }

  closePopup() {
    this.close.emit();
  }

  onSubmit() {
    const userRequest = {
      tag: this.logedUser?.userTag,
      pseudo: this.userName.length > 0 ? this.userName : null,
      password: this.password.length > 0 ? this.password : null
    };
    if (!this.userName || !this.password) {
      this.closePopup();
    }
    this.userService.updateUser(userRequest).subscribe({
      next: () => {
        this.userStateService.resetLoggedUser(this.logedUser!);
        this.router.navigate(['/profile', this.logedUser?.userTag]).then(() => {
          window.location.reload();
        });
      },
      error: (err) => {
        console.error('Erreur lors de la mise à jour du profil:', err);
      }
    });
  }

  onDelete() {
    if (confirm('Êtes-vous sûr de vouloir supprimer votre compte ?')) {
      this.userService.deleteUser(this.userTag).subscribe({
        next: () => {
          this.userStateService.clearLoggedUser();
          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Erreur lors de la suppression du compte:', err);
        }
      });
    }
  }
}