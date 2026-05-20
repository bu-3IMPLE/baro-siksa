package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.viewpager2.widget.ViewPager2;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.ImageCarouselAdapter;
import kr.ac.baekseok.java_project.model.Restaurant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * 가게 상세 화면 (이미지 3 우측)
 *
 * - 상단에 이미지 ViewPager2 (자동 슬라이드)
 * - 인디케이터 도트 동기화
 * - 댓글/찜 버튼
 * - 주소 옆 지도 아이콘 → 지도 화면으로 이동
 */
public class RestaurantDetailActivity extends BaseActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";
    public static final String EXTRA_RESTAURANT_NAME = "extra_restaurant_name";

    private ViewPager2 vpImages;
    private LinearLayout indicatorDots;

    private boolean isFavorited = false;

    @Override
    protected int getCurrentTab() {
        return -1;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_detail);

        setupBottomNavigation();
        setupBackButton();
        bindRestaurantData();
        setupImageCarousel();
        setupActionButtons();
        setupMapButton();
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void bindRestaurantData() {
        // Intent로 전달된 데이터 또는 더미 데이터로 화면 채우기
        String name = getIntent().getStringExtra(EXTRA_RESTAURANT_NAME);
        if (name == null) name = "00칼국수";

        Restaurant restaurant = new Restaurant(
                getIntent().getIntExtra(EXTRA_RESTAURANT_ID, 1),
                name,
                5.0f,
                "영업시간:09시30분 시작",
                "주소:충청남도 천안시 00구",
                "000길 000",
                true,
                new String[]{"000국수", "00돈까스"},
                new int[]{9000, 8000}
        );

        TextView tvName = findViewById(R.id.tv_store_name);
        if (tvName != null) tvName.setText(restaurant.getName());

        TextView tvRating = findViewById(R.id.tv_rating);
        if (tvRating != null) {
            tvRating.setText(String.format(Locale.getDefault(),
                    "평점:%.1f", restaurant.getRating()));
        }

        TextView tvHours = findViewById(R.id.tv_opening_hours);
        if (tvHours != null) tvHours.setText(restaurant.getOpeningHours());

        TextView tvAddress = findViewById(R.id.tv_address);
        if (tvAddress != null && restaurant.getAddress() != null) {
            tvAddress.setText(restaurant.getAddress());
        }

        TextView tvAddressDetail = findViewById(R.id.tv_address_detail);
        if (tvAddressDetail != null && restaurant.getAddressDetail() != null) {
            tvAddressDetail.setText(restaurant.getAddressDetail());
        }

        // 메뉴 정보
        String[] menus = restaurant.getMenuItems();
        int[] prices = restaurant.getMenuPrices();
        if (menus != null && prices != null && menus.length >= 2 && prices.length >= 2) {
            TextView tvMenu1 = findViewById(R.id.tv_menu_1);
            TextView tvMenu2 = findViewById(R.id.tv_menu_2);
            if (tvMenu1 != null) {
                tvMenu1.setText(String.format(Locale.getDefault(),
                        "%s:%,d원", menus[0], prices[0]));
            }
            if (tvMenu2 != null) {
                tvMenu2.setText(String.format(Locale.getDefault(),
                        "%s:%,d원", menus[1], prices[1]));
            }
        }
    }

    private void setupImageCarousel() {
        vpImages = findViewById(R.id.vp_images);
        indicatorDots = findViewById(R.id.indicator_dots);
        if (vpImages == null) return;

        // 더미 이미지 (실제로는 서버 URL을 받아 Glide/Picasso로 로드)
        List<Integer> images = new ArrayList<>(Arrays.asList(0, 0, 0));  // 3장 placeholder

        ImageCarouselAdapter adapter = new ImageCarouselAdapter(images);
        vpImages.setAdapter(adapter);

        // 페이지 변경 시 인디케이터 동기화
        vpImages.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                updateIndicator(position, images.size());
            }
        });

        updateIndicator(0, images.size());
    }

    private void updateIndicator(int selectedIndex, int total) {
        if (indicatorDots == null) return;

        int activeColor = ContextCompat.getColor(this, R.color.text_primary);
        int inactiveColor = ContextCompat.getColor(this, R.color.text_hint);

        // 기존 도트 개수가 total과 다르면 재구성
        if (indicatorDots.getChildCount() != total) {
            indicatorDots.removeAllViews();
            float density = getResources().getDisplayMetrics().density;
            int dotSize = (int) (6 * density);
            int dotMargin = (int) (4 * density);

            for (int i = 0; i < total; i++) {
                View dot = new View(this);
                LinearLayout.LayoutParams params =
                        new LinearLayout.LayoutParams(dotSize, dotSize);
                if (i > 0) params.leftMargin = dotMargin;
                dot.setLayoutParams(params);
                indicatorDots.addView(dot);
            }
        }

        // 색상 갱신
        for (int i = 0; i < indicatorDots.getChildCount(); i++) {
            View dot = indicatorDots.getChildAt(i);
            dot.setBackgroundColor(i == selectedIndex ? activeColor : inactiveColor);
        }
    }

    private void setupActionButtons() {
        // 댓글 아이콘
        View btnComment = findViewById(R.id.btn_comment);
        if (btnComment != null) {
            btnComment.setOnClickListener(v ->
                    Toast.makeText(this, "댓글 화면으로 이동", Toast.LENGTH_SHORT).show());
        }

        // 찜 아이콘 (토글)
        View btnFavorite = findViewById(R.id.btn_favorite);
        if (btnFavorite != null) {
            btnFavorite.setOnClickListener(v -> {
                isFavorited = !isFavorited;
                String message = isFavorited ? "찜 추가됨" : "찜 해제됨";
                Toast.makeText(this, message, Toast.LENGTH_SHORT).show();

                if (btnFavorite instanceof android.widget.ImageView) {
                    ((android.widget.ImageView) btnFavorite).setColorFilter(
                            isFavorited ? Color.RED
                                    : ContextCompat.getColor(this, R.color.text_primary));
                }
            });
        }
    }

    private void setupMapButton() {
        View btnMap = findViewById(R.id.btn_map_view);
        if (btnMap != null) {
            btnMap.setOnClickListener(v -> {
                TextView tvAddress = findViewById(R.id.tv_address);
                TextView tvDetail = findViewById(R.id.tv_address_detail);

                Intent intent = new Intent(this, MapActivity.class);
                if (tvAddress != null) {
                    intent.putExtra(MapActivity.EXTRA_ADDRESS,
                            tvAddress.getText().toString());
                }
                if (tvDetail != null) {
                    intent.putExtra(MapActivity.EXTRA_ADDRESS_DETAIL,
                            tvDetail.getText().toString());
                }
                startActivity(intent);
            });
        }
    }
}
