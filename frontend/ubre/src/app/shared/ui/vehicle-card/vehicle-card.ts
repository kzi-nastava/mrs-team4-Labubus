import { Component, Input, Output, EventEmitter } from '@angular/core';
import { Vehicle } from '../../../models/vehicle';

@Component({
  selector: 'app-vehicle-card',
  imports: [],
  templateUrl: './vehicle-card.html',
  styleUrl: './vehicle-card.css',
})
export class VehicleCard {
  @Input({ required: true }) vehicle : Vehicle = {model: "", type: "Standard", image: ""};
  @Input() icon : string = "";
  @Output() onAction = new EventEmitter<void>();

  onIconClick() {
    this.onAction.emit();
  }
}

