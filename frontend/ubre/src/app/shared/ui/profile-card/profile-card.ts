import { Component, Input, Output, EventEmitter } from '@angular/core';
import { User } from '../../../dtos/user';

@Component({
  selector: 'app-profile-card',
  imports: [],
  templateUrl: './profile-card.html',
  styleUrl: './profile-card.css',
})
export class ProfileCard {
  @Input({ required: true }) user : User = {email: "", firstName: "", lastName: "", profilePicture: "", role: "guest"};
  @Input() icon : string = "";
  @Output() onAction = new EventEmitter<void>();

  onIconClick() {
    this.onAction.emit();
  }
}
