import { inject, Injectable } from '@angular/core';
import { UserService } from './user-service';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, Observer, take } from 'rxjs';
import { ComplaintDto } from '../dtos/complaint-dto';
import { UserDto } from '../dtos/user-dto';

@Injectable({
  providedIn: 'root',
})
export class ComplaintService {
  private readonly BASE_URL : string = "http://localhost:8080/api/";
  private readonly userService : UserService = inject(UserService);
  private readonly http = inject(HttpClient);

  private readonly showComplaintModal : BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public readonly showComplaintModal$ : Observable<boolean> = this.showComplaintModal.asObservable();

  private rideId : number | undefined;

  public newComplaint(rideId : number) : void {
    this.rideId = rideId;
    this.showComplaintModal.next(true);
  }

  public cancelComplaint() {
    this.rideId = undefined;
    this.showComplaintModal.next(false);
  }

  public submitComplaint(complaint : ComplaintDto, callback : Partial<Observer<ComplaintDto>> | ((value: ComplaintDto) => void) | undefined) : void {
    this.userService.getCurrentUser().pipe(take(1)).subscribe((currentUser : UserDto) => {
      complaint.userId = currentUser.id;
      this.http.post<ComplaintDto>(`${this.BASE_URL}complaints/ride/${this.rideId}`, complaint).subscribe(callback)
    })
  } 
}
