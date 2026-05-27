package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.model.Comment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * 게시판 게시물 상세 보기 화면
 *
 * Intent extras:
 *   EXTRA_POST_NO       : 게시물 번호
 *   EXTRA_POST_SUBJECT  : 제목
 *   EXTRA_POST_WRITER   : 작성자
 *   EXTRA_POST_CONTENT  : 본문 (선택)
 *   EXTRA_POST_DATE     : 작성일 (선택)
 */
public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_NO = "extra_post_no";
    public static final String EXTRA_POST_SUBJECT = "extra_post_subject";
    public static final String EXTRA_POST_WRITER = "extra_post_writer";
    public static final String EXTRA_POST_CONTENT = "extra_post_content";
    public static final String EXTRA_POST_DATE = "extra_post_date";

    private TextView tvSubject, tvWriter, tvDate, tvContent, tvCommentHeader;
    private LinearLayout commentListContainer;
    private EditText etComment;

    private final List<Comment> comments = new ArrayList<>();
    private int nextCommentId = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);

        bindViews();
        setupBackButton();
        loadPostFromIntent();
        prepareSampleComments();
        renderComments();
        setupCommentInput();
    }

    private void bindViews() {
        tvSubject = findViewById(R.id.tv_subject);
        tvWriter = findViewById(R.id.tv_writer);
        tvDate = findViewById(R.id.tv_date);
        tvContent = findViewById(R.id.tv_content);
        tvCommentHeader = findViewById(R.id.tv_comment_header);
        commentListContainer = findViewById(R.id.comment_list_container);
        etComment = findViewById(R.id.et_comment);
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void loadPostFromIntent() {
        String subject = getIntent().getStringExtra(EXTRA_POST_SUBJECT);
        String writer = getIntent().getStringExtra(EXTRA_POST_WRITER);
        String content = getIntent().getStringExtra(EXTRA_POST_CONTENT);
        String date = getIntent().getStringExtra(EXTRA_POST_DATE);

        if (subject != null) tvSubject.setText(subject);
        if (writer != null) tvWriter.setText(writer);
        if (date != null) tvDate.setText(date);
        else tvDate.setText(today());

        if (content != null) {
            tvContent.setText(content);
        } else {
            // 본문이 전달되지 않았을 때 더미 텍스트
            tvContent.setText("이 음식점은 분위기가 좋고 가성비도 훌륭합니다. "
                    + "특히 매콤한 양념과 푸짐한 양이 인상적이었어요. "
                    + "주차 공간이 협소한 게 아쉽지만 음식 자체는 다시 찾고 싶습니다.");
        }
    }

    private void prepareSampleComments() {
        comments.add(new Comment(nextCommentId++, "맛집탐험가",
                "저도 가봤는데 진짜 맛있었어요!", today()));
        comments.add(new Comment(nextCommentId++, "동네주민",
                "주차 진짜 불편해요... 그래도 음식은 인정", today()));
    }

    private void renderComments() {
        commentListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (Comment c : comments) {
            View row = inflater.inflate(R.layout.item_comment,
                    commentListContainer, false);
            TextView writer = row.findViewById(R.id.tv_comment_writer);
            TextView date = row.findViewById(R.id.tv_comment_date);
            TextView content = row.findViewById(R.id.tv_comment_content);

            if (writer != null) writer.setText(c.getWriter());
            if (date != null) date.setText(c.getCreatedAt());
            if (content != null) content.setText(c.getContent());

            commentListContainer.addView(row);
        }

        tvCommentHeader.setText(String.format(Locale.getDefault(),
                "답글 %d", comments.size()));
    }

    private void setupCommentInput() {
        View btnSend = findViewById(R.id.btn_send);
        if (btnSend != null) {
            btnSend.setOnClickListener(v -> submitComment());
        }

        if (etComment != null) {
            etComment.setOnEditorActionListener((v, actionId, event) -> {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    submitComment();
                    return true;
                }
                return false;
            });
        }
    }

    private void submitComment() {
        if (etComment == null) return;
        String text = etComment.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "답글 내용을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 실제 앱에서는 서버에 POST 요청
        Comment newComment = new Comment(nextCommentId++, "나", text, today());
        comments.add(newComment);

        etComment.setText("");
        renderComments();
        Toast.makeText(this, "답글이 등록되었습니다", Toast.LENGTH_SHORT).show();
    }

    private String today() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                .format(new Date());
    }
}
