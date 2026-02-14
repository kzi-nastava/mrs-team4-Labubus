import {
  Component,
  ElementRef,
  EventEmitter,
  inject,
  OnInit,
  Output,
  ViewChild,
  AfterViewChecked,
  OnDestroy,
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
import { Chart, ChartConfiguration, registerables } from 'chart.js';

Chart.register(...registerables);

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, Button],
  templateUrl: './reports.html',
  styleUrl: './reports.css',
})
export class Reports implements OnInit, AfterViewChecked, OnDestroy {
  @Output() onError = new EventEmitter<Error>();

  @ViewChild('canvasRides') canvasRidesRef?: ElementRef<HTMLCanvasElement>;
  @ViewChild('canvasDistance') canvasDistanceRef?: ElementRef<HTMLCanvasElement>;
  @ViewChild('canvasMoney') canvasMoneyRef?: ElementRef<HTMLCanvasElement>;

  private reportService = inject(ReportService);
  userService = inject(UserService);

  Role = Role;

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

  readonly dayWidthPx = 44;
  get chartMinWidth(): number {
    return this.data?.dailyData?.length ? this.data.dailyData.length * this.dayWidthPx : 0;
  }

  private chartRides: Chart | null = null;
  private chartDistance: Chart | null = null;
  private chartMoney: Chart | null = null;
  private chartsInitialized = false;

  ngOnInit(): void {
    this.userService.currentUser$.subscribe((u) => {
      this.isAdmin = u?.role === Role.ADMIN;
    });
  }

  ngAfterViewChecked(): void {
    if (this.data?.dailyData?.length && !this.chartsInitialized && this.canvasRidesRef?.nativeElement) {
      this.chartsInitialized = true;
      setTimeout(() => this.initCharts(), 0);
    }
    if (!this.data?.dailyData?.length && this.chartsInitialized) {
      this.destroyCharts();
      this.chartsInitialized = false;
    }
  }

  ngOnDestroy(): void {
    this.destroyCharts();
  }

  private destroyCharts(): void {
    this.chartRides?.destroy();
    this.chartRides = null;
    this.chartDistance?.destroy();
    this.chartDistance = null;
    this.chartMoney?.destroy();
    this.chartMoney = null;
  }

  private initCharts(): void {
    const entries = this.data?.dailyData ?? [];
    if (entries.length === 0) return;

    this.destroyCharts();

    const labels = entries.map((e) => this.formatDayLabel(e.date));

    const commonOptions: ChartConfiguration<'line'>['options'] = {
      responsive: true,
      maintainAspectRatio: false,
      layout: { padding: { top: 8, right: 8, bottom: 4, left: 4 } },
      scales: {
        x: {
          grid: { display: false },
          ticks: { maxRotation: 0, font: { size: 10 }, color: '#5f6368' },
        },
        y: {
          beginAtZero: true,
          grid: { color: '#f1f3f4' },
          ticks: { font: { size: 10 }, color: '#5f6368' },
        },
      },
      plugins: {
        legend: { display: false },
        tooltip: {
          backgroundColor: '#202124',
          titleFont: { size: 12 },
          bodyFont: { size: 12 },
          padding: 10,
          cornerRadius: 8,
        },
      },
      interaction: { intersect: false, mode: 'index' },
    };

    const lineStyle = {
      borderWidth: 1.2,
      tension: 0,
      pointRadius: 2.5,
      pointHoverRadius: 4,
      pointBackgroundColor: '#fff',
      pointBorderWidth: 1,
      fill: false,
    };

    if (this.canvasRidesRef?.nativeElement) {
      this.chartRides = new Chart(this.canvasRidesRef.nativeElement, {
        type: 'line',
        data: {
          labels,
          datasets: [{
            label: 'Vožnje',
            data: entries.map((e) => e.rideCount),
            borderColor: '#2563eb',
            pointBorderColor: '#2563eb',
            ...lineStyle,
          }],
        },
        options: {
          ...commonOptions,
          plugins: {
            ...commonOptions.plugins,
            tooltip: {
              ...commonOptions.plugins?.tooltip,
              callbacks: {
                label: (c) => `${c.parsed['y']} vožnji`,
              },
            },
          },
        },
      });
    }

    if (this.canvasDistanceRef?.nativeElement) {
      this.chartDistance = new Chart(this.canvasDistanceRef.nativeElement, {
        type: 'line',
        data: {
          labels,
          datasets: [{
            label: 'km',
            data: entries.map((e) => e.distanceKm),
            borderColor: '#059669',
            pointBorderColor: '#059669',
            ...lineStyle,
          }],
        },
        options: {
          ...commonOptions,
          plugins: {
            ...commonOptions.plugins,
            tooltip: {
              ...commonOptions.plugins?.tooltip,
              callbacks: {
                label: (c) => `${Number(c.parsed['y']).toFixed(1)} km`,
              },
            },
          },
        },
      });
    }

    if (this.canvasMoneyRef?.nativeElement) {
      this.chartMoney = new Chart(this.canvasMoneyRef.nativeElement, {
        type: 'line',
        data: {
          labels,
          datasets: [{
            label: '$',
            data: entries.map((e) => e.amountMoney),
            borderColor: '#7c3aed',
            pointBorderColor: '#7c3aed',
            ...lineStyle,
          }],
        },
        options: {
          ...commonOptions,
          scales: {
            ...commonOptions.scales,
            y: {
              ...commonOptions.scales?.['y'],
              ticks: {
                ...commonOptions.scales?.['y']?.ticks,
                callback: (value) => (typeof value === 'number' ? value.toFixed(0) + ' $' : value),
              },
            },
          },
          plugins: {
            ...commonOptions.plugins,
            tooltip: {
              ...commonOptions.plugins?.tooltip,
              callbacks: {
                label: (c) => this.formatMoney(Number(c.parsed['y'])),
              },
            },
          },
        },
      });
    }
    if (this.chartRides || this.chartDistance || this.chartMoney) {
      this.chartsInitialized = true;
    }
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
    this.chartsInitialized = false;

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

  formatMoney(n: number): string {
    const sign = n >= 0 ? '' : '−';
    return sign + Math.abs(n).toFixed(2) + ' $';
  }
}
