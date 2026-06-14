package kr.ac.baekseok.java_project.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.response.OwnerReservationResponse;
import kr.ac.baekseok.java_project.dto.response.ReservationItemDetail;
import kr.ac.baekseok.java_project.util.DateTimeUtil;
import kr.ac.baekseok.java_project.util.ReservationFormat;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * 사장님용 예약 목록 어댑터.
 * 각 예약에 상태 변경 버튼(확정/취소/완료)을 제공.
 */
public class OwnerReservationAdapter
        extends RecyclerView.Adapter<OwnerReservationAdapter.VH> {

    public interface OnStatusChangeListener {
        void onStatusChange(OwnerReservationResponse reservation, String newStatus);
    }

    private final List<OwnerReservationResponse> items;
    private final OnStatusChangeListener listener;

    public OwnerReservationAdapter(List<OwnerReservationResponse> items,
                                   OnStatusChangeListener listener) {
        this.items = items;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_owner_reservation, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        OwnerReservationResponse r = items.get(position);

        holder.tvTime.setText(DateTimeUtil.isoToDisplay(r.reservationTime));
        holder.tvStatus.setText(ReservationFormat.statusKo(r.status));
        holder.tvStatus.setTextColor(ReservationFormat.statusColor(r.status));
        holder.tvPrice.setText(
                NumberFormat.getNumberInstance(Locale.KOREA).format(r.totalPrice) + "원");

        // 메뉴 요약
        StringBuilder sb = new StringBuilder();
        if (r.items != null) {
            for (ReservationItemDetail item : r.items) {
                if (sb.length() > 0) sb.append(", ");
                sb.append(item.menuName).append(" x").append(item.quantity);
            }
        }
        holder.tvMenus.setText(sb.toString());

        // 상태에 따라 버튼 표시 제어
        // PENDING: 확정/거절(취소) 가능
        // CONFIRMED: 완료 처리 가능
        boolean isPending = "PENDING".equals(r.status);
        boolean isConfirmed = "CONFIRMED".equals(r.status);

        holder.btnConfirm.setVisibility(isPending ? View.VISIBLE : View.GONE);
        holder.btnReject.setVisibility(isPending ? View.VISIBLE : View.GONE);
        holder.btnComplete.setVisibility(isConfirmed ? View.VISIBLE : View.GONE);

        holder.btnConfirm.setOnClickListener(v -> {
            if (listener != null) listener.onStatusChange(r, "CONFIRMED");
        });
        holder.btnReject.setOnClickListener(v -> {
            if (listener != null) listener.onStatusChange(r, "CANCELED");
        });
        holder.btnComplete.setOnClickListener(v -> {
            if (listener != null) listener.onStatusChange(r, "COMPLETED");
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTime, tvStatus, tvMenus, tvPrice, btnConfirm, btnReject, btnComplete;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTime = itemView.findViewById(R.id.tv_time);
            tvStatus = itemView.findViewById(R.id.tv_status);
            tvMenus = itemView.findViewById(R.id.tv_menus);
            tvPrice = itemView.findViewById(R.id.tv_price);
            btnConfirm = itemView.findViewById(R.id.btn_confirm);
            btnReject = itemView.findViewById(R.id.btn_reject);
            btnComplete = itemView.findViewById(R.id.btn_complete);
        }
    }
}
