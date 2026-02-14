/**
 * Jedan dnevni unos u izveštaju (broj vožnji, km, novac za taj dan).
 */
export interface DailyReportEntryDto {
  /** Datum u ISO 8601 formatu (npr. "2025-02-14"). */
  date: string;
  /** Broj vožnji za taj dan. */
  rideCount: number;
  /** Pređeni kilometri za taj dan. */
  distanceKm: number;
  /** Novac: negativno = potrošeno (putnik), pozitivno = zarađeno (vozač). */
  amountMoney: number;
}
