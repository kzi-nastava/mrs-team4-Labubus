import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { RideList } from '../ride-list/ride-list';
import { RideService } from '../../../services/ride-service';
import { RideCardDto } from '../../../dtos/ride-card-dto';
import { UserDto } from '../../../dtos/user-dto';
import { UserService } from '../../../services/user-service';
import { Role } from '../../../enums/role';


@Component({
  selector: 'app-ride-history',
  imports: [RideList],
  templateUrl: './ride-history.html',
  styleUrl: './ride-history.css',
})
export class RideHistory {
  @Input() open : boolean = false;
  @Output() onClose = new EventEmitter<void>();

  rideService : RideService = inject(RideService)
  userService : UserService = inject(UserService)

  rides : RideCardDto[] = [];
  currentUser : UserDto = {
        email: '',
        name: '',
        surname: '',
        avatarUrl: '',
        role: Role.GUEST,
        id: 0,
        phone: "",
        address: ""
      };;

  ngOnInit() {
    this.rideService.getHistory().subscribe((rides : RideCardDto[]) => {
      this.rides = rides;
    })
    this.userService.getCurrentUser().subscribe((user : UserDto) => {
      this.currentUser = user;
    })
  }
}
