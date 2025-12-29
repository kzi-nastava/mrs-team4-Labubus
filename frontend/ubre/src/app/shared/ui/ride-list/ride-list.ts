import { Component, EventEmitter, Input, Output } from '@angular/core';
import { KeyValuePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RideCard } from '../ride-card/ride-card';
import { ModalContainer } from '../modal-container/modal-container';
import { RideDetails } from '../ride-details/ride-details';
import { RideDto } from '../../../dtos/ride-dto';
import { User } from '../../../dtos/user';


@Component({
  selector: 'app-ride-list',
  imports: [RideCard, ModalContainer, KeyValuePipe, FormsModule, RideDetails],
  templateUrl: './ride-list.html',
  styleUrl: './ride-list.css',
})
export class RideList {
  @Input() rides : RideDto[] = [];
  @Input() title : string = "";
  @Input() open : boolean = false;
  @Input() user : User = {email: '', firstName: '', lastName: '', profilePicture: '', role: 'guest'}
  @Output() onClose = new EventEmitter<void>();

  selectedRide : RideDto | undefined = undefined;
  favoriteRides : Number[] = [];

  filterDate : Date = new Date();
  filterDriver : string = "";
  sortOpen : boolean = false;
  selectedField : string = "";
  ascending : boolean = false;

  onRideSelected(ride : RideDto) {
    if (this.selectedRide !== undefined && this.selectedRide.id == ride.id)
      this.selectedRide = undefined;
    else
      this.selectedRide = ride;
  }

  onRideAction(ride : RideDto) {
    if (this.favoriteRides.includes(ride.id))
      this.favoriteRides = this.favoriteRides.filter(id => id != ride.id)
    else
      this.favoriteRides.push(ride.id);
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
