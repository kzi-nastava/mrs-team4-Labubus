package com.example.ubre.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.model.RideDto;

import java.time.format.DateTimeFormatter;

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.RideCardViewHolder> {
    private RideDto[] rides;

    public RideListAdapter(RideDto[] rides) {
        this.rides = rides;
    }

    @NonNull
    @Override
    public RideCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_ride_card, parent, false);
        return new RideCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RideCardViewHolder holder, int position) {
        RideDto ride = rides[position];

        holder.time.setText(ride.getStart().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));
        holder.start.setText(ride.getWaypoints()[0]);
        holder.end.setText(ride.getWaypoints()[ride.getWaypoints().length - 1]);
    }

    @Override
    public int getItemCount() {
        return rides.length;
    }

    static class RideCardViewHolder extends RecyclerView.ViewHolder {
        TextView time, start, end;

        public RideCardViewHolder(View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.ride_card_start);
            start = itemView.findViewById(R.id.ride_card_waypoint1);
            end = itemView.findViewById(R.id.ride_card_waypoint2);
        }
    }
}
