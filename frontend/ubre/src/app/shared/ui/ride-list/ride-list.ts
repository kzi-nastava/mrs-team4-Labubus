import { Component, EventEmitter, inject, Input, Output, signal } from '@angular/core';
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
import { VehicleType } from '../../../enums/vehicle-type';
import { RideStatus } from '../../../enums/ride-status';


@Component({
  selector: 'app-ride-list',
  imports: [RideCard, ModalContainer, FormsModule, RideDetails],
  templateUrl: './ride-list.html',
  styleUrl: './ride-list.css',
})
export class RideList {
  @Input() rides : RideCardDto[] | null = [];
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

  selectedRide = signal<RideDto>({
        id: -1,
        start: new Date(),
        end: new Date(),
        waypoints: [],
        driver: {
          email: 'pera@peric.com',
          name: 'Pera',
          surname: 'Peric',
          avatarUrl: '',
          role: Role.DRIVER,
          id: 1,
          phone: "1251323523",
          address: "Test adress 123"
        },
        vehicle: { model: 'Toyota Carolla 2021', type: VehicleType.STANDARD, id: 1, seats: 5, babyFriendly: true, petFriendly: false, plates: "123123123" },
        passengers: [],
        price: 16.13,
        distance: 10.3,
        panic: false,
        canceledBy: null,
        status: RideStatus.ACCEPTED,
        createdBy: -1
      },);
  detailsOpen = signal<boolean>(false)

  query : RideQueryDto = new RideQueryDto(null, "", false, null);

  filterDate : Date = new Date();
  filterDriver : string = "";
  sortOpen : boolean = false;
  selectedField : string = "";
  ascending : boolean = false;

  onRideSelected(rideCard : RideCardDto) {
    if (this.detailsOpen() && this.selectedRide().id == rideCard.id)
      this.detailsOpen.set(false);
    else {
        this.rideService.getRide(rideCard.id).subscribe((ride : RideDto) => {
        console.log(ride)
        this.selectedRide.set(ride);
        this.detailsOpen.set(true)
      })
    }
  }

  onRideAction(ride : RideCardDto) {
    if (ride.favorite)
      this.rideService.removeFromFavorites(ride.id)
    else
      this.rideService.addToFavorites(ride.id)
  }

  onToggleSort() {
    this.sortOpen = !this.sortOpen;
  }

  onToggleSortDirection() {
    this.query.ascending = !this.query.ascending;
    this.onQueryChange.emit(this.query)
  }

  onSelectField(event : Event, field : string) {
    this.query.sortBy = field
    this.selectedField = (event.target as HTMLElement).innerText
    this.sortOpen = false;
    this.onQueryChange.emit(this.query)
  }

  onSelectDate(event : Event) {
    const value : string = (event.target as HTMLInputElement).value;
    this.query.date = value != '' ?  new Date(value) : null;
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
