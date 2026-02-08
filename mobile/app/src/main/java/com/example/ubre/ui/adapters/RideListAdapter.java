package com.example.ubre.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.RideCardDto;

import java.time.format.DateTimeFormatter;
import java.util.List;

public class RideListAdapter extends RecyclerView.Adapter<RideListAdapter.RideCardViewHolder> {
    private List<RideCardDto> rides;
    private final OnItemClickedListener listener;

    public RideListAdapter(List<RideCardDto> rides, OnItemClickedListener listener) {
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
        RideCardDto ride = rides.get(position);

        holder.time.setText(ride.getStartTime().format(DateTimeFormatter.ofPattern("d MMM yyyy HH:mm")));
        String firstStop = ride.getWaypoints().get(0).getLabel();
        holder.start.setText(firstStop.length() > 30 ? firstStop.substring(0, 27) + "..." : firstStop);
        String lastStop = ride.getWaypoints().get(ride.getWaypoints().size() - 1).getLabel();
        holder.end.setText(lastStop.length() > 30 ? lastStop.substring(0, 27) + "..." : lastStop);


    }

    @Override
    public int getItemCount() {
        return rides.size();
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
                            listener.onItemClicked(rides.get(position));
                        }
                    }
                }
            });
        }
    }

    public void updateItems(List<RideCardDto> rides) {
        this.rides = rides;
        notifyDataSetChanged();
    }

    public interface  OnItemClickedListener {
        void onItemClicked(RideCardDto ride);
    }
}
