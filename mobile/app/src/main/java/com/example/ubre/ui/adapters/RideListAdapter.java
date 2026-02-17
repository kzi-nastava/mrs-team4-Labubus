package com.example.ubre.ui.adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ubre.R;
import com.example.ubre.ui.dtos.RideCardDto;
import com.example.ubre.ui.services.RideService;

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
        if (Boolean.TRUE.equals(ride.favorite))
            holder.icon.setImageResource(R.drawable.ic_favorite_red);
        else
            holder.icon.setImageResource(R.drawable.ic_favorite_grey);
        holder.icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    RideService.getInstance().toggleFavorite(holder.icon.getContext(), ride);
                } catch (Exception e) {
                    Toast.makeText(holder.icon.getContext(), "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("TOGGLE RIDE FAVORITE", e.getMessage());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return rides.size();
    }

    class RideCardViewHolder extends RecyclerView.ViewHolder {
        TextView time, start, end;
        ImageView icon;

        public RideCardViewHolder(View itemView) {

            super(itemView);

            time = itemView.findViewById(R.id.ride_card_start);
            start = itemView.findViewById(R.id.ride_card_waypoint1);
            end = itemView.findViewById(R.id.ride_card_waypoint2);
            icon = itemView.findViewById(R.id.ride_card_favorite);

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
        List<RideCardDto> old = this.rides == null ? List.of() : new java.util.ArrayList<>(this.rides);
        List<RideCardDto> updated = rides == null ? List.of() : new java.util.ArrayList<>(rides);
        DiffUtil.DiffResult diff = DiffUtil.calculateDiff(new DiffUtil.Callback() {
            @Override
            public int getOldListSize() {
                return old.size();
            }

            @Override
            public int getNewListSize() {
                return updated.size();
            }

            @Override
            public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                return old.get(oldItemPosition).getId().equals(updated.get(newItemPosition).getId());
            }

            @Override
            public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                RideCardDto o = old.get(oldItemPosition);
                RideCardDto n = updated.get(newItemPosition);
                if (!o.getId().equals(n.getId())) return false;
                if (o.favorite == null && n.favorite != null) return false;
                if (o.favorite != null && !o.favorite.equals(n.favorite)) return false;
                if (o.getStartTime() != null && n.getStartTime() != null && !o.getStartTime().equals(n.getStartTime()))
                    return false;
                return true;
            }
        });
        this.rides = updated;
        diff.dispatchUpdatesTo(this);
    }

    public interface  OnItemClickedListener {
        void onItemClicked(RideCardDto ride);
    }
}
