package kr.ac.baekseok.java_project.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.response.TableResponse;

public class TableAdapter extends RecyclerView.Adapter<TableAdapter.ViewHolder> {

    public interface OnTableClickListener {
        void onTableClick(TableResponse table);
    }

    public interface OnTableLongClickListener {
        void onTableLongClick(TableResponse table);
    }

    private final List<TableResponse> tables;
    private final OnTableClickListener listener;
    private OnTableLongClickListener longClickListener;

    public TableAdapter(List<TableResponse> tables, OnTableClickListener listener) {
        this.tables = tables;
        this.listener = listener;
    }

    public void setLongClickListener(OnTableLongClickListener longClickListener) {
        this.longClickListener = longClickListener;
    }

    /** WebSocket으로 단일 테이블 상태 업데이트 */
    public void updateTable(TableResponse updated) {
        for (int i = 0; i < tables.size(); i++) {
            if (tables.get(i).tableId.equals(updated.tableId)) {
                tables.set(i, updated);
                notifyItemChanged(i);
                return;
            }
        }
        // 신규 테이블이면 추가
        tables.add(updated);
        notifyItemInserted(tables.size() - 1);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_table, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TableResponse table = tables.get(position);
        holder.bind(table, listener, longClickListener);
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        private final View cardView;
        private final TextView tvNumber;
        private final TextView tvCapacity;
        private final TextView tvStatus;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_table);
            tvNumber = itemView.findViewById(R.id.tv_table_number);
            tvCapacity = itemView.findViewById(R.id.tv_table_capacity);
            tvStatus = itemView.findViewById(R.id.tv_table_status);
        }

        void bind(TableResponse table, OnTableClickListener listener,
                  OnTableLongClickListener longClickListener) {
            tvNumber.setText(table.tableNumber);
            tvCapacity.setText(table.capacity + "인석");

            boolean isAvailable = "AVAILABLE".equals(table.status);

            String status = table.status != null ? table.status : "";
            switch (status) {
                case "AVAILABLE":
                    tvStatus.setText("예약 가능");
                    cardView.setBackgroundColor(Color.parseColor("#E8F5E9"));
                    tvStatus.setTextColor(Color.parseColor("#2E7D32"));
                    break;
                case "RESERVED":
                    tvStatus.setText("예약 중");
                    cardView.setBackgroundColor(Color.parseColor("#FFF9C4"));
                    tvStatus.setTextColor(Color.parseColor("#F57F17"));
                    break;
                case "OCCUPIED":
                    tvStatus.setText("착석 중");
                    cardView.setBackgroundColor(Color.parseColor("#FFEBEE"));
                    tvStatus.setTextColor(Color.parseColor("#C62828"));
                    break;
                default:
                    tvStatus.setText("-");
                    cardView.setBackgroundColor(Color.LTGRAY);
                    break;
            }

            if (isAvailable) {
                cardView.setOnClickListener(v -> listener.onTableClick(table));
                cardView.setAlpha(1.0f);
            } else {
                cardView.setOnClickListener(null);
                cardView.setAlpha(0.7f);
            }

            if (longClickListener != null) {
                cardView.setOnLongClickListener(v -> {
                    longClickListener.onTableLongClick(table);
                    return true;
                });
            } else {
                cardView.setOnLongClickListener(null);
            }
        }
    }
}
