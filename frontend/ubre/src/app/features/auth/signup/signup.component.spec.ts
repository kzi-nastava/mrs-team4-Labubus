import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule } from '@angular/forms';
import { SignupComponent, UserRegistrationDto } from './signup.component';
import { AuthService } from '../auth-service';
import { DriverRegistrationService } from '../../../services/driver-registration-service';
import { of, throwError } from 'rxjs';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { ChangeDetectorRef, DebugElement } from '@angular/core';
import { By } from '@angular/platform-browser';
import { UserDto } from '../../../dtos/user-dto';
import { Role } from '../../../enums/role';

describe('SignupComponent', () => {
  let component: SignupComponent;
  let fixture: ComponentFixture<SignupComponent>;
  let authService: jasmine.SpyObj<AuthService>;
  let avatarService: jasmine.SpyObj<DriverRegistrationService>;

  beforeEach(async () => {
    const authServiceSpy = jasmine.createSpyObj('AuthService', ['register']);
    const avatarServiceSpy = jasmine.createSpyObj('DriverRegistrationService', [
      'uploadAvatar',
      'setAvatarFile',
    ]);

    await TestBed.configureTestingModule({
      imports: [SignupComponent, ReactiveFormsModule, HttpClientTestingModule],
      providers: [
        { provide: AuthService, useValue: authServiceSpy },
        { provide: DriverRegistrationService, useValue: avatarServiceSpy },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(SignupComponent);
    component = fixture.componentInstance;
    authService = TestBed.inject(AuthService) as jasmine.SpyObj<AuthService>;
    avatarService = TestBed.inject(
      DriverRegistrationService
    ) as jasmine.SpyObj<DriverRegistrationService>;

    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should initialize form with all required fields', () => {
    expect(component.signUpForm.contains('name')).toBeTruthy();
    expect(component.signUpForm.contains('surname')).toBeTruthy();
    expect(component.signUpForm.contains('phoneNumber')).toBeTruthy();
    expect(component.signUpForm.contains('address')).toBeTruthy();
    expect(component.signUpForm.contains('email')).toBeTruthy();
    expect(component.signUpForm.contains('password')).toBeTruthy();
    expect(component.signUpForm.contains('confirmPassword')).toBeTruthy();
    expect(component.signUpForm.contains('profilePicture')).toBeTruthy();
  });

  it('should have all fields empty initially', () => {
    expect(component.signUpForm.get('name')?.value).toBe('');
    expect(component.signUpForm.get('surname')?.value).toBe('');
    expect(component.signUpForm.get('phoneNumber')?.value).toBe('');
    expect(component.signUpForm.get('address')?.value).toBe('');
    expect(component.signUpForm.get('email')?.value).toBe('');
    expect(component.signUpForm.get('password')?.value).toBe('');
    expect(component.signUpForm.get('confirmPassword')?.value).toBe('');
  });

  it('should have form invalid when empty', () => {
    expect(component.signUpForm.valid).toBeFalsy();
  });

  it('should make name field required', () => {
    const nameControl = component.signUpForm.get('name');
    nameControl?.setValue('');
    expect(nameControl?.hasError('required')).toBeTruthy();
  });

  it('should accept valid name', () => {
    const nameControl = component.signUpForm.get('name');
    nameControl?.setValue('John');
    expect(nameControl?.valid).toBeTruthy();
  });

  it('should make surname field required', () => {
    const surnameControl = component.signUpForm.get('surname');
    surnameControl?.setValue('');
    expect(surnameControl?.hasError('required')).toBeTruthy();
  });

  it('should accept valid surname', () => {
    const surnameControl = component.signUpForm.get('surname');
    surnameControl?.setValue('Doe');
    expect(surnameControl?.valid).toBeTruthy();
  });

  it('should make phone number field required', () => {
    const phoneControl = component.signUpForm.get('phoneNumber');
    phoneControl?.setValue('');
    expect(phoneControl?.hasError('required')).toBeTruthy();
  });

  it('should reject phone number with non-numeric characters', () => {
    const phoneControl = component.signUpForm.get('phoneNumber');
    phoneControl?.setValue('123abc456');
    expect(phoneControl?.hasError('pattern')).toBeTruthy();
  });

  it('should accept valid phone number with only digits', () => {
    const phoneControl = component.signUpForm.get('phoneNumber');
    phoneControl?.setValue('1234567890');
    expect(phoneControl?.valid).toBeTruthy();
  });

  it('should make address field required', () => {
    const addressControl = component.signUpForm.get('address');
    addressControl?.setValue('');
    expect(addressControl?.hasError('required')).toBeTruthy();
  });

  it('should accept valid address', () => {
    const addressControl = component.signUpForm.get('address');
    addressControl?.setValue('123 Main St');
    expect(addressControl?.valid).toBeTruthy();
  });

  it('should make email field required', () => {
    const emailControl = component.signUpForm.get('email');
    emailControl?.setValue('');
    expect(emailControl?.hasError('required')).toBeTruthy();
  });

  it('should reject invalid email format', () => {
    const emailControl = component.signUpForm.get('email');
    emailControl?.setValue('invalid-email');
    expect(emailControl?.hasError('email')).toBeTruthy();
  });

  it('should accept valid email format', () => {
    const emailControl = component.signUpForm.get('email');
    emailControl?.setValue('test@example.com');
    expect(emailControl?.valid).toBeTruthy();
  });


  it('should make password field required', () => {
    const passwordControl = component.signUpForm.get('password');
    passwordControl?.setValue('');
    expect(passwordControl?.hasError('required')).toBeTruthy();
  });

  it('should reject password shorter than 6 characters', () => {
    const passwordControl = component.signUpForm.get('password');
    passwordControl?.setValue('12345');
    expect(passwordControl?.hasError('minlength')).toBeTruthy();
  });

  it('should accept password with 6 or more characters', () => {
    const passwordControl = component.signUpForm.get('password');
    passwordControl?.setValue('123456');
    expect(passwordControl?.valid).toBeTruthy();
  });

  it('should make confirm password field required', () => {
    const confirmPasswordControl = component.signUpForm.get('confirmPassword');
    confirmPasswordControl?.setValue('');
    expect(confirmPasswordControl?.hasError('required')).toBeTruthy();
  });

  it('should show error when passwords do not match', () => {
    component.signUpForm.patchValue({
      password: 'password123',
      confirmPassword: 'different123',
    });
    expect(component.signUpForm.hasError('passwordMismatch')).toBeTruthy();
  });

  it('should not show error when passwords match', () => {
    component.signUpForm.patchValue({
      password: 'password123',
      confirmPassword: 'password123',
    });
    expect(component.signUpForm.hasError('passwordMismatch')).toBeFalsy();
  });

  it('should have invalid form when any required field is empty', () => {
    component.signUpForm.patchValue({
      name: 'John',
      surname: 'Doe',
      phoneNumber: '1234567890',
      address: '',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123',
    });
    expect(component.signUpForm.valid).toBeFalsy();
  });

  it('should have valid form when all fields are correctly filled', () => {
    component.signUpForm.patchValue({
      name: 'John',
      surname: 'Doe',
      phoneNumber: '1234567890',
      address: '123 Main St',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123',
    });
    expect(component.signUpForm.valid).toBeTruthy();
  });

  it('should update form values when user types in fields', () => {
    const nameInput: HTMLInputElement = fixture.debugElement.query(
      By.css('input[formControlName="name"]')
    ).nativeElement;
    const emailInput: HTMLInputElement = fixture.debugElement.query(
      By.css('input[formControlName="email"]')
    ).nativeElement;

    nameInput.value = 'Jane';
    nameInput.dispatchEvent(new Event('input'));

    emailInput.value = 'jane@example.com';
    emailInput.dispatchEvent(new Event('input'));

    fixture.detectChanges();

    expect(component.signUpForm.get('name')?.value).toBe('Jane');
    expect(component.signUpForm.get('email')?.value).toBe('jane@example.com');
  });

  it('should call authService.register with correct data on valid form submit', () => {
    const mockUser: UserDto = {
      id: 1,
      email: 'test@example.com',
      name: 'John',
      surname: 'Doe',
      phone: '1234567890',
      address: '123 Main St',
      role: Role.REGISTERED_USER,
      avatarUrl: 'default-avatar.jpg',
    };

    authService.register.and.returnValue(of(mockUser));

    component.signUpForm.patchValue({
      name: 'John',
      surname: 'Doe',
      phoneNumber: '1234567890',
      address: '123 Main St',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123',
    });

    component.onSubmit();

    expect(authService.register).toHaveBeenCalledWith({
      name: 'John',
      surname: 'Doe',
      phone: '1234567890',
      address: '123 Main St',
      email: 'test@example.com',
      password: 'password123',
      avatarUrl: 'default-avatar.jpg',
    });
  });

  it('should show success modal on successful registration', () => {
    const mockUser: UserDto = {
      id: 1,
      email: 'test@example.com',
      name: 'John',
      surname: 'Doe',
      phone: '1234567890',
      address: '123 Main St',
      role: Role.REGISTERED_USER,
      avatarUrl: 'default-avatar.jpg',
    };

    authService.register.and.returnValue(of(mockUser));

    component.signUpForm.patchValue({
      name: 'John',
      surname: 'Doe',
      phoneNumber: '1234567890',
      address: '123 Main St',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123',
    });

    component.onSubmit();

    expect(component.showSuccessModal).toBeTruthy();
  });

  it('should show error modal on registration failure', () => {
    const errorResponse = { error: 'Email already exists' };
    authService.register.and.returnValue(throwError(() => errorResponse));

    component.signUpForm.patchValue({
      name: 'John',
      surname: 'Doe',
      phoneNumber: '1234567890',
      address: '123 Main St',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123',
    });

    component.onSubmit();

    expect(component.showErrorModal).toBeTruthy();
    expect(component.errorMessage).toBe('Email already exists');
  });

  it('should mark all fields as touched when submitting invalid form', () => {
    component.signUpForm.patchValue({
      name: '',
      surname: '',
      phoneNumber: '',
      address: '',
      email: '',
      password: '',
      confirmPassword: '',
    });

    spyOn(component.signUpForm, 'markAllAsTouched');

    component.onSubmit();

    expect(component.signUpForm.markAllAsTouched).toHaveBeenCalled();
    expect(authService.register).not.toHaveBeenCalled();
  });


  it('should generate correct avatarUrl with selected file', () => {
    const file = new File(['dummy content'], 'my-photo.jpg', {
      type: 'image/jpeg',
    });
    component.selectedFile = file;

    const mockUser: UserDto = {
      id: 1,
      email: 'test@example.com',
      name: 'John',
      surname: 'Doe',
      phone: '1234567890',
      address: '123 Main St',
      role: Role.REGISTERED_USER,
      avatarUrl: 'test@example.com_my-photo.jpg',
    };

    authService.register.and.returnValue(of(mockUser));
    avatarService.uploadAvatar.and.returnValue(of(void 0));

    component.signUpForm.patchValue({
      name: 'John',
      surname: 'Doe',
      phoneNumber: '1234567890',
      address: '123 Main St',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123',
    });

    component.onSubmit();

    expect(authService.register).toHaveBeenCalledWith(
      jasmine.objectContaining({
        avatarUrl: 'test@example.com_my-photo.jpg',
      })
    );
  });

  it('should use default avatar when no file selected', () => {
    component.selectedFile = null;

    const mockUser: UserDto = {
      id: 1,
      email: 'test@example.com',
      name: 'John',
      surname: 'Doe',
      phone: '1234567890',
      address: '123 Main St',
      role: Role.REGISTERED_USER,
      avatarUrl: 'default-avatar.jpg',
    };

    authService.register.and.returnValue(of(mockUser));

    component.signUpForm.patchValue({
      name: 'John',
      surname: 'Doe',
      phoneNumber: '1234567890',
      address: '123 Main St',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123',
    });

    component.onSubmit();

    expect(authService.register).toHaveBeenCalledWith(
      jasmine.objectContaining({
        avatarUrl: 'default-avatar.jpg',
      })
    );
  });

  it('should upload avatar after successful registration when file is selected', () => {
    const file = new File(['dummy'], 'avatar.jpg', { type: 'image/jpeg' });
    component.selectedFile = file;

    const mockUser: UserDto = {
      id: 123,
      email: 'test@example.com',
      name: 'John',
      surname: 'Doe',
      phone: '1234567890',
      address: '123 Main St',
      role: Role.REGISTERED_USER,
      avatarUrl: 'test@example.com_avatar.jpg',
    };

    authService.register.and.returnValue(of(mockUser));
    avatarService.uploadAvatar.and.returnValue(of(void 0));

    component.signUpForm.patchValue({
      name: 'John',
      surname: 'Doe',
      phoneNumber: '1234567890',
      address: '123 Main St',
      email: 'test@example.com',
      password: 'password123',
      confirmPassword: 'password123',
    });

    component.onSubmit();

    expect(avatarService.uploadAvatar).toHaveBeenCalledWith(123, file);
  });


  it('should return true from isInvalid when field is invalid and touched', () => {
    const nameControl = component.signUpForm.get('name');
    nameControl?.setValue('');
    nameControl?.markAsTouched();

    expect(component.isInvalid('name')).toBeTruthy();
  });

  it('should return false from isInvalid when field is valid', () => {
    const nameControl = component.signUpForm.get('name');
    nameControl?.setValue('John');

    expect(component.isInvalid('name')).toBeFalsy();
  });

  it('should return false from isInvalid when field is invalid but not touched', () => {
    const nameControl = component.signUpForm.get('name');
    nameControl?.setValue('');

    expect(component.isInvalid('name')).toBeFalsy();
  });


  it('should close success modal when onCdModalAction is called', () => {
    component.showSuccessModal = true;

    component.onCdModalAction();

    expect(component.showSuccessModal).toBeFalsy();
  });

  it('should close error modal when onCdModalAction is called', () => {
    component.showErrorModal = true;

    component.onCdModalAction();

    expect(component.showErrorModal).toBeFalsy();
  });


  it('should toggle password visibility', () => {
    expect(component.showPassword).toBeFalsy();

    component.showPassword = true;

    expect(component.showPassword).toBeTruthy();
  });

  it('should toggle confirm password visibility', () => {
    expect(component.showConfirmPassword).toBeFalsy();

    component.showConfirmPassword = true;

    expect(component.showConfirmPassword).toBeTruthy();
  });
});