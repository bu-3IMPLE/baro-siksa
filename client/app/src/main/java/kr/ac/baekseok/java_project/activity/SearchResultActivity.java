package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.SearchResultAdapter;
import kr.ac.baekseok.java_project.model.Restaurant;

import java.util.ArrayList;
import java.util.List;

/**
 * 검색 결과 화면 (이미지 3 가운데)
 */
public class SearchResultActivity extends BaseActivity {

    public static final String EXTRA_KEYWORD = "extra_keyword";

    private EditText etSearch;
    private TextView tvResultLabel;
    private RecyclerView rvResults;
    private SearchResultAdapter adapter;

    private final List<Restaurant> allResults = new ArrayList<>();

    @Override
    protected int getCurrentTab() {
        return -1;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        setupBottomNavigation();
        setupViews();
        setupBackButton();
        setupSearchInput();
        setupFilterButton();
        setupRecyclerView();

        // Intent로 전달된 키워드로 초기 검색
        String keyword = getIntent().getStringExtra(EXTRA_KEYWORD);
        if (keyword != null && !keyword.isEmpty()) {
            etSearch.setText(keyword);
            performSearch(keyword);
        }
    }

    private void setupViews() {
        etSearch = findViewById(R.id.et_search);
        tvResultLabel = findViewById(R.id.tv_search_result_label);
        rvResults = findViewById(R.id.rv_search_results);
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        ImageView btnClear = findViewById(R.id.btn_clear);
        if (btnClear != null) {
            btnClear.setOnClickListener(v -> {
                if (etSearch != null) etSearch.setText("");
            });
        }
    }

    private void setupSearchInput() {
        if (etSearch == null) return;

        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String keyword = etSearch.getText().toString().trim();
                if (!keyword.isEmpty()) performSearch(keyword);
                return true;
            }
            return false;
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void setupFilterButton() {
        View btnFilter = findViewById(R.id.btn_filter);
        if (btnFilter != null) {
            btnFilter.setOnClickListener(v ->
                    Toast.makeText(this, "필터 설정 - 추후 구현", Toast.LENGTH_SHORT).show());
        }
    }

    private void setupRecyclerView() {
        if (rvResults == null) return;
        rvResults.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SearchResultAdapter(allResults, this::openDetail);
        rvResults.setAdapter(adapter);
    }

    private void performSearch(String keyword) {
        // 검색 결과 라벨 갱신
        if (tvResultLabel != null) {
            tvResultLabel.setText(keyword + " 검색결과");
        }

        // 더미 데이터로 검색 결과 생성
        allResults.clear();
        allResults.add(new Restaurant(101, "00" + keyword, 5.0f, "영업시간:09시30분 영업시작"));
        allResults.add(new Restaurant(102, "00 " + keyword, 5.0f, "영업시간:09시30분 영업시작"));
        allResults.add(new Restaurant(103, "상호명", 4.0f, "영업시간:09시30분 영업시작"));
        allResults.add(new Restaurant(104, "상호명", 5.0f, "영업시간:09시30분 영업시작"));
        allResults.add(new Restaurant(105, "상호명", 5.0f, "영업시간:09시30분 영업시작"));

        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void openDetail(Restaurant restaurant) {
        Intent intent = new Intent(this, RestaurantDetailActivity.class);
        intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT_ID, restaurant.getId());
        intent.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT_NAME, restaurant.getName());
        startActivity(intent);
    }
}
