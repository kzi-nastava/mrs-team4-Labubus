import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { ReviewDto } from '../../../dtos/review-dto';
import { ReviewService } from '../../../services/review-service';

@Component({
  selector: 'app-ride-history-reviews',
  imports: [],
  templateUrl: './ride-history-reviews.html',
  styleUrl: './ride-history-reviews.css',
})
export class RideHistoryReviews implements OnInit, OnChanges {
  @Input() rideId!: number;

  review!: ReviewDto;

  constructor(private reviewService: ReviewService) {}

  ngOnInit() {
    this.loadReviews();
  }

  ngOnChanges(changes: SimpleChanges) {
    if (changes['rideId'] && !changes['rideId'].firstChange) {
      this.loadReviews();
    }
  }

  loadReviews() {
    this.reviewService.getReviewsForRide(this.rideId).subscribe({
      next: (review) => {
        this.review = review;
      },
      error: (err) => {
        console.error('Failed to load reviews', err);
      },
    });
  }

  getStars(rating: number): string {
    const fullStars = '⭐'.repeat(Math.floor(rating));
    const emptyStars = '☆'.repeat(5 - Math.floor(rating));
    return fullStars + emptyStars;
  }

}
