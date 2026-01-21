import { Component, EventEmitter, Output } from '@angular/core';
import { Button } from '../button/button';

@Component({
  selector: 'app-schedule-timer',
  standalone: true,
  imports: [Button],
  templateUrl: './schedule-timer.html',
  styleUrl: './schedule-timer.css',
})
export class ScheduleTimer {
  hours: number = 9; // 12-hour format (1-12)
  minutes: number = 30;
  isAM: boolean = true;

  @Output() back = new EventEmitter<void>();
  @Output() checkout = new EventEmitter<{ hours: number; minutes: number; isAM: boolean }>();

  onBack() {
    this.back.emit();
  }

  onCheckout() {
    this.checkout.emit({
      hours: this.hours,
      minutes: this.minutes,
      isAM: this.isAM
    });
  }

  incrementHours() {
    this.hours = this.hours === 12 ? 1 : this.hours + 1;
  }

  decrementHours() {
    this.hours = this.hours === 1 ? 12 : this.hours - 1;
  }

  incrementMinutes() {
    this.minutes = (this.minutes + 1) % 60;
  }

  decrementMinutes() {
    this.minutes = this.minutes === 0 ? 59 : this.minutes - 1;
  }

  toggleAMPM() {
    this.isAM = !this.isAM;
  }

  formatTime(value: number): string {
    return value.toString().padStart(2, '0');
  }

  get displayHours(): string {
    return this.formatTime(this.hours);
  }
}
