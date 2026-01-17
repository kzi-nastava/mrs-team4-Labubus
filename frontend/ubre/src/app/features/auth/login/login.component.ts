import { ChangeDetectorRef, Component } from '@angular/core';
import { Button } from '../../../shared/ui/button/button';
import {
  FormControl,
  FormGroup,
  Validators,
  ReactiveFormsModule,
  FormBuilder,
} from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth-service';
import { LoginResponseDto } from '../../../dtos/login-response';
import { LoginDto } from '../../../dtos/login-dto';
import { Modal } from '../../../shared/ui/modal/modal';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [Button, ReactiveFormsModule, CommonModule, Modal],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  loginForm!: FormGroup;

  showPassword = false;
  showModal: any;
  constructor(
    private fb: FormBuilder,
    private router: Router,
    private authService: AuthService,
    private cdr: ChangeDetectorRef
  ) {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      rememberMe: new FormControl(true),
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      const formValue: LoginDto = {
        email: this.loginForm.value.email!,
        password: this.loginForm.value.password!,
      };
      this.authService.login(formValue).subscribe({
        next: (res: LoginResponseDto) => {
          localStorage.setItem('accessToken', res.accessToken);

          this.authService.setUser();

          this.router.navigate(['/']);
        },
        error: (err) => {
          console.error('Login failed', err);
          this.showModal = true;
          this.cdr.detectChanges();
        },
      });
    } else {
      console.log('Form is invalid:', this.loginForm.value);
      this.loginForm.markAllAsTouched();
    }
  }

  togglePassword() {
    this.showPassword = !this.showPassword;
  }

  continueAsGuest() {
    this.router.navigate(['/']);
  }
  onCdModalAction() {
    this.showModal = false;
  }
}
