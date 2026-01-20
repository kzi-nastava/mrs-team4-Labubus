import { ChangeDetectorRef, Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormGroup, FormControl, Validators } from '@angular/forms';
import { Button } from '../../../shared/ui/button/button';
import { Modal } from '../../../shared/ui/modal/modal';
import { AuthService } from '../auth-service';

@Component({
  selector: 'app-forgot-password',
  imports: [CommonModule, ReactiveFormsModule, Button, Modal],
  templateUrl: './forgot-password.html',
  styleUrl: './forgot-password.css',
})
export class ForgotPassword {
  showSuccessModal = false;
  showErrorModal = false;
  message = '';

  constructor(private authService: AuthService, private cdr: ChangeDetectorRef){}

  forgotForm = new FormGroup({
    email: new FormControl('', [Validators.required, Validators.email]),
  });

  isInvalid(controlName: string): boolean {
    const control = this.forgotForm.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    if (this.forgotForm.valid) {
      const email = this.forgotForm.value.email!;
      this.authService.forgotPassword(email).subscribe({
      next: (response) => {
        this.message = response;
        this.showSuccessModal = true;
        this.cdr.detectChanges();
      },
      error: (err) => {
        this.message = "Unexpected error";
        this.showSuccessModal = true;
        this.cdr.detectChanges();
      }
    });
    } else {
      this.forgotForm.markAllAsTouched();
    }
  }

  onCdModalAction() {
    this.showSuccessModal = false;
  }
}
