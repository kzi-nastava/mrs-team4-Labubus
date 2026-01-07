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
import { OnInit } from '@angular/core';
import { UserService } from '../../services/user-service';
import { UserDto } from '../../dtos/user-dto';
import { UserStatsDto } from '../../dtos/user-stats-dto';
import { VehicleDto } from '../../dtos/vehicle-dto';
import { Role } from '../../enums/role';
import { DriverRegistrationDto } from '../../dtos/driver-registration-dto';
import { VehicleType } from '../../enums/vehicle-type';
import { MapService } from '../../services/map-service';
import { forkJoin } from 'rxjs';

@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [Map,IconButton,SideMenu,Toast,
    Modal,ModalContainer,StatCard,Button,
    Sheet,FormsModule,RideHistory,],
  templateUrl: './user-layout.html',
  styleUrl: './user-layout.css',
})
export class UserLayout implements OnInit {
  constructor(private cdr: ChangeDetectorRef, private http: HttpClient, private router: Router) {}

  private userService = inject(UserService);
  public mapService = inject(MapService);

  Role = Role;
  VehicleType = VehicleType;

  user!: UserDto;
  userStats!: UserStatsDto;
  vehicle!: VehicleDto;
  driverRegistration! : DriverRegistrationDto;

  ngOnInit() {
    this.userService.getCurrentUser().subscribe((user: UserDto) => {
      this.user = user;

      forkJoin({
        stats: this.userService.getUserStats(user.id),
        veh: this.userService.getUserVehicle(user.id),
        reg: this.userService.getDriverRegistration(),
      }).subscribe(({ stats, veh, reg }) => {
        this.userStats = stats;
        this.vehicle = veh;
        this.driverRegistration = reg;
      });
    });
  }




  // map logic
  cdModalOpen = true; // Choose a destination modal
  
  onDestBack() {
    this.mapService.resetDest();
  }
  
  toggleDest() {
    this.mapService.toggleDest();
    if (this.mapService.destOpen) this.cdModalOpen = false;
  }
  
  onCdProceed() {
    if (this.mapService.waypoints.length === 0) {
      this.showToast('No destination', 'Please add at least one destination waypoint.');
      return;
    }
    this.mapService.closeDest();
    this.rideOptionsOpen = true;
  }








  editing: UserDto = { ...this.user };
  hidePassword = true;

  menuOpen = false;

  toastOpen = false;
  toastTitle = 'Ignore this toast';
  toastMessage = 'This is just a demo message for the toast';


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
    this.mapService.closeDest();
    this.closeRideHistory();
  }

  handleMenuAction(action: string) {
    if (action === 'logout') {
      this.user = { ...this.user, name: 'Guest', surname: '', phone: '', role: Role.GUEST };
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
    this.mapService.openDest();
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



















  // DRIVER REGISTRATION SHEET LOGIC

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

  decDriverSeats() {
    this.driverRegistration.vehicle.seats = Math.max(0, this.driverRegistration.vehicle.seats - 1);
  }
  incDriverSeats() {
    this.driverRegistration.vehicle.seats = Math.min(9, this.driverRegistration.vehicle.seats + 1);
  }

  confirmPasswordDR = '';
  passwordError = false;

  validateDriverPassword() {
    this.passwordError = !this.driverRegistration.password?.trim();
  }

  validateConfirmPassword() {
    this.passwordError =
      !this.driverRegistration.password?.trim() ||
      this.driverRegistration.password !== this.confirmPasswordDR;
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
    this.mapService.openDest();
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
