package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.request.PostCommentCreateRequest;
import kr.ac.baekseok.java_project.dto.response.PostCommentResponse;
import kr.ac.baekseok.java_project.dto.response.PostDetailResponse;
import kr.ac.baekseok.java_project.network.ApiService;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostDetailActivity extends AppCompatActivity {

    public static final String EXTRA_POST_ID = "extra_post_id";
    public static final String EXTRA_POST_SUBJECT = "extra_post_subject";
    public static final String EXTRA_POST_WRITER = "extra_post_writer";

    // 이전 코드와의 호환성 유지
    public static final String EXTRA_POST_NO = "extra_post_no";
    public static final String EXTRA_POST_CONTENT = "extra_post_content";
    public static final String EXTRA_POST_DATE = "extra_post_date";

    private long postId;
    private TextView tvSubject, tvWriter, tvDate, tvContent, tvCommentHeader;
    private LinearLayout commentListContainer;
    private EditText etComment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_detail);
        BaseActivity.applySystemBarInsets(this);

        bindViews();
        setupBackButton();

        postId = getIntent().getLongExtra(EXTRA_POST_ID, -1L);

        // 화면 즉시 표시를 위해 Intent로 받은 기본 정보 먼저 채움
        String subject = getIntent().getStringExtra(EXTRA_POST_SUBJECT);
        String writer = getIntent().getStringExtra(EXTRA_POST_WRITER);
        if (subject != null) tvSubject.setText(subject);
        if (writer != null) tvWriter.setText(writer);

        if (postId != -1L) {
            loadPost();
            loadComments();
        }

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

    private void loadPost() {
        RetrofitClient.getApi().getPost(postId).enqueue(new Callback<PostDetailResponse>() {
            @Override
            public void onResponse(@NonNull Call<PostDetailResponse> call,
                                   @NonNull Response<PostDetailResponse> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                PostDetailResponse body = response.body();
                tvSubject.setText(body.title);
                tvWriter.setText(body.writerName);
                tvContent.setText(body.content);
                if (body.createdAt != null) tvDate.setText(body.createdAt.substring(0, 10));
            }

            @Override
            public void onFailure(@NonNull Call<PostDetailResponse> call, @NonNull Throwable t) {
                Toast.makeText(PostDetailActivity.this, "게시글을 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadComments() {
        RetrofitClient.getApi().getComments(postId).enqueue(new Callback<List<PostCommentResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<PostCommentResponse>> call,
                                   @NonNull Response<List<PostCommentResponse>> response) {
                if (!response.isSuccessful() || response.body() == null) return;
                renderComments(response.body());
            }

            @Override
            public void onFailure(@NonNull Call<List<PostCommentResponse>> call, @NonNull Throwable t) {}
        });
    }

    private void renderComments(List<PostCommentResponse> comments) {
        commentListContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (PostCommentResponse c : comments) {
            View row = inflater.inflate(R.layout.item_comment, commentListContainer, false);
            TextView writer = row.findViewById(R.id.tv_comment_writer);
            TextView date = row.findViewById(R.id.tv_comment_date);
            TextView content = row.findViewById(R.id.tv_comment_content);

            if (writer != null) writer.setText(c.writerName);
            if (date != null && c.createdAt != null) date.setText(c.createdAt.substring(0, 10));
            if (content != null) content.setText(c.content);

            commentListContainer.addView(row);
        }

        if (tvCommentHeader != null) {
            tvCommentHeader.setText(String.format(Locale.getDefault(), "답글 %d", comments.size()));
        }
    }

    private void setupCommentInput() {
        View btnSend = findViewById(R.id.btn_send);
        if (btnSend != null) btnSend.setOnClickListener(v -> submitComment());

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
        if (etComment == null || postId == -1L) return;
        String text = etComment.getText().toString().trim();
        if (text.isEmpty()) {
            Toast.makeText(this, "답글 내용을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService api = RetrofitClient.getApi();
        api.createComment(postId, new PostCommentCreateRequest(text))
                .enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                        if (!response.isSuccessful()) {
                            Toast.makeText(PostDetailActivity.this, "답글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        etComment.setText("");
                        Toast.makeText(PostDetailActivity.this, "답글이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        loadComments();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                        Toast.makeText(PostDetailActivity.this, "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
