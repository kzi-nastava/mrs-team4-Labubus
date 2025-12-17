export type Role = 'admin' | 'driver' | 'registered' | 'guest';

export type MenuItem = {
  label: string;
  icon: string;     // npr: 'icons/history.svg' (iz public)
  action: string;   // npr: 'ride-history', 'logout'...
};

export const MENU_BY_ROLE: Record<Role, MenuItem[]> = {
  registered: [
    { label: 'Ride history',      icon: 'directions_car_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',   action: 'ride-history' },
    { label: 'My favourites',     icon: 'favorite_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',     action: 'favourites' },
    { label: 'Account settings',  icon: 'settings_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',  action: 'account-settings' },
    { label: 'Reports',           icon: 'analytics_24dp_000000_FILL0_wght400_GRAD0_opsz24.svg',    action: 'reports' },
    { label: 'Log Out',           icon: 'logout_24dp_E53935_FILL0_wght400_GRAD0_opsz24.svg',     action: 'logout' },
  ],
  driver: [
    { label: 'My rides',          icon: 'icons/history.svg',   action: 'driver-rides' },
    { label: 'Earnings',          icon: 'icons/money.svg',     action: 'earnings' },
    { label: 'Settings',          icon: 'icons/settings.svg',  action: 'settings' },
    { label: 'Log off',           icon: 'icons/power.svg',     action: 'logout' },
  ],
  admin: [
    { label: 'Dashboard',         icon: 'icons/dashboard.svg', action: 'admin-dashboard' },
    { label: 'Users',             icon: 'icons/users.svg',     action: 'admin-users' },
    { label: 'Reports',           icon: 'icons/report.svg',    action: 'reports' },
    { label: 'Log off',           icon: 'icons/power.svg',     action: 'logout' },
  ],
  guest: [
    { label: 'Sign in',           icon: 'icons/login.svg',     action: 'login' },
    { label: 'Register',          icon: 'icons/user-plus.svg', action: 'register' },
  ],
};
