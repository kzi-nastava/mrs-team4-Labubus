import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Button } from '../button/button';

@Component({
  selector: 'app-driver-cancel-dialog',
  standalone: true,
  imports: [FormsModule, Button],
  templateUrl: './driver-cancel-dialog.html',
  styleUrl: './driver-cancel-dialog.css'
})
export class DriverCancelDialog {
  selectedReason: string = '';

  @Output() close = new EventEmitter<void>();
  @Output() confirmed = new EventEmitter<string>();

  onCancel(): void {
    this.close.emit();
  }

  onConfirm(): void {
    if (this.selectedReason) {
      this.confirmed.emit(this.selectedReason);
    }
  }
}