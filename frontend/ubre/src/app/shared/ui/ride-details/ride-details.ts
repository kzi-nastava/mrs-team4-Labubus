import { Component, EventEmitter, inject, Input, Output, SimpleChanges } from '@angular/core';
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
import { RideHistoryReviews } from "../ride-history-reviews/ride-history-reviews";
import { RideHistoryComplaints } from "../ride-history-complaints/ride-history-complaints";

@Component({
  selector: 'app-ride-details',
  imports: [ProfileCard, VehicleCard, ModalContainer, RouteTable, DatePipe, StatCard, Button, RideHistoryReviews, RideHistoryComplaints],
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
  @Output() onError = new EventEmitter<Error>();
  @Output() onReorder = new EventEmitter<RideDto>();

  reviewService : ReviewService = inject(ReviewService);
  userService : UserService = inject(UserService);

  start : Date = new Date();
  end : Date = new Date();

  onOpenReview(rideId : number) {
    this.userService.getCurrentUser().subscribe((currentUser : UserDto) => {
      if (currentUser.id == this.ride.createdBy)
        this.reviewService.newReview(rideId)
      else
        this.onError.emit(new Error("You can only review your rides"))
    })
  }

  ngOnChanges(changes : SimpleChanges): void {
    if (changes['ride']) {
      this.start = new Date(this.ride.startTime)
      this.end = new Date(this.ride.endTime)
    }
  }

  onReorderClick() {
    this.onReorder.emit(this.ride);
  }
}
