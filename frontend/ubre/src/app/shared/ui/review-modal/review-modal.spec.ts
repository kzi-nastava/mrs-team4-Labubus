import { ComponentFixture, TestBed } from '@angular/core/testing';

import { ReviewModal } from './review-modal';
import { ReviewService } from '../../../services/review-service';
import { ReviewDto } from '../../../dtos/review-dto';
import { BehaviorSubject, Observer, of } from 'rxjs';
import { By } from '@angular/platform-browser';
import { HttpErrorResponse } from '@angular/common/http';

describe('ReviewModal', () => {
  let component: ReviewModal;
  let fixture: ComponentFixture<ReviewModal>;
  let mockReviewService: jasmine.SpyObj<ReviewService>

  let modalOpen : BehaviorSubject<boolean>;

  beforeEach(async () => {
    mockReviewService = jasmine.createSpyObj<ReviewService>(['cancelReview', 'submitReview'])
    mockReviewService.cancelReview.and.callFake(() => modalOpen.next(false))

    await TestBed.configureTestingModule({
      imports: [ReviewModal],
      providers: [{ provide: ReviewService, useValue: mockReviewService }]
    })
    .compileComponents();

    fixture = TestBed.createComponent(ReviewModal);
    component = fixture.componentInstance;
    modalOpen = new BehaviorSubject<boolean>(true);
    component.show = modalOpen.asObservable();
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should open', async () => {
    modalOpen.next(true);
    fixture.detectChanges();

    expect((fixture.debugElement.query(By.css(`div[testId="review-backdrop"]`)).nativeElement as HTMLElement).classList.contains("fade")).toBe(false)
  });

  it('should close', async () => {
    modalOpen.next(false);
    fixture.detectChanges();

    expect((fixture.debugElement.query(By.css(`div[testId="review-backdrop"]`)).nativeElement as HTMLElement).classList.contains("fade")).toBe(true)
  });

  it('should set review rating', async () => {
    component.onSelectRating(4)
    fixture.detectChanges();

    expect(component.review.rating).toBe(4)
    for (let i = 0; i < 4; i++)
      expect(fixture.debugElement.query(By.css(`img[testId="review-start-${i}"]`)).nativeElement.getAttribute('src')).toBe('star_filled.svg')
    expect(fixture.debugElement.query(By.css(`img[testId="review-start-4"]`)).nativeElement.getAttribute('src')).toBe('star_outline.svg')
  });

  it('should not accept invalid rating', () => {
    const oldRating = component.review.rating
    spyOn(component.onError, 'emit')

    component.onSelectRating(7)

    expect(component.review.rating).toBe(oldRating)
    expect(component.onError.emit).toHaveBeenCalled()
  });

  it('should set review text', async () => {
    spyOn(component, 'onSetText')

    const input : HTMLInputElement = fixture.debugElement.query(By.css(`textarea[testid="review-text-input"]`)).nativeElement
    input.value = "Test review"
    input.dispatchEvent(new Event("input"))
    fixture.detectChanges();

    expect(component.review.text).toBe("Test review")
    expect(component.onSetText).toHaveBeenCalled();
  });

  it('should validate review text', () => {
    spyOn(component, 'onSetText')

    const input : HTMLInputElement = fixture.debugElement.query(By.css(`textarea[testid="review-text-input"]`)).nativeElement
    input.value = ""
    input.dispatchEvent(new Event("input"))
    fixture.detectChanges();

    expect(component.review.text).toBe("")
    expect(component.onSetText).toHaveBeenCalled();
    expect(component.error).toBe(true)
  });

  it('should submit review', async () => {
    mockReviewService.submitReview.and.callFake((review : ReviewDto, callback : Partial<Observer<ReviewDto>> | ((value: ReviewDto) => void) | undefined) => {
      if (typeof callback != 'function') {
        const typedCallback = callback as Partial<Observer<ReviewDto>>
        if (typedCallback.next)
          typedCallback.next(review)
      }
      else
        callback(review)
    })

    component.review = new ReviewDto(1, 1, 2, 4, "Test review")
    component.onSubmit()
    fixture.detectChanges();
    await fixture.whenStable();

    expect(mockReviewService.submitReview).toHaveBeenCalled();
    expect(mockReviewService.cancelReview).toHaveBeenCalled();
    expect((fixture.debugElement.nativeElement as HTMLElement).classList.contains("fade")).toBe(true)
  });

  it('should react to error on submit', async () => {
    mockReviewService.submitReview.and.callFake((review : ReviewDto, callback : Partial<Observer<ReviewDto>> | ((value: ReviewDto) => void) | undefined) => {
      if (typeof callback != 'function') {
        const typedCallback = callback as Partial<Observer<ReviewDto>>
        if (typedCallback.error)
          typedCallback.error(new HttpErrorResponse({error:"Review text cannot be empty", status: 400}))
      }
    })
    spyOn(component.onError, 'emit')

    component.review = new ReviewDto(1, 1, 2, 4, "")
    component.onSubmit()

    expect(mockReviewService.submitReview).toHaveBeenCalledWith(component.review, jasmine.anything);
    expect(component.onError.emit).toHaveBeenCalled()
  });
});
