import { inject, Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { ReviewDto } from '../dtos/review-dto';
import { HttpClient } from '@angular/common/http';
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

  public newReview(rideId : number) : void {
    this.rideId = rideId;
    this.showReviewModal.next(true);
    console.log(this.showReviewModal.value)
  }

  public cancelReview() {
    this.rideId = undefined;
    this.showReviewModal.next(false);
    console.log(this.showReviewModal.value)
  }

  public submitReview(review : ReviewDto) : void {
    this.userService.getCurrentUser().subscribe((currentUser : UserDto) => {
      review.userId = currentUser.id;
      this.http.post<ReviewDto>(`${this.BASE_URL}reviews/ride/${this.rideId}`, review).subscribe({
        next: (value : ReviewDto) => {
          this.rideId = undefined;
          this.cancelReview();
        },
        error: (err) => {
          console.log(err)
        }
      })
    })
  } 
}
