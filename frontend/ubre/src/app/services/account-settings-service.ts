// account settings service - učitava korisnika tek kada se otvori account settings
// promene se čuvaju tek kada korisnik klikne save, zatvaranjem se sve briše

import { inject, Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, take, tap } from 'rxjs/operators';
import { UserService } from './user-service';
import { UserDto } from '../dtos/user-dto';

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

    loadDraft(): void {
        this.userService.currentUser$.pipe(take(1)).subscribe(user => {
            if (!user || !user.id) return;
            this.draft = { ...user };
            this.fieldErrors = null; 
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
            return throwError(() => 'Validation failed');
        }

        if (!this.draft || !this.draft.id) {
            return throwError(() => 'No draft to save');
        }

        return this.http.put<UserDto>(`${this.api}/users/${this.draft.id}`, this.draft).pipe(
            tap(updatedUser => {
                this.userService.setCurrentUserById(updatedUser.id);
                this.draft = { ...updatedUser };
                this.fieldErrors = null; 
            }),
            catchError((err: HttpErrorResponse) => {
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
}