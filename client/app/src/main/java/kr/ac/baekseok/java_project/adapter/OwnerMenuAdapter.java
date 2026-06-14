package kr.ac.baekseok.java_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.response.MenuResponse;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * 사장님용 메뉴 목록 어댑터.
 * 항목을 누르면 수정 화면으로 (콜백).
 */
public class OwnerMenuAdapter
        extends RecyclerView.Adapter<OwnerMenuAdapter.VH> {

    public interface OnMenuClickListener {
        void onMenuClick(MenuResponse menu);
    }

    private final List<MenuResponse> items;
    private final OnMenuClickListener listener;

    public OwnerMenuAdapter(List<MenuResponse> items, OnMenuClickListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_owner_menu, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        MenuResponse m = items.get(position);
        holder.tvName.setText(m.name);
        holder.tvPrice.setText(
                NumberFormat.getNumberInstance(Locale.KOREA).format(m.price) + "원");
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) listener.onMenuClick(m);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice;
        VH(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_menu_name);
            tvPrice = itemView.findViewById(R.id.tv_menu_price);
        }
    }
}
