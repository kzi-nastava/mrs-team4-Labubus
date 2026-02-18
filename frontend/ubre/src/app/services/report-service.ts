import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import type { ReportsRequestDto } from '../dtos/reports-request-dto';
import type { ReportsResponseDto } from '../dtos/reports-response-dto';

/**
 * API ugovor za izveštaje:
 *
 * Endpoint: POST /api/reports
 * Body: ReportsRequestDto (JSON)
 * Response: ReportsResponseDto (JSON)
 *
 * Primer za običnog korisnika (sopstvene vožnje):
 *   POST /api/reports
 *   { "dateFrom": "2025-02-01", "dateTo": "2025-02-14" }
 *
 * Primer za admina – svi vozači:
 *   POST /api/reports
 *   { "dateFrom": "2025-02-01", "dateTo": "2025-02-14", "scope": "all_drivers" }
 *
 * Primer za admina – jedan korisnik po emailu:
 *   POST /api/reports
 *   { "dateFrom": "2025-02-01", "dateTo": "2025-02-14", "scope": "single_user", "userEmail": "user@example.com" }
 */
@Injectable({
  providedIn: 'root',
})
export class ReportService {
  private readonly BASE_URL = 'http://localhost:8080/api/';
  private readonly http = inject(HttpClient);

  /**
   * Dohvata izveštaj za zadati opseg i (za admina) scope/user.
   * Backend očekuje POST sa JSON body ReportsRequestDto.
   */
  getReports(request: ReportsRequestDto): Observable<ReportsResponseDto> {
    return this.http.post<ReportsResponseDto>(`${this.BASE_URL}reports`, request);
  }
}
