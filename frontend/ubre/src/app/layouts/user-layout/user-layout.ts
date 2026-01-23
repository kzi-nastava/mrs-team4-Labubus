import { Component } from '@angular/core';
import { Map } from '../../services/ride-planning/map/map';
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
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Router } from '@angular/router';
import { RideHistory } from '../../shared/ui/ride-history/ride-history';
import { OnInit } from '@angular/core';
import { UserService } from '../../services/user-service';
import { UserDto } from '../../dtos/user-dto';
import { UserStatsDto } from '../../dtos/user-stats-dto';
import { VehicleDto } from '../../dtos/vehicle-dto';
import { Role } from '../../enums/role';
import { VehicleType } from '../../enums/vehicle-type';
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
import { ChangePasswordService } from '../../services/change-password-service';
import { UserStatsService } from '../../services/user-stats-service';
import { RidePlanningStore } from '../../services/ride-planning/ride-planning-store';
import { ScheduleTimer } from '../../shared/ui/schedule-timer/schedule-timer';
import { InvitePassengers } from '../../shared/ui/invite-passengers/invite-passengers';
import { RideOptions } from '../../shared/ui/ride-options/ride-options';
import { RideOptionsDto } from '../../dtos/ride-options-dto';
import { NotificationType } from '../../enums/notification-type';

@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [Map,IconButton,SideMenu,Toast,
    Modal,ModalContainer,StatCard,Button,
    Sheet,FormsModule,RideHistory,ProfileChangeCard,
    AsyncPipe,ReviewModal,ScheduleTimer,InvitePassengers,RideOptions],
    templateUrl: './user-layout.html',
    styleUrl: './user-layout.css',
  })
  export class UserLayout implements OnInit {
    constructor(private cdr: ChangeDetectorRef, private http: HttpClient, private router: Router) {}
    
    public userService = inject(UserService);
    private authService = inject(AuthService);
    private reviewService : ReviewService = inject(ReviewService)
    public driverRegistrationService = inject(DriverRegistrationService);
    public ridePlanningStore = inject(RidePlanningStore);
    private confetti = inject(ConfettiService);
    public profileChangeService = inject(ProfileChangeService); 
    public accountSettingsService = inject(AccountSettingsService);
    public webSocketService = inject(WebSocketService);
    public changePasswordService = inject(ChangePasswordService);
    public userStatsService = inject(UserStatsService);

  Role = Role;
  VehicleType = VehicleType;

  userStats!: UserStatsDto;

  private websocketUserId: number | null = null;
  private profileChangeSubscription?: Subscription;
  private rideAssignmentSubscription?: Subscription;
  private rideReminderSubscription?: Subscription;


  ngOnInit() {
    const userId = this.authService.getId();

    if (userId !== null && userId !== 0) {
      this.userService.setCurrentUserById(userId);
    } 
      
    if (userId === 0 || userId === null) {
      this.profileChangeSubscription?.unsubscribe();
      this.webSocketService.disconnect();
      this.websocketUserId = null;
      return;
    }

    if (this.websocketUserId === userId) {
      return;
    }

    this.websocketUserId = userId;
    this.profileChangeSubscription?.unsubscribe();
    this.webSocketService.connect();
    this.profileChangeSubscription = this.webSocketService
      .profileChangeNotifications(userId)
      .subscribe({
        next: (notification) => {
          if (notification.status === NotificationType.PROFILE_CHANGE_APPROVED && notification.user) {
            this.userService.setCurrentUserById(notification.user.id);
            this.showToast('Profile change approved', 'Your profile change request has been approved.');
            this.cdr.detectChanges();
            this.userService.loadAvatar(notification.user.id);
            return;
          }

          if (notification.status === NotificationType.PROFILE_CHANGE_REJECTED && notification.user) {
            this.showToast('Profile change rejected', 'Your profile change request has been rejected.');
          }
        },
        error: () => {
          this.showToast('Connection error', 'Could not receive profile change updates.');
        },
      });
    
    this.rideAssignmentSubscription = this.webSocketService
      .rideAssignmentNotifications(userId)
      .subscribe({
        next: (notification) => {
          if (notification.status === NotificationType.RIDE_ASSIGNED && notification.ride) {
            this.showToast('New ride assigned', 'Check your notifications for more details.');
          }
        },
      });

    this.ui.reviewModalOpen = this.reviewService.showReviewModal$;
    
    this.rideReminderSubscription = this.webSocketService
      .rideReminderNotifications(userId)
      .subscribe({
        next: (notification) => {
          if (notification.status === NotificationType.RIDE_REMINDER && notification.time) {
            this.showToast('Ride reminder', 'You have a ride scheduled at ' + notification.time + '.');
          }
        },
      });
  }


  ngOnDestroy() {
    this.profileChangeSubscription?.unsubscribe();
    this.rideAssignmentSubscription?.unsubscribe();
    this.rideReminderSubscription?.unsubscribe();
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
    reviewModalOpen: of(false),
    scheduleTimerOpen: false,
    invitePassengersOpen: false,
  };

  private previousScreenBeforeInvite: 'schedule-timer' | 'ride-options' | null = null;


  
  
  
  
  
  
  
  onDestBack() {
    this.ridePlanningStore.resetDest();
  }

  toggleDest() {
    this.ridePlanningStore.toggleDestOpen();
    if (this.ridePlanningStore.destOpen) this.ui.cdModalOpen = false;
  }

  onCdProceed() {
    // close destination card
    this.ridePlanningStore.closeDest();
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
    this.ridePlanningStore.closeDest();
    this.closeRideHistory();
    this.closeProfileChanges();
  }

  handleMenuAction(action: string) {
    if (action === 'logout') {
      this.userService.setCurrentUserById(0);     // set current user to guest
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
    this.ridePlanningStore.openDest();
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
    this.userStatsService.loadUserStats();
  }
  
  closeAccountSettings() {
    this.ui.accountSettingsOpen = false;
    this.accountSettingsService.clearDraft();
  }

  saveAccountSettings() {
    this.userService.currentUser$.pipe(take(1)).subscribe(user => {
      if (user.role === Role.DRIVER) {
        this.accountSettingsService.requestProfileChange().pipe(take(1)).subscribe({
          next: () =>
            this.showToast('Profile change requested', 'Your profile change request has been sent.')
        });
      } else {
        this.accountSettingsService.save().pipe(take(1)).subscribe({
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
  
  onChangePassword() {
    this.ui.accountSettingsOpen = false;
    this.ui.changePasswordOpen = true;
    this.changePasswordService.clearAllErrors();
  }
  
  closeChangePassword() {
    this.ui.changePasswordOpen = false;
  }
  
  onChangePasswordBack() {
    this.closeChangePassword();
    this.ui.accountSettingsOpen = true;
  }
  
  savePassword() {
    const errors = this.changePasswordService.validate();
    if (Object.keys(errors).length > 0) {
      this.changePasswordService.fieldErrors = errors;
      return;
    }
    this.changePasswordService.changePassword().pipe(take(1)).subscribe({
      next: () => {
        this.showToast('Password changed', 'Your might need to login again to perform certain actions.');
        this.changePasswordService.clearAllErrors();
        // close change password sheet and open account settings sheet
        this.closeChangePassword();
        this.openAccountSettings();
      },
      error: (err) => {
        if (typeof err === 'string') {
          this.showToast('Error changing password', err);
        }
      }
    });
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

  onEmmitError(error : Error) {
    this.showToast(error.name, error.message)
  }

































  // RIDE OPTIONS SHEET LOGIC

  closeRideOptions() {
    this.ui.rideOptionsOpen = false;
  }
  openRideOptions() {
    this.ui.rideOptionsOpen = true;
  }
  onRideOptionsBack() {
    this.ui.rideOptionsOpen = false;
    this.ridePlanningStore.openDest();
  }

  onRideOptionsScheduleRide(options: RideOptionsDto) {
    this.closeRideOptions();
    this.ridePlanningStore.setRideOptions(options);
    this.ui.scheduleTimerOpen = true;
  }

  onRideOptionsProceed(options: RideOptionsDto) {
    this.closeRideOptions();
    this.previousScreenBeforeInvite = 'ride-options';
    this.ridePlanningStore.setRideOptions(options);
    this.ui.invitePassengersOpen = true;
  }

  onScheduleTimerBack() {
    this.ui.scheduleTimerOpen = false;
    this.ui.rideOptionsOpen = true;
    this.ridePlanningStore.clearScheduledTime();
  }

  onScheduleTimerCheckout(timeData: { hours: number; minutes: number; isAM: boolean }) {
    this.ui.scheduleTimerOpen = false;
    this.previousScreenBeforeInvite = 'schedule-timer';
    this.ui.invitePassengersOpen = true;
    this.ridePlanningStore.setScheduledTime(timeData);
  }





  onCheckoutModalBack() {
    this.ui.checkoutModalOpen = false;
    this.ui.invitePassengersOpen = true;
  }






  onInvitePassengersBack() {
    this.ui.invitePassengersOpen = false;
    // Go back to previous screen - either schedule timer or ride options
    if (this.previousScreenBeforeInvite === 'schedule-timer') {
      this.ui.scheduleTimerOpen = true;
    } else if (this.previousScreenBeforeInvite === 'ride-options') {
      this.ui.rideOptionsOpen = true;
    }
    this.previousScreenBeforeInvite = null;
  }

  onInvitePassengersProceed(emails: string[]) {
    this.ui.invitePassengersOpen = false;
    this.ui.checkoutModalOpen = true;
    this.ridePlanningStore.setPassengersEmails(emails);
    this.ridePlanningStore.estimatePrice();
  }

  onConfirmRide() {
    this.ui.checkoutModalOpen = false;
    this.ridePlanningStore.orderRide().subscribe({
      next: () => {
        this.showToast('Ride ordered', 'Your ride has been ordered successfully.');
      },
      error: (err: HttpErrorResponse) => {
        let errorMessage = 'Failed to order ride';
        if (err.error && typeof err.error === 'string') {
          errorMessage = err.error;
        } else if (err.error && err.error.message && typeof err.error.message === 'string') {
          errorMessage = err.error.message;
        } else if (err.message) {
          errorMessage = err.message;
        }
        this.showToast('Error ordering ride', errorMessage);
      }
    });
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
