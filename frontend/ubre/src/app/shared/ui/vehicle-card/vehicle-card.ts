import { Component, Input, Output, EventEmitter } from '@angular/core';
import { VehicleDto } from '../../../dtos/vehicle-dto';
import { VehicleType } from '../../../enums/vehicle-type';

@Component({
  selector: 'app-vehicle-card',
  imports: [],
  templateUrl: './vehicle-card.html',
  styleUrl: './vehicle-card.css',
})
export class VehicleCard {
  @Input({ required: true }) vehicle : VehicleDto = { model: 'Missing vehicle', type: VehicleType.STANDARD, id: 0, seats: 0, babyFriendly: false, petFriendly: false, plates: "" };
  @Input() icon : string = "";
  @Output() onAction = new EventEmitter<void>();

  onIconClick() {
    this.onAction.emit();
  }
}

