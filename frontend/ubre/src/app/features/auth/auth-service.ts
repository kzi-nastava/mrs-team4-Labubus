import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { LoginResponseDto } from '../../dtos/login-response';
import { LoginDto } from '../../dtos/login-dto';
import { JwtHelperService } from '@auth0/angular-jwt';
import { UserRegistrationDto } from './signup/signup.component';
import { UserDto } from '../../dtos/user-dto';

@Injectable({
  providedIn: 'root',
})
export class AuthService {

  private user$ = new BehaviorSubject<string | null>('');
  userState$ = this.user$.asObservable();

  constructor(private http: HttpClient) {}

  apiHost = 'http://localhost:8080/';
  //apiHost = 'https://ubre.notixdms.com/'; ??

  isLoggedIn(): boolean {
    return !!localStorage.getItem('accessToken');
  }

  getToken() {
    return localStorage.getItem('accessToken');
  }

  getDecodedToken() {
    const token = this.getToken();
    if (!token) return null;
    return new JwtHelperService().decodeToken(token);
  }

  getRole(): string | null {
    return this.getDecodedToken()?.roles || null;
  }

  getId(): number | null {
    return this.getDecodedToken()?.id || null;
  }

  setUser() {
    this.user$.next(this.getRole());
  }

  getEmail(): string | null {
    return this.getDecodedToken()?.sub || null;
  }

  logout() {
    return this.http.get('http://localhost:8080/api/auth/logout', { responseType: 'text' });
  }

  removeToken() {
    return localStorage.removeItem('accessToken');
  }

  login(auth: LoginDto): Observable<LoginResponseDto> {
    return this.http.post<LoginResponseDto>(this.apiHost + 'api/auth/login', auth);
  }

  register(user: UserRegistrationDto): Observable<UserDto> {
    return this.http.post<UserDto>(this.apiHost + 'api/users/register', user);
  }

  forgotPassword(email: string) {
    return this.http.post(this.apiHost + "api/auth/forgot-password", email, { responseType: 'text' });
  }

  resetPassword(dto: ResetPasswordDto): Observable<any> {
    return this.http.post(this.apiHost + "api/auth/reset-password", dto, { responseType: 'text' });
  }

  changeDriverStatus() {
    return this.http.put('http://localhost:8080/api/auth/status', {}, { responseType: 'text' });
  }
}

export interface ResetPasswordDto {
token:string,
newPassword:string
}
