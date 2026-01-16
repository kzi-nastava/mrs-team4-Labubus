// account settings service - učitava korisnika tek kada se otvori account settings
// promene se čuvaju tek kada korisnik klikne save, zatvaranjem se sve briše

import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { catchError, switchMap, map, take, tap } from 'rxjs/operators';
import { UserService } from './user-service';
import { UserDto } from '../dtos/user-dto';
import { of } from 'rxjs';

type FieldErrors = Partial<Record<
    'name' | 'surname' | 'phone' | 'address',
    string
>>;

@Injectable({ providedIn: 'root' })
export class AccountSettingsService {
    private readonly userService = inject(UserService);
    private readonly http = inject(HttpClient);
    private readonly api = 'http://localhost:8080/api';

    draft: UserDto | null = null;
    fieldErrors: FieldErrors | null = null;

    private avatarSrcSubject = new BehaviorSubject<string>('default-avatar.jpg');
    readonly avatarSrc$ = this.avatarSrcSubject.asObservable();
    avatarFile: File | null = null;

    loadDraft(): void {
        this.userService.currentUser$.pipe(take(1)).subscribe(user => {
            if (!user || !user.id) return;
            this.draft = { ...user };
            this.fieldErrors = null; 
            // pull avatar src from user service just once 
            this.userService.avatarSrc$.pipe(take(1)).subscribe(src => {
                this.avatarSrcSubject.next(src);
            });
        });
    }

    patchDraft(updates: Partial<UserDto>): void {
        if (!this.draft) return;

        this.draft = {
            ...this.draft,
            ...updates
        };
    }

    save(): Observable<UserDto> {
        this.fieldErrors = null;

        const errors = this.validate();
        if (Object.keys(errors).length > 0) {
            this.fieldErrors = errors;
            return throwError(() => 'Input fields validation has failed. Please check the fields and try again.');
        }

        if (!this.draft || !this.draft.id) {
            return throwError(() => 'No draft to save. Please try again.');
        }

        return this.http.put<UserDto>(`${this.api}/users/${this.draft.id}`, this.draft).pipe(
            switchMap(updatedUser => {
                if (!this.avatarFile) return of(updatedUser);
          
                return this.uploadAvatar(updatedUser.id, this.avatarFile).pipe(
                  map(() => updatedUser)
                );
              }),
              tap(updatedUser => {
                this.userService.setCurrentUserById(updatedUser.id);
                this.draft = { ...updatedUser };
                this.fieldErrors = null;
          
                this.avatarFile = null;
              }),
            catchError((err: HttpErrorResponse) => {
                if (err.status === 401) {
                    return throwError(() => 'Unauthorized. Please login again.');
                }
                if (err.status === 403) {
                    return throwError(() => 'Forbidden. You are not authorized to access this resource.');
                }
                if (err.error && typeof err.error === 'object' && !err.error.detail) {
                    this.fieldErrors = err.error as FieldErrors;
                }
                
                const reason =
                    typeof err.error === 'string'
                        ? err.error
                        : err.error?.detail || err.message || 'Failed to save account settings';
                return throwError(() => reason);
            })
        );
    }

    clearDraft(): void {
        this.draft = null;
        this.fieldErrors = null;
    }

    validate(): FieldErrors {
        const e: FieldErrors = {};

        if (!this.draft) {
            return e;
        }

        const name = (this.draft.name ?? '').trim();
        const surname = (this.draft.surname ?? '').trim();
        const phone = (this.draft.phone ?? '').trim();
        const address = (this.draft.address ?? '').trim();

        if (!name) e.name = 'Required';
        if (!surname) e.surname = 'Required';
        if (!phone) e.phone = 'Required';
        if (!address) e.address = 'Required';

        return e;
    }

    clearFieldError(field: keyof FieldErrors): void {
        if (!this.fieldErrors) return;
        this.fieldErrors = { ...this.fieldErrors, [field]: null };
    }

    setAvatarFile(file: File | null): void {
        const prev = this.avatarSrcSubject.value;
        if (prev.startsWith('blob:')) URL.revokeObjectURL(prev);

        if (!file) {
            this.avatarSrcSubject.next('default-avatar.jpg');
            this.avatarFile = null;
            return;
        }

        const src = URL.createObjectURL(file);
        this.avatarSrcSubject.next(src);
        this.avatarFile = file;
    }

    // use upload avatar from register a driver
    uploadAvatar(userId: number, file: File): Observable<void> {
        const formData = new FormData();
        formData.append('file', file);
        return this.http.post<void>(`${this.api}/users/${userId}/avatar`, formData);
    }
}