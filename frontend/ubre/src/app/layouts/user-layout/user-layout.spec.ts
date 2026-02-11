import { ComponentFixture, TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { provideRouter } from '@angular/router';
import { of, throwError } from 'rxjs';

import { UserLayout } from './user-layout';
import { DriverRegistrationService } from '../../services/driver-registration-service';
import { VehicleType } from '../../enums/vehicle-type';
import { Role } from '../../enums/role';

describe('UserLayout', () => {
  let component: UserLayout;
  let fixture: ComponentFixture<UserLayout>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [UserLayout],
    }).compileComponents();

    fixture = TestBed.createComponent(UserLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});

describe('UserLayout – Driver registration form and submit', () => {
  let component: UserLayout;
  let fixture: ComponentFixture<UserLayout>;
  let mockDriverRegistrationService: jasmine.SpyObj<DriverRegistrationService>;

  const draftSnapshot = {
    id: 0,
    avatarUrl: 'default-avatar.jpg',
    email: '',
    password: '',
    name: '',
    surname: '',
    phone: '',
    address: '',
    vehicle: {
      id: 0,
      model: '',
      type: VehicleType.STANDARD,
      seats: 4,
      babyFriendly: false,
      petFriendly: false,
      plates: '',
    },
  };

  beforeEach(async () => {
    mockDriverRegistrationService = jasmine.createSpyObj<DriverRegistrationService>(
      'DriverRegistrationService',
      [
        'patchDraft',
        'register',
        'getDraftSnapshot',
        'resetDraft',
        'validate',
        'clearFieldError',
        'setAvatarFile',
        'decSeats',
        'incSeats',
      ]
    );
    Object.assign(mockDriverRegistrationService, {
      draft$: of(draftSnapshot),
      avatarSrc$: of(''),
      fieldErrors: null as Record<string, string> | null,
    });
    mockDriverRegistrationService.getDraftSnapshot.and.returnValue(draftSnapshot);
    mockDriverRegistrationService.register.and.returnValue(
      of({
        id: 1,
        role: Role.DRIVER,
        name: '',
        surname: '',
        email: '',
        avatarUrl: '',
        phone: '',
        address: '',
      })
    );

    await TestBed.configureTestingModule({
      imports: [UserLayout, HttpClientTestingModule],
      providers: [
        provideRouter([]),
        { provide: DriverRegistrationService, useValue: mockDriverRegistrationService },
      ],
    }).compileComponents();

    fixture = TestBed.createComponent(UserLayout);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should not call resetDraft when register fails (e.g. validation errors)', () => {
    mockDriverRegistrationService.register.and.returnValue(throwError(() => 'validation failed'));
    component.confirmPasswordDR = 'pass';
    component.onRegisterDriver();
    expect(mockDriverRegistrationService.register).toHaveBeenCalledWith('pass');
    expect(mockDriverRegistrationService.resetDraft).not.toHaveBeenCalled();
  });

  it('should call register when validation would return no errors', () => {
    mockDriverRegistrationService.register.and.returnValue(
      of({ id: 1, role: Role.DRIVER, name: '', surname: '', email: '', avatarUrl: '', phone: '', address: '' })
    );
    component.confirmPasswordDR = 'password123';
    component.onRegisterDriver();
    expect(mockDriverRegistrationService.register).toHaveBeenCalledWith('password123');
  });

  it('should call resetDraft on successful submit', () => {
    mockDriverRegistrationService.register.and.returnValue(
      of({ id: 1, role: Role.DRIVER, name: '', surname: '', email: '', avatarUrl: '', phone: '', address: '' })
    );
    component.confirmPasswordDR = 'password123';
    component.onRegisterDriver();
    expect(mockDriverRegistrationService.resetDraft).toHaveBeenCalled();
  });

  it('should call patchDraft when patchDriverRegistration is called with changes', () => {
    const changes = { email: 'driver@example.com' };
    component.patchDriverRegistration(changes);
    expect(mockDriverRegistrationService.patchDraft).toHaveBeenCalledWith(changes);
  });

  it('should call register with confirmPasswordDR when onRegisterDriver is called', () => {
    component.confirmPasswordDR = 'myPassword123';
    component.onRegisterDriver();
    expect(mockDriverRegistrationService.register).toHaveBeenCalledWith('myPassword123');
  });

  it('should set fieldErrors on service when validateAll is called and validation returns errors', () => {
    const errors = { email: 'Required', name: 'Required' };
    mockDriverRegistrationService.validate.and.returnValue(errors);
    component.confirmPasswordDR = '';
    component.validateAll();
    expect(mockDriverRegistrationService.validate).toHaveBeenCalledWith(draftSnapshot, '');
    expect(mockDriverRegistrationService.fieldErrors).toEqual(errors);
  });

  it('should set fieldErrors to null when validateAll returns no errors', () => {
    mockDriverRegistrationService.validate.and.returnValue({});
    mockDriverRegistrationService.fieldErrors = { email: 'Required' };
    component.validateAll();
    expect(mockDriverRegistrationService.fieldErrors).toBeNull();
  });

  it('should close driver registration and reset confirm password on closeRegisterDriver', () => {
    component.ui.registerDriverOpen = true;
    component.confirmPasswordDR = 'pass';
    component.closeRegisterDriver();
    expect(component.ui.registerDriverOpen).toBe(false);
    expect(component.confirmPasswordDR).toBe('');
    expect(mockDriverRegistrationService.resetDraft).toHaveBeenCalled();
  });

  it('should open driver registration and clear field errors on openRegisterDriver', () => {
    mockDriverRegistrationService.fieldErrors = { email: 'Required' };
    component.openRegisterDriver();
    expect(component.ui.registerDriverOpen).toBe(true);
    expect(mockDriverRegistrationService.fieldErrors).toBeNull();
  });

  it('should call decSeats on service when decDriverSeats is called', () => {
    component.decDriverSeats();
    expect(mockDriverRegistrationService.decSeats).toHaveBeenCalled();
  });

  it('should call incSeats on service when incDriverSeats is called', () => {
    component.incDriverSeats();
    expect(mockDriverRegistrationService.incSeats).toHaveBeenCalled();
  });

  it('should pass vehicle type to service in setVehiceleType', () => {
    component.setVehiceleType(VehicleType.VAN);
    expect(mockDriverRegistrationService.patchDraft).toHaveBeenCalledWith({
      vehicle: { type: VehicleType.VAN },
    });
  });

  it('should toggle babyFriendly via service when toggleBabyFriendly is called', () => {
    mockDriverRegistrationService.getDraftSnapshot.and.returnValue({
      ...draftSnapshot,
      vehicle: { ...draftSnapshot.vehicle, babyFriendly: false },
    });
    component.toggleBabyFriendly();
    expect(mockDriverRegistrationService.patchDraft).toHaveBeenCalledWith({
      vehicle: { babyFriendly: true },
    });
  });

  it('should toggle petFriendly via service when togglePetFriendly is called', () => {
    mockDriverRegistrationService.getDraftSnapshot.and.returnValue({
      ...draftSnapshot,
      vehicle: { ...draftSnapshot.vehicle, petFriendly: true },
    });
    component.togglePetFriendly();
    expect(mockDriverRegistrationService.patchDraft).toHaveBeenCalledWith({
      vehicle: { petFriendly: false },
    });
  });

  it('should call setAvatarFile when onDriverRegistrationAvatarSelected is called with file input', () => {
    const file = new File([''], 'avatar.jpg', { type: 'image/jpeg' });
    const input = document.createElement('input');
    input.type = 'file';
    Object.defineProperty(input, 'files', { value: [file], configurable: true });
    const event = { target: input } as unknown as Event;
    component.onDriverRegistrationAvatarSelected(event);
    expect(mockDriverRegistrationService.setAvatarFile).toHaveBeenCalledWith(file);
  });

  it('should call setAvatarFile with null when input has no files', () => {
    const input = document.createElement('input');
    input.type = 'file';
    Object.defineProperty(input, 'files', { value: [], configurable: true });
    const event = { target: input } as unknown as Event;
    component.onDriverRegistrationAvatarSelected(event);
    expect(mockDriverRegistrationService.setAvatarFile).toHaveBeenCalledWith(null);
  });
});
