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

    loadDraft(): void {
        this.userService.currentUser$.pipe(take(1)).subscribe(user => {
            if (!user || !user.id) return;
            this.draft = { ...user };
        });
    }

    patchDraft(updates: Partial<UserDto>): void {
        if (!this.draft) return;

        this.draft = {
            ...this.draft,
            ...updates
        };
    }


    saveDraft(): Observable<UserDto> {
        if (!this.draft || !this.draft.id) {
            return throwError(() => 'No draft to save');
        }

        return this.http.put<UserDto>(`${this.api}/users/${this.draft.id}`, this.draft).pipe(
            tap(updatedUser => {
                this.userService.setCurrentUserById(updatedUser.id);
                this.draft = { ...updatedUser };
            }),
            catchError((err: HttpErrorResponse) => {
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
    }

    /**
     * Validira draft i vraća greške po poljima
     */
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
}