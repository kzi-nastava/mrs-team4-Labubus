import { Component } from '@angular/core';
import { Input, Output, EventEmitter } from '@angular/core';
import { ProfileChangeDto } from '../../../dtos/profile-change-dto';

@Component({
  selector: 'app-profile-change-card',
  imports: [],
  templateUrl: './profile-change-card.html',
  styleUrl: './profile-change-card.css',
  standalone : true,
})
export class ProfileChangeCard {
  @Input({ required: true }) item!: ProfileChangeDto;
  @Output() accept = new EventEmitter<number>();
  @Output() reject = new EventEmitter<number>();

  leaving = false;

  onAccept() { this.leaveThen(() => this.accept.emit(this.item.id)); }
  onReject() { this.leaveThen(() => this.reject.emit(this.item.id)); }

  private leaveThen(fn: () => void) {
    if (this.leaving) return;
    this.leaving = true;
    setTimeout(fn, 280);
  }
}
