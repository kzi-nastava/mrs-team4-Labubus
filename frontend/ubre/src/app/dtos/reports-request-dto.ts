/**
 * Zahtev ka backendu za izveštaj.
 * - Običan korisnik: šalje dateFrom, dateTo; scope se ignoriše (uvek "self").
 * - Admin: može scope = all_drivers | all_passengers | single_user;
 *   za single_user obavezno userEmail.
 */
export type ReportScope =
  | 'self'
  | 'all_drivers'
  | 'all_passengers'
  | 'single_user';

export interface ReportsRequestDto {
  /** Početak opsega datuma (ISO 8601, npr. "2025-02-01"). */
  dateFrom: string;
  /** Kraj opsega datuma (ISO 8601, npr. "2025-02-14"). */
  dateTo: string;
  /**
   * Samo za ADMIN:
   * - 'self' – sopstvene vožnje (kao običan korisnik)
   * - 'all_drivers' – agregat za sve vozače
   * - 'all_passengers' – agregat za sve putnike
   * - 'single_user' – podaci za jednog korisnika (koristi userEmail)
   */
  scope?: ReportScope;
  /**
   * Obavezno kada je scope === 'single_user'.
   * Email korisnika za koga se traži izveštaj.
   */
  userEmail?: string;
}
