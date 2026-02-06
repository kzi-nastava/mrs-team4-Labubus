package com.example.ubre.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.RideDto;

import java.time.format.DateTimeFormatter;

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.RideCardViewHolder> {
    private RideDto[] rides;
    private final OnItemClickedListener listener;

    public RideListAdapter(RideDto[] rides, OnItemClickedListener listener) {
        this.rides = rides;
        this.listener = listener;
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

        holder.time.setText(ride.getStartTime().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));
        if (ride.getWaypoints() != null && !ride.getWaypoints().isEmpty()) {
            holder.start.setText(ride.getWaypoints().get(0).getLabel());
            holder.end.setText(ride.getWaypoints().get(0).getLabel());
        }
        else {
            holder.start.setText("");
            holder.end.setText("");
        }


    }

    @Override
    public int getItemCount() {
        return rides.length;
    }

    class RideCardViewHolder extends RecyclerView.ViewHolder {
        TextView time, start, end;

        public RideCardViewHolder(View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.ride_card_start);
            start = itemView.findViewById(R.id.ride_card_waypoint1);
            end = itemView.findViewById(R.id.ride_card_waypoint2);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClicked(rides[position]);
                        }
                    }
                }
            });
        }
    }

    public interface  OnItemClickedListener {
        void onItemClicked(RideDto ride);
    }
}
