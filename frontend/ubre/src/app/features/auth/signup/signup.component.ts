import { Component } from '@angular/core';
import {
  AbstractControl,
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from '@angular/forms';
import { Button } from '../../../shared/ui/button/button';
import { CommonModule } from '@angular/common';
import { Modal } from '../../../shared/ui/modal/modal';

@Component({
  selector: 'app-signup',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, Button, Modal],
  templateUrl: './signup.component.html',
  styleUrl: './signup.component.css',
})
export class SignupComponent {
  showSuccessModal = false;

  showPassword = false;
  showConfirmPassword = false;
  fileName = '';
  selectedFile: File | null = null;

  signUpForm = new FormGroup(
    {
      name: new FormControl('', [Validators.required]),
      surname: new FormControl('', [Validators.required]),
      phoneNumber: new FormControl('', [Validators.required, Validators.pattern('^[0-9]*$')]),
      address: new FormControl('', [Validators.required]),
      email: new FormControl('', [Validators.required, Validators.email]),
      profilePicture: new FormControl(''),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      confirmPassword: new FormControl('', [Validators.required]),
    },
    { validators: this.passwordMatchValidator }
  );

  passwordMatchValidator(control: AbstractControl): ValidationErrors | null {
    const password = control.get('password');
    const confirmPassword = control.get('confirmPassword');

    if (password && confirmPassword && password.value !== confirmPassword.value) {
      return { passwordMismatch: true };
    }
    return null;
  }

  isInvalid(controlName: string): boolean {
    const control = this.signUpForm.get(controlName);
    return !!(control && control.invalid && (control.dirty || control.touched));
  }

  onSubmit() {
    if (this.signUpForm.valid) {
      console.log('Form Data:', this.signUpForm.value);
      this.showSuccessModal = true;
    } else {
      this.signUpForm.markAllAsTouched();
    }
  }

  onFileSelected(event: any) {
    const file: File = event.target.files[0];

    if (file) {
      this.fileName = file.name;
      this.selectedFile = file;
      this.signUpForm.patchValue({
        profilePicture: file.name,
      });

      console.log('Selected file:', file);
    }
  }
  onCdModalAction() {
    this.showSuccessModal = false;
  }
}
