import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { RideList } from '../ride-list/ride-list';
import { RideService } from '../../../services/ride-service';
import { RideCardDto } from '../../../dtos/ride-card-dto';
import { UserDto } from '../../../dtos/user-dto';
import { UserService } from '../../../services/user-service';
import { Role } from '../../../enums/role';
import { RideQueryDto } from '../../../dtos/ride-query';
import { Observable, of } from 'rxjs';
import { AsyncPipe } from '@angular/common';


@Component({
  selector: 'app-ride-history',
  imports: [RideList, AsyncPipe],
  templateUrl: './ride-history.html',
  styleUrl: './ride-history.css',
})
export class RideHistory {
  @Input() open : boolean = false;
  @Output() onClose = new EventEmitter<void>();

  rideService : RideService = inject(RideService)
  userService : UserService = inject(UserService)

  rides$ : Observable<RideCardDto[]> = of([]);
  currentUser : UserDto = {
        email: '',
        name: '',
        surname: '',
        avatarUrl: '',
        role: Role.GUEST,
        id: 0,
        phone: "",
        address: ""
      };

  onQueryChange(qurey : RideQueryDto) {
    this.rideService.clearHistory();
    console.log(qurey)
    this.rideService.fetchHistory(qurey)
  }

  ngOnInit() {
    this.rides$ = this.rideService.history$;
    this.userService.getCurrentUser().subscribe((user : UserDto) => {
      this.currentUser = user;
    })

    this.onQueryChange(new RideQueryDto(null, "", false, null));
  }
}
