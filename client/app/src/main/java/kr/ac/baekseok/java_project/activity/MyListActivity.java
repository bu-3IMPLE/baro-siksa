package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.MyListAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * 마이페이지에서 진입하는 리스트 화면 공용 Activity.
 *
 * EXTRA_LIST_TYPE 값에 따라 표시되는 데이터/제목/클릭 동작이 달라진다:
 *   TYPE_MY_POSTS       : 내가 쓴 게시물
 *   TYPE_MY_REPLIES     : 내가 쓴 답글
 *   TYPE_MY_RESTAURANTS : 나만의 맛집
 */
public class MyListActivity extends AppCompatActivity {

    public static final String EXTRA_LIST_TYPE = "extra_list_type";

    public static final int TYPE_MY_POSTS = 1;
    public static final int TYPE_MY_REPLIES = 2;
    public static final int TYPE_MY_RESTAURANTS = 3;

    private int listType = TYPE_MY_POSTS;
    private TextView tvTitle, tvEmpty;
    private RecyclerView rvItems;
    private View emptyLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);
        BaseActivity.applySystemBarInsets(this);

        listType = getIntent().getIntExtra(EXTRA_LIST_TYPE, TYPE_MY_POSTS);

        bindViews();
        setupBackButton();
        setupTitleAndData();
    }

    private void bindViews() {
        tvTitle = findViewById(R.id.tv_title);
        rvItems = findViewById(R.id.rv_items);
        emptyLayout = findViewById(R.id.empty_layout);
        tvEmpty = findViewById(R.id.tv_empty);

        rvItems.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupTitleAndData() {
        List<MyListAdapter.Item> items = new ArrayList<>();

        switch (listType) {
            case TYPE_MY_POSTS:
                tvTitle.setText("내가 쓴 게시물");
                tvEmpty.setText("아직 작성한 게시물이 없습니다");
                items = loadMyPosts();
                break;
            case TYPE_MY_REPLIES:
                tvTitle.setText("내가 쓴 답글");
                tvEmpty.setText("아직 작성한 답글이 없습니다");
                items = loadMyReplies();
                break;
            case TYPE_MY_RESTAURANTS:
                tvTitle.setText("나만의 맛집");
                tvEmpty.setText("아직 등록한 맛집이 없습니다");
                items = loadMyRestaurants();
                break;
        }

        if (items.isEmpty()) {
            rvItems.setVisibility(View.GONE);
            emptyLayout.setVisibility(View.VISIBLE);
        } else {
            rvItems.setVisibility(View.VISIBLE);
            emptyLayout.setVisibility(View.GONE);
            rvItems.setAdapter(new MyListAdapter(items, this::onItemClick));
        }
    }

    private void onItemClick(MyListAdapter.Item item) {
        switch (listType) {
            case TYPE_MY_POSTS:
            case TYPE_MY_REPLIES:
                // 게시물 상세로 이동
                Intent intent = new Intent(this, PostDetailActivity.class);
                intent.putExtra(PostDetailActivity.EXTRA_POST_SUBJECT, item.title);
                intent.putExtra(PostDetailActivity.EXTRA_POST_WRITER, "나");
                intent.putExtra(PostDetailActivity.EXTRA_POST_CONTENT, item.subtitle);
                startActivity(intent);
                break;
            case TYPE_MY_RESTAURANTS:
                // 가게 상세로 이동
                Intent detail = new Intent(this, RestaurantDetailActivity.class);
                if (item.payload instanceof Integer) {
                    detail.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT_ID,
                            (Integer) item.payload);
                }
                detail.putExtra(RestaurantDetailActivity.EXTRA_RESTAURANT_NAME, item.title);
                startActivity(detail);
                break;
            default:
                Toast.makeText(this, item.title, Toast.LENGTH_SHORT).show();
        }
    }

    // ====== 데이터 로딩 (실제로는 서버 호출) ======

    private List<MyListAdapter.Item> loadMyPosts() {
        List<MyListAdapter.Item> list = new ArrayList<>();
        list.add(new MyListAdapter.Item(
                "00칼국수 후기",
                "면이 쫄깃하고 국물이 진해요. 추천합니다.",
                1));
        list.add(new MyListAdapter.Item(
                "동네 카페 추천",
                "조용하고 분위기 좋은 곳을 찾으시면 여기로",
                2));
        list.add(new MyListAdapter.Item(
                "할머니 손맛 가게",
                "오래된 가게인데 정말 맛있어요. 가성비도 좋고.",
                3));
        return list;
    }

    private List<MyListAdapter.Item> loadMyReplies() {
        List<MyListAdapter.Item> list = new ArrayList<>();
        list.add(new MyListAdapter.Item(
                "OOO 맛있더라",
                "→ 저도 가봤는데 진짜 맛있었어요!",
                10));
        list.add(new MyListAdapter.Item(
                "OOO 맛있고 분위기 좋다",
                "→ 주차장이 좀 좁은 게 흠이긴 해요",
                11));
        return list;
    }

    private List<MyListAdapter.Item> loadMyRestaurants() {
        List<MyListAdapter.Item> list = new ArrayList<>();
        list.add(new MyListAdapter.Item(
                "00칼국수",
                "평점 5.0 · 영업시간 09:30~",
                101));
        list.add(new MyListAdapter.Item(
                "맛있는 분식",
                "평점 4.5 · 영업시간 10:00~",
                102));
        list.add(new MyListAdapter.Item(
                "할머니 손맛",
                "평점 5.0 · 영업시간 11:00~",
                103));
        return list;
    }
}
