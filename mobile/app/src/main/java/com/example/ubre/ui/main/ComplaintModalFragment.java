package com.example.ubre.ui.main;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.ComplaintDto;
import com.example.ubre.ui.services.ComplaintService;
import com.example.ubre.ui.storages.ComplaintStorage;
import com.example.ubre.ui.storages.UserStorage;

public class ComplaintModalFragment extends Fragment {

    private final ComplaintStorage storage = ComplaintStorage.getInstance();
    private String complaintText = "";

    public ComplaintModalFragment() {}

    public static ComplaintModalFragment newInstance() {
        ComplaintModalFragment fragment = new ComplaintModalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.complaint_modal, container, false);

        View.OnClickListener clickToClose = v -> closeModal();

        ConstraintLayout backdrop = root.findViewById(R.id.complaint_modal_backdrop);
        backdrop.setOnClickListener(clickToClose);

        LinearLayout content = root.findViewById(R.id.complaint_modal_content);
        content.setOnClickListener(v -> {});

        EditText input = root.findViewById(R.id.complaint_modal_text);
        input.setText("");
        input.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void afterTextChanged(android.text.Editable s) {}

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                complaintText = s.toString();
            }
        });

        Button send = root.findViewById(R.id.complaint_modal_send);
        send.setOnClickListener(v -> {
            Long userId = UserStorage.getInstance().getCurrentUser().getValue().getId();
            try {
                ComplaintService.getInstance().createComplaint(
                        getContext(),
                        storage.getRideId().getValue(),
                        new ComplaintDto(null, null, userId, complaintText)
                );
            } catch (Exception e) {
                Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("SEND COMPLAINT", e.getMessage());
            }
        });

        Button cancel = root.findViewById(R.id.complaint_modal_cancel);
        cancel.setOnClickListener(clickToClose);

        return root;
    }

    private void closeModal() {
        complaintText = "";
        storage.setRideId(null);
    }
}
