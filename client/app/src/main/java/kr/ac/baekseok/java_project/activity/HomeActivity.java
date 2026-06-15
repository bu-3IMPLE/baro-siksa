package kr.ac.baekseok.java_project.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.location.LocationManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.RestaurantListAdapter;
import kr.ac.baekseok.java_project.dto.response.PageRestaurantResponse;
import kr.ac.baekseok.java_project.dto.response.RestaurantResponse;
import kr.ac.baekseok.java_project.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeActivity extends BaseActivity {

    private RecyclerView rvRestaurants;
    private ProgressBar progress;
    private TextView tvEmpty;
    private TextView tvLocation;

    private final List<RestaurantResponse> restaurants = new ArrayList<>();
    private RestaurantListAdapter adapter;
    private LocationManager locationManager;

    private final ActivityResultLauncher<String[]> locationPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), result -> {
                boolean granted = Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_FINE_LOCATION))
                        || Boolean.TRUE.equals(result.get(Manifest.permission.ACCESS_COARSE_LOCATION));
                if (granted) {
                    fetchLocation();
                } else {
                    tvLocation.setText("위치 권한 없음");
                }
            });

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
        setupSearchBar();

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        requestLocationOrFetch();
        loadRestaurants();
    }

    private void bindViews() {
        rvRestaurants = findViewById(R.id.rv_restaurants);
        progress = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);
        tvLocation = findViewById(R.id.tv_location);
    }

    private void setupRecyclerView() {
        rvRestaurants.setLayoutManager(new LinearLayoutManager(this));
        adapter = new RestaurantListAdapter(restaurants, this::openDetail);
        rvRestaurants.setAdapter(adapter);
    }

    private void setupSearchBar() {
        View searchBar = findViewById(R.id.search_bar);
        if (searchBar != null) {
            searchBar.setOnClickListener(v ->
                    startActivity(new Intent(this, SearchActivity.class)));
        }
    }

    private void requestLocationOrFetch() {
        boolean fineGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
        boolean coarseGranted = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;

        if (fineGranted || coarseGranted) {
            fetchLocation();
        } else {
            locationPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        // GPS → Network 순으로 사용 가능한 provider 선택 (에뮬레이터 mock location 지원)
        String provider;
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            provider = LocationManager.GPS_PROVIDER;
        } else if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            provider = LocationManager.NETWORK_PROVIDER;
        } else {
            tvLocation.setText("위치를 사용할 수 없습니다");
            return;
        }

        locationManager.getCurrentLocation(provider, null, getMainExecutor(), location -> {
            if (location != null) {
                updateLocationLabel(location);
            } else {
                // getCurrentLocation 실패 시 마지막 알려진 위치 사용
                Location last = locationManager.getLastKnownLocation(provider);
                if (last != null) updateLocationLabel(last);
            }
        });
    }

    private void updateLocationLabel(Location location) {
        Geocoder geocoder = new Geocoder(this, Locale.KOREAN);
        // API 33+ 비동기 Geocoder 사용
        geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1, addresses -> {
            if (addresses != null && !addresses.isEmpty()) {
                Address addr = addresses.get(0);
                String dong = addr.getSubLocality();   // 동
                String gu = addr.getSubAdminArea();    // 구
                String city = addr.getLocality();      // 시
                String label = dong != null ? dong : (gu != null ? gu : (city != null ? city : "현재 위치"));
                runOnUiThread(() -> tvLocation.setText(label));
            }
        });
    }

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
                        } else {
                            Toast.makeText(HomeActivity.this,
                                    "목록을 불러오지 못했습니다 (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                        updateEmptyState();
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
