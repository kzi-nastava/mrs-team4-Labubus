import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { map, switchMap, tap } from 'rxjs/operators';

import { DriverRegistrationDto } from '../dtos/driver-registration-dto';
import { UserDto } from '../dtos/user-dto';
import { VehicleDto } from '../dtos/vehicle-dto';
import { VehicleType } from '../enums/vehicle-type';

import { catchError } from 'rxjs/operators';

type FieldErrors = Partial<Record<
  'email' | 'password' | 'passwordConfirm' | 'name' | 'surname' | 'phone' | 'address' | 'plates' | 'model',
  string
>>;

@Injectable({ providedIn: 'root' })
export class DriverRegistrationService {
  private readonly http = inject(HttpClient);
  private readonly driversApi = 'http://localhost:8080/api/drivers';
  private readonly usersApi = 'http://localhost:8080/api/users';

  private readonly avatarSrcSubject = new BehaviorSubject<string>('');
  readonly avatarSrc$ = this.avatarSrcSubject.asObservable();

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

  fieldErrors: FieldErrors | null = null;

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
    this.setAvatarFile(null);
    this.fieldErrors = null;
  }

  decSeats() {
    const s = this.getDraftSnapshot().vehicle.seats ?? 2;
    this.patchDraft({ vehicle: { seats: Math.max(2, s - 1) } });
  }

  incSeats() {
    const s = this.getDraftSnapshot().vehicle.seats ?? 0;
    this.patchDraft({ vehicle: { seats: Math.min(9, s + 1) } });
  }

  validate(dto: DriverRegistrationDto, confirmPassword?: string): FieldErrors {
    const e: FieldErrors = {};

    const email = (dto.email ?? '').trim();
    const pass = (dto.password ?? '').trim();
    const name = (dto.name ?? '').trim();
    const surname = (dto.surname ?? '').trim();
    const phone = (dto.phone ?? '').trim();
    const address = (dto.address ?? '').trim();

    const model = (dto.vehicle?.model ?? '').trim();
    const plates = (dto.vehicle?.plates ?? '').trim();

    if (!email) e.email = 'Required';
    else if (!/^\S+@\S+\.\S+$/.test(email)) e.email = 'Email format is not valid';

    if (!pass) e.password = 'Required';
    else if (pass.length < 6) e.password = 'Password too short';

    if (confirmPassword != null && pass !== confirmPassword) {
      e.passwordConfirm = 'Passwords do not match';
    }

    if (!name) e.name = 'Required'; if (!surname) e.surname = 'Required';
    if (!phone) e.phone = 'Required'; if (!address) e.address = 'Required';
    if (!model) e.model = 'Required'; if (!plates) e.plates = 'Required';

    return e;
  }

  register(confirmPassword?: string): Observable<UserDto> {
    this.fieldErrors = null;

    const dto = this.getDraftSnapshot();
    const errors = this.validate(dto, confirmPassword);
  
    if (Object.keys(errors).length > 0) {
      this.fieldErrors = errors;
      return throwError(() => 'Validation failed');
    }
  
    return this.http.post<UserDto>(this.driversApi, dto).pipe(
      switchMap(driver => {
        if (!this.avatarFile) return new Observable<UserDto>(sub => { sub.next(driver); sub.complete(); });
  
        return this.uploadAvatar(driver.id, this.avatarFile).pipe(
          map(() => driver)
        );
      }),
      tap(() => {
        this.resetDraft();
        this.fieldErrors = null;
      }),
      catchError((err: HttpErrorResponse) => {
        if (err.error && typeof err.error === 'object' && !err.error.detail) {
          this.fieldErrors = err.error as FieldErrors;
        }
        
        const reason =
          typeof err.error === 'string'
            ? err.error
            : err.error?.detail || err.message || 'Registration failed';
        const msg = `Registration couldn't be completed. ${reason} (Error ${err.status}).`;
        return throwError(() => msg);
      })
    );
  }
  

  private clone(v: DriverRegistrationDto): DriverRegistrationDto {
    return { ...v, vehicle: { ...v.vehicle } };
  }

  private avatarFile: File | null = null;

  setAvatarFile(file: File | null) {
    const prev = this.avatarSrcSubject.value;
    if (prev.startsWith('blob:')) URL.revokeObjectURL(prev);

    if (!file) {
      this.avatarSrcSubject.next('');
      this.patchDraft({ avatarUrl: 'default-avatar.jpg' });
      return;
    }

    const src = URL.createObjectURL(file);
    this.avatarSrcSubject.next(src);
    this.patchDraft({ avatarUrl: file.name });

    this.avatarFile = file;
  }

  uploadAvatar(driverId: number, file: File): Observable<void> {
    const formData = new FormData();
    formData.append('file', file);

    return this.http.post<void>(`${this.usersApi}/${driverId}/avatar`, formData);
  }

  clearFieldError(field: keyof FieldErrors): void {
    if (!this.fieldErrors) return;
    this.fieldErrors = { ...this.fieldErrors, [field]: null };
  }

}
