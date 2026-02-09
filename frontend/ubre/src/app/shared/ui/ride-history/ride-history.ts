import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { RideList } from '../ride-list/ride-list';
import { RideService } from '../../../services/ride-service';
import { RideCardDto } from '../../../dtos/ride-card-dto';
import { UserDto } from '../../../dtos/user-dto';
import { UserService } from '../../../services/user-service';
import { Role } from '../../../enums/role';
import { RideQueryDto } from '../../../dtos/ride-query';
import { Observable, of } from 'rxjs';
import { AsyncPipe } from '@angular/common';
import { WaypointDto } from '../../../dtos/waypoint-dto';
import { RideDto } from '../../../dtos/ride-dto';


@Component({
  selector: 'app-ride-history',
  imports: [RideList, AsyncPipe],
  templateUrl: './ride-history.html',
  styleUrl: './ride-history.css',
})
export class RideHistory {
  private _open : boolean = false;
  @Input() set open(value : boolean) {this._open = value; if (value) this.onQueryChange(this.lastQuery);} get open() {return this._open}
  @Output() onClose = new EventEmitter<void>();
  @Output() onError = new EventEmitter<Error>();
  @Output() onReorder = new EventEmitter<RideDto>();
  @Output() onRenderWaypoints = new EventEmitter<WaypointDto[]>();

  rideService : RideService = inject(RideService)
  userService : UserService = inject(UserService)

  rides$ : Observable<RideCardDto[]> = of([]);
  lastQuery : RideQueryDto = new RideQueryDto(null, "", false, null)

  onQueryChange(qurey : RideQueryDto) {
    this.rideService.clearHistory();
    this.rideService.fetchHistory(qurey, Math.ceil((window.screen.height * 0.7 - 126) / window.devicePixelRatio / 171))
    this.lastQuery = qurey
  }

  onScrollToBottom(qurey : RideQueryDto) {
    this.rideService.fetchHistory(qurey, Math.ceil((window.screen.height * 0.7 - 126) / window.devicePixelRatio / 171))
  }

  ngOnInit() {
    this.rides$ = this.rideService.history$;
    
    this.onQueryChange(new RideQueryDto(null, "", false, null));
  }
}
