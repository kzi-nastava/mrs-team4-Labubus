import { Role } from "../../../enums/role";

export type MenuItem = {
  label: string;
  icon: string; // npr: 'icons/history.svg' (iz public)
  action: string; // npr: 'ride-history', 'logout'...
};

export const MENU_BY_ROLE: Record<Role, MenuItem[]> = {
  [Role.REGISTERED_USER]: [
    {
      label: 'Ride history',
      icon: 'directions_car_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'ride-history',
    },
    {
      label: 'My favourites',
      icon: 'favorite_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'favourites',
    },
    {
      label: 'Account settings',
      icon: 'settings_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'account-settings',
    },
    {
      label: 'Reports',
      icon: 'analytics_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'reports',
    },
    {
      label: 'Log Out',
      icon: 'logout_24dp_E53935_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'logout',
    },
  ],
  [Role.DRIVER]: [
    {
      label: 'Ride history',
      icon: 'directions_car_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'ride-history',
    },
    {
      label: 'Scheduled rides',
      icon: 'scheduled.svg',
      action: 'scheduled',
    },
    {
      label: 'Account settings',
      icon: 'settings_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'account-settings',
    },
    {
      label: 'Reports',
      icon: 'analytics_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'reports',
    },
    {
      label: 'Log out',
      icon: 'logout_24dp_E53935_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'logout',
    },
  ],
  [Role.ADMIN]: [
    {
      label: 'Ride history',
      icon: 'directions_car_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'ride-history',
    },
    { label: 'Active rides', 
      icon: 'active-rides-primary-text.svg', 
      action: 'admin-users' 
    },
    {
      label: 'Account settings',
      icon: 'settings_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'account-settings',
    },
    { 
      label: 'Price adjustment', 
      icon: 'price-adjustment-primary-text.svg', 
      action: 'admin-users' 
    },
    {
      label: 'Register a driver',
      icon: 'person_add_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'register-driver',
    },
    { 
      label: 'Panic notifications', 
      icon: 'warning-primary-text.svg', 
      action: 'admin-panics' 
    },
    {
      label: 'Reports',
      icon: 'analytics_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'reports',
    },
    {
      label: 'Profile changes',
      icon: 'person_edit_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'profile-changes',
    },
    {
      label: 'Block users',
      icon: 'person_24dp_1F1F1F_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'block-users',
    },
    {
      label: 'Log out',
      icon: 'logout_24dp_E53935_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'logout',
    },
  ],
  [Role.GUEST]: [
    { 
      label: 'Log in', 
      icon: 'login_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg', 
      action: 'login' 
    },
    {
      label: 'Sign up',
      icon: 'person_add_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'sign-up',
    },
  ],
};
