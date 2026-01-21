import { Component, inject, Input } from '@angular/core';
import { ModalContainer } from '../modal-container/modal-container';
import { ProfileCard } from '../profile-card/profile-card';
import { VehicleCard } from '../vehicle-card/vehicle-card';
import { RouteTable } from '../route-table/route-table';
import { StatCard } from '../stat-card/stat-card';
import { Button } from '../button/button';
import { RideDto } from '../../../dtos/ride-dto';
import { DatePipe } from '@angular/common';
import { UserDto } from '../../../dtos/user-dto';
import { Role } from '../../../enums/role';
import { ReviewService } from '../../../services/review-service';
import { UserService } from '../../../services/user-service';

@Component({
  selector: 'app-ride-details',
  imports: [ProfileCard, VehicleCard, ModalContainer, RouteTable, DatePipe, StatCard, Button],
  templateUrl: './ride-details.html',
  styleUrl: './ride-details.css',
})
export class RideDetails {
  @Input({required: true}) ride! : RideDto;
  @Input() user : UserDto = {
        email: '',
        name: '',
        surname: '',
        avatarUrl: '',
        role: Role.GUEST,
        id: 0,
        phone: "",
        address: ""
      };

  reviewService : ReviewService = inject(ReviewService);
  userService : UserService = inject(UserService);

  onOpenReview(rideId : number) {
    this.userService.getCurrentUser().subscribe((currentUser : UserDto) => {
      // if (currentUser.id == this.ride.createdBy)
        this.reviewService.newReview(rideId)
      console.log("Hmmm")
    })
  }
}
