import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, take } from 'rxjs';
import { VehicleTypePricingDto } from '../dtos/vehicle-type-pricing-dto';

@Injectable({ providedIn: 'root' })
export class PricingService {
  private readonly http = inject(HttpClient);
  private readonly api = 'http://localhost:8080/api/pricing';

  private pricingSubject = new BehaviorSubject<VehicleTypePricingDto | null>(null);
  pricing$ = this.pricingSubject.asObservable();

  draft: VehicleTypePricingDto | null = null;

  loadPricing(): void {
    this.http.get<VehicleTypePricingDto>(this.api).pipe(take(1)).subscribe({
      next: (pricing) => {
        this.pricingSubject.next(pricing);
        this.draft = { ...pricing };
      },
    });
  }

  savePricing(): Observable<VehicleTypePricingDto> {
    const obs = this.http.put<VehicleTypePricingDto>(this.api, this.draft!);
    obs.pipe(take(1)).subscribe({
      next: (pricing) => {
        this.pricingSubject.next(pricing);
        this.draft = { ...pricing };
      },
    });
    return obs;
  }

  discardChanges(): void {
    const current = this.pricingSubject.value;
    if (current) {
      this.draft = { ...current };
    }
  }
}
