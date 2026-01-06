import { Component } from '@angular/core';
import { Map } from '../../features/map/map';
import { IconButton } from '../../shared/ui/icon-button/icon-button';
import { SideMenu } from '../../shared/ui/side-menu/side-menu';
import { Toast } from '../../shared/ui/toast/toast';
import { Modal } from '../../shared/ui/modal/modal';
import { ModalContainer } from '../../shared/ui/modal-container/modal-container';
import { StatCard } from '../../shared/ui/stat-card/stat-card';
import { Button } from '../../shared/ui/button/button';
import { ChangeDetectorRef } from '@angular/core';
import { Sheet } from '../../shared/ui/sheet/sheet';
import { FormsModule } from '@angular/forms';
import { ConfettiService } from '../../services/confetti';
import { inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { RideHistory } from '../../shared/ui/ride-history/ride-history';



// bogdan

type UserSettingsVM = {
  role: 'registered-user' | 'driver' | 'admin' | 'guest';
  avatarUrl: string;
  email: string;
  passwordMasked: string;
  name: string;
  surname: string;
  address: string;
  phone: string;
  activeLast24h?: string; // driver
};


// bogdan

// TESTING PURPOSES ONLY - WILL NOT BE AVAILABLE FOR THE USER TO EDIT
type VehicleInformationVM = {
  model: string;
  type: string;
  plates: string;
  seats: number;
  babyFriendly: 'Yes' | 'No';
  petFriendly: 'Yes' | 'No';
};


// bogdan

type DriverRegisterVM = {
  avatarUrl: string;

  email: string;
  password: string;
  confirmPassword: string;
  passwordError: boolean;

  name: string;
  surname: string;
  address: string;
  phone: string;

  vehicleModel: string;
  vehicleType: 'Standard' | 'Luxury' | 'Van';
  plates: string;
  seats: number;
  babyFriendly: boolean;
  petFriendly: boolean;
};

type Waypoint = { id: string; label: string; lat: number; lon: number };

type NominatimItem = {
  place_id: string;
  display_name: string;
  lat: string;
  lon: string;
};


@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [
    Map,
    IconButton,
    SideMenu,
    Toast,
    Modal,
    ModalContainer,
    StatCard,
    Button,
    Sheet,
    FormsModule,
    RideHistory,
  ],
  templateUrl: './user-layout.html',
  styleUrl: './user-layout.css',
})
export class UserLayout {
  constructor(private cdr: ChangeDetectorRef, private http: HttpClient, private router: Router) {}

  user: UserSettingsVM = {
    role: 'admin',
    avatarUrl: 'default-avatar.jpg',
    email: 'john@doe.com',
    passwordMasked: '********',
    name: 'John',
    surname: 'Doe',
    address: '123 Main St, Anytown, USA',
    phone: '+1 234 567 8900',
    activeLast24h: '5h 30m',
  };

  // NOTE: Dummy vehicle data is here ONLY for testing in registered-user layout.
  vehicle: VehicleInformationVM = {
    model: 'Toyota Corolla 2021',
    type: 'Standard',
    plates: 'AB-123-CD',
    seats: 4,
    babyFriendly: 'Yes',
    petFriendly: 'No',
  };

  driverRegister: DriverRegisterVM = {
    avatarUrl: 'default-avatar.jpg',
    email: '',
    password: '',
    confirmPassword: '',
    passwordError: false,

    name: '',
    surname: '',
    address: '',
    phone: '',

    vehicleModel: '',
    vehicleType: 'Standard',
    plates: '',
    seats: 4,
    babyFriendly: false,
    petFriendly: false,
  };

  // MAP AND DESTINATION SELECTION LOGIC
  waypoints: Waypoint[] = [];
  query = '';
  suggestions: NominatimItem[] = [];
  suggestTimer: any = null;
  private suggestReqId = 0;

  destOpen = false;

  openDest() {
    this.destOpen = true;
  }
  closeDest() {
    this.destOpen = false;
  }

  toLatin(s: string) {
    const map: Record<string, string> = {
      А: 'A',
      Б: 'B',
      В: 'V',
      Г: 'G',
      Д: 'D',
      Ђ: 'Đ',
      Е: 'E',
      Ж: 'Ž',
      З: 'Z',
      И: 'I',
      Ј: 'J',
      К: 'K',
      Л: 'L',
      Љ: 'Lj',
      М: 'M',
      Н: 'N',
      Њ: 'Nj',
      О: 'O',
      П: 'P',
      Р: 'R',
      С: 'S',
      Т: 'T',
      Ћ: 'Ć',
      У: 'U',
      Ф: 'F',
      Х: 'H',
      Ц: 'C',
      Ч: 'Č',
      Џ: 'Dž',
      Ш: 'Š',
      а: 'a',
      б: 'b',
      в: 'v',
      г: 'g',
      д: 'd',
      ђ: 'đ',
      е: 'e',
      ж: 'ž',
      з: 'z',
      и: 'i',
      ј: 'j',
      к: 'k',
      л: 'l',
      љ: 'lj',
      м: 'm',
      н: 'n',
      њ: 'nj',
      о: 'o',
      п: 'p',
      р: 'r',
      с: 's',
      т: 't',
      ћ: 'ć',
      у: 'u',
      ф: 'f',
      х: 'h',
      ц: 'c',
      ч: 'č',
      џ: 'dž',
      ш: 'š',
    };

    return s.replace(/[\u0400-\u04FF]/g, (ch) => map[ch] ?? ch);
  }

  onQueryChange() {
    const q = this.query.trim();

    if (this.suggestTimer) clearTimeout(this.suggestTimer);

    if (q.length < 3) {
      this.suggestions = [];
      this.cdr.detectChanges();
      return;
    }

    this.suggestTimer = setTimeout(() => {
      const reqId = ++this.suggestReqId;

      const url = `https://nominatim.openstreetmap.org/search?format=jsonv2&limit=6&q=${encodeURIComponent(
        q + ', Novi Sad, Serbia'
      )}`;

      this.http.get<NominatimItem[]>(url).subscribe((items) => {
        if (reqId !== this.suggestReqId) return;

        this.suggestions = (items ?? []).map((i) => ({
          ...i,
          display_name: this.toLatin(i.display_name),
        }));
        this.cdr.detectChanges();
      });
    }, 250);
  }

  addFromSuggestion(s: NominatimItem) {
    const wp: Waypoint = {
      id: String(s.place_id),
      label: s.display_name,
      lat: Number(s.lat),
      lon: Number(s.lon),
    };
    this.waypoints = [...this.waypoints, wp];

    this.query = '';
    this.suggestions = [];
  }

  addFromMapClick(lat: number, lon: number) {
    if (this.destOpen === false) return;
    const id = crypto.randomUUID();

    const fallback = `${lat.toFixed(5)}, ${lon.toFixed(5)}`;

    this.waypoints = [...this.waypoints, { id, label: fallback, lat, lon }];

    const url = `https://nominatim.openstreetmap.org/reverse?format=json&lat=${lat}&lon=${lon}&accept-language=sr-Latn`;

    this.http.get<any>(url).subscribe({
      next: (res) => {
        const label = res?.display_name ? this.toLatin(res.display_name) : fallback;

        this.waypoints = this.waypoints.map((w) => (w.id === id ? { ...w, label } : w));

        this.cdr.detectChanges();
      },
      error: () => {},
    });
  }

  removeWaypoint(id: string) {
    this.waypoints = this.waypoints.filter((w) => w.id !== id);
  }

  get currentRoute() {
    return this.waypoints.map((wp) => [wp.lat, wp.lon] as [number, number]);
  }

  onDestBack() {
    this.waypoints = [];
    this.suggestions = [];
    this.query = '';
    this.closeDest();
  }

  toggleDest() {
    this.destOpen = !this.destOpen;
    if (this.destOpen) this.cdModalOpen = false;
  }

  onCdProceed() {
    if (this.waypoints.length === 0) {
      this.showToast('No destination', 'Please add at least one destination waypoint.');
      return;
    }
    this.closeDest();
    this.rideOptionsOpen = true;
  }

  editing: UserSettingsVM = { ...this.user };
  hidePassword = true;

  menuOpen = false;

  toastOpen = false;
  toastTitle = 'Ignore this toast';
  toastMessage = 'This is just a demo message for the toast';

  cdModalOpen = true; // Choose a destination modal

  openMenu() {
    this.menuOpen = true;
  }
  closeMenu() {
    this.menuOpen = false;
  }
  openCdModal() {
    this.cdModalOpen = true;
  }
  closeCdModal() {
    this.cdModalOpen = false;
  }

  closeAllSidePanels() {
    this.closeMenu();
    this.closeAccountSettings();
    this.closeChangePassword();
    this.closeVehicleInfo();
    this.closeRegisterDriver();
    this.closeDest();
    this.closeRideHistory();
  }

  handleMenuAction(action: string) {
    if (action === 'logout') {
      this.user = { ...this.user, name: 'Guest', surname: '', phone: '', role: 'guest' };
    }
    if (action === 'account-settings') {
      this.openAccountSettings();
    }
    if (action === 'register-driver') {
      this.openRegisterDriver();
    }
    if (action === 'ride-history') {
      this.openRideHistory();
    }
    if (action === 'login') {
      this.router.navigate(['/login']);
    }
    if (action === 'sign-up') {
      this.router.navigate(['/signup']);
    }
    this.closeMenu();
  }

  showToast(title: string, message: string) {
    this.toastTitle = title;
    this.toastMessage = message;
    this.toastOpen = true;

    setTimeout(() => {
      this.hideToast();
      this.cdr.detectChanges();
    }, 3000);
  }

  hideToast() {
    this.toastOpen = false;
  }

  onCdModalAction() {
    this.cdModalOpen = false;
    this.destOpen = true;
  }

  openChat() {
    // Open chat widget
  }

  // ACCOUNT SETTINGS SHEET LOGIC
  accountSettingsOpen = false;

  openAccountSettings() {
    this.accountSettingsOpen = true;
    this.editing = { ...this.user };
  }
  closeAccountSettings() {
    this.accountSettingsOpen = false;
    this.user = { ...this.editing };
  }

  saveAccountSettings() {
    // Save account settings logic
    this.closeAccountSettings();
    this.showToast('Settings saved', 'Your account settings have been updated.');
  }

  onAccountSettingsBack() {
    this.closeAccountSettings();
    this.menuOpen = true;
  }












































  // CHANGE PASSWORD SHEET LOGIC
  changePasswordOpen = false;

  newPassword = '';
  confirmPassword = '';
  passwordMismatch = false;

  onChangePassword() {
    this.accountSettingsOpen = false;
    this.changePasswordOpen = true;

    this.newPassword = '';
    this.confirmPassword = '';
    this.passwordMismatch = false;
  }

  closeChangePassword() {
    this.changePasswordOpen = false;
    this.passwordMismatch = false;
  }

  onChangePasswordBack() {
    this.closeChangePassword();
    this.accountSettingsOpen = true;
  }

  savePassword() {
    this.passwordMismatch = this.newPassword !== this.confirmPassword;

    if (this.passwordMismatch) return;

    // TODO: API call za promenu lozinke
    this.closeChangePassword();
    this.showToast('Password changed', 'Your password has been updated.');
  }

  // VEHICLE INFORMATION SHEET LOGIC
  vehicleInfoOpen = false;

  openVehicleInfo() {
    this.vehicleInfoOpen = true;
  }
  closeVehicleInfo() {
    this.vehicleInfoOpen = false;
  }

  onVehicleInfoBack() {
    this.closeVehicleInfo();
    this.accountSettingsOpen = true;
  }

  onViewVehicleInfo() {
    this.accountSettingsOpen = false;
    this.openVehicleInfo();
  }

  registerDriverOpen = false;

  openRegisterDriver() {
    this.registerDriverOpen = true;
  }
  closeRegisterDriver() {
    this.registerDriverOpen = false;
  }

  onRegisterDriver() {
    // TODO: API call za registraciju vozača

    this.closeRegisterDriver();
    this.showToast('Driver registered', 'Activation mail has been sent to the driver.');
    this.confetti.fire();
  }

  onRegisterDriverBack() {
    this.closeRegisterDriver();
    this.menuOpen = true;
  }

  validateDriverPassword() {
    this.driverRegister.passwordError = !this.driverRegister.password.trim();
  }

  decDriverSeats() {
    this.driverRegister.seats = Math.max(0, this.driverRegister.seats - 1);
  }
  incDriverSeats() {
    this.driverRegister.seats = Math.min(9, this.driverRegister.seats + 1);
  }

  // EASTER EGG

  private confetti = inject(ConfettiService);
  



























  // Ride HISTORY SHEET LOGIC
  showRideHistory = false;

  onRideHistoryBack() {
    this.showRideHistory = false;
    this.menuOpen = true;
  }

  openRideHistory() {
    this.showRideHistory = true;
    this.menuOpen = false;
  }

  closeRideHistory() {
    this.showRideHistory = false;
  }




































  // RIDE OPTIONS SHEET LOGIC

  rideOptions = {
    rideType: 'Standard' as 'Standard' | 'Luxury' | 'Van',
    babyFriendly: false,
    petFriendly: false,
  };

  rideOptionsOpen = false;
  setRideType(type: 'Standard' | 'Luxury' | 'Van') {
    this.rideOptions.rideType = type;
  }

  toggleRideBaby() {
    this.rideOptions.babyFriendly = !this.rideOptions.babyFriendly;
  }
  toggleRidePet() {
    this.rideOptions.petFriendly = !this.rideOptions.petFriendly;
  }

  closeRideOptions() {
    this.rideOptionsOpen = false;
  }
  openRideOptions() {
    this.rideOptionsOpen = true;
  }
  onRideOptionsBack() {
    this.rideOptionsOpen = false;
    this.destOpen = true;
  }















  onScheduleRide() {
    this.closeRideOptions();
    this.showToast('Ride scheduled', 'Your ride has been scheduled successfully.');
  }



























  onCheckout() {
    this.closeRideOptions();
    this.checkoutModalOpen = true;
  }

  // CHECKOUT MODAL LOGIC
  checkoutModalOpen = false;

  onCheckoutModalBack() {
    this.checkoutModalOpen = false;
    this.rideOptionsOpen = true;
  }

  onConfirmRide() {
    // TODO: API call za potvrdu vožnje
    this.checkoutModalOpen = false;
    this.showToast('Ride confirmed', 'Your ride has been confirmed successfully.');
  }
}
