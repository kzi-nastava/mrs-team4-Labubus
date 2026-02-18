import { Component, EventEmitter, inject, Input, Output, SimpleChanges } from '@angular/core';
import { ModalContainer } from '../modal-container/modal-container';
import { ProfileCard } from '../profile-card/profile-card';
import { VehicleCard } from '../vehicle-card/vehicle-card';
import { RouteTable } from '../route-table/route-table';
import { StatCard } from '../stat-card/stat-card';
import { Button } from '../button/button';
import { RideDto } from '../../../dtos/ride-dto';
import { AsyncPipe, DatePipe } from '@angular/common';
import { UserDto } from '../../../dtos/user-dto';
import { Role } from '../../../enums/role';
import { ReviewService } from '../../../services/review-service';
import { UserService } from '../../../services/user-service';
import { RideHistoryReviews } from "../ride-history-reviews/ride-history-reviews";
import { RideHistoryComplaints } from "../ride-history-complaints/ride-history-complaints";
import { RidePlanningStore } from '../../../services/ride-planning/ride-planning-store';
import { RideService } from '../../../services/ride-service';
import { Toast } from '../toast/toast';

@Component({
  selector: 'app-ride-details',
  imports: [ProfileCard, VehicleCard, ModalContainer, RouteTable, DatePipe, StatCard, Button, RideHistoryReviews, RideHistoryComplaints, AsyncPipe],
  templateUrl: './ride-details.html',
  styleUrl: './ride-details.css',
})
export class RideDetails {
  
  public rideService = inject(RideService);

  @Input({required: true}) ride! : RideDto;
  @Input() user : UserDto = {
        email: '',
        name: '',
        surname: '',
        avatarUrl: '',
        role: Role.GUEST,
        id: 0,
        phone: "",
        address: "",
        isBlocked: false
      };
  @Input() testIdPrefix: string | null = null;
  @Output() onError = new EventEmitter<Error>();
  @Output() onReorder = new EventEmitter<RideDto>();
  @Output() showToast = new EventEmitter<Toast>();

  reviewService : ReviewService = inject(ReviewService);
  userService : UserService = inject(UserService);
  ridePlanningStore : RidePlanningStore = inject(RidePlanningStore)

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
    console.log(this.ride)
    if (changes['ride']) {
      this.start = new Date(this.ride.startTime)
      this.end = new Date(this.ride.endTime)
    }
  }

  onReorderClick() {
    this.onReorder.emit(this.ride);
  }

  onToggleTrack() {
    if (this.ridePlanningStore.currentRideSubject$.value == null || this.ridePlanningStore.currentRideSubject$.value.id != this.ride.id)
      this.ridePlanningStore.currentRideSubject$.next(this.ride);
    else
      this.ridePlanningStore.currentRideSubject$.next(null);
  }

  cancelScheduleRide(rideId: number) {
    this.rideService.cancelRideUser(rideId).subscribe({
        next: () => {
          this.showToast.emit({title: 'Ride cancelled', message: 'Ride cancelled successfully.'});
        },
        error: (err: any) => {
          const errorMessage = err.error || 'Error cancelling ride';
          this.onError.emit(new Error(errorMessage))
        }
      });
    }
}
