import { Component, Input, input, Output, EventEmitter, inject, signal, effect } from '@angular/core';
import { VehicleDto } from '../../../dtos/vehicle-dto';
import { VehicleType } from '../../../enums/vehicle-type';
import { VehicleService } from '../../../services/vehicle-service';

@Component({
  selector: 'app-vehicle-card',
  imports: [],
  templateUrl: './vehicle-card.html',
  styleUrl: './vehicle-card.css',
})
export class VehicleCard {
  driverId = input.required<number>();
  icon = input("");
  @Output() onAction = new EventEmitter<void>();
  
  vehicleService : VehicleService = inject(VehicleService);
  vehicle = signal<VehicleDto>({ model: 'Missing vehicle', type: VehicleType.STANDARD, id: 0, seats: 0, babyFriendly: false, petFriendly: false, plates: "" });

  constructor() {
    effect(() => {
      const id = this.driverId();
      this.vehicleService
        .getVehicleByDriver(id)
        .subscribe(vehicle => this.vehicle.set(vehicle));
    });
  }

  onIconClick() {
    this.onAction.emit();
  }

  // ngOnInit() {
  //     this.vehicleService.getVehicleByDriver(this.driverId()).subscribe((vehicle : VehicleDto) => {
  //       this.vehicle.set(vehicle);
  //     })
  //   }
}

