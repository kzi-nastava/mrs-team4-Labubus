import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { KeyValuePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RideCard } from '../ride-card/ride-card';
import { ModalContainer } from '../modal-container/modal-container';
import { RideDetails } from '../ride-details/ride-details';
import { RideDto } from '../../../dtos/ride-dto';
import { UserDto } from '../../../dtos/user-dto';
import { RideCardDto } from '../../../dtos/ride-card-dto';
import { RideService } from '../../../services/ride-service';
import { Role } from '../../../enums/role';


@Component({
  selector: 'app-ride-list',
  imports: [RideCard, ModalContainer, KeyValuePipe, FormsModule, RideDetails],
  templateUrl: './ride-list.html',
  styleUrl: './ride-list.css',
})
export class RideList {
  @Input() rides : RideCardDto[] = [];
  @Input() title : string = "";
  @Input() open : boolean = false;
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
  @Output() onClose = new EventEmitter<void>();

  rideService : RideService = inject(RideService);

  selectedRide : RideDto | undefined = undefined;

  filterDate : Date = new Date();
  filterDriver : string = "";
  sortOpen : boolean = false;
  selectedField : string = "";
  ascending : boolean = false;

  onRideSelected(rideCard : RideCardDto) {
    if (this.selectedRide !== undefined && this.selectedRide.id == rideCard.rideId)
      this.selectedRide = undefined;
    else {
      this.rideService.getRide(rideCard.rideId).subscribe((ride : RideDto | undefined) => {
        this.selectedRide = ride;
      })
    }
  }

  onRideAction(ride : RideCardDto) {
    this.rideService.toggleFavorite(this.user.id, ride.rideId)
  }

  onToggleSort() {
    this.sortOpen = !this.sortOpen;
  }

  onToggleSortDirection() {
    this.ascending = !this.ascending;
  }

  onSelectField(field : string) {
    this.selectedField = field
    this.sortOpen = false;
  }

  onBack() {
    this.onClose.emit();
  }
}
