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
import { VehicleType } from '../../enums/vehicle-type';
import { MapService } from '../../services/map-service';
import { Observable, Subscription, forkJoin, of, take } from 'rxjs';
import { DriverRegistrationService } from '../../services/driver-registration-service';
import { ProfileChangeService } from '../../services/profile-change-service';
import { ProfileChangeDto } from '../../dtos/profile-change-dto';
import { ProfileChangeCard } from '../../shared/ui/profile-change-card/profile-change-card';
import { AsyncPipe } from '@angular/common';
import { AccountSettingsService } from '../../services/account-settings-service';
import { AuthService } from '../../features/auth/auth-service';
import { DriverRegistrationDto } from '../../dtos/driver-registration-dto';
import { WebSocketService } from '../../services/websocket-service';
import { StatItemDto } from '../../dtos/stat-item-dto';
import { ReviewService } from '../../services/review-service';
import { ReviewModal } from '../../shared/ui/review-modal/review-modal';

@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [Map,IconButton,SideMenu,Toast,
    Modal,ModalContainer,StatCard,Button,
    Sheet,FormsModule,RideHistory,ProfileChangeCard,
    AsyncPipe,ReviewModal],
    templateUrl: './user-layout.html',
    styleUrl: './user-layout.css',
  })
  export class UserLayout implements OnInit {
    constructor(private cdr: ChangeDetectorRef, private http: HttpClient, private router: Router) {}
    
    public userService = inject(UserService);
    private authService = inject(AuthService);
    private reviewService : ReviewService = inject(ReviewService)
    public driverRegistrationService = inject(DriverRegistrationService);
    public mapService = inject(MapService);
    private confetti = inject(ConfettiService);
    public profileChangeService = inject(ProfileChangeService); // profile changes, and password change (todo later)
    public accountSettingsService = inject(AccountSettingsService);
    public webSocketService = inject(WebSocketService);

  Role = Role;
  VehicleType = VehicleType;

  user!: UserDto;
  userStats!: UserStatsDto;
  vehicle!: VehicleDto;

  private websocketUserId: number | null = null;
  private profileChangeSubscription?: Subscription;


  ngOnInit() {
    const userId = this.authService.getId();

    if (userId !== null) {
      this.userService.setCurrentUserById(userId);
    } else {
      this.user = {
        id: 0,
        name: '',
        surname: '',
        phone: '',
        email: '',
        address: '',
        role: Role.GUEST,
        avatarUrl: '',
      };
    }
    
    this.userService.currentUser$.subscribe(user => {
      if (!user) return;
      
      this.user = user;
      
      forkJoin({
        stats: this.userService.getUserStats(user.id),         // remove later
      }).subscribe(({ stats }) => {
        this.userStats = stats;
      });

      if (!user || user.id === 0) {
        this.profileChangeSubscription?.unsubscribe();
        this.webSocketService.disconnect();
        this.websocketUserId = null;
        return;
      }

      if (this.websocketUserId === user.id) {
        return;
      }

      this.websocketUserId = user.id;
      this.profileChangeSubscription?.unsubscribe();
      this.webSocketService.connect();
      this.profileChangeSubscription = this.webSocketService
        .profileChangeNotifications(user.id)
        .subscribe({
          next: (notification) => {
            if (notification.status === 'APPROVED' && notification.user) {
              this.userService.setCurrentUserById(notification.user.id);
              this.showToast('Profile change approved', 'Your profile change request has been approved.');
              this.cdr.detectChanges();
              this.userService.loadAvatar(notification.user.id);
              return;
            }

            if (notification.status === 'REJECTED') {
              this.showToast('Profile change rejected', 'Your profile change request has been rejected.');
            }
          },
          error: () => {
            this.showToast('Connection error', 'Could not receive profile change updates.');
          },
        });
    });

    this.ui.reviewModalOpen = this.reviewService.showReviewModal$;
  }
  
  ngOnDestroy() {
    this.profileChangeSubscription?.unsubscribe();
    this.webSocketService.disconnect();
  }
  ui = {
    menuOpen: false,
    cdModalOpen: true,
    accountSettingsOpen: false,
    changePasswordOpen: false,
    vehicleInfoOpen: false,
    registerDriverOpen: false,
    rideHistoryOpen: false,
    rideOptionsOpen: false,
    checkoutModalOpen: false,
    toastOpen: false,
    profileChangesOpen: false,
    reviewModalOpen: of(false)
  };


  
  
  
  
  
  
  
  onDestBack() {
    this.mapService.resetDest();
  }

  toggleDest() {
    this.mapService.toggleDest();
    if (this.mapService.destOpen) this.ui.cdModalOpen = false;
  }

  onCdProceed() {
    if (this.mapService.waypoints.length === 0) {
      this.showToast('No destination', 'Please add at least one destination waypoint.');
      return;
    }
    this.mapService.closeDest();
    this.ui.rideOptionsOpen = true;
  }

  
  
  
  
  
  
  
  
  
  
  openMenu() {
    this.ui.menuOpen = true;
  }
  closeMenu() {
    this.ui.menuOpen = false;
  }
  openCdModal() {
    this.ui.cdModalOpen = true;
  }
  closeCdModal() {
    this.ui.cdModalOpen = false;
  }

  closeAllSidePanels() {
    this.closeMenu();
    this.closeAccountSettings();
    this.closeChangePassword();
    this.closeVehicleInfo();
    this.closeRegisterDriver();
    this.mapService.closeDest();
    this.closeRideHistory();
    this.closeProfileChanges();
  }

  handleMenuAction(action: string) {
    if (action === 'logout') {
      this.user = { ...this.user, name: 'Guest', surname: '', phone: '', role: Role.GUEST };
      this.authService.logout();
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
    if (action === 'profile-changes') {
      this.openProfileChanges();
    }
    this.closeMenu();
  }
  
  private toastTimer: any = null;
  public toastTitle: string = '';
  public toastMessage: string = '';
  showToast(title: string, message: string) {
    if (this.toastTimer) {
      clearTimeout(this.toastTimer);
      this.toastTimer = null;
    }

    this.ui.toastOpen = false;
    this.cdr.detectChanges();

    setTimeout(() => {
      this.toastTitle = title;
      this.toastMessage = message;

      this.ui.toastOpen = true;
      this.cdr.detectChanges();

      this.toastTimer = setTimeout(() => {
        this.ui.toastOpen = false;
        this.cdr.detectChanges();
        this.toastTimer = null;
      }, 3000);
    }, 0);
  }


  
  hideToast() {
    this.ui.toastOpen = false;
  }

  onCdModalAction() {
    this.ui.cdModalOpen = false;
    this.mapService.openDest();
  }

  openChat() {
    // Open chat widget
  }


  focusNext(el: HTMLElement) {
    el.focus();
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  hidePassword = true;
  
  // ACCOUNT SETTINGS SHEET LOGIC
  openAccountSettings() {
    this.accountSettingsService.loadDraft();
    this.ui.accountSettingsOpen = true;
  }
  
  closeAccountSettings() {
    this.ui.accountSettingsOpen = false;
    this.accountSettingsService.clearDraft();
  }

  saveAccountSettings() {
    this.userService.currentUser$.pipe(take(1)).subscribe(user => {
      if (user.role === Role.DRIVER) {
        this.accountSettingsService.requestProfileChange().subscribe({
          next: () =>
            this.showToast('Profile change requested', 'Your profile change request has been sent.')
        });
      } else {
        this.accountSettingsService.save().subscribe({
          next: () =>
            this.showToast('Settings saved', 'Your account settings have been updated.'),
          error: (err) => {
            if (typeof err === 'string') {
              this.showToast('Error saving settings', err);
            }
          }
        });
      }
    });
  }
  
  
  onAccountSettingsBack() {
    this.closeAccountSettings();
    this.ui.menuOpen = true;
  }

  onAccountSettingsAvatarSelected(e: Event) {
    const input = e.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.accountSettingsService.setAvatarFile(file);
    input.value = ''; 
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  


  
  // CHANGE PASSWORD SHEET LOGIC
  newPassword = '';
  confirmPassword = '';
  passwordMismatch = false;
  
  onChangePassword() {
    this.ui.accountSettingsOpen = false;
    this.ui.changePasswordOpen = true;
    
    this.newPassword = '';
    this.confirmPassword = '';
    this.passwordMismatch = false;
  }
  
  closeChangePassword() {
    this.ui.changePasswordOpen = false;
    this.passwordMismatch = false;
  }
  
  onChangePasswordBack() {
    this.closeChangePassword();
    this.ui.accountSettingsOpen = true;
  }
  
  savePassword() {
    this.passwordMismatch = this.newPassword !== this.confirmPassword;
    
    if (this.passwordMismatch) return;
    
    // TODO: API call za promenu lozinke
    this.closeChangePassword();
    this.showToast('Password changed', 'Your password has been updated.');
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  // VEHICLE INFORMATION SHEET LOGIC
  
  openVehicleInfo() {
    this.ui.vehicleInfoOpen = true;
  }
  closeVehicleInfo() {
    this.ui.vehicleInfoOpen = false;
  }
  
  onVehicleInfoBack() {
    this.closeVehicleInfo();
    this.ui.accountSettingsOpen = true;
  }
  
  onViewVehicleInfo() {
    this.ui.accountSettingsOpen = false;
    this.openVehicleInfo();
  }

  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  
  confirmPasswordDR = '';

  // DRIVER REGISTRATION SHEET LOGIC
  openRegisterDriver() {
    this.ui.registerDriverOpen = true;
    this.driverRegistrationService.fieldErrors = null;
  }

  closeRegisterDriver() {
    this.ui.registerDriverOpen = false;
    this.driverRegistrationService.resetDraft();
    this.confirmPasswordDR = '';
  }

  patchDriverRegistration(changes : any) {
    this.driverRegistrationService.patchDraft(changes);
  }

  setVehiceleType(type: VehicleType) {
    this.driverRegistrationService.patchDraft({ vehicle: { type } });
  }

  toggleBabyFriendly() {
    const curr = this.driverRegistrationService.getDraftSnapshot().vehicle.babyFriendly;
    this.driverRegistrationService.patchDraft({ vehicle: { babyFriendly: !curr } });
  }

  togglePetFriendly() {
    const curr = this.driverRegistrationService.getDraftSnapshot().vehicle.petFriendly;
    this.driverRegistrationService.patchDraft({ vehicle: { petFriendly: !curr } });
  }

  onRegisterDriver() {
    this.driverRegistrationService.register(this.confirmPasswordDR).subscribe({
      next: () => {
        this.closeRegisterDriver();
        this.showToast('Driver registered', 'Activation mail has been sent to the driver.');
        this.confetti.fire();
        this.confirmPasswordDR = '';
      },
      error: (e) => {
        if (typeof e === 'string') {
          this.showToast('Registration error', e);
        }
      }
    });
  }

  onRegisterDriverBack() {
    this.closeRegisterDriver();
    this.ui.menuOpen = true;
  }

  decDriverSeats() {
    this.driverRegistrationService.decSeats();
  }

  incDriverSeats() {
    this.driverRegistrationService.incSeats();
  }

  validateAll() {
    const errors = this.driverRegistrationService.validate(
      this.driverRegistrationService.getDraftSnapshot(),
      this.confirmPasswordDR
    );
    this.driverRegistrationService.fieldErrors = Object.keys(errors).length > 0 ? errors : null;
  }

  onDriverRegistrationAvatarSelected(e: Event) {
    const input = e.target as HTMLInputElement;
    const file = input.files?.[0] ?? null;
    this.driverRegistrationService.setAvatarFile(file);
    input.value = '';
  }






  



























  // Ride HISTORY SHEET LOGIC
  showRideHistory = false;

  onRideHistoryBack() {
    this.showRideHistory = false;
    this.ui.menuOpen = true;
  }

  openRideHistory() {
    this.showRideHistory = true;
    this.ui.menuOpen = false;
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
    this.ui.rideOptionsOpen = false;
  }
  openRideOptions() {
    this.ui.rideOptionsOpen = true;
  }
  onRideOptionsBack() {
    this.ui.rideOptionsOpen = false;
    this.mapService.openDest();
  }

  onScheduleRide() {
    this.closeRideOptions();
    // TODO: API call za zakazivanje vožnje
    this.showToast('Ride scheduled', 'Your ride has been scheduled successfully.');
  }

  onCheckout() {
    this.closeRideOptions();
    this.ui.checkoutModalOpen = true;
  }

  onCheckoutModalBack() {
    this.ui.checkoutModalOpen = false;
    this.ui.rideOptionsOpen = true;
  }

  onConfirmRide() {
    // TODO: API call za potvrdu vožnje
    this.ui.checkoutModalOpen = false;
    this.showToast('Ride confirmed', 'Your ride has been confirmed successfully.');
  }




























  // PROFILE CHANGES SHEET LOGIC
  loadProfileChanges() {
    this.profileChangeService.loadPendingProfileChanges();
  }

  openProfileChanges() {
    this.ui.menuOpen = false;
    this.ui.profileChangesOpen = true;
    this.loadProfileChanges();
  }

  closeProfileChanges() {
    this.ui.profileChangesOpen = false;
  }

  onProfileChangesBack() {
    this.closeProfileChanges();
    this.ui.menuOpen = true;
  }

  approveProfileChange(id: number) {
    this.profileChangeService.approve(id);
  }

  rejectProfileChange(id: number) {
    this.profileChangeService.reject(id);
  }
}
