import { Component, EventEmitter, inject, Input, Output, signal, SimpleChanges } from '@angular/core';
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
import { UserService } from '../../../services/user-service';
import { HttpErrorResponse } from '@angular/common/http';
import { take } from 'rxjs';
import { WaypointDto } from '../../../dtos/waypoint-dto';


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
  @Input() testIdPrefix: string | null = null;
  @Output() onClose = new EventEmitter<void>();
  @Output() onQueryChange = new EventEmitter<RideQueryDto>();
  @Output() onScrollToBottom = new EventEmitter<RideQueryDto>();
  @Output() onError = new EventEmitter<Error>();
  @Output() onReorder = new EventEmitter<RideDto>();
  @Output() onRenderWaypoints = new EventEmitter<WaypointDto[]>();

  rideService : RideService = inject(RideService);
  userService : UserService = inject(UserService);

  selectedRide = signal<RideDto>({
        id: -1,
        startTime: new Date().toISOString(),
        endTime: new Date().toISOString(),
        waypoints: [],
        driver: {
          email: 'pera@peric.com',
          name: 'Pera',
          surname: 'Peric',
          avatarUrl: '',
          role: Role.DRIVER,
          id: 1,
          phone: "1251323523",
          address: "Test adress 123",
          isBlocked: false
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
  user = signal<UserDto>({ email: '', name: 'Guest', surname: '', avatarUrl: '', role: Role.GUEST, id: 0, phone: '', address: '', isBlocked: false })

  query : RideQueryDto = new RideQueryDto(null, "", false, null);
  page : number = 0;

  filterDate : Date = new Date();
  filterUser = signal<string>("");
  userList = signal<UserDto[]>([]);
  sortOpen : boolean = false;
  selectedField : string = "";
  ascending : boolean = false;

  ngOnChanges(changes : SimpleChanges): void {
    if (changes['open'] && !this.open)
      this.onRenderWaypoints.emit([])
  }

  onRideSelected(rideCard : RideCardDto) {
    if (this.detailsOpen() && this.selectedRide().id == rideCard.id) {
      this.onRenderWaypoints.emit([])
      this.detailsOpen.set(false);
    }
    else {
        this.rideService.getRide(rideCard.id).pipe(take(1)).subscribe((ride : RideDto) => {
        this.onRenderWaypoints.emit(ride.waypoints)
        this.selectedRide.set(ride);
        this.detailsOpen.set(true)
      })
    }
  }

  onRideAction(ride : RideCardDto) {
    if (ride.favorite)
      this.rideService.removeFromFavorites(ride.id, {error: (err : HttpErrorResponse) => { this.onError.emit(new Error(err.error))}})
    else
      this.rideService.addToFavorites(ride.id, {error: (err : HttpErrorResponse) => { this.onError.emit(new Error(err.error))}})
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

  onInputUser(event : Event, id : number) {
    this.filterUser.set((event.target as HTMLElement).innerText)
    this.query.userId = id;
    this.onQueryChange.emit(this.query);
    this.userList.set([]);
  }

  onInputFilter(event : Event) {
    let filter : string = (event.target as HTMLInputElement).value;
    this.userService.getUsersByFullName(filter).subscribe((users : UserDto[]) => {
        if (filter == "") {
          this.userList.set([])
          this.query.userId = null
          this.onQueryChange.emit(this.query);
        }
        else
          this.userList.set(users)
    })
  }

  onBack() {
    this.onClose.emit();
  }

  onScroll(event : Event) {
    let container : HTMLElement = event.target as HTMLElement
    if (container.offsetHeight + container.scrollTop >= container.scrollHeight - 1)
      this.onScrollToBottom.emit(this.query)
  }

  ngOnInit() {
    this.userService.getCurrentUser().subscribe((currentUser : UserDto) => {
      this.user.set(currentUser)
    })
  }
}
