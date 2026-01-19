import { Component, EventEmitter, Output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Button } from '../button/button';

@Component({
  selector: 'app-invite-passengers',
  standalone: true,
  imports: [FormsModule, Button],
  templateUrl: './invite-passengers.html',
  styleUrl: './invite-passengers.css',
})
export class InvitePassengers {
  emails: string[] = [];
  currentEmail: string = '';

  @Output() back = new EventEmitter<void>();
  @Output() proceed = new EventEmitter<string[]>();

  onBack() {
    this.back.emit();
  }

  onProceed() {
    this.proceed.emit([...this.emails]);
  }

  addEmail() {
    if (this.currentEmail.trim() && this.isValidEmail(this.currentEmail.trim())) {
      // Check if email already exists
      if (!this.emails.includes(this.currentEmail.trim())) {
        this.emails.push(this.currentEmail.trim());
        this.currentEmail = '';
      }
    }
  }

  removeEmail(index: number) {
    this.emails.splice(index, 1);
  }

  isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  onEmailKeyPress(event: KeyboardEvent) {
    if (event.key === 'Enter') {
      this.addEmail();
    }
  }
}
