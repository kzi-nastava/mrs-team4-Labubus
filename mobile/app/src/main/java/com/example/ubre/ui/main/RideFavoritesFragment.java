package com.example.ubre.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.adapters.RideListAdapter;
import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.services.RideService;
import com.example.ubre.ui.storages.FavoriteRidesStorage;

import java.util.List;

public class RideFavoritesFragment extends Fragment implements RideListAdapter.OnItemClickedListener {

    public RideFavoritesFragment() {
    }

    public static RideFavoritesFragment newInstance() {
        RideFavoritesFragment fragment = new RideFavoritesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onStart() {
        super.onStart();

        View options = this.getView().findViewById(R.id.ride_list_options);
        if (options != null) {
            options.setVisibility(View.GONE);
        }

        TextView title = this.getView().findViewById(R.id.ride_list_title);
        if (title != null) {
            title.setText(R.string.my_favourites);
        }

        this.getView().findViewById(R.id.btn_back).setOnClickListener(v ->
                requireActivity().getSupportFragmentManager().popBackStack()
        );

        RecyclerView cards = this.getView().findViewById(R.id.ride_list_cards);
        int topPadding = dpToPx(72);
        cards.setPadding(cards.getPaddingLeft(), topPadding, cards.getPaddingRight(), cards.getPaddingBottom());
        cards.setAdapter(new RideListAdapter(List.of(), this));
        cards.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        FavoriteRidesStorage.getInstance().getFavoritesReadOnly().observe(getViewLifecycleOwner(), rides -> {
            ((RideListAdapter) cards.getAdapter()).updateItems(rides);
        });

        try {
            RideService.getInstance().getFavorites(getContext(), null);
        } catch (Exception e) {
            Toast.makeText(getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            Log.e("FAVORITES FETCH", e.getMessage());
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        FavoriteRidesStorage.getInstance().clear();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.ride_list, container, false);
    }

    private int dpToPx(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density);
    }

    @Override
    public void onItemClicked(RideCardDto ride) {
        MainActivity activity = (MainActivity) this.getActivity();
        Fragment f = RideDetailsFragment.newInstance(ride.getId(), true);
        activity.showFragment(f);
    }
}
