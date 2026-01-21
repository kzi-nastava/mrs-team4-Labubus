import { Component, inject, Input } from '@angular/core';
import { ReviewDto } from '../../../dtos/review-dto';
import { ReviewService } from '../../../services/review-service';
import { ModalContainer } from '../modal-container/modal-container';
import { Observable, of } from 'rxjs';
import { AsyncPipe } from '@angular/common';

@Component({
  selector: 'app-review-modal',
  imports: [ModalContainer, AsyncPipe],
  templateUrl: './review-modal.html',
  styleUrl: './review-modal.css',
})
export class ReviewModal {
  @Input() show : Observable<boolean> = of(false);

  private reviewService : ReviewService = inject(ReviewService)

  review : ReviewDto = {
    id: null,
    driverId: null,
    userId: null,
    rating: 3,
    text: ""
  }
  error : boolean = false;

  onClose(event : Event) {
    if (event.target === event.currentTarget) {
      this.reviewService.cancelReview();
    }
  }

  onSelectRating(rating : number) {
    if ([1, 2, 3, 4, 5].includes(rating))
      this.review.rating = rating as 1 | 2 | 3 | 4 | 5
  }

  onSetText(event : Event) {
    this.review.text = (event.target as HTMLInputElement).value
    if (this.review.text != "")
      this.error = false; 
  }

  onSubmit() {
    if (this.review.text == "") {
      this.error = true;
      return;
    }

    this.reviewService.submitReview(this.review)
  }
}
