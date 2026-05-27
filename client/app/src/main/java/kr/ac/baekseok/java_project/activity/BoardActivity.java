package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.BoardAdapter;
import kr.ac.baekseok.java_project.model.BoardPost;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 맛집 공유 게시판 (이미지 2 가운데)
 */
public class BoardActivity extends BaseActivity {

    private static final int PAGE_SIZE = 4;

    private RecyclerView rvPosts;
    private BoardAdapter adapter;
    private LinearLayout paginationLayout;

    private List<BoardPost> allPosts;
    private int currentPage = 1;
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
        prepareSampleData();
        setupRecyclerView();
        setupPagination();
        setupFab();
        loadPage(1);
    }

    private void registerWriteLauncher() {
        // 글쓰기 결과 수신 (ActivityResultLauncher 방식 - onActivityResult deprecated 대체)
        writeLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() != RESULT_OK || result.getData() == null) return;

                    String subject = result.getData()
                            .getStringExtra(PostWriteActivity.EXTRA_NEW_SUBJECT);
                    String content = result.getData()
                            .getStringExtra(PostWriteActivity.EXTRA_NEW_CONTENT);

                    if (subject != null) {
                        // 새 게시물을 맨 앞에 추가
                        int newNo = allPosts.size() + 1;
                        allPosts.add(0, new BoardPost(
                                newNo, subject, "나", 0, content, today()));
                        totalPages = (int) Math.ceil((double) allPosts.size() / PAGE_SIZE);
                        loadPage(1);
                    }
                });
    }

    private void prepareSampleData() {
        allPosts = new ArrayList<>();
        String[] subjects = {
                "OOO 맛있더라",
                "OOO 맛있고 분위기 좋다",
                "OOO 맛있더라",
                "OOO 맛있더라"
        };
        String[] writers = {
                "돌아다니는 맛돌이",
                "전국 맛집 탐방",
                "소확행",
                "미식"
        };

        int totalItems = 36;
        for (int i = 1; i <= totalItems; i++) {
            int idx = (i - 1) % subjects.length;
            allPosts.add(new BoardPost(
                    i,
                    subjects[idx],
                    writers[idx],
                    1 + (i % 5),
                    "이 게시물의 본문 내용입니다. 사용자가 직접 작성한 후기/리뷰가 여기에 표시됩니다.",
                    today()
            ));
        }
        totalPages = (int) Math.ceil((double) allPosts.size() / PAGE_SIZE);
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

        if (btnFirst != null) btnFirst.setOnClickListener(v -> loadPage(1));
        if (btnPrev != null) btnPrev.setOnClickListener(v ->
                loadPage(Math.max(1, currentPage - 1)));
        if (btnNext != null) btnNext.setOnClickListener(v ->
                loadPage(Math.min(totalPages, currentPage + 1)));
        if (btnLast != null) btnLast.setOnClickListener(v -> loadPage(totalPages));

        attachPageNumberListeners();
    }

    private void attachPageNumberListeners() {
        if (paginationLayout == null) return;
        for (int i = 0; i < paginationLayout.getChildCount(); i++) {
            View child = paginationLayout.getChildAt(i);
            if (!(child instanceof TextView)) continue;
            String s = ((TextView) child).getText().toString().trim();
            try {
                final int page = Integer.parseInt(s);
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
        if (page < 1 || page > totalPages) return;
        currentPage = page;

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allPosts.size());
        List<BoardPost> pageItems = new ArrayList<>(allPosts.subList(start, end));

        adapter = new BoardAdapter(pageItems, this::openPostDetail);
        rvPosts.setAdapter(adapter);

        highlightCurrentPage();
    }

    private void openPostDetail(BoardPost post) {
        Intent intent = new Intent(this, PostDetailActivity.class);
        intent.putExtra(PostDetailActivity.EXTRA_POST_NO, post.getNo());
        intent.putExtra(PostDetailActivity.EXTRA_POST_SUBJECT, post.getSubject());
        intent.putExtra(PostDetailActivity.EXTRA_POST_WRITER, post.getWriter());
        if (post.getContent() != null) {
            intent.putExtra(PostDetailActivity.EXTRA_POST_CONTENT, post.getContent());
        }
        if (post.getCreatedAt() != null) {
            intent.putExtra(PostDetailActivity.EXTRA_POST_DATE, post.getCreatedAt());
        }
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
                int page = Integer.parseInt(s);
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

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
    }
}
