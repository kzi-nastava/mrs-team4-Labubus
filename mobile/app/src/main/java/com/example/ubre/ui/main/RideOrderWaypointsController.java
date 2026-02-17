package com.example.ubre.ui.main;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.WaypointDto;
import com.example.ubre.ui.services.RidePlanningService;
import com.google.android.material.textfield.TextInputEditText;

import java.util.List;

public class RideOrderWaypointsController {
    private final Activity activity;
    private final LinearLayout stopsContainer;
    private final TextInputEditText fromInput;
    private final TextInputEditText toInput;
    private final View useMyLocation;

    public RideOrderWaypointsController(Activity activity,
                                        LinearLayout stopsContainer,
                                        TextInputEditText fromInput,
                                        TextInputEditText toInput,
                                        View useMyLocation) {
        this.activity = activity;
        this.stopsContainer = stopsContainer;
        this.fromInput = fromInput;
        this.toInput = toInput;
        this.useMyLocation = useMyLocation;
    }

    public void renderWaypoints(List<WaypointDto> waypoints) {
        if (waypoints.isEmpty()) {
            fromInput.setText("");
            toInput.setText("");
            useMyLocation.setVisibility(View.VISIBLE);
            stopsContainer.removeAllViews();
            return;
        }

        WaypointDto first = waypoints.get(0);
        fromInput.setText(first.getLabel());
        useMyLocation.setVisibility(View.GONE);

        if (waypoints.size() >= 2) {
            WaypointDto second = waypoints.get(1);
            toInput.setText(second.getLabel());
        } else {
            toInput.setText("");
        }

        stopsContainer.removeAllViews();
        for (int i = 0; i < waypoints.size(); i++) {
            int index = i;
            WaypointDto waypoint = waypoints.get(i);

            LinearLayout row = new LinearLayout(activity);
            row.setOrientation(LinearLayout.HORIZONTAL);
            row.setGravity(android.view.Gravity.CENTER_VERTICAL);
            row.setBackground(ContextCompat.getDrawable(activity, R.drawable.bg_waypoint_chip));
            row.setPadding(dpToPx(12), dpToPx(8), dpToPx(8), dpToPx(8));

            TextView label = new TextView(activity);
            String displayLabel = ellipsizeLabel(waypoint.getLabel(), 44);
            label.setText((i + 1) + ".\u00A0\u00A0" + displayLabel);
            label.setTextColor(ContextCompat.getColor(activity, R.color.text_primary));
            label.setTextSize(android.util.TypedValue.COMPLEX_UNIT_SP, 13);
            label.setTypeface(ResourcesCompat.getFont(activity, R.font.poppins_medium));
            LinearLayout.LayoutParams labelParams = new LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
            );
            label.setLayoutParams(labelParams);

            ImageView remove = new ImageView(activity);
            remove.setImageDrawable(ContextCompat.getDrawable(activity, R.drawable.ic_cancel));
            remove.setColorFilter(ContextCompat.getColor(activity, R.color.text_secondary));
            LinearLayout.LayoutParams removeParams = new LinearLayout.LayoutParams(
                    dpToPx(18),
                    dpToPx(18)
            );
            remove.setLayoutParams(removeParams);
            remove.setOnClickListener(v -> removeWaypointAt(index));

            row.addView(label);
            row.addView(remove);

            LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            rowParams.setMargins(0, dpToPx(6), 0, 0);
            row.setLayoutParams(rowParams);

            stopsContainer.addView(row);
        }
    }

    private void removeWaypointAt(int index) {
        RidePlanningService.getInstance().removeWaypointAt(index);
    }

    private String ellipsizeLabel(String text, int maxChars) {
        if (text == null) {
            return "";
        }
        if (text.length() <= maxChars) {
            return text;
        }
        return text.substring(0, Math.max(0, maxChars - 3)).trim() + "...";
    }

    private int dpToPx(int dp) {
        float density = activity.getResources().getDisplayMetrics().density;
        return Math.round(dp * density);
    }
}
