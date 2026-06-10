package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.BoardAdapter;
import kr.ac.baekseok.java_project.dto.response.PagePostResponse;
import kr.ac.baekseok.java_project.dto.response.PostResponse;
import kr.ac.baekseok.java_project.model.BoardPost;
import kr.ac.baekseok.java_project.network.ApiService;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardActivity extends BaseActivity {

    private static final int PAGE_SIZE = 4;

    private RecyclerView rvPosts;
    private BoardAdapter adapter;
    private LinearLayout paginationLayout;

    private int currentPage = 0;   // 서버는 0-based
    private int totalPages = 1;

    private ActivityResultLauncher<Intent> writeLauncher;

    @Override
    protected int getCurrentTab() {
        return 2;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        setupBottomNavigation();
        registerWriteLauncher();
        setupRecyclerView();
        setupPagination();
        setupFab();
        loadPage(0);
    }

    private void registerWriteLauncher() {
        writeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK) {
                        loadPage(0);
                    }
                });
    }

    private void setupRecyclerView() {
        rvPosts = findViewById(R.id.rv_board_posts);
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
                // UI는 1-based, 서버는 0-based
                final int page = Integer.parseInt(s) - 1;
                child.setOnClickListener(v -> loadPage(page));
            } catch (NumberFormatException ignored) {}
        }
    }

    private void setupFab() {
        View fab = findViewById(R.id.fab_write);
        if (fab != null) {
            fab.setOnClickListener(v -> {
                Intent intent = new Intent(this, PostWriteActivity.class);
                writeLauncher.launch(intent);
            });
        }
    }

    private void loadPage(int page) {
        ApiService api = RetrofitClient.getApi();
        api.getPosts(page, PAGE_SIZE).enqueue(new Callback<PagePostResponse>() {
            @Override
            public void onResponse(@NonNull Call<PagePostResponse> call,
                                   @NonNull Response<PagePostResponse> response) {
                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(BoardActivity.this, "게시글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
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

                adapter = new BoardAdapter(posts, BoardActivity.this::openPostDetail);
                rvPosts.setAdapter(adapter);
                highlightCurrentPage();
            }

            @Override
            public void onFailure(@NonNull Call<PagePostResponse> call, @NonNull Throwable t) {
                Toast.makeText(BoardActivity.this, "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
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
                int page = Integer.parseInt(s) - 1; // UI 1-based → 0-based
                if (page == currentPage) {
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
