package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.response.ReservationResponse;
import kr.ac.baekseok.java_project.network.RetrofitClient;
import kr.ac.baekseok.java_project.util.ReservationFormat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReservationActivity extends AppCompatActivity {

    private RecyclerView rvReservations;
    private ProgressBar progress;
    private View emptyLayout;
    private final List<ReservationResponse> reservations = new ArrayList<>();
    private ReservationAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservation);
        BaseActivity.applySystemBarInsets(this);

        bindViews();
        setupBackButton();
        setupRecyclerView();
        loadReservations();
    }

    private void bindViews() {
        rvReservations = findViewById(R.id.rv_reservations);
        progress = findViewById(R.id.progress);
        emptyLayout = findViewById(R.id.empty_layout);
    }

    private void setupBackButton() {
        View btn = findViewById(R.id.btn_back);
        if (btn != null) btn.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new ReservationAdapter();
        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        rvReservations.setAdapter(adapter);
    }

    private void loadReservations() {
        showLoading(true);

        RetrofitClient.getApi().getMyReservations().enqueue(new Callback<List<ReservationResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<ReservationResponse>> call,
                                   @NonNull Response<List<ReservationResponse>> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    reservations.clear();
                    reservations.addAll(response.body());
                    adapter.notifyDataSetChanged();
                } else if (response.code() == 401) {
                    Toast.makeText(MyReservationActivity.this,
                            "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(MyReservationActivity.this,
                            "예약 내역을 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
                }
                updateEmptyState();
            }

            @Override
            public void onFailure(@NonNull Call<List<ReservationResponse>> call, @NonNull Throwable t) {
                showLoading(false);
                Toast.makeText(MyReservationActivity.this,
                        "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                updateEmptyState();
            }
        });
    }

    private void cancelReservation(ReservationResponse r, int position) {
        new AlertDialog.Builder(this)
                .setTitle("예약 취소")
                .setMessage("예약을 취소하시겠습니까?")
                .setPositiveButton("취소하기", (dialog, which) -> doCancel(r, position))
                .setNegativeButton("닫기", null)
                .show();
    }

    private void doCancel(ReservationResponse r, int position) {
        RetrofitClient.getApi()
                .deleteReservation(r.restaurantId, r.reservationId)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            Toast.makeText(MyReservationActivity.this,
                                    "예약이 취소되었습니다", Toast.LENGTH_SHORT).show();
                            reservations.remove(position);
                            adapter.notifyItemRemoved(position);
                            adapter.notifyItemRangeChanged(position, reservations.size());
                            updateEmptyState();
                        } else {
                            Toast.makeText(MyReservationActivity.this,
                                    "취소에 실패했습니다 (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(MyReservationActivity.this,
                                "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showLoading(boolean loading) {
        if (progress != null) progress.setVisibility(loading ? View.VISIBLE : View.GONE);
    }

    private void updateEmptyState() {
        if (emptyLayout != null) {
            boolean empty = reservations.isEmpty();
            emptyLayout.setVisibility(empty ? View.VISIBLE : View.GONE);
            rvReservations.setVisibility(empty ? View.GONE : View.VISIBLE);
        }
    }

    /** "2026-06-15T16:30:00" → "2026년 06월 15일 16:30" */
    private static String formatDatetime(String iso) {
        if (iso == null || iso.length() < 16) return iso != null ? iso : "";
        try {
            String[] dt = iso.split("T");
            String[] d = dt[0].split("-");
            String time = dt[1].substring(0, 5);
            return d[0] + "년 " + d[1] + "월 " + d[2] + "일 " + time;
        } catch (Exception e) {
            return iso;
        }
    }

    // ── 인라인 어댑터 ────────────────────────────────────────────────
    class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_my_reservation, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            ReservationResponse r = reservations.get(position);

            holder.tvRestaurantName.setText(r.restaurantName != null ? r.restaurantName : "식당");
            holder.tvDatetime.setText(formatDatetime(r.reservationTime));
            holder.tvStatus.setText(ReservationFormat.statusKo(r.status));
            holder.tvStatus.setTextColor(ReservationFormat.statusColor(r.status));

            // 메뉴 요약
            if (r.items != null && !r.items.isEmpty()) {
                String menuSummary = r.items.stream()
                        .map(item -> item.menuName + " x" + item.quantity)
                        .collect(Collectors.joining(", "));
                holder.tvMenus.setText(menuSummary);
                holder.tvMenus.setVisibility(View.VISIBLE);
            } else {
                holder.tvMenus.setVisibility(View.GONE);
            }

            // 총 금액
            holder.tvTotalPrice.setText(
                    NumberFormat.getInstance(Locale.KOREA).format(r.totalPrice) + "원");

            // 취소 버튼 (PENDING 상태만)
            if ("PENDING".equals(r.status)) {
                holder.btnCancel.setVisibility(View.VISIBLE);
                holder.btnCancel.setOnClickListener(v -> {
                    int pos = holder.getAdapterPosition();
                    if (pos != RecyclerView.NO_ID) cancelReservation(r, pos);
                });
            } else {
                holder.btnCancel.setVisibility(View.GONE);
            }
        }

        @Override
        public int getItemCount() {
            return reservations.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvRestaurantName, tvStatus, tvDatetime, tvMenus, tvTotalPrice, btnCancel;

            VH(@NonNull View itemView) {
                super(itemView);
                tvRestaurantName = itemView.findViewById(R.id.tv_restaurant_name);
                tvStatus = itemView.findViewById(R.id.tv_status);
                tvDatetime = itemView.findViewById(R.id.tv_datetime);
                tvMenus = itemView.findViewById(R.id.tv_menus);
                tvTotalPrice = itemView.findViewById(R.id.tv_total_price);
                btnCancel = itemView.findViewById(R.id.btn_cancel);
            }
        }
    }
}
