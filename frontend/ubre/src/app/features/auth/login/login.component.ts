import { Component } from '@angular/core';
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

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [Button, ReactiveFormsModule, CommonModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css',
})
export class LoginComponent {
  loginForm!: FormGroup;

  showPassword = false;
  constructor(private fb: FormBuilder, private router: Router) {
    this.loginForm = new FormGroup({
      email: new FormControl('', [Validators.required, Validators.email]),
      password: new FormControl('', [Validators.required, Validators.minLength(6)]),
      rememberMe: new FormControl(true),
    });
  }

  onSubmit() {
    if (this.loginForm.valid) {
      console.log('Form values:', this.loginForm.value);
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
}
