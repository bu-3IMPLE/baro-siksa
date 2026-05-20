package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.BoardAdapter;
import kr.ac.baekseok.java_project.model.BoardPost;

import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected int getCurrentTab() {
        return 2;  // 게시판 탭
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board);

        setupBottomNavigation();
        prepareSampleData();
        setupRecyclerView();
        setupPagination();
        loadPage(1);
    }

    private void prepareSampleData() {
        allPosts = new ArrayList<>();
        // 페이지네이션 테스트용 더미 데이터
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

        // 페이지가 9개가 되도록 36개 생성 (디자인의 1~9 페이지 매칭)
        int totalItems = 36;
        for (int i = 1; i <= totalItems; i++) {
            int idx = (i - 1) % subjects.length;
            allPosts.add(new BoardPost(
                    i,
                    subjects[idx],
                    writers[idx],
                    1 + (i % 5)
            ));
        }
        totalPages = (int) Math.ceil((double) allPosts.size() / PAGE_SIZE);
    }

    private void setupRecyclerView() {
        rvPosts = findViewById(R.id.rv_board_posts);
        rvPosts.setLayoutManager(new LinearLayoutManager(this));
        adapter = new BoardAdapter(new ArrayList<>(), post -> {
            Toast.makeText(this,
                    "게시물: " + post.getSubject(),
                    Toast.LENGTH_SHORT).show();
            // 실제로는 게시물 상세 Activity로 이동
        });
        rvPosts.setAdapter(adapter);
    }

    private void setupPagination() {
        paginationLayout = findViewById(R.id.pagination_layout);

        // 처음/이전/다음/마지막 버튼
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

        // 페이지 번호 텍스트들의 클릭 리스너 설정
        attachPageNumberListeners();
    }

    private void attachPageNumberListeners() {
        if (paginationLayout == null) return;

        // 페이지네이션 LinearLayout 내부의 자식 중 숫자만 있는 TextView를 찾아서 리스너 등록
        for (int i = 0; i < paginationLayout.getChildCount(); i++) {
            View child = paginationLayout.getChildAt(i);
            if (!(child instanceof TextView)) continue;

            CharSequence text = ((TextView) child).getText();
            if (text == null) continue;

            String s = text.toString().trim();
            // 1~9 사이 숫자만
            try {
                final int page = Integer.parseInt(s);
                child.setOnClickListener(v -> loadPage(page));
            } catch (NumberFormatException ignored) {
                // 숫자가 아니면 무시 (처음/이전/다음/마지막은 위에서 처리)
            }
        }
    }

    private void loadPage(int page) {
        if (page < 1 || page > totalPages) return;
        currentPage = page;

        int start = (page - 1) * PAGE_SIZE;
        int end = Math.min(start + PAGE_SIZE, allPosts.size());
        List<BoardPost> pageItems = new ArrayList<>(allPosts.subList(start, end));

        adapter = new BoardAdapter(pageItems, post ->
                Toast.makeText(this,
                        "게시물: " + post.getSubject(),
                        Toast.LENGTH_SHORT).show());
        rvPosts.setAdapter(adapter);

        highlightCurrentPage();
    }

    /**
     * 현재 페이지 번호를 굵게 표시한다.
     */
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
            } catch (NumberFormatException ignored) {
                // 화살표 버튼은 그대로 유지
            }
        }
    }
}
