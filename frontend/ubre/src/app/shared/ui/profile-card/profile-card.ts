import { Component, Input, Output, EventEmitter } from '@angular/core';
import { UserDto } from '../../../dtos/user-dto';
import { Role } from '../../../enums/role';

@Component({
  selector: 'app-profile-card',
  imports: [],
  templateUrl: './profile-card.html',
  styleUrl: './profile-card.css',
})
export class ProfileCard {
  @Input({ required: true }) user : UserDto = {
        email: '',
        name: '',
        surname: '',
        avatarUrl: '',
        role: Role.GUEST,
        id: 0,
        phone: "",
        address: ""
      };;
  @Input() icon : string = "";
  @Output() onAction = new EventEmitter<void>();

  onIconClick() {
    this.onAction.emit();
  }
}
