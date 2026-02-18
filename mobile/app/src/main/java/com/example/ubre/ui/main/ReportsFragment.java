package com.example.ubre.ui.main;

import android.app.DatePickerDialog;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.view.animation.DecelerateInterpolator;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.DailyReportEntryDto;
import com.example.ubre.ui.dtos.ReportSummaryDto;
import com.example.ubre.ui.dtos.ReportsRequestDto;
import com.example.ubre.ui.dtos.ReportsResponseDto;
import com.example.ubre.ui.enums.Role;
import com.example.ubre.ui.services.ReportsService;
import com.example.ubre.ui.storages.UserStorage;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import androidx.core.content.ContextCompat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReportsFragment extends Fragment {

    private Button btnDateFrom;
    private Button btnDateTo;
    private Button btnGenerate;
    private TextInputLayout scopeLayout;
    private MaterialAutoCompleteTextView scopeInput;
    private TextInputLayout userEmailLayout;
    private TextInputEditText userEmailInput;
    private TextView summaryTotalRides;
    private TextView summaryTotalDistance;
    private TextView summaryTotalMoney;
    private TextView summaryTotalMoneyHint;
    private TextView summaryAvgRides;
    private TextView summaryAvgDistance;
    private TextView summaryAvgMoney;
    private TextView summaryAvgMoneyHint;
    private LineChart chartRides;
    private LineChart chartDistance;
    private LineChart chartMoney;
    private TextView noDailyData;

    private String dateFromIso;
    private String dateToIso;
    private String selectedScopeValue = "self";

    public ReportsFragment() {
        super(R.layout.reports);
    }

    public static ReportsFragment newInstance() {
        return new ReportsFragment();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView btnBack = view.findViewById(R.id.btn_back);
        btnBack.setOnClickListener(v -> requireActivity().getSupportFragmentManager().popBackStack());

        btnDateFrom = view.findViewById(R.id.btn_date_from);
        btnDateTo = view.findViewById(R.id.btn_date_to);
        btnGenerate = view.findViewById(R.id.btn_generate);
        scopeLayout = view.findViewById(R.id.til_scope);
        scopeInput = view.findViewById(R.id.et_scope);
        userEmailLayout = view.findViewById(R.id.til_user_email);
        userEmailInput = view.findViewById(R.id.et_user_email);

        summaryTotalRides = view.findViewById(R.id.summary_total_rides_value);
        summaryTotalDistance = view.findViewById(R.id.summary_total_distance_value);
        summaryTotalMoney = view.findViewById(R.id.summary_total_money_value);
        summaryTotalMoneyHint = view.findViewById(R.id.summary_total_money_hint);
        summaryAvgRides = view.findViewById(R.id.summary_avg_rides_value);
        summaryAvgDistance = view.findViewById(R.id.summary_avg_distance_value);
        summaryAvgMoney = view.findViewById(R.id.summary_avg_money_value);
        summaryAvgMoneyHint = view.findViewById(R.id.summary_avg_money_hint);

        chartRides = view.findViewById(R.id.chart_rides);
        chartDistance = view.findViewById(R.id.chart_distance);
        chartMoney = view.findViewById(R.id.chart_money);
        noDailyData = view.findViewById(R.id.no_daily_data);

        setupAdminScope();
        setupDatePicker(btnDateFrom, true);
        setupDatePicker(btnDateTo, false);

        btnGenerate.setOnClickListener(v -> loadReports());

        resetSummary();
        resetCharts();
    }

    private void setupAdminScope() {
        Role role = Role.GUEST;
        if (UserStorage.getInstance().getCurrentUser().getValue() != null) {
            role = UserStorage.getInstance().getCurrentUser().getValue().getRole();
        }

        if (role == Role.ADMIN) {
            scopeLayout.setVisibility(View.VISIBLE);
            List<String> options = Arrays.asList(
                    "My rides",
                    "All drivers",
                    "All passengers",
                    "Single user"
            );
            ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(), R.layout.autocomplete_item, options);
            scopeInput.setAdapter(adapter);
            scopeInput.setDropDownBackgroundDrawable(
                    ContextCompat.getDrawable(requireContext(), R.drawable.bg_autocomplete_dropdown)
            );
            scopeInput.setText("My rides", false);
            selectedScopeValue = "self";
            scopeInput.setOnItemClickListener((parent, view, position, id) -> {
                String label = options.get(position);
                selectedScopeValue = mapScopeLabelToValue(label);
                toggleUserEmail(selectedScopeValue);
            });
            toggleUserEmail("self");
        } else {
            scopeLayout.setVisibility(View.GONE);
            userEmailLayout.setVisibility(View.GONE);
        }
    }

    private void toggleUserEmail(String scope) {
        if ("single_user".equals(scope)) {
            userEmailLayout.setVisibility(View.VISIBLE);
        } else {
            userEmailLayout.setVisibility(View.GONE);
            userEmailInput.setText("");
        }
    }

    private void setupDatePicker(Button button, boolean isFrom) {
        button.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(
                    requireContext(),
                    (DatePicker view, int year, int month, int dayOfMonth) -> {
                        LocalDate selected = LocalDate.of(year, month + 1, dayOfMonth);
                        String display = selected.format(DateTimeFormatter.ofPattern("d MMM yyyy", Locale.UK));
                        String iso = selected.format(DateTimeFormatter.ISO_LOCAL_DATE);
                        button.setText(display);
                        if (isFrom) {
                            dateFromIso = iso;
                        } else {
                            dateToIso = iso;
                        }
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );
            dialog.show();
        });
    }

    private void loadReports() {
        if (dateFromIso == null || dateToIso == null) {
            Toast.makeText(getContext(), "Please select both dates.", Toast.LENGTH_SHORT).show();
            return;
        }

        LocalDate from = LocalDate.parse(dateFromIso);
        LocalDate to = LocalDate.parse(dateToIso);
        if (from.isAfter(to)) {
            Toast.makeText(getContext(), "Date from must be before or equal to date to.", Toast.LENGTH_SHORT).show();
            return;
        }

        String scope = null;
        String userEmail = null;
        Role role = Role.GUEST;
        if (UserStorage.getInstance().getCurrentUser().getValue() != null) {
            role = UserStorage.getInstance().getCurrentUser().getValue().getRole();
        }

        if (role == Role.ADMIN) {
            scope = selectedScopeValue == null || selectedScopeValue.isEmpty() ? "self" : selectedScopeValue;
            if ("single_user".equals(scope)) {
                userEmail = userEmailInput.getText() != null ? userEmailInput.getText().toString().trim() : "";
                if (userEmail.isEmpty()) {
                    Toast.makeText(getContext(), "User email is required for single_user scope.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }
        }

        ReportsRequestDto request = new ReportsRequestDto(dateFromIso, dateToIso, scope, userEmail);

        try {
            ReportsService.getInstance(requireContext()).loadReports(request, new Callback<ReportsResponseDto>() {
                @Override
                public void onResponse(Call<ReportsResponseDto> call, Response<ReportsResponseDto> response) {
                    if (!response.isSuccessful()) {
                        return;
                    }
                    ReportsResponseDto body = response.body();
                    if (body == null) {
                        resetSummary();
                        resetCharts();
                        return;
                    }
                    renderSummary(body.getSummary());
                    renderCharts(body.getDailyData());
                }

                @Override
                public void onFailure(Call<ReportsResponseDto> call, Throwable t) {
                    resetSummary();
                    resetCharts();
                }
            });
        } catch (Exception e) {
            Toast.makeText(getContext(), "User not authenticated.", Toast.LENGTH_SHORT).show();
        }
    }

    private void renderSummary(ReportSummaryDto summary) {
        if (summary == null) {
            resetSummary();
            return;
        }

        summaryTotalMoneyHint.setVisibility(View.VISIBLE);
        summaryAvgMoneyHint.setVisibility(View.VISIBLE);
        animateInteger(summaryTotalRides, summary.getTotalRides());
        animateDistance(summaryTotalDistance, summary.getTotalDistanceKm());
        animateMoney(summaryTotalMoney, summary.getTotalAmountMoney());
        summaryTotalMoneyHint.setText(summary.getTotalAmountMoney() < 0
                ? getString(R.string.report_money_spent)
                : getString(R.string.report_money_earned));

        animateDecimal(summaryAvgRides, summary.getAverageRidesPerDay());
        animateDistance(summaryAvgDistance, summary.getAverageDistancePerDay());
        animateMoney(summaryAvgMoney, summary.getAverageMoneyPerDay());
        summaryAvgMoneyHint.setText(summary.getAverageMoneyPerDay() < 0
                ? getString(R.string.report_money_spent)
                : getString(R.string.report_money_earned));
    }

    private void resetSummary() {
        summaryTotalRides.setText("0");
        summaryTotalDistance.setText("0 km");
        summaryTotalMoney.setText("+0.00 $");
        summaryTotalMoneyHint.setText(getString(R.string.report_money_earned));
        summaryTotalMoneyHint.setVisibility(View.VISIBLE);
        updateMoneyColor(summaryTotalMoney, 0.0);
        summaryAvgRides.setText("0");
        summaryAvgDistance.setText("0 km");
        summaryAvgMoney.setText("+0.00 $");
        summaryAvgMoneyHint.setText(getString(R.string.report_money_earned));
        summaryAvgMoneyHint.setVisibility(View.VISIBLE);
        updateMoneyColor(summaryAvgMoney, 0.0);
    }

    private void renderCharts(List<DailyReportEntryDto> dailyData) {
        if (dailyData == null || dailyData.isEmpty()) {
            noDailyData.setVisibility(View.VISIBLE);
            chartRides.setVisibility(View.GONE);
            chartDistance.setVisibility(View.GONE);
            chartMoney.setVisibility(View.GONE);
            return;
        }

        noDailyData.setVisibility(View.GONE);
        chartRides.setVisibility(View.VISIBLE);
        chartDistance.setVisibility(View.VISIBLE);
        chartMoney.setVisibility(View.VISIBLE);

        List<String> labels = new ArrayList<>();
        List<Entry> rideEntries = new ArrayList<>();
        List<Entry> distanceEntries = new ArrayList<>();
        List<Entry> moneyEntries = new ArrayList<>();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMM", Locale.UK);

        for (int i = 0; i < dailyData.size(); i++) {
            DailyReportEntryDto entry = dailyData.get(i);
            labels.add(formatDateLabel(entry.getDate(), formatter));
            rideEntries.add(new Entry(i, entry.getRideCount()));
            distanceEntries.add(new Entry(i, (float) entry.getDistanceKm()));
            moneyEntries.add(new Entry(i, (float) entry.getAmountMoney()));
        }

        setupChart(chartRides, "Rides", rideEntries, labels);
        setupChart(chartDistance, "Km", distanceEntries, labels);
        setupChart(chartMoney, "Money", moneyEntries, labels);
    }

    private void resetCharts() {
        noDailyData.setVisibility(View.GONE);
        setupChart(chartRides, "Rides", new ArrayList<>(), new ArrayList<>());
        setupChart(chartDistance, "Km", new ArrayList<>(), new ArrayList<>());
        setupChart(chartMoney, "Money", new ArrayList<>(), new ArrayList<>());
    }

    private void setupChart(LineChart chart, String label, List<Entry> entries, List<String> labels) {
        LineDataSet dataSet = new LineDataSet(entries, label);
        dataSet.setLineWidth(2f);
        dataSet.setCircleRadius(3f);
        dataSet.setDrawValues(false);
        dataSet.setColor(ContextCompat.getColor(requireContext(), R.color.primary_dark));
        dataSet.setCircleColor(ContextCompat.getColor(requireContext(), R.color.primary_light));

        LineData data = new LineData(dataSet);
        chart.setData(data);
        chart.getDescription().setEnabled(false);
        chart.getLegend().setEnabled(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(labels));

        chart.getAxisRight().setEnabled(false);
        chart.invalidate();
    }

    private String formatDistance(double km) {
        return String.format(Locale.US, "%.2f km", km);
    }

    private String formatNumber(double value) {
        return String.format(Locale.US, "%.2f", value);
    }

    private String formatMoney(double value) {
        double abs = Math.abs(value);
        String sign = value < 0 ? "-" : "+";
        return String.format(Locale.US, "%s%.2f $", sign, abs);
    }

    private String formatDateLabel(String isoDate, DateTimeFormatter formatter) {
        try {
            LocalDate date = LocalDate.parse(isoDate);
            return date.format(formatter);
        } catch (Exception e) {
            return isoDate;
        }
    }

    private void updateMoneyColor(TextView view, double value) {
        int color;
        if (value < 0) {
            color = ContextCompat.getColor(requireContext(), R.color.error);
        } else if (value > 0) {
            color = ContextCompat.getColor(requireContext(), R.color.success);
        } else {
            color = ContextCompat.getColor(requireContext(), R.color.primary_dark);
        }
        view.setTextColor(color);
    }

    private void animateInteger(TextView view, int target) {
        ValueAnimator animator = ValueAnimator.ofInt(0, target);
        animator.setDuration(2800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a -> view.setText(String.valueOf((int) a.getAnimatedValue())));
        animator.start();
    }

    private void animateDecimal(TextView view, double target) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, (float) target);
        animator.setDuration(2800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a -> view.setText(formatNumber((float) a.getAnimatedValue())));
        animator.start();
    }

    private void animateDistance(TextView view, double target) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, (float) target);
        animator.setDuration(2800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a -> view.setText(formatDistance((float) a.getAnimatedValue())));
        animator.start();
    }

    private void animateMoney(TextView view, double target) {
        ValueAnimator animator = ValueAnimator.ofFloat(0f, (float) target);
        animator.setDuration(2800);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.addUpdateListener(a -> {
            double value = (float) a.getAnimatedValue();
            view.setText(formatMoney(value));
            updateMoneyColor(view, value);
        });
        animator.start();
    }

    private String mapScopeLabelToValue(String label) {
        if ("All drivers".equalsIgnoreCase(label)) {
            return "all_drivers";
        }
        if ("All passengers".equalsIgnoreCase(label)) {
            return "all_passengers";
        }
        if ("Single user".equalsIgnoreCase(label)) {
            return "single_user";
        }
        return "self";
    }
}
