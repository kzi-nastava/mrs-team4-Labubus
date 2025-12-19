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
import { RideCard } from '../../shared/ui/ride-card/ride-card';



type UserSettingsVM = {
  role: 'registered-user' | 'driver' | 'admin' | 'guest';
  avatarUrl: string;        
  email: string;
  passwordMasked: string;   
  name: string;
  surname: string;
  address: string;
  phone: string;
  activeLast24h?: string;   // driver
};

// TESTING PURPOSES ONLY - WILL NOT BE AVAILABLE FOR THE USER TO EDIT
type VehicleInformationVM = {
  model: string;
  type: string;
  plates: string;
  seats: number;
  babyFriendly: 'Yes' | 'No';
  petFriendly: 'Yes' | 'No';
};



@Component({
  selector: 'app-registered-user',
  standalone: true,
  imports: [Map, IconButton, SideMenu, Toast, 
            Modal, ModalContainer, StatCard, 
            Button, Sheet, FormsModule, RideCard],
  templateUrl: './registered-user.html',
  styleUrl: './registered-user.css',
})
export class RegisteredUser {

  constructor(private cdr: ChangeDetectorRef) {}





  user : UserSettingsVM = {
    role: 'driver',
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
  // In real app, it lives in driver layout / driver state.
  vehicle: VehicleInformationVM = {
    model: 'Toyota Corolla 2021',
    type: 'Standard',
    plates: 'AB-123-CD',
    seats: 4,
    babyFriendly: 'Yes',
    petFriendly: 'No',
  };






  editing : UserSettingsVM = { ...this.user };
  hidePassword = true;

  menuOpen = false;

  
  toastOpen = false;
  toastTitle = 'Ignore this toast';
  toastMessage = 'This is just a demo message for the toast';
  
  cdModalOpen = true; // Choose a destination modal
  
  
  checkoutModalOpen = true
  
  openMenu() { this.menuOpen = true; }
  closeMenu() { this.menuOpen = false; }
  openCdModal() { this.cdModalOpen = true; }
  closeCdModal() { this.cdModalOpen = false; }
  
  closeAllSidePanels() {
    this.closeMenu();
    this.closeAccountSettings();
    this.closeChangePassword();
  }
  
  handleMenuAction(action: string) {
    if (action === 'logout') { /* logout */ }
    if (action === 'account-settings') { this.openAccountSettings(); }
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
  
  hideToast() { this.toastOpen = false; }
  
  onCdModalAction() {
    this.cdModalOpen = false;
    this.showToast('Destination chosen', 'You have successfully chosen a destination.');
  }
  
  onCheckoutModalBack() {
    this.checkoutModalOpen = false
  }
  
  openChat() {
    // Open chat widget
  }
  
  



  
  
  // ACCOUNT SETTINGS SHEET LOGIC
  
  accountSettingsOpen = false;
  
  openAccountSettings() { this.accountSettingsOpen = true; this.editing = { ...this.user }; }
  closeAccountSettings() { this.accountSettingsOpen = false; this.user = { ...this.editing }; }
  
  saveAccountSettings() {
    // Save account settings logic
    this.closeAccountSettings();
    this.showToast('Settings saved', 'Your account settings have been updated.');
  }
  
  onAccountSettingsBack() {
    this.closeAccountSettings();
    this.menuOpen = true;
  }
  









  // EXAMPLE OF RIDE CARD CONTROL VARIABLES
  // selectedRide = undefined;
  // favoriteRides : any[] = [];
  
  // EXAMPLE FOR RIDE CARD EVENT HANDLERS
  // onRideSelected(ride : any) {
    //   if (this.selectedRide === ride.id)
    //     this.selectedRide = undefined;
    //   else
      //     this.selectedRide = ride.id;
    // }
    
  // onRideAction(ride : any) {
  //   if (this.favoriteRides.includes(ride.id))
  //     this.favoriteRides = this.favoriteRides.filter(id => id != ride.id)
  //   else
  //     this.favoriteRides.push(ride.id);
  // }













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

  openVehicleInfo() { this.vehicleInfoOpen = true; }
  closeVehicleInfo() { this.vehicleInfoOpen = false; }

  onVehicleInfoBack() {
    this.closeVehicleInfo();
    this.accountSettingsOpen = true;
  }

  onViewVehicleInfo() {
    this.accountSettingsOpen = false;
    this.openVehicleInfo();
  }


}

