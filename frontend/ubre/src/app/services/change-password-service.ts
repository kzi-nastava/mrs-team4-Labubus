// user sends new password to server, and thats it 
import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable } from 'rxjs';
import { PasswordChangeDto } from '../dtos/password-change-dto';
import { UserService } from './user-service';

type FieldErrors = Partial<Record<
    'newPassword' | 'confirmNewPassword' | 'passwordMismatch',
    string
>>;

@Injectable({ providedIn: 'root' })
export class ChangePasswordService {
    private readonly http = inject(HttpClient);
    private readonly userService = inject(UserService);
    private readonly api = 'http://localhost:8080/api';

    // errors, such as too short, required or mismatch
    fieldErrors: FieldErrors | null = null;
    newPassword: string = '';
    confirmNewPassword: string = '';

    changePassword(): Observable<void> {
        const changePasswordDto: PasswordChangeDto = new PasswordChangeDto(this.userService.getCurrentUserId(), this.newPassword);
        return this.http.put<void>(`${this.api}/users/change-password`, changePasswordDto);
    }

    getCurrentUserId(): number {
        return this.userService.getCurrentUserId();
    }

    validate(): FieldErrors {
        const errors: FieldErrors = {};
        if (this.newPassword.length < 6) {
            errors.newPassword = 'Password must be at least 6 characters long';
        }
        if (this.confirmNewPassword !== this.newPassword) {
            errors.confirmNewPassword = 'Passwords do not match';
        }
        return errors;
    }

    clearError(field: keyof FieldErrors): void {
        if (!this.fieldErrors) return;
        this.fieldErrors = { ...this.fieldErrors, [field]: null };
    }

    clearAllErrors(): void {
        this.fieldErrors = null;
        this.newPassword = '';
        this.confirmNewPassword = '';
    }

    setNewPassword(newPassword: string): void {
        this.newPassword = newPassword;
        this.clearError('newPassword');
    }

    setConfirmNewPassword(confirmNewPassword: string): void {
        this.confirmNewPassword = confirmNewPassword;
        this.clearError('confirmNewPassword');
    }

    setPasswordMismatch(passwordMismatch: boolean): void {
        if (passwordMismatch) {
            this.fieldErrors = { ...this.fieldErrors, passwordMismatch: 'Passwords do not match' };
        } else {
            this.clearError('passwordMismatch');
        }
    }
}
