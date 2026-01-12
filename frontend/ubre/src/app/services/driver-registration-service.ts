import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

import { DriverRegistrationDto } from '../dtos/driver-registration-dto';
import { UserDto } from '../dtos/user-dto';
import { VehicleDto } from '../dtos/vehicle-dto';
import { VehicleType } from '../enums/vehicle-type';

type ValidationErrors = Partial<Record<
  'email' | 'password' | 'passwordConfirm' | 'name' | 'surname' | 'phone' | 'address' | 'plates' | 'model',
  string
>>;

@Injectable({ providedIn: 'root' })
export class DriverRegistrationService {
  private readonly http = inject(HttpClient);
  private readonly driversApi = 'http://localhost:8080/api/drivers';
  private readonly vehiclesApi = 'http://localhost:8080/api/vehicles';

  private readonly initialDraft: DriverRegistrationDto = {
    id: 0,
    avatarUrl: 'default-avatar.jpg',
    email: '',
    password: '',
    name: '',
    surname: '',
    phone: '',
    address: '',
    vehicle: {
      id: 0,
      model: '',
      type: VehicleType.STANDARD,
      seats: 4,
      babyFriendly: false,
      petFriendly: false,
      plates: '',
    },
  };

  private readonly draftSubject = new BehaviorSubject<DriverRegistrationDto>(this.clone(this.initialDraft));
  readonly draft$ = this.draftSubject.asObservable();

  getDraftSnapshot(): DriverRegistrationDto {
    return this.draftSubject.value;
  }

  setDraft(next: DriverRegistrationDto) {
    this.draftSubject.next(this.clone(next));
  }

  patchDraft(
    patch: Omit<Partial<DriverRegistrationDto>, 'vehicle'> & {
      vehicle?: Partial<DriverRegistrationDto['vehicle']>;
    }
  ) {
    const curr = this.getDraftSnapshot();

    this.draftSubject.next(this.clone({
      ...curr,
      ...patch,
      vehicle: { ...curr.vehicle, ...(patch.vehicle ?? {}) },
    }));
  }

  resetDraft() {
    this.draftSubject.next(this.clone(this.initialDraft));
  }

  decSeats() {
    const s = this.getDraftSnapshot().vehicle.seats ?? 2;
    this.patchDraft({ vehicle: { seats: Math.max(2, s - 1) } });
  }

  incSeats() {
    const s = this.getDraftSnapshot().vehicle.seats ?? 0;
    this.patchDraft({ vehicle: { seats: Math.min(9, s + 1) } });
  }

  validate(dto: DriverRegistrationDto, confirmPassword?: string): ValidationErrors {
    const e: ValidationErrors = {};

    const email = (dto.email ?? '').trim();
    const pass = (dto.password ?? '').trim();
    const name = (dto.name ?? '').trim();
    const surname = (dto.surname ?? '').trim();
    const phone = (dto.phone ?? '').trim();
    const address = (dto.address ?? '').trim();

    const model = (dto.vehicle?.model ?? '').trim();
    const plates = (dto.vehicle?.plates ?? '').trim();
    const seats = dto.vehicle?.seats ?? 0;

    if (!email) e.email = 'Required';
    else if (!/^\S+@\S+\.\S+$/.test(email)) e.email = 'Email format is not valid';

    if (!pass) e.password = 'Required';
    else if (pass.length < 6) e.password = 'Password too short';

    if (confirmPassword != null && pass !== confirmPassword) {
      e.passwordConfirm = 'Passwords do not match';
    }

    if (!name) e.name = 'Required';
    if (!surname) e.surname = 'Required';
    if (!phone) e.phone = 'Required';
    if (!address) e.address = 'Required';

    if (!model) e.model = 'Required';
    if (!plates) e.plates = 'Required';

    return e;
  }

  register(confirmPassword?: string): Observable<{ driver: UserDto; vehicle: VehicleDto }> {
    const dto = this.getDraftSnapshot();
    const errors = this.validate(dto, confirmPassword);

    if (Object.keys(errors).length) {
      return throwError(() => errors);
    }

    return this.http.post<UserDto>(this.driversApi, dto).pipe(
      switchMap(driver =>
        this.http.post<VehicleDto>(`${this.vehiclesApi}/driver/${driver.id}`, dto.vehicle).pipe(
          map(vehicle => ({ driver, vehicle }))
        )
      ),
      tap(() => this.resetDraft())
    );
  }

  private clone(v: DriverRegistrationDto): DriverRegistrationDto {
    return { ...v, vehicle: { ...v.vehicle } };
  }
}
