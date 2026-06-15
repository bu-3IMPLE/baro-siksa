package kr.ac.baekseok.java_project.activity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.request.ReservationCreateRequest;
import kr.ac.baekseok.java_project.dto.request.ReservationMenuItemRequest;
import kr.ac.baekseok.java_project.dto.response.MenuResponse;
import kr.ac.baekseok.java_project.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationFormActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";
    public static final String EXTRA_RESTAURANT_NAME = "extra_restaurant_name";
    public static final String EXTRA_TABLE_ID = "extra_table_id";
    public static final String EXTRA_TABLE_NUMBER = "extra_table_number";

    private long restaurantId;
    private long tableId;
    private String tableNumber;

    private TextView tvTableInfo;
    private TextView tvSelectedDatetime;
    private TextView tvTotalPrice;
    private RecyclerView rvMenus;

    private String selectedDatetime = null;
    private final List<MenuResponse> menuList = new ArrayList<>();
    private final List<Integer> quantities = new ArrayList<>();

    private MenuSelectionAdapter menuAdapter;
    private boolean isSubmitting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_form);
        BaseActivity.applySystemBarInsets(this);

        restaurantId = getIntent().getLongExtra(EXTRA_RESTAURANT_ID, -1);
        tableId = getIntent().getLongExtra(EXTRA_TABLE_ID, -1);
        tableNumber = getIntent().getStringExtra(EXTRA_TABLE_NUMBER);

        bindViews();
        setupBackButton();
        setupDatetimePicker();
        setupSubmitButton();
        loadMenus();
    }

    private void bindViews() {
        tvTableInfo = findViewById(R.id.tv_table_info);
        tvSelectedDatetime = findViewById(R.id.tv_selected_datetime);
        tvTotalPrice = findViewById(R.id.tv_total_price);
        rvMenus = findViewById(R.id.rv_menus);

        tvTableInfo.setText("테이블 " + tableNumber);
    }

    private void setupBackButton() {
        View btn = findViewById(R.id.btn_back);
        if (btn != null) btn.setOnClickListener(v -> finish());
    }

    private void setupDatetimePicker() {
        Button btnPick = findViewById(R.id.btn_pick_datetime);
        if (btnPick == null) return;

        btnPick.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            new DatePickerDialog(this, (view, year, month, day) -> {
                new TimePickerDialog(this, (tv, hour, minute) -> {
                    selectedDatetime = String.format(Locale.getDefault(),
                            "%04d-%02d-%02dT%02d:%02d:00", year, month + 1, day, hour, minute);
                    tvSelectedDatetime.setText(String.format(Locale.getDefault(),
                            "%04d년 %02d월 %02d일 %02d:%02d", year, month + 1, day, hour, minute));
                }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true).show();
            }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void loadMenus() {
        RetrofitClient.getApi().getMenus(restaurantId).enqueue(new Callback<List<MenuResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<MenuResponse>> call,
                                   @NonNull Response<List<MenuResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    menuList.clear();
                    menuList.addAll(response.body());
                    quantities.clear();
                    for (int i = 0; i < menuList.size(); i++) quantities.add(0);
                    setupMenuRecyclerView();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<MenuResponse>> call, @NonNull Throwable t) {
                Toast.makeText(ReservationFormActivity.this, "메뉴를 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupMenuRecyclerView() {
        menuAdapter = new MenuSelectionAdapter();
        rvMenus.setLayoutManager(new LinearLayoutManager(this));
        rvMenus.setAdapter(menuAdapter);
    }

    private void updateTotalPrice() {
        int total = 0;
        for (int i = 0; i < menuList.size(); i++) {
            total += menuList.get(i).price * quantities.get(i);
        }
        tvTotalPrice.setText(NumberFormat.getInstance(Locale.KOREA).format(total) + "원");
    }

    private void setupSubmitButton() {
        Button btnSubmit = findViewById(R.id.btn_submit);
        if (btnSubmit == null) return;

        btnSubmit.setOnClickListener(v -> {
            if (isSubmitting) return;

            if (selectedDatetime == null) {
                Toast.makeText(this, "예약 날짜/시간을 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            List<ReservationMenuItemRequest> items = new ArrayList<>();
            for (int i = 0; i < menuList.size(); i++) {
                if (quantities.get(i) > 0) {
                    items.add(new ReservationMenuItemRequest(menuList.get(i).id, quantities.get(i)));
                }
            }

            if (items.isEmpty()) {
                Toast.makeText(this, "메뉴를 최소 1개 이상 선택해주세요", Toast.LENGTH_SHORT).show();
                return;
            }

            isSubmitting = true;
            btnSubmit.setEnabled(false);

            ReservationCreateRequest request = new ReservationCreateRequest(tableId, selectedDatetime, items);
            RetrofitClient.getApi().createReservation(restaurantId, request).enqueue(new Callback<Long>() {
                @Override
                public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                    isSubmitting = false;
                    btnSubmit.setEnabled(true);
                    if (response.isSuccessful()) {
                        Toast.makeText(ReservationFormActivity.this,
                                "예약이 완료되었습니다!", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    } else {
                        String msg = response.code() == 400
                                ? "이미 예약된 테이블입니다"
                                : "예약에 실패했습니다 (" + response.code() + ")";
                        Toast.makeText(ReservationFormActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                    isSubmitting = false;
                    btnSubmit.setEnabled(true);
                    Toast.makeText(ReservationFormActivity.this, "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                }
            });
        });
    }

    // 인라인 어댑터: 메뉴별 수량 선택
    class MenuSelectionAdapter extends RecyclerView.Adapter<MenuSelectionAdapter.VH> {

        @NonNull
        @Override
        public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_reservation_menu, parent, false);
            return new VH(v);
        }

        @Override
        public void onBindViewHolder(@NonNull VH holder, int position) {
            MenuResponse menu = menuList.get(position);
            holder.tvName.setText(menu.name);
            holder.tvPrice.setText(NumberFormat.getInstance(Locale.KOREA).format(menu.price) + "원");
            holder.tvQty.setText(String.valueOf(quantities.get(position)));

            holder.btnMinus.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                int qty = quantities.get(pos);
                if (qty > 0) {
                    quantities.set(pos, qty - 1);
                    holder.tvQty.setText(String.valueOf(qty - 1));
                    updateTotalPrice();
                }
            });

            holder.btnPlus.setOnClickListener(v -> {
                int pos = holder.getAdapterPosition();
                int qty = quantities.get(pos);
                quantities.set(pos, qty + 1);
                holder.tvQty.setText(String.valueOf(qty + 1));
                updateTotalPrice();
            });
        }

        @Override
        public int getItemCount() {
            return menuList.size();
        }

        class VH extends RecyclerView.ViewHolder {
            TextView tvName, tvPrice, tvQty;
            View btnPlus, btnMinus;

            VH(@NonNull View itemView) {
                super(itemView);
                tvName = itemView.findViewById(R.id.tv_menu_name);
                tvPrice = itemView.findViewById(R.id.tv_menu_price);
                tvQty = itemView.findViewById(R.id.tv_quantity);
                btnPlus = itemView.findViewById(R.id.btn_plus);
                btnMinus = itemView.findViewById(R.id.btn_minus);
            }
        }
    }
}
