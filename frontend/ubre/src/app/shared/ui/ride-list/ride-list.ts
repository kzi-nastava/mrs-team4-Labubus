import { Component, EventEmitter, Input, Output } from '@angular/core';
import { KeyValuePipe } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RideCard } from '../ride-card/ride-card';
import { ModalContainer } from '../modal-container/modal-container';
import { Ride } from '../../../models/ride';


@Component({
  selector: 'app-ride-list',
  imports: [RideCard, ModalContainer, KeyValuePipe, FormsModule],
  templateUrl: './ride-list.html',
  styleUrl: './ride-list.css',
})
export class RideList {
  @Input() rides : Ride[] = [];
  @Input() title : string = "";
  @Input() open : boolean = false;
  @Output() onClose = new EventEmitter<void>();

  selectedRide : Number | undefined = undefined;
  favoriteRides : Number[] = [];

  filterDate : Date = new Date();
  filterDriver : string = "";
  sortOpen : boolean = false;
  selectedField : string = "";
  ascending : boolean = false;

  onRideSelected(ride : Ride) {
    if (this.selectedRide === ride.id)
      this.selectedRide = undefined;
    else
      this.selectedRide = ride.id;
  }

  onRideAction(ride : Ride) {
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
