import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { NotificationComponent } from '../shared/components/notification/notification.component';

@Injectable({
  providedIn: 'root'
})
export class NotificationService {
  constructor(private snackBar: MatSnackBar) {}

  showSuccess(message: string, duration: number = 3000) {
    this.snackBar.openFromComponent(NotificationComponent, {
      data: { message, type: 'success' },
      duration,
      panelClass: ['success-snackbar'],
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }

  showError(message: string, duration: number = 3000) {
    this.snackBar.openFromComponent(NotificationComponent, {
      data: { message, type: 'error' },
      duration,
      panelClass: ['error-snackbar'],
      horizontalPosition: 'right',
      verticalPosition: 'top'
    });
  }
}