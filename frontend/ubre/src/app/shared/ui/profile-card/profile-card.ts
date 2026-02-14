import { Component, Input, Output, EventEmitter, inject, input, effect, signal } from '@angular/core';
import { UserDto } from '../../../dtos/user-dto';
import { Role } from '../../../enums/role';
import { UserService } from '../../../services/user-service';
import { RideCardDto } from '../../../dtos/ride-card-dto';

@Component({
  selector: 'app-profile-card',
  imports: [],
  templateUrl: './profile-card.html',
  styleUrl: './profile-card.css',
})
export class ProfileCard {
  user = input.required<UserDto>();
  icon = input<string>();
  @Input() testIdPrefix : string | null = null;
  @Output() onAction = new EventEmitter<void>();

  userService : UserService = inject(UserService);
  avatarUrl = signal<string>("default-avatar.jpg");

  constructor() {
    effect(() => {
      this.userService.getUserAvatar(this.user().id).subscribe({
        next: blob => this.avatarUrl.set(URL.createObjectURL(blob))
      });
      console.log(this.testIdPrefix)
    });
  }

  onIconClick() {
    this.onAction.emit();
  }
}
