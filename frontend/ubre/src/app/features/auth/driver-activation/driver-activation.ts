import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { Button } from '../../../shared/ui/button/button';
import { Modal } from '../../../shared/ui/modal/modal';

@Component({
  selector: 'app-driver-activation',
  imports: [CommonModule, ReactiveFormsModule, Button, Modal],
  standalone: true,
  templateUrl: './driver-activation.html',
  styleUrl: './driver-activation.css',
})
export class DriverActivation implements OnInit {
  showSuccessModal = false;
  successMessage = '';
  showErrorModal = false;
  errorMessage = '';
  token = '';
  email = '';
  private readonly api = 'http://localhost:8080/api';

  resetForm = new FormGroup(
    {
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new FormControl('', [Validators.required]),
    },
    { validators: this.passwordMatchValidator }
  );

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private http: HttpClient
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.token = params['token'] || '';
      this.email = params['email'] || ''; 
      
      if (!this.token || !this.email) {
        this.showError('Activation token or email not found. Please check the link.');
      }
    });
  }

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password')?.value;
    const confirm = control.get('confirmPassword')?.value;
    return password === confirm ? null : { passwordMismatch: true };
  }

  isInvalid(controlName: string): boolean {
    const control = this.resetForm.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    // if (this.resetForm.valid && this.token && this.email) {
    //   const newPassword = this.resetForm.get('password')?.value;
      
    //   // Call to backend for driver activation and password change
    //   this.http
    //     .post(`${this.api}/drivers/activate`, {
    //       token: this.token,
    //       email: this.email,
    //       newPassword: newPassword,
    //     })
    //     .subscribe({
    //       next: () => {
    //         // Successful activation - redirect to login
    //         this.router.navigate(['/login']);
    //       },
    //       error: (error) => {
    //         // Check if the token has expired or another error occurred
    //         if (error.status === 400 || error.status === 401 || error.status === 403) {
    //           this.showError('Activation token has expired or is invalid. Please contact the administrator.');
    //         } else {
    //           this.showError('An error occurred during activation. Please try again.');
    //         }
    //       },
    //     });
    // } else {
    //   this.resetForm.markAllAsTouched();
    // }
    // pretend to be successful and show message and redirect to login
    this.showSuccess('Account activated successfully. Please login.');
  }

  showError(message: string) {
    this.errorMessage = message;
    this.showErrorModal = true;
  }

  onErrorModalAction() {
    this.showErrorModal = false;
  }

  showSuccess(message: string) {
    this.successMessage = message;
    this.showSuccessModal = true;
  }

  onSuccessModalAction() {
    this.showSuccessModal = false;
    this.router.navigate(['/login']);
  }
}
