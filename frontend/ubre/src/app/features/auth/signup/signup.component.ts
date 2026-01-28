import { ChangeDetectorRef, Component } from '@angular/core';
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
import { AuthService } from '../auth-service';
import { UserService } from '../../../services/user-service';
import { DriverRegistrationService } from '../../../services/driver-registration-service';

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
  errorMessage = "Registration error";
  showErrorModal = false;

  constructor(private authService: AuthService, private avatarService: DriverRegistrationService, private cdr: ChangeDetectorRef) {}

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
      
      const avatarUrlValue = this.selectedFile 
        ? `${this.signUpForm.value.email}_${this.selectedFile.name}` 
        : 'default-avatar.jpg';

      const dto: UserRegistrationDto = {
        name: this.signUpForm.value.name!,
        surname: this.signUpForm.value.surname!,
        phone: this.signUpForm.value.phoneNumber!,
        address: this.signUpForm.value.address!,
        email: this.signUpForm.value.email!,
        password: this.signUpForm.value.password!,
        avatarUrl: avatarUrlValue
      };

      this.authService.register(dto).subscribe({
        next: (user) => {
          this.showSuccessModal = true;
          this.cdr.detectChanges();
          if (this.selectedFile) this.avatarService.uploadAvatar(user.id, this.selectedFile).subscribe({
            next: () => {
              this.showSuccessModal = true;
              this.cdr.detectChanges();
            },
            error: (err) => console.error('Avatar upload failed', err)
          });
        },
        error: (err) => {
          this.showErrorModal = true;
          this.errorMessage = err.error;
          this.cdr.detectChanges();
        },
      });
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

      this.avatarService.setAvatarFile(file);
    }
  }
  onCdModalAction() {
    this.showSuccessModal = false;
    this.showErrorModal = false;
  }
}

export interface UserRegistrationDto {
  name: string;
  surname: string;
  phone: string;
  address: string;
  email: string;
  password: string;
  avatarUrl?: string;
}
