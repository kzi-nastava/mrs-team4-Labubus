import type { DailyReportEntryDto } from './daily-report-entry-dto';
import type { ReportSummaryDto } from './report-summary-dto';

/**
 * Odgovor backend-a za izveštaj.
 * - dailyData: podaci po danima (za grafove).
 * - summary: kumulativna suma i proseci za ceo opseg.
 */
export interface ReportsResponseDto {
  dailyData: DailyReportEntryDto[];
  summary: ReportSummaryDto;
}
