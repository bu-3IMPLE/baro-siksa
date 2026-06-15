package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.MenuListAdapter;
import kr.ac.baekseok.java_project.dto.response.MenuResponse;
import kr.ac.baekseok.java_project.dto.response.RestaurantResponse;
import kr.ac.baekseok.java_project.network.RetrofitClient;
import kr.ac.baekseok.java_project.util.RestaurantFormat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 식당 상세 화면 - 실제 서버 연동.
 * getRestaurant(상세) + getMenus(메뉴 목록) 두 API 호출.
 * 더미 데이터 제거.
 */
public class RestaurantDetailActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";
    public static final String EXTRA_RESTAURANT_NAME = "extra_restaurant_name";

    private long restaurantId;

    private TextView tvTitle, tvName, tvCategory, tvDescription, tvHours,
            tvBreak, tvClosedDays, tvPhone, tvAddress, tvMenuEmpty;
    private View breakRow, closedRow, phoneRow;
    private ProgressBar progress;
    private RecyclerView rvMenus;

    private final List<MenuResponse> menus = new ArrayList<>();
    private MenuListAdapter menuAdapter;

    private RestaurantResponse restaurant;  // 예약 화면에 넘길 때 사용

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);
        BaseActivity.applySystemBarInsets(this);

        restaurantId = getIntent().getLongExtra(EXTRA_RESTAURANT_ID, -1);
        String name = getIntent().getStringExtra(EXTRA_RESTAURANT_NAME);

        bindViews();
        setupMenuList();

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        // 제목은 넘겨받은 이름으로 우선 표시 (상세 로드 전 빈 화면 방지)
        if (name != null) {
            tvTitle.setText(name);
            tvName.setText(name);
        }

        View btnReserve = findViewById(R.id.btn_reserve);
        if (btnReserve != null) {
            btnReserve.setOnClickListener(v ->
                    Toast.makeText(this, "예약 기능은 다음 단계에서 연결됩니다",
                            Toast.LENGTH_SHORT).show());
        }

        if (restaurantId < 0) {
            Toast.makeText(this, "잘못된 접근입니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadRestaurant();
        loadMenus();
    }

    private void bindViews() {
        tvTitle = findViewById(R.id.tv_title);
        tvName = findViewById(R.id.tv_name);
        tvCategory = findViewById(R.id.tv_category);
        tvDescription = findViewById(R.id.tv_description);
        tvHours = findViewById(R.id.tv_hours);
        tvBreak = findViewById(R.id.tv_break);
        tvClosedDays = findViewById(R.id.tv_closed_days);
        tvPhone = findViewById(R.id.tv_phone);
        tvAddress = findViewById(R.id.tv_address);
        tvMenuEmpty = findViewById(R.id.tv_menu_empty);
        breakRow = findViewById(R.id.break_row);
        closedRow = findViewById(R.id.closed_row);
        phoneRow = findViewById(R.id.phone_row);
        progress = findViewById(R.id.progress);
        rvMenus = findViewById(R.id.rv_menus);
    }

    private void setupMenuList() {
        rvMenus.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new MenuListAdapter(menus);
        rvMenus.setAdapter(menuAdapter);
    }

    /** 식당 상세 정보 조회 */
    private void loadRestaurant() {
        if (progress != null) progress.setVisibility(View.VISIBLE);

        RetrofitClient.getApi().getRestaurant(restaurantId)
                .enqueue(new Callback<RestaurantResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RestaurantResponse> call,
                                           @NonNull Response<RestaurantResponse> response) {
                        if (progress != null) progress.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            restaurant = response.body();
                            bindRestaurant(restaurant);
                        } else {
                            Toast.makeText(RestaurantDetailActivity.this,
                                    "식당 정보를 불러오지 못했습니다 (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<RestaurantResponse> call,
                                          @NonNull Throwable t) {
                        if (progress != null) progress.setVisibility(View.GONE);
                        Toast.makeText(RestaurantDetailActivity.this,
                                "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void bindRestaurant(RestaurantResponse r) {
        tvTitle.setText(r.name);
        tvName.setText(r.name);
        tvCategory.setText(RestaurantFormat.categoryKo(r.category));

        if (r.description != null && !r.description.isEmpty()) {
            tvDescription.setVisibility(View.VISIBLE);
            tvDescription.setText(r.description);
        } else {
            tvDescription.setVisibility(View.GONE);
        }

        tvHours.setText(RestaurantFormat.openHours(r.openTime, r.closeTime));
        tvAddress.setText(r.address != null ? r.address : "-");

        // 브레이크타임 (둘 다 있을 때만)
        if (r.breakStartTime != null && r.breakEndTime != null) {
            breakRow.setVisibility(View.VISIBLE);
            tvBreak.setText(RestaurantFormat.openHours(r.breakStartTime, r.breakEndTime));
        }

        // 휴무일
        if (r.closedDays != null && !r.closedDays.isEmpty()) {
            closedRow.setVisibility(View.VISIBLE);
            tvClosedDays.setText(r.closedDays);
        }

        // 전화번호
        if (r.phoneNumber != null && !r.phoneNumber.isEmpty()) {
            phoneRow.setVisibility(View.VISIBLE);
            tvPhone.setText(r.phoneNumber);
        }
    }

    /** 식당 메뉴 목록 조회 */
    private void loadMenus() {
        RetrofitClient.getApi().getMenus(restaurantId)
                .enqueue(new Callback<List<MenuResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<MenuResponse>> call,
                                           @NonNull Response<List<MenuResponse>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            menus.clear();
                            menus.addAll(response.body());
                            menuAdapter.notifyDataSetChanged();
                            updateMenuEmptyState();
                        } else {
                            updateMenuEmptyState();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<MenuResponse>> call,
                                          @NonNull Throwable t) {
                        updateMenuEmptyState();
                    }
                });
    }

    private void updateMenuEmptyState() {
        if (tvMenuEmpty != null) {
            tvMenuEmpty.setVisibility(menus.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
