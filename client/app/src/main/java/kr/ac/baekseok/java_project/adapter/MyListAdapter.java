package kr.ac.baekseok.java_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;

import java.util.List;

/**
 * 마이페이지 하위 리스트(내 게시물, 답글, 나만의 맛집)에 공통으로 사용되는 어댑터.
 *
 * 각 아이템은 (title, subtitle, payload) 형태로 구성된다.
 * payload는 클릭 시 다음 화면으로 전달할 식별자 또는 객체.
 */
public class MyListAdapter extends RecyclerView.Adapter<MyListAdapter.ItemViewHolder> {

    public static class Item {
        public final String title;
        public final String subtitle;
        public final Object payload;

        public Item(String title, String subtitle, Object payload) {
            this.title = title;
            this.subtitle = subtitle;
            this.payload = payload;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(Item item);
    }

    private final List<Item> items;
    private final OnItemClickListener listener;

    public MyListAdapter(List<Item> items, OnItemClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_my_list, parent, false);
        return new ItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
        Item item = items.get(position);
        holder.tvTitle.setText(item.title);

        if (item.subtitle != null) {
            holder.tvSubtitle.setText(item.subtitle);
            holder.tvSubtitle.setVisibility(View.VISIBLE);
        } else {
            holder.tvSubtitle.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onItemClick(item);
        });
    }

    @Override
    public int getItemCount() { return items.size(); }

    static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvSubtitle;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tv_item_title);
            tvSubtitle = itemView.findViewById(R.id.tv_item_subtitle);
        }
    }
}
