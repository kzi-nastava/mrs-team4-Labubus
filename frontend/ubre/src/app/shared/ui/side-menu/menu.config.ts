export type Role = 'admin' | 'driver' | 'registered-user' | 'guest';

export type MenuItem = {
  label: string;
  icon: string;     // npr: 'icons/history.svg' (iz public)
  action: string;   // npr: 'ride-history', 'logout'...
};

export const MENU_BY_ROLE: Record<Role, MenuItem[]> = {
  'registered-user': [
    { label: 'Ride history',      icon: 'directions_car_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',   action: 'ride-history' },
    { label: 'My favourites',     icon: 'favorite_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',     action: 'favourites' },
    { label: 'Account settings',  icon: 'settings_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',  action: 'account-settings' },
    { label: 'Reports',           icon: 'analytics_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',    action: 'reports' },
    { label: 'Log Out',           icon: 'logout_24dp_E53935_FILL0_wght400_GRAD0_opsz24.svg',     action: 'logout' },
  ],
  driver: [
    { label: 'My rides',          icon: '',   action: 'driver-rides' },
    { label: 'Earnings',          icon: '',     action: 'earnings' },
    { label: 'Settings',          icon: '',  action: 'settings' },
    { label: 'Log out',           icon: 'logout_24dp_E53935_FILL0_wght400_GRAD0_opsz24.svg',     action: 'logout' },
  ],
  admin: [
    { label: 'Ride history',         icon: '', action: 'admin-dashboard' },
    { label: 'Active rides',             icon: '',     action: 'admin-users' },
    { label: 'Account settings',             icon: 'settings_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',     action: 'account-settings' },
    { label: 'Price adjustment',             icon: '',     action: 'admin-users' },
    { label: 'Register a driver',             icon: 'person_add_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',     action: 'register-driver' },
    { label: 'Panic notifications',             icon: '',     action: 'admin-users' },
    { label: 'Reports',           icon: '',    action: 'reports' },
    { label: 'Log out',           icon: 'logout_24dp_E53935_FILL0_wght400_GRAD0_opsz24.svg',     action: 'logout' },
  ],
  guest: [
    { label: 'Log in',           icon: '',     action: 'login' },
    { label: 'Register',          icon: '', action: 'register' },
  ],
};
