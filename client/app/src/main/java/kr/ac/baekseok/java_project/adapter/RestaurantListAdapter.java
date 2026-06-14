package kr.ac.baekseok.java_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.response.RestaurantResponse;
import kr.ac.baekseok.java_project.util.RestaurantFormat;

import java.util.List;

/**
 * 식당 목록 RecyclerView 어댑터.
 * 홈 화면, 검색 결과 등에서 공용으로 사용.
 */
public class RestaurantListAdapter
        extends RecyclerView.Adapter<RestaurantListAdapter.RestaurantViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(RestaurantResponse restaurant);
    }

    private final List<RestaurantResponse> items;
    private final OnItemClickListener listener;

    public RestaurantListAdapter(List<RestaurantResponse> items,
                                 OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_restaurant_list, parent, false);
        return new RestaurantViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        RestaurantResponse r = items.get(position);

        holder.tvName.setText(r.name);
        holder.tvCategory.setText(RestaurantFormat.categoryKo(r.category));
        holder.tvHours.setText(RestaurantFormat.openHours(r.openTime, r.closeTime));
        holder.tvAddress.setText(r.address != null ? r.address : "");

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(r);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class RestaurantViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvCategory, tvHours, tvAddress;

        RestaurantViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvCategory = itemView.findViewById(R.id.tv_category);
            tvHours = itemView.findViewById(R.id.tv_hours);
            tvAddress = itemView.findViewById(R.id.tv_address);
        }
    }
}
