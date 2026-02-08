package com.example.ubre.ui.main;

import android.os.Bundle;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.ubre.R;
import com.example.ubre.ui.dtos.ReviewDto;
import com.example.ubre.ui.services.ReviewService;
import com.example.ubre.ui.storages.ReviewStorage;
import com.example.ubre.ui.storages.UserStorage;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ReviewModalFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReviewModalFragment extends Fragment {

    private final ReviewStorage storage = ReviewStorage.getInstance();

    public ReviewModalFragment() {
        // Required empty public constructor
    }

    public static ReviewModalFragment newInstance() {
        ReviewModalFragment fragment = new ReviewModalFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.review_modal, container, false);

        View.OnClickListener clickToClose = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeModal();
            }
        };

        ConstraintLayout backdrop = root.findViewById(R.id.review_modal_backdrop);
        backdrop.setOnClickListener(clickToClose);

        LinearLayout content = root.findViewById(R.id.review_modal_content);
        content.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        LinearLayout stars = root.findViewById(R.id.review_modal_stars);
        storage.getRating().observe(this, (rating) -> {
            stars.removeAllViews();
            for (int i = 0; i < 5; ++i) {
                final int positon = i + 1;
                ImageView star = new ImageView(stars.getContext());
                star.setScaleType(ImageView.ScaleType.FIT_XY);
                LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(toDP(30), toDP(30));
                if (i < rating)
                    Glide.with(this).load(R.drawable.ic_star_filled).circleCrop().into(star);
                else
                    Glide.with(this).load(R.drawable.ic_star_outline).circleCrop().into(star);
                star.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        storage.setRating(positon);
                    }
                });
                star.setLayoutParams(layoutParams);
                stars.addView(star);
            }
        });

        EditText input = root.findViewById(R.id.review_modal_text);
        input.setText("");
        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                storage.setReviewText(s.toString());
            }
        });

        Button send = root.findViewById(R.id.review_modal_send);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Long userId = UserStorage.getInstance().getCurrentUser().getValue().getId();
                try {
                    ReviewService.getInstance().createReview(getContext(), storage.getRideId().getValue(), new ReviewDto(null, null, userId, storage.getRating().getValue(), storage.getReviewText().getValue()));
                } catch (Exception e) {
                    Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("SEND REVIEW", e.getMessage());
                }
            }
        });

        Button cancel = root.findViewById(R.id.review_modal_cancel);
        cancel.setOnClickListener(clickToClose);

        return root;
    }

    private void closeModal() {
        storage.setRating(3);
        storage.setReviewText("");
        storage.setRideId(null);
    }

    private int toDP(int value) {
        return  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,  value, this.getResources().getDisplayMetrics());
    }
}