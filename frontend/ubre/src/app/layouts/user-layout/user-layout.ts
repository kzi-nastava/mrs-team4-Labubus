import { Component, ViewChild } from '@angular/core';
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
import { BehaviorSubject, Observable, Subscription, forkJoin, of, take } from 'rxjs';
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
import { FavoriteRides } from '../../shared/ui/favorite-rides/favorite-rides';
import { RideStatus } from '../../enums/ride-status';
import { RideDto } from '../../dtos/ride-dto';
import { DriverCancelDialog } from '../../shared/ui/driver-cancel-dialog/driver-cancel-dialog';
import { RideService } from '../../services/ride-service';
import { VehicleService } from '../../services/vehicle-service';
import { RideTrackingStore } from '../../services/ride-planning/ride-tracking-store';
import { WaypointDto } from '../../dtos/waypoint-dto';
import { GeocodingService } from '../../services/ride-planning/geocoding-service';
import { RoutingService } from '../../services/ride-planning/routing-service';
import { RouteInfo } from '../../services/ride-planning/ride-types';
import { PanicList } from '../../features/panic/panic-list/panic-list';
import { PanicButton } from "../../shared/ui/panic-button/panic-button";
import { PanicToast } from '../../features/panic/panic-toast/panic-toast';
import { ComplaintModal } from '../../shared/ui/complaint-modal/complaint-modal';
import { ComplaintService } from '../../services/complaint-service';

@Component({
  selector: 'app-user-layout',
  standalone: true,
  imports: [Map,IconButton,SideMenu,Toast,
    Modal,ModalContainer,StatCard,Button,
    Sheet,FormsModule,RideHistory,ProfileChangeCard,
    AsyncPipe,ReviewModal,ScheduleTimer,InvitePassengers,
    RideOptions, FavoriteRides, DriverCancelDialog,
    ComplaintModal, PanicList, PanicButton, PanicToast,],
    templateUrl: './user-layout.html',
    styleUrl: './user-layout.css',
  })
  export class UserLayout implements OnInit {

    constructor(private cdr: ChangeDetectorRef, private http: HttpClient, private router: Router) {}
    
    public userService = inject(UserService);
    private authService = inject(AuthService);
    private reviewService : ReviewService = inject(ReviewService)
    private complaintService : ComplaintService = inject(ComplaintService)
    public driverRegistrationService = inject(DriverRegistrationService);
    public ridePlanningStore = inject(RidePlanningStore);
    private confetti = inject(ConfettiService);
    public profileChangeService = inject(ProfileChangeService); 
    public accountSettingsService = inject(AccountSettingsService);
    public webSocketService = inject(WebSocketService);
    public changePasswordService = inject(ChangePasswordService);
    public userStatsService = inject(UserStatsService);
    public rideService = inject(RideService);
    public geocodingService = inject(GeocodingService);


    public vehicleService = inject(VehicleService)
    public rideTrackingStore = inject(RideTrackingStore)

  Role = Role;
  VehicleType = VehicleType;
  RideStatus = RideStatus;

  userStats!: UserStatsDto;

  private websocketUserId: number | null = null;
  private profileChangeSubscription?: Subscription;
  private rideAssignmentSubscription?: Subscription;
  private rideReminderSubscription?: Subscription;
  private panicSubscription?: Subscription;
  private currentRideSubscription?: Subscription; // this subscription represents a current ride, for user and for a driver

  @ViewChild(PanicToast) panicToast!: PanicToast;


  ngOnInit() {
    const userId = this.authService.getId();

    if (userId !== null && userId !== 0) {
      this.userService.setCurrentUserById(userId);

      this.rideService.getCurrentRide().subscribe({
      next: (ride) => {
        if (ride) {
          this.ridePlanningStore.currentRideSubject$.next(ride);
        }
      },
      error: (err) => {
        console.error('Error fetching active ride', err);
      }
    });
    }

    
      
    if (userId === 0 || userId === null) {
      this.profileChangeSubscription?.unsubscribe();
      // this.webSocketService.disconnect();
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
            this.playNotificationSound();
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
            this.playNotificationSound();
            this.showToast('New ride assigned', 'Check your notifications for more details.');
          }
        },
      });

    this.ui.reviewModalOpen = this.reviewService.showReviewModal$;
    this.ui.complaintModalOpen = this.complaintService.showComplaintModal$;
    
    this.rideReminderSubscription = this.webSocketService
      .rideReminderNotifications(userId)
      .subscribe({
        next: (notification) => {
          if (notification.status === NotificationType.RIDE_REMINDER && notification.time) {
            this.playNotificationSound();
            this.showToast('Ride reminder', 'You have a ride scheduled at ' + notification.time + '.');
          }
        },
      });

    // this subscription receives notifications for a current ride via websocket
    this.currentRideSubscription = this.webSocketService
      .currentRideNotifications(userId)
      .subscribe({
        next: (notification) => {
          // notification that time for a ride has come
          if (notification.status === NotificationType.TIME_FOR_A_RIDE)
            this.showToast('Get ready', 'Your ride is starting soon...');
      
          if (notification.status === NotificationType.RIDE_STARTED) 
            this.showToast('Ride started', 'Your ride has been started successfully.');
          
          if (notification.status === NotificationType.RIDE_CANCELLED) 
          
            if (notification.reason)
              this.showToast('Ride cancelled', notification.reason);
            else
              this.showToast('Ride cancelled', "Ride has been cancelled by the user.");

          if (notification.status === NotificationType.RIDE_COMPLETED) {
            this.showToast('Ride completed', "Ride completed.");
            this.rideService.getCurrentRide().pipe(take(1)).subscribe((nextRide : RideDto | null) => {
              this.userService.getCurrentUser().pipe(take(1)).subscribe((user : UserDto) => {
                if (user.role == Role.REGISTERED_USER && this.ridePlanningStore.currentRideSubject$.value != null)
                  this.reviewService.newReview(this.ridePlanningStore.currentRideSubject$.value.id)
              })
              this.ridePlanningStore.currentRideSubject$.next(nextRide)
            })
          }


          if (notification.ride)
            this.ridePlanningStore.currentRideSubject$.next(notification.ride);
        },
      });

      this.subscribeToPanicNotifications();
    }

  ngOnDestroy() {
    this.profileChangeSubscription?.unsubscribe();
    this.rideAssignmentSubscription?.unsubscribe();
    this.rideReminderSubscription?.unsubscribe();
    this.currentRideSubscription?.unsubscribe();
    this.panicSubscription?.unsubscribe();
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
    complaintModalOpen: of(false),
    scheduleTimerOpen: false,
    invitePassengersOpen: false,
    timeEstimate: false,
    showRideHistory: false,
    showFavourites: false,
    showCancelModal: false,
    panicListOpen: false,
    toastPanicOpen: false,
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

    //if user isn't logged in, proceed to time estimate
    if (!this.isLoggedIn()) {
      this.ui.timeEstimate = true;
      return
    }

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
    this.closeFavourites();
    this.closeProfileChanges();
  }

  handleMenuAction(action: string) {
    if (action === 'logout') {
      this.userService.setCurrentUserById(0);     // set current user to guest
      this.authService.logout();
      this.userService.resetAvatar();
      this.closeAllSidePanels();
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
    if (action === 'favourites') {
      this.openFavourites();
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
    if (action === 'admin-changes') {
      this.openProfileChanges();
    }
    if (action === 'admin-panics') {
      this.ui.panicListOpen = true;
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

  openComplaintModal() {
    this.ridePlanningStore.currentRide$.pipe(take(1)).subscribe((ride : RideDto | null) => {
      if (ride != null)
        this.complaintService.newComplaint(ride?.id)
    })
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
  onRideHistoryBack() {
    this.ui.showRideHistory = false;
    this.ui.menuOpen = true;
  }

  openRideHistory() {
    this.ui.showFavourites = false;
    this.ui.showRideHistory = true;
    this.ui.menuOpen = false;
  }

  closeRideHistory() {
    this.ui.showRideHistory = false;
  }

  onEmmitError(error : Error) {
    this.showToast(error.name, error.message)
  }

  // TODO: Move this logic outside user-layout
  // This is probably not the right way to do it, but I cant fix it right now...
  private selectedRideWaypoints = new BehaviorSubject<WaypointDto[]>([]);
  selectedRideWaypoints$ = this.selectedRideWaypoints.asObservable()
  private selectedRideRoute = new BehaviorSubject<RouteInfo | null>(null);
  selectedRideRoute$  = this.selectedRideRoute.asObservable();

  private routingService : RoutingService = inject(RoutingService)

  onRenderWaypoints(waypoints : WaypointDto[]) {
    if (waypoints.length < 1) {
      this.selectedRideWaypoints.next([])
      this.selectedRideRoute.next(null)
      return
    }

    this.routingService.route(waypoints).pipe(take(1)).subscribe({
      next: (routeInfo) => {
          console.log(waypoints)
          this.selectedRideWaypoints.next(waypoints)
          console.log(this.selectedRideWaypoints.value)
          this.selectedRideRoute.next(routeInfo)
        },
        error: (err) => {
            console.log(err)
        },
    });
  }



  // Favourites SHEET LOGIC
  onFavouritesBack() {
    this.ui.showFavourites = false;
    this.ui.menuOpen = true;
  }

  openFavourites() {
    this.ui.showRideHistory = false;
    this.ui.showFavourites = true;
    this.ui.menuOpen = false;
  }

  closeFavourites() {
    this.ui.showFavourites = false;
  }












  playNotificationSound() {
    try {
      const audio = new Audio('/new-notification-07-210334.mp3');
      
      audio.volume = 0.3;
      
      audio.play().catch((error) => {
        console.warn('Failed to play notification sound:', error);
        const audioAlt = new Audio('new-notification-07-210334.mp3');
        audioAlt.volume = 0.3;
        audioAlt.play().catch((err) => {
          console.warn('Failed to play notification sound with alternative path:', err);
        });
      });
    } catch (error) {
      console.warn('Error creating audio element:', error);
    }
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
        this.playNotificationSound();
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

  onStartRideClick() {
    const currentRide = this.ridePlanningStore.getCurrentRide();
    if (!currentRide) {
      this.showToast('No ride available', 'There is no ride available to start.');
    } else {
      this.ridePlanningStore.startCurrentRide().pipe(take(1)).subscribe({
        next: () => {
          this.showToast('Ride started', 'Please drive carefully and enjoy your ride.');
        },
        error: (err: HttpErrorResponse) => {
          this.showToast('Error starting ride', err.error.message);
        }
      });
    }
  }


  // REORDER RIDE FROM FAVORITES
  onFavoriteReorder(ride: RideDto) {
    // close favourites sheet
    this.closeFavourites();
    // close initial choose-destination modal
    this.ui.cdModalOpen = false;
    // prepare state for new ride using existing waypoints
    this.ridePlanningStore.clearRidePlanningState();
    this.ridePlanningStore.setWaypoints(ride.waypoints);
    // open destination card and recalculate route
    this.ridePlanningStore.openDest();
    this.ridePlanningStore.recalculateRoute();
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

  isLoggedIn(): boolean {
    return this.authService.isLoggedIn();
  }

  getWaypointCount(): number {
    return this.ridePlanningStore.waypoints.length;
  }

  onEstimateTime() {
    this.ui.timeEstimate = false;
    this.ui.cdModalOpen = true;
    this.ridePlanningStore.setWaypoints([]);
    this.ridePlanningStore.clearRoute();
  }

  calculateEstimatedTime() {
    return this.ridePlanningStore.getDurationMinutes();
  }

  onCancelRideClick() {
    this.ui.showCancelModal = true;
  }

  handleCancelRide(reason: string) {
    const rideId = this.ridePlanningStore.currentRideSubject$.getValue()!.id;
    this.rideService.cancelRideDriver(rideId, reason).subscribe({
        next: () => {
          this.ui.showCancelModal = false;
          this.ridePlanningStore.currentRideSubject$.next(null);
          this.showToast('Ride cancelled', 'Ride cancelled successfully.');
        },
        error: (err: any) => {
          this.showToast('Error cancelling ride', err.error.message);
        }
      });
  }

  canCancelRide(): boolean {
    const ride = this.ridePlanningStore.getCurrentRide();
    return !!ride && ride.status === 'PENDING';
  }

  
  canStopRide(): boolean {
    const ride = this.ridePlanningStore.getCurrentRide();
    return !!ride && ride.status === 'IN_PROGRESS';
  }

  onStopRideClick() {
    const ride = this.ridePlanningStore.getCurrentRide();

    navigator.geolocation.getCurrentPosition(
      position => {
        const lat = position.coords.latitude;
        const lon = position.coords.longitude;

        this.geocodingService.reverse(lat, lon).subscribe({
          next: (label: string | null) => {
            if (label) {
              const stopWaypoint: WaypointDto = {
                latitude: lat,
                longitude: lon,
                label: label,
                id: 0,
              };

              console.log("Stop waypoint DTO:", stopWaypoint);

              this.rideService.stopRide(ride!.id, stopWaypoint).subscribe({
                next: (price) => {
                  const finalPrice = price;
                  this.showToast("New price", finalPrice.toString());
                  this.rideService.getCurrentRide().pipe(take(1)).subscribe((nextRide : RideDto | null) => {
                    this.ridePlanningStore.currentRideSubject$.next(nextRide)
                    this.userService.getCurrentUser().pipe(take(1)).subscribe((user : UserDto) => {
                      if (user.role == Role.REGISTERED_USER && ride != null)
                        this.reviewService.newReview(ride.id)
                    })
                  })
                },
                error: (err) => {
                }
              });
            } 
          },
          error: (err) => {
            console.error("Error", err);
          }
        });
      },
      error => {
        console.error("Geolocation error", error);

        // Fallback if the location permisions are denied - DELETE LATER
        const lat = 45.264180;
        const lon = 19.830198;

        this.geocodingService.reverse(lat, lon).subscribe({
          next: (label: string | null) => {
            if (label) {
              const stopWaypoint: WaypointDto = {
                latitude: lat,
                longitude: lon,
                label: label,
                id: 0,
              };

              console.log("Stop waypoint DTO:", stopWaypoint);

              this.rideService.stopRide(ride!.id, stopWaypoint).subscribe({
                next: (price) => {
                  const finalPrice = price;
                  this.showToast("New price", finalPrice.toString());
                  this.rideService.getCurrentRide().pipe(take(1)).subscribe((nextRide : RideDto | null) => {
                    this.ridePlanningStore.currentRideSubject$.next(nextRide)
                    this.userService.getCurrentUser().pipe(take(1)).subscribe((user : UserDto) => {
                      if (user.role == Role.REGISTERED_USER && ride != null)
                        this.reviewService.newReview(ride.id)
                    })
                  })
                },
                error: (err) => {
                }
              });
            } 
          },
          error: (err) => {
            console.error("Error", err);
          }
        });
      },
      { enableHighAccuracy: true }
    );
  }


  onCancelUserClick() {
    const rideId = this.ridePlanningStore.currentRideSubject$.getValue()!.id;
    this.rideService.cancelRideUser(rideId).subscribe({
        next: () => {
          this.ui.showCancelModal = false;
          this.ridePlanningStore.currentRideSubject$.next(null);
          this.showToast('Ride cancelled', 'Ride cancelled successfully.');
        },
        error: (err: any) => {
          this.showToast('Error cancelling ride', err.error.message);
        }
      });
  }

  activatePanic() {
    const rideId = this.ridePlanningStore.currentRideSubject$.getValue()!.id;
    this.rideService.activatePanic(rideId).subscribe({
      next: () => this.showToast('Panic activated', 'Admins are notified.'),
      error: err => console.error(err)
    });
  }

  subscribeToPanicNotifications() {
    console.log(this.authService.getRole())
    if (this.authService.getRole() === "ADMIN") { 
        this.panicSubscription = this.webSocketService
        .panicNotifications()
        .subscribe({
          next: (panic) => {
            this.panicToast.show(panic.rideId.toString());
            this.playNotificationSound();
          },
          error: () => {
            this.showToast('Connection error', 'Could not receive panic updates.');
          }
        });
      }
    }

}

