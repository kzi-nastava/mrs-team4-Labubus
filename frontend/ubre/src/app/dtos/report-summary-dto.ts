/**
 * Kumulativne i prosečne vrednosti za ceo opseg datuma izveštaja.
 */
export interface ReportSummaryDto {
  /** Ukupan broj vožnji u opsegu. */
  totalRides: number;
  /** Ukupno pređenih kilometara. */
  totalDistanceKm: number;
  /** Ukupno novca (negativno potrošeno, pozitivno zarađeno). */
  totalAmountMoney: number;
  /** Prosečan broj vožnji po danu. */
  averageRidesPerDay: number;
  /** Prosečna distanca po danu (km). */
  averageDistancePerDay: number;
  /** Prosečan novac po danu. */
  averageMoneyPerDay: number;
}
