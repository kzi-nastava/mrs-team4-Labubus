export type Role = 'admin' | 'driver' | 'registered-user' | 'guest';

export type MenuItem = {
  label: string;
  icon: string; // npr: 'icons/history.svg' (iz public)
  action: string; // npr: 'ride-history', 'logout'...
};

export const MENU_BY_ROLE: Record<Role, MenuItem[]> = {
  'registered-user': [
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
  driver: [
    {
      label: 'Ride history',
      icon: 'directions_car_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'ride-history',
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
  admin: [
    {
      label: 'Ride history',
      icon: 'directions_car_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'ride-history',
    }, //admin-dashboard
    { label: 'Active rides', icon: 'active-rides-primary-text.svg', action: 'admin-users' },
    {
      label: 'Account settings',
      icon: 'settings_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'account-settings',
    },
    { label: 'Price adjustment', icon: 'price-adjustment-primary-text.svg', action: 'admin-users' },
    {
      label: 'Register a driver',
      icon: 'person_add_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'register-driver',
    },
    { label: 'Panic notifications', icon: 'warning-primary-text.svg', action: 'admin-users' },
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
  guest: [
    { label: 'Log in', icon: 'login_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg', action: 'login' },
    {
      label: 'Sign up',
      icon: 'person_add_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',
      action: 'sign-up',
    },
  ],
};
