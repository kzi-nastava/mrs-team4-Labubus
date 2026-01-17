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
import { RideQueryDto } from '../../../dtos/ride-query';


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
  @Output() onQueryChange = new EventEmitter<RideQueryDto>();

  rideService : RideService = inject(RideService);

  selectedRide : RideDto | undefined = undefined;

  query : RideQueryDto = new RideQueryDto(null, "", false, null);

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
    this.query.ascending = !this.query.ascending;
    this.onQueryChange.emit(this.query)
  }

  onSelectField(field : string) {
    this.query.sortBy = field
    this.sortOpen = false;
    this.onQueryChange.emit(this.query)
  }

  onSelectDate(event : Event) {
    this.query.date = new Date((event.target as HTMLInputElement).value);
    this.onQueryChange.emit(this.query);
  }

  onInputUser(event : Event) {
    this.query.userId = new Number((event.target as HTMLInputElement).value).valueOf();
    this.onQueryChange.emit(this.query)
  }

  onBack() {
    this.onClose.emit();
  }
}
