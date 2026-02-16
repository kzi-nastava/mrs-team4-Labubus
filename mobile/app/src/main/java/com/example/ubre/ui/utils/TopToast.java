package com.example.ubre.ui.utils;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ubre.R;

public class TopToast {
    public static void show(Context context, String title, String message) {
        if (context == null) return;

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.top_toast, null);

        TextView tvTitle = view.findViewById(R.id.toast_title);
        TextView tvMessage = view.findViewById(R.id.toast_message);

        tvTitle.setText(title);
        tvMessage.setText(message);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setView(view);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.show();
    }
}
