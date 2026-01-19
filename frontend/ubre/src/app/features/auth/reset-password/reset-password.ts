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
import { Button } from '../../../shared/ui/button/button';
import { Modal } from '../../../shared/ui/modal/modal';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService, ResetPasswordDto } from '../auth-service';

@Component({
  selector: 'app-reset-password',
  imports: [CommonModule, ReactiveFormsModule, Button, Modal],
  templateUrl: './reset-password.html',
  styleUrl: './reset-password.css',
})
export class ResetPassword implements OnInit{
  showPassword = false;
  showConfirmPassword = false;
  showSuccessModal = false;
  token!: string;

  constructor(private route: ActivatedRoute, private router: Router, private authService: AuthService) {}

  ngOnInit(): void {
    this.token = this.route.snapshot.queryParamMap.get('token') || '';
    if (!this.token) {
      console.error('No token found in URL!');
      this.router.navigate(['/']);
    }
  }

  resetForm = new FormGroup(
    {
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new FormControl('', [Validators.required]),
    },
    { validators: this.passwordMatchValidator }
  );

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
    if (this.resetForm.valid && this.token) {
      const resetDto: ResetPasswordDto = {
        token: this.token,
        newPassword: this.resetForm.value.password!
      };
      this.authService.resetPassword(resetDto).subscribe({
        next: (res) => {
          console.log('Password successfully reset!');
          this.showSuccessModal = true;
        },
        error: (err) => {
          console.error(err);
        }
      });
    } else {
      this.resetForm.markAllAsTouched();
    }
  }
  onCdModalAction() {
    this.showSuccessModal = false;
  }
}
