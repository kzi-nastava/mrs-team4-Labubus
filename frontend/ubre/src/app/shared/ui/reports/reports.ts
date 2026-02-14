import {
  Component,
  EventEmitter,
  inject,
  Input,
  OnInit,
  Output,
} from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Button } from '../button/button';
import { ReportService } from '../../../services/report-service';
import { UserService } from '../../../services/user-service';
import { Role } from '../../../enums/role';
import type {
  ReportsRequestDto,
  ReportScope,
} from '../../../dtos/reports-request-dto';
import type {
  ReportsResponseDto,
} from '../../../dtos/reports-response-dto';
import type { DailyReportEntryDto } from '../../../dtos/daily-report-entry-dto';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, Button],
  templateUrl: './reports.html',
  styleUrl: './reports.css',
})
export class Reports implements OnInit {
  @Output() onError = new EventEmitter<Error>();

  private reportService = inject(ReportService);
  userService = inject(UserService);

  Role = Role;
  Math = Math;

  dateFrom = '';
  dateTo = '';
  scope: ReportScope = 'self';
  userEmail = '';

  loading = false;
  errorMessage: string | null = null;
  data: ReportsResponseDto | null = null;

  readonly scopeOptions: { value: ReportScope; label: string }[] = [
    { value: 'self', label: 'My rides' },
    { value: 'all_drivers', label: 'All drivers' },
    { value: 'all_passengers', label: 'All passengers' },
    { value: 'single_user', label: 'Single user (by email)' },
  ];

  isAdmin = false;

  ngOnInit(): void {
    this.userService.currentUser$.subscribe((u) => {
      this.isAdmin = u?.role === Role.ADMIN;
    });
  }

  generateReport(): void {
    const from = this.dateFrom?.trim();
    const to = this.dateTo?.trim();
    if (!from || !to) {
      this.errorMessage = 'Please select both start and end date.';
      return;
    }
    const fromDate = new Date(from);
    const toDate = new Date(to);
    if (fromDate > toDate) {
      this.errorMessage = 'Start date must be before end date.';
      return;
    }
    if (this.scope === 'single_user' && !this.userEmail?.trim()) {
      this.errorMessage = 'Please enter user email for single user report.';
      return;
    }

    this.errorMessage = null;
    this.loading = true;
    this.data = null;

    const request: ReportsRequestDto = {
      dateFrom: from,
      dateTo: to,
    };
    if (this.isAdmin) {
      request.scope = this.scope;
      if (this.scope === 'single_user') {
        request.userEmail = this.userEmail.trim();
      }
    }

    this.reportService.getReports(request).subscribe({
      next: (res) => {
        this.loading = false;
        this.data = res;
      },
      error: (err) => {
        this.loading = false;
        this.errorMessage =
          err?.error?.message || err?.message || 'Failed to load report.';
        this.onError.emit(err instanceof Error ? err : new Error(this.errorMessage!));
      },
    });
  }

  formatDayLabel(dateStr: string): string {
    const d = new Date(dateStr + 'Z');
    return d.toLocaleDateString('en-GB', {
      day: '2-digit',
      month: 'short',
    });
  }

  maxRides(entries: DailyReportEntryDto[]): number {
    const m = Math.max(...entries.map((e) => e.rideCount), 1);
    return m;
  }
  maxKm(entries: DailyReportEntryDto[]): number {
    const m = Math.max(...entries.map((e) => e.distanceKm), 1);
    return m;
  }
  maxMoney(entries: DailyReportEntryDto[]): number {
    const abs = entries.map((e) => Math.abs(e.amountMoney));
    const m = Math.max(...abs, 1);
    return m;
  }

  formatMoney(n: number): string {
    const sign = n >= 0 ? '' : '−';
    return sign + Math.abs(n).toFixed(2) + ' €';
  }
}
