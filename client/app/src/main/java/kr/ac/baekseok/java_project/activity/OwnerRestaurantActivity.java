package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.OwnerMenuAdapter;
import kr.ac.baekseok.java_project.dto.response.MenuResponse;
import kr.ac.baekseok.java_project.dto.response.RestaurantResponse;
import kr.ac.baekseok.java_project.network.RetrofitClient;
import kr.ac.baekseok.java_project.util.OwnerStore;
import kr.ac.baekseok.java_project.util.RestaurantFormat;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 사장님(OWNER) 식당 관리 허브.
 *
 * - 내 식당이 없으면: 등록 유도 화면
 * - 내 식당이 있으면: 식당 정보 + 메뉴 목록 + 예약 관리 진입
 *
 * "내 식당"은 OwnerStore에 저장된 restaurantId 기준.
 * (서버에 내 식당 목록 API가 없어서 등록 시 받은 id를 로컬 저장)
 */
public class OwnerRestaurantActivity extends AppCompatActivity {

    private View infoCard, emptyNoRestaurant;
    private TextView tvName, tvCategoryAddress, tvHours, tvMenuEmpty;
    private ProgressBar progress;
    private RecyclerView rvMenus;

    private final List<MenuResponse> menus = new ArrayList<>();
    private OwnerMenuAdapter menuAdapter;

    private long restaurantId = -1;

    private ActivityResultLauncher<Intent> formLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_restaurant);
        BaseActivity.applySystemBarInsets(this);

        bindViews();
        setupMenuList();
        registerLauncher();
        setupButtons();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 화면에 돌아올 때마다 최신 상태 반영
        refresh();
    }

    private void bindViews() {
        infoCard = findViewById(R.id.info_card);
        emptyNoRestaurant = findViewById(R.id.empty_no_restaurant);
        tvName = findViewById(R.id.tv_name);
        tvCategoryAddress = findViewById(R.id.tv_category_address);
        tvHours = findViewById(R.id.tv_hours);
        tvMenuEmpty = findViewById(R.id.tv_menu_empty);
        progress = findViewById(R.id.progress);
        rvMenus = findViewById(R.id.rv_menus);

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupMenuList() {
        rvMenus.setLayoutManager(new LinearLayoutManager(this));
        menuAdapter = new OwnerMenuAdapter(menus, this::openMenuEdit);
        rvMenus.setAdapter(menuAdapter);
    }

    private void registerLauncher() {
        // 등록/수정 화면에서 돌아오면 갱신
        formLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> refresh());
    }

    private void setupButtons() {
        // 식당 등록 (식당 없을 때)
        setClick(R.id.btn_register_restaurant, v -> {
            Intent i = new Intent(this, RestaurantFormActivity.class);
            formLauncher.launch(i);
        });

        // 식당 정보 수정
        setClick(R.id.btn_edit_info, v -> {
            Intent i = new Intent(this, RestaurantFormActivity.class);
            i.putExtra(RestaurantFormActivity.EXTRA_RESTAURANT_ID, restaurantId);
            formLauncher.launch(i);
        });

        // 메뉴 추가
        setClick(R.id.btn_add_menu, v -> {
            Intent i = new Intent(this, MenuFormActivity.class);
            i.putExtra(MenuFormActivity.EXTRA_RESTAURANT_ID, restaurantId);
            formLauncher.launch(i);
        });

        // 예약 관리
        setClick(R.id.btn_manage_reservations, v -> {
            Intent i = new Intent(this, OwnerReservationActivity.class);
            i.putExtra(OwnerReservationActivity.EXTRA_RESTAURANT_ID, restaurantId);
            startActivity(i);
        });
    }

    /** 내 식당 유무에 따라 화면 전환 */
    private void refresh() {
        restaurantId = OwnerStore.getRestaurantId(this);

        if (restaurantId < 0) {
            // 식당 없음 → 등록 유도
            infoCard.setVisibility(View.GONE);
            emptyNoRestaurant.setVisibility(View.VISIBLE);
            // ScrollView 안의 내용도 숨기려면 부모를 가리는 게 낫지만,
            // 간단히 빈 상태 화면만 위에 띄움
            return;
        }

        emptyNoRestaurant.setVisibility(View.GONE);
        infoCard.setVisibility(View.VISIBLE);
        loadRestaurant();
        loadMenus();
    }

    private void loadRestaurant() {
        progress.setVisibility(View.VISIBLE);
        RetrofitClient.getApi().getRestaurant(restaurantId)
                .enqueue(new Callback<RestaurantResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RestaurantResponse> call,
                                           @NonNull Response<RestaurantResponse> response) {
                        progress.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            bindRestaurant(response.body());
                        } else if (response.code() == 404) {
                            // 서버에서 식당이 삭제됨 → 로컬 정보 클리어
                            OwnerStore.clear(OwnerRestaurantActivity.this);
                            refresh();
                        } else {
                            toast("식당 정보를 불러오지 못했습니다 (" + response.code() + ")");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RestaurantResponse> call,
                                          @NonNull Throwable t) {
                        progress.setVisibility(View.GONE);
                        toast("서버에 연결할 수 없습니다");
                    }
                });
    }

    private void bindRestaurant(RestaurantResponse r) {
        tvName.setText(r.name);
        tvCategoryAddress.setText(
                RestaurantFormat.categoryKo(r.category) + " · " +
                        (r.address != null ? r.address : ""));
        tvHours.setText("영업 " + RestaurantFormat.openHours(r.openTime, r.closeTime));
    }

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
                        }
                        updateMenuEmpty();
                    }
                    @Override
                    public void onFailure(@NonNull Call<List<MenuResponse>> call,
                                          @NonNull Throwable t) {
                        updateMenuEmpty();
                    }
                });
    }

    private void updateMenuEmpty() {
        tvMenuEmpty.setVisibility(menus.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void openMenuEdit(MenuResponse m) {
        Intent i = new Intent(this, MenuFormActivity.class);
        i.putExtra(MenuFormActivity.EXTRA_RESTAURANT_ID, restaurantId);
        i.putExtra(MenuFormActivity.EXTRA_MENU_ID, m.id);
        i.putExtra(MenuFormActivity.EXTRA_MENU_NAME, m.name);
        i.putExtra(MenuFormActivity.EXTRA_MENU_PRICE, m.price);
        i.putExtra(MenuFormActivity.EXTRA_MENU_DESC, m.description);
        formLauncher.launch(i);
    }

    private void setClick(int id, View.OnClickListener l) {
        View v = findViewById(id);
        if (v != null) v.setOnClickListener(l);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
