// notification.component.ts
import { CommonModule } from '@angular/common';
import { Component, Inject } from '@angular/core';
import { MatIconModule } from '@angular/material/icon';
import { MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';

@Component({
  selector: 'app-notification',
  templateUrl: './notification.component.html',
  styleUrls: ['./notification.component.scss'],
  imports: [
    CommonModule,
    MatIconModule
  ]
})
export class NotificationComponent {
  constructor(@Inject(MAT_SNACK_BAR_DATA) public data: any) {}
}