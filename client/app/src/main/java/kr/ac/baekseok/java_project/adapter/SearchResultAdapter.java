package kr.ac.baekseok.java_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.model.Restaurant;

import java.util.List;

/**
 * 검색 결과 RecyclerView 어댑터
 *
 * 별점 표시는 단순화하여 텍스트와 상호명만 바인딩한다.
 * 평점에 따라 별 색을 동적으로 채우고 싶다면
 * item_search_result.xml의 각 별 ImageView에 id를 부여한 뒤
 * ViewHolder에서 findViewById로 가져와서 setColorFilter()로 처리하면 된다.
 */
public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ResultViewHolder> {

    private final List<Restaurant> restaurants;
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Restaurant restaurant);
    }

    public SearchResultAdapter(List<Restaurant> restaurants, OnItemClickListener listener) {
        this.restaurants = restaurants;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new ResultViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ResultViewHolder holder, int position) {
        Restaurant r = restaurants.get(position);
        holder.tvStoreName.setText(r.getName());
        holder.tvOpeningHours.setText(r.getOpeningHours());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(r);
        });
    }

    @Override
    public int getItemCount() {
        return restaurants.size();
    }

    static class ResultViewHolder extends RecyclerView.ViewHolder {
        TextView tvStoreName, tvOpeningHours;

        ResultViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStoreName = itemView.findViewById(R.id.tv_store_name);
            tvOpeningHours = itemView.findViewById(R.id.tv_opening_hours);
        }
    }
}
