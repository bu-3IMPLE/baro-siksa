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
 * 메뉴 목록 어댑터.
 * MenuResponse.inDangerous == true 이면 알레르기 경고를 표시한다.
 */
public class MenuListAdapter
        extends RecyclerView.Adapter<MenuListAdapter.MenuViewHolder> {

    private final List<MenuResponse> items;

    public MenuListAdapter(List<MenuResponse> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public MenuViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu, parent, false);
        return new MenuViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuViewHolder holder, int position) {
        MenuResponse m = items.get(position);

        holder.tvName.setText(m.name);
        holder.tvPrice.setText(formatPrice(m.price));
        if (m.description != null && !m.description.isEmpty()) {
            holder.tvDesc.setVisibility(View.VISIBLE);
            holder.tvDesc.setText(m.description);
        } else {
            holder.tvDesc.setVisibility(View.GONE);
        }

        // 알레르기 경고 (서버가 회원 취향 기준으로 판단한 값)
        holder.tvWarning.setVisibility(m.inDangerous ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private String formatPrice(int price) {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(price) + "원";
    }

    static class MenuViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPrice, tvDesc, tvWarning;

        MenuViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_menu_name);
            tvPrice = itemView.findViewById(R.id.tv_menu_price);
            tvDesc = itemView.findViewById(R.id.tv_menu_desc);
            tvWarning = itemView.findViewById(R.id.tv_menu_warning);
        }
    }
}
