import { Component, EventEmitter, inject, Input, Output } from '@angular/core';
import { RideService } from '../../../services/ride-service';
import { UserService } from '../../../services/user-service';
import { Observable, of } from 'rxjs';
import { RideCardDto } from '../../../dtos/ride-card-dto';
import { RideQueryDto } from '../../../dtos/ride-query';
import { RideList } from '../ride-list/ride-list';
import { AsyncPipe } from '@angular/common';
import { RideDto } from '../../../dtos/ride-dto';

@Component({
  selector: 'app-favorite-rides',
  imports: [RideList, AsyncPipe],
  templateUrl: './favorite-rides.html',
  styleUrl: './favorite-rides.css',
})
export class FavoriteRides {
  private _open : boolean = false;
  @Input() set open(value : boolean) {this._open = value; if (value) this.onQueryChange(this.lastQuery);} get open() {return this._open}
  @Output() onClose = new EventEmitter<void>();
  @Output() onError = new EventEmitter<Error>();
  @Output() onReorder = new EventEmitter<RideDto>();

  rideService : RideService = inject(RideService)
  userService : UserService = inject(UserService)

  rides$ : Observable<RideCardDto[]> = of([]);
  lastQuery : RideQueryDto = new RideQueryDto(null, "", false, null)

  onQueryChange(qurey : RideQueryDto) {
    this.rideService.clearFavorites();
    this.rideService.fetchFavorites(qurey, Math.ceil((window.screen.height * 0.7 - 126) / window.devicePixelRatio / 171))
    this.lastQuery = qurey
  }

  onScrollToBottom(qurey : RideQueryDto) {
    this.rideService.fetchFavorites(qurey, Math.ceil((window.screen.height * 0.7 - 126) / window.devicePixelRatio / 171))
  }

  ngOnInit() {
    this.rides$ = this.rideService.favorites$;
    
    this.onQueryChange(this.lastQuery);
  }
}
