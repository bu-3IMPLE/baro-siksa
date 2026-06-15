package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.BoardAdapter;
import kr.ac.baekseok.java_project.dto.response.PagePostResponse;
import kr.ac.baekseok.java_project.dto.response.PostResponse;
import kr.ac.baekseok.java_project.model.BoardPost;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPostsActivity extends AppCompatActivity {

    private static final int PAGE_SIZE = 4;

    private RecyclerView rvPosts;
    private LinearLayout paginationLayout;
    private TextView tvEmpty;

    private int currentPage = 0;
    private int totalPages = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_posts);
        BaseActivity.applySystemBarInsets(this);

        setupBackButton();
        setupRecyclerView();
        setupPagination();
        loadPage(0);
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        rvPosts = findViewById(R.id.rv_board_posts);
        tvEmpty = findViewById(R.id.tv_empty);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupPagination() {
        paginationLayout = findViewById(R.id.pagination_layout);

        View btnFirst = findViewById(R.id.btn_first);
        View btnPrev = findViewById(R.id.btn_prev);
        View btnNext = findViewById(R.id.btn_next);
        View btnLast = findViewById(R.id.btn_last);

        if (btnFirst != null) btnFirst.setOnClickListener(v -> loadPage(0));
        if (btnPrev != null) btnPrev.setOnClickListener(v -> loadPage(Math.max(0, currentPage - 1)));
        if (btnNext != null) btnNext.setOnClickListener(v -> loadPage(Math.min(totalPages - 1, currentPage + 1)));
        if (btnLast != null) btnLast.setOnClickListener(v -> loadPage(totalPages - 1));

        attachPageNumberListeners();
    }

    private void attachPageNumberListeners() {
        if (paginationLayout == null) return;
        for (int i = 0; i < paginationLayout.getChildCount(); i++) {
            View child = paginationLayout.getChildAt(i);
            if (!(child instanceof TextView)) continue;
            String s = ((TextView) child).getText().toString().trim();
            try {
                final int page = Integer.parseInt(s) - 1;
                child.setOnClickListener(v -> loadPage(page));
            } catch (NumberFormatException ignored) {}
        }
    }

    private void loadPage(int page) {
        RetrofitClient.getApi().getMyPosts(page, PAGE_SIZE).enqueue(new Callback<PagePostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PagePostResponse> call,
                                   @NonNull Response<PagePostResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MyPostsActivity.this, "게시글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                PagePostResponse body = response.body();
                currentPage = body.number;
                totalPages = Math.max(1, body.totalPages);

                List<BoardPost> posts = new ArrayList<>();
                if (body.content != null) {
                    for (PostResponse p : body.content) {
                        posts.add(new BoardPost(
                                p.postId.intValue(),
                                p.title,
                                p.writerName,
                                p.commentCount,
                                null,
                                p.createdAt != null ? p.createdAt.substring(0, 10) : ""
                        ));
                    }
                }

                if (posts.isEmpty()) {
                    if (tvEmpty != null) tvEmpty.setVisibility(View.VISIBLE);
                    rvPosts.setVisibility(View.GONE);
                } else {
                    if (tvEmpty != null) tvEmpty.setVisibility(View.GONE);
                    rvPosts.setVisibility(View.VISIBLE);
                }

                BoardAdapter adapter = new BoardAdapter(posts, MyPostsActivity.this::openPostDetail);
                rvPosts.setAdapter(adapter);
                highlightCurrentPage();
            }

            @Override
            public void onFailure(@NonNull Call<PagePostResponse> call, @NonNull Throwable t) {
                Toast.makeText(MyPostsActivity.this, "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void openPostDetail(BoardPost post) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_POST_ID, (long) post.getNo());
        intent.putExtra(PostDetailActivity.EXTRA_POST_SUBJECT, post.getSubject());
        intent.putExtra(PostDetailActivity.EXTRA_POST_WRITER, post.getWriter());
        startActivity(intent);
    }

    private void highlightCurrentPage() {
        if (paginationLayout == null) return;

        int primary = ContextCompat.getColor(this, R.color.text_primary);
        int secondary = ContextCompat.getColor(this, R.color.text_secondary);

        for (int i = 0; i < paginationLayout.getChildCount(); i++) {
            View child = paginationLayout.getChildAt(i);
            if (!(child instanceof TextView)) continue;

            TextView tv = (TextView) child;
            String s = tv.getText().toString().trim();
            try {
                int p = Integer.parseInt(s) - 1;
                if (p == currentPage) {
                    tv.setTextColor(primary);
                    tv.setTypeface(null, android.graphics.Typeface.BOLD);
                } else {
                    tv.setTextColor(secondary);
                    tv.setTypeface(null, android.graphics.Typeface.NORMAL);
                }
            } catch (NumberFormatException ignored) {}
        }
    }
}
