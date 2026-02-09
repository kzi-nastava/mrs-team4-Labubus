import { Routes } from '@angular/router';
import { UserLayout } from './layouts/user-layout/user-layout';
import { LoginComponent } from './features/auth/login/login.component';
import { SignupComponent } from './features/auth/signup/signup.component';
import { ForgotPassword } from './features/auth/forgot-password/forgot-password';
import { ResetPassword } from './features/auth/reset-password/reset-password';
import { DriverActivation } from './features/auth/driver-activation/driver-activation';

export const routes: Routes = [
  { path: '', component: UserLayout },
  { path: 'login', component: LoginComponent },
  { path: 'signup', component: SignupComponent },
  { path: 'forgot-password', component: ForgotPassword },
  { path: 'reset-password', component: ResetPassword },
  { path: 'driver-activation', component: DriverActivation },
];
