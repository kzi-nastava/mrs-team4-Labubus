import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { DriverRegistrationService } from './driver-registration-service';
import { DriverRegistrationDto } from '../dtos/driver-registration-dto';
import { VehicleType } from '../enums/vehicle-type';
import { UserDto } from '../dtos/user-dto';
import { Role } from '../enums/role';

describe('DriverRegistrationService', () => {
  let service: DriverRegistrationService;
  let httpMock: HttpTestingController;

  const validDraft = (overrides: Partial<DriverRegistrationDto> = {}): DriverRegistrationDto => ({
    id: 0,
    avatarUrl: 'default-avatar.jpg',
    email: 'driver@example.com',
    password: 'password123',
    name: 'John',
    surname: 'Doe',
    phone: '+381601234567',
    address: 'Street 1, City',
    vehicle: {
      id: 0,
      model: 'Toyota',
      type: VehicleType.STANDARD,
      seats: 4,
      babyFriendly: false,
      petFriendly: false,
      plates: 'AB-123-CD',
    },
    ...overrides,
  });

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [DriverRegistrationService],
    });
    service = TestBed.inject(DriverRegistrationService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  describe('getDraftSnapshot and initial state', () => {
    it('should return initial draft with empty driver fields and default vehicle', () => {
      const draft = service.getDraftSnapshot();
      expect(draft.email).toBe('');
      expect(draft.password).toBe('');
      expect(draft.name).toBe('');
      expect(draft.surname).toBe('');
      expect(draft.phone).toBe('');
      expect(draft.address).toBe('');
      expect(draft.vehicle.model).toBe('');
      expect(draft.vehicle.type).toBe(VehicleType.STANDARD);
      expect(draft.vehicle.seats).toBe(4);
      expect(draft.vehicle.plates).toBe('');
    });
  });

  describe('patchDraft', () => {
    it('should update driver fields', () => {
      service.patchDraft({ email: 'a@b.com', name: 'Jane' });
      const draft = service.getDraftSnapshot();
      expect(draft.email).toBe('a@b.com');
      expect(draft.name).toBe('Jane');
    });

    it('should update vehicle fields', () => {
      service.patchDraft({ vehicle: { model: 'BMW', plates: 'XY-99', type: VehicleType.LUXURY } });
      const draft = service.getDraftSnapshot();
      expect(draft.vehicle.model).toBe('BMW');
      expect(draft.vehicle.plates).toBe('XY-99');
      expect(draft.vehicle.type).toBe(VehicleType.LUXURY);
    });

    it('should merge partial vehicle without wiping other vehicle fields', () => {
      service.patchDraft({ vehicle: { model: 'Audi' } });
      const draft = service.getDraftSnapshot();
      expect(draft.vehicle.model).toBe('Audi');
      expect(draft.vehicle.plates).toBe('');
      expect(draft.vehicle.seats).toBe(4);
    });
  });

  describe('resetDraft', () => {
    it('should restore initial draft and clear fieldErrors', () => {
      service.patchDraft({ email: 'x@y.com', name: 'Test' });
      service.fieldErrors = { email: 'Required' };
      service.resetDraft();
      const draft = service.getDraftSnapshot();
      expect(draft.email).toBe('');
      expect(draft.name).toBe('');
      expect(service.fieldErrors).toBeNull();
    });
  });

  describe('decSeats / incSeats', () => {
    it('should not go below 2 seats', () => {
      service.patchDraft({ vehicle: { seats: 2 } });
      service.decSeats();
      service.decSeats();
      expect(service.getDraftSnapshot().vehicle.seats).toBe(2);
    });

    it('should not exceed 9 seats', () => {
      service.patchDraft({ vehicle: { seats: 9 } });
      service.incSeats();
      service.incSeats();
      expect(service.getDraftSnapshot().vehicle.seats).toBe(9);
    });

    it('should increment and decrement within range', () => {
      service.patchDraft({ vehicle: { seats: 4 } });
      service.incSeats();
      expect(service.getDraftSnapshot().vehicle.seats).toBe(5);
      service.decSeats();
      expect(service.getDraftSnapshot().vehicle.seats).toBe(4);
    });
  });

  describe('validate', () => {
    it('should return required errors when all fields are empty', () => {
      service.setDraft(validDraft({
        email: '', password: '', name: '', surname: '', phone: '', address: '',
        vehicle: { ...validDraft().vehicle, model: '', plates: '' },
      }));
      const errors = service.validate(service.getDraftSnapshot(), 'password123');
      expect(errors.email).toBe('Required');
      expect(errors.password).toBe('Required');
      expect(errors.name).toBe('Required');
      expect(errors.surname).toBe('Required');
      expect(errors.phone).toBe('Required');
      expect(errors.address).toBe('Required');
      expect(errors.model).toBe('Required');
      expect(errors.plates).toBe('Required');
    });

    it('should return error for invalid email format', () => {
      service.setDraft(validDraft({ email: 'not-an-email' }));
      const errors = service.validate(service.getDraftSnapshot());
      expect(errors.email).toBe('Email format is not valid');
    });

    it('should accept valid email format', () => {
      service.setDraft(validDraft({ email: 'user@domain.co' }));
      const errors = service.validate(service.getDraftSnapshot());
      expect(errors.email).toBeUndefined();
    });

    it('should return error when password is too short', () => {
      service.setDraft(validDraft({ password: '12345' }));
      const errors = service.validate(service.getDraftSnapshot());
      expect(errors.password).toBe('Password too short');
    });

    it('should accept password with length 6 (boundary)', () => {
      service.setDraft(validDraft({ password: '123456' }));
      const errors = service.validate(service.getDraftSnapshot());
      expect(errors.password).toBeUndefined();
    });

    it('should return passwordConfirm error when passwords do not match', () => {
      service.setDraft(validDraft({ password: 'password123' }));
      const errors = service.validate(service.getDraftSnapshot(), 'different');
      expect(errors.passwordConfirm).toBe('Passwords do not match');
    });

    it('should not set passwordConfirm error when passwords match', () => {
      service.setDraft(validDraft({ password: 'password123' }));
      const errors = service.validate(service.getDraftSnapshot(), 'password123');
      expect(errors.passwordConfirm).toBeUndefined();
    });

    it('should return no errors for valid draft (happy path)', () => {
      service.setDraft(validDraft());
      const errors = service.validate(service.getDraftSnapshot(), 'password123');
      expect(Object.keys(errors).length).toBe(0);
    });

    it('should trim values before validating', () => {
      service.setDraft(validDraft({ email: '  user@x.com  ', name: '  Jane  ' }));
      const errors = service.validate(service.getDraftSnapshot(), 'password123');
      expect(errors.email).toBeUndefined();
      expect(errors.name).toBeUndefined();
    });
  });

  describe('register (HTTP)', () => {
    it('should not send HTTP request when validation fails and should set fieldErrors', () => {
      service.setDraft(validDraft({ email: '' }));
      service.register('password123').subscribe({
        error: () => {},
      });
      expect(service.fieldErrors).toBeDefined();
      expect(service.fieldErrors?.email).toBe('Required');
      httpMock.expectNone('http://localhost:8080/api/drivers');
    });

    it('should POST to /api/drivers with exact draft payload on valid submit', () => {
      const draft = validDraft();
      service.setDraft(draft);
      service.register('password123').subscribe((user) => {
        expect(user.id).toBe(1);
        expect(user.role).toBe(Role.DRIVER);
      });

      const req = httpMock.expectOne('http://localhost:8080/api/drivers');
      expect(req.request.method).toBe('POST');
      expect(req.request.body).toEqual(draft);
      req.flush({ id: 1, role: Role.DRIVER, name: draft.name, surname: draft.surname, email: draft.email, avatarUrl: '', phone: draft.phone, address: draft.address });
    });

    it('should clear fieldErrors before registering and reset draft on success', () => {
      service.fieldErrors = { email: 'Required' };
      service.setDraft(validDraft());
      service.register('password123').subscribe();

      const req = httpMock.expectOne('http://localhost:8080/api/drivers');
      req.flush({ id: 1, role: Role.DRIVER, name: 'John', surname: 'Doe', email: 'driver@example.com', avatarUrl: '', phone: '+381601234567', address: 'Street 1' });
      expect(service.fieldErrors).toBeNull();
      expect(service.getDraftSnapshot().email).toBe('');
    });
  });

  describe('clearFieldError', () => {
    it('should clear single field error', () => {
      service.fieldErrors = { email: 'Required', password: 'Too short' };
      service.clearFieldError('email');
      expect(service.fieldErrors?.email).toBeNull();
      expect(service.fieldErrors?.password).toBe('Too short');
    });

    it('should do nothing when fieldErrors is null', () => {
      service.clearFieldError('email');
      expect(service.fieldErrors).toBeNull();
    });
  });
});
