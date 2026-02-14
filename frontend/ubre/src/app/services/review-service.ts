import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable, Observer, take } from 'rxjs';
import { ReviewDto } from '../dtos/review-dto';
import { HttpClient, HttpErrorResponse, HttpResponse } from '@angular/common/http';
import { UserService } from './user-service';
import { UserDto } from '../dtos/user-dto';

@Injectable({
  providedIn: 'root',
})
export class ReviewService {
  private readonly BASE_URL : string = "http://localhost:8080/api/";
  private readonly userService : UserService = inject(UserService);
  private readonly http = inject(HttpClient);

  private readonly showReviewModal : BehaviorSubject<boolean> = new BehaviorSubject<boolean>(false);
  public readonly showReviewModal$ : Observable<boolean> = this.showReviewModal.asObservable();

  private rideId : number | undefined;

  public getRideId() : number | undefined {
    return this.rideId
  }

  public newReview(rideId : number) : void {
    this.rideId = rideId;
    this.showReviewModal.next(true);
  }

  public cancelReview() {
    this.rideId = undefined;
    this.showReviewModal.next(false);
  }

  public submitReview(review : ReviewDto, callback : Partial<Observer<ReviewDto>> | ((value: ReviewDto) => void) | undefined) : void {
    if (review.text === "") {
      if (typeof callback != 'function') {
        const typedCallback = callback as Partial<Observer<ReviewDto>>
        if (typedCallback.error)
          typedCallback.error(new HttpErrorResponse({error:"Review text cannot be empty", status: 400}))
      }
      return
    }

    if (this.rideId === undefined) {
      if (typeof callback != 'function') {
        const typedCallback = callback as Partial<Observer<ReviewDto>>
        if (typedCallback.error)
          typedCallback.error(new HttpErrorResponse({error:"No ride selected for review", status: 400}))
      }
      return
    }

    this.userService.getCurrentUser().pipe(take(1)).subscribe((currentUser : UserDto) => {
      review.userId = currentUser.id;
      this.http.post<ReviewDto>(`${this.BASE_URL}reviews/ride/${this.rideId}`, review).subscribe(callback)
    })
  } 

  getReviewsForRide(rideId: number): Observable<ReviewDto> {
    return this.http.get<ReviewDto>(`${this.BASE_URL}reviews/ride/${rideId}`);
  }
}
