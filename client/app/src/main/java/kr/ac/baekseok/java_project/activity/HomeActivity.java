package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.RestaurantListAdapter;
import kr.ac.baekseok.java_project.dto.response.PageRestaurantResponse;
import kr.ac.baekseok.java_project.dto.response.RestaurantResponse;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 홈 화면 - 실제 서버에서 식당 목록 조회 (searchRestaurants).
 * 더미 데이터 제거. RecyclerView로 표시.
 *
 * 위치/지도 기능은 제외한 버전.
 * (CurrentLocationActivity가 필요하면 v5의 Google Maps 설정을 적용한 뒤 연결)
 */
public class HomeActivity extends BaseActivity {

    private RecyclerView rvRestaurants;
    private ProgressBar progress;
    private TextView tvEmpty;

    private final List<RestaurantResponse> restaurants = new ArrayList<>();
    private RestaurantListAdapter adapter;

    @Override
    protected int getCurrentTab() {
        return 0;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupBottomNavigation();
        bindViews();
        setupRecyclerView();
        setupHeader();

        loadRestaurants();
    }

    private void bindViews() {
        rvRestaurants = findViewById(R.id.rv_restaurants);
        progress = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);
    }

    private void setupRecyclerView() {
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantListAdapter(restaurants, this::openDetail);
        rvRestaurants.setAdapter(adapter);
    }

    private void setupHeader() {
        // 위치 탭 → 지도 기능 제외 상태이므로 안내만
        View locationRow = findViewById(R.id.location_row);
        if (locationRow != null) {
            locationRow.setOnClickListener(v ->
                    Toast.makeText(this, "위치 기능은 준비 중입니다",
                            Toast.LENGTH_SHORT).show());
        }

        // 검색바 탭 → 검색 화면
        View searchBar = findViewById(R.id.search_bar);
        if (searchBar != null) {
            searchBar.setOnClickListener(v ->
                    startActivity(new Intent(this, SearchActivity.class)));
        }
    }

    /**
     * 서버에서 식당 목록을 가져온다.
     * searchRestaurants(name=null, category=null, page=0, size=20)
     * → 조건 없이 첫 페이지 20개.
     */
    private void loadRestaurants() {
        showLoading(true);

        RetrofitClient.getApi()
                .searchRestaurants(null, null, 0, 20)
                .enqueue(new Callback<PageRestaurantResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<PageRestaurantResponse> call,
                                           @NonNull Response<PageRestaurantResponse> response) {
                        showLoading(false);
                        if (response.isSuccessful() && response.body() != null) {
                            List<RestaurantResponse> content = response.body().content;
                            restaurants.clear();
                            if (content != null) restaurants.addAll(content);
                            adapter.notifyDataSetChanged();
                            updateEmptyState();
                        } else {
                            Toast.makeText(HomeActivity.this,
                                    "목록을 불러오지 못했습니다 (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                            updateEmptyState();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<PageRestaurantResponse> call,
                                          @NonNull Throwable t) {
                        showLoading(false);
                        Toast.makeText(HomeActivity.this,
                                "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                        updateEmptyState();
                    }
                });
    }

    private void openDetail(RestaurantResponse r) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT_ID, r.id);
        intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT_NAME, r.name);
        startActivity(intent);
    }

    private void showLoading(boolean loading) {
        if (progress != null) progress.setVisibility(loading ? View.VISIBLE : View.GONE);
        if (loading && tvEmpty != null) tvEmpty.setVisibility(View.GONE);
    }

    private void updateEmptyState() {
        if (tvEmpty != null) {
            tvEmpty.setVisibility(restaurants.isEmpty() ? View.VISIBLE : View.GONE);
        }
    }
}
