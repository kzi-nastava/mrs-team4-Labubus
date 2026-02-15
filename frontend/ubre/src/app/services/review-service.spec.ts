import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { ReviewService } from './review-service';
import { Observable, of, take } from 'rxjs';
import { ReviewDto } from '../dtos/review-dto';
import { UserDto } from '../dtos/user-dto';
import { Role } from '../enums/role';
import { UserService } from './user-service';
import { HttpErrorResponse } from '@angular/common/http';

describe('ReviewService', () => {
  let service: ReviewService;
  let httpMock: HttpTestingController;
  let mockUserService: jasmine.SpyObj<UserService>

  const validReview = new ReviewDto(1, 1, 2, 4, "Test review")
  const expectedBody = new ReviewDto(1, 1, 3, 4, "Test review")
  const invalidReview = new ReviewDto(2, 1, 2, 4, "")

  const currentUser = new UserDto(3, Role.REGISTERED_USER, "", "", "", "", "", "", false)

  beforeEach(() => {
    mockUserService = jasmine.createSpyObj<UserService>(['getCurrentUser'])
    mockUserService.getCurrentUser.and.returnValue(of(currentUser))

    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [ReviewService, { provide: UserService, useValue: mockUserService }],
    });
    service = TestBed.inject(ReviewService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('should set ride to review and modal to open', () => {
    service.newReview(1)

    service.showReviewModal$.pipe(take(1)).subscribe(showModal => expect(showModal).toBe(true))

    expect(service.getRideId()).toBe(1)
  })

  it('should remove the ride to review and close the modal', () => {
    service.newReview(1)
    service.cancelReview();

    service.showReviewModal$.pipe(take(1)).subscribe(showModal => expect(showModal).toBe(false))

    expect(service.getRideId()).toBe(undefined)
  })

  it('should create a review', () => {
    service.newReview(1)

    service.submitReview(validReview, {
      next: (value : ReviewDto) => {
        expect(value.driverId).toBe(1);
        expect(value.userId).toBe(3);
        expect(value.rating).toBe(4);
        expect(value.text).toBe("Test review");
      },
      error: (err : HttpErrorResponse) => {
        fail(`Request should be valid, got error: ${err.message}`)
      }
    })

    const req = httpMock.expectOne(`http://localhost:8080/api/reviews/ride/1`);
    expect(req.request.body).toEqual(expectedBody)

    req.flush(expectedBody)
  })

  it('should not have empty review text', () => {
    service.newReview(1)

    service.submitReview(invalidReview, {
      next: (value : ReviewDto) => {
        fail(`Expected error, got: ${value}`)
      },
      error: (err : HttpErrorResponse) => {
        expect(err.status).toBe(400)
      }
    })

    const req = httpMock.expectNone(`http://localhost:8080/api/reviews/ride/1`);
  })

  it('should not submit without ride id', () => {
    service.submitReview(validReview, {
      next: (value : ReviewDto) => {
        fail(`Expected error, got: ${value}`)
      },
      error: (err : HttpErrorResponse) => {
        expect(err.status).toBe(400)
      }
    })

    const req = httpMock.expectNone(`http://localhost:8080/api/reviews/ride/1`);
  })
});
