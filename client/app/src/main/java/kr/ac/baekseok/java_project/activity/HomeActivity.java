package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.model.Restaurant;

/**
 * 홈 화면 - 오늘 동네 맛집 그리드 (이미지 1)
 */
public class HomeActivity extends BaseActivity {

    private Restaurant[] todayRestaurants;

    @Override
    protected int getCurrentTab() {
        return 0;  // 홈 탭
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        setupBottomNavigation();
        prepareSampleData();
        setupSearchBar();
        setupSeeMore();
        setupRestaurantCards();
    }

    private void prepareSampleData() {
        // 실제 앱에서는 서버 또는 DB에서 가져온다
        todayRestaurants = new Restaurant[]{
                new Restaurant(1, "00칼국수", 5.0f, "영업시간:09시30분 영업시작"),
                new Restaurant(2, "맛있는 분식", 4.5f, "영업시간:10시 영업시작"),
                new Restaurant(3, "할머니 손맛", 5.0f, "영업시간:11시 영업시작"),
                new Restaurant(4, "동네 카페", 4.0f, "영업시간:08시 영업시작"),
        };
    }

    private void setupSearchBar() {
        View searchBar = findViewById(R.id.search_bar);
        if (searchBar != null) {
            searchBar.setOnClickListener(v -> {
                Intent intent = new Intent(this, SearchActivity.class);
                startActivity(intent);
            });
        }
    }

    private void setupSeeMore() {
        TextView seeMore = findViewById(R.id.tv_see_more);
        if (seeMore != null) {
            seeMore.setOnClickListener(v -> {
                Toast.makeText(this, "더보기 - 추후 구현", Toast.LENGTH_SHORT).show();
            });
        }
    }

    private void setupRestaurantCards() {
        // 4개의 include된 카드에 데이터와 클릭 리스너 연결
        int[] cardIds = {
                R.id.item_restaurant_1,
                R.id.item_restaurant_2,
                R.id.item_restaurant_3,
                R.id.item_restaurant_4
        };

        for (int i = 0; i < cardIds.length && i < todayRestaurants.length; i++) {
            View card = findViewById(cardIds[i]);
            if (card == null) continue;

            final Restaurant restaurant = todayRestaurants[i];

            // include된 레이아웃의 상호명 TextView 갱신
            TextView tvStoreName = card.findViewById(R.id.tv_store_name);
            if (tvStoreName != null) {
                tvStoreName.setText(restaurant.getName());
            }

            // 카드 클릭 시 상세 화면으로 이동
            card.setOnClickListener(v -> openDetail(restaurant));
        }
    }

    private void openDetail(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT_ID, restaurant.getId());
        intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
        startActivity(intent);
    }
}
