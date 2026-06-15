package kr.ac.baekseok.java_project.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.request.PostCreateRequest;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PostWriteActivity extends AppCompatActivity {

    public static final String EXTRA_NEW_SUBJECT = "extra_new_subject";
    public static final String EXTRA_NEW_CONTENT = "extra_new_content";

    private EditText etSubject;
    private EditText etContent;
    private boolean isSubmitting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);
        BaseActivity.applySystemBarInsets(this);

        etSubject = findViewById(R.id.et_subject);
        etContent = findViewById(R.id.et_content);

        setupBackButton();
        setupSubmitButton();
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> confirmExit());
    }

    private void setupSubmitButton() {
        View btnSubmit = findViewById(R.id.btn_submit);
        if (btnSubmit != null) btnSubmit.setOnClickListener(v -> submit());
    }

    private void submit() {
        if (isSubmitting) return;

        String title = etSubject.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty()) {
            Toast.makeText(this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
            etSubject.requestFocus();
            return;
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
            etContent.requestFocus();
            return;
        }

        isSubmitting = true;

        RetrofitClient.getApi()
                .createPost(new PostCreateRequest(title, content))
                .enqueue(new Callback<Long>() {
                    @Override
                    public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                        isSubmitting = false;
                        if (!response.isSuccessful()) {
                            Toast.makeText(PostWriteActivity.this, "게시글 등록에 실패했습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        Toast.makeText(PostWriteActivity.this, "게시물이 등록되었습니다.", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                        isSubmitting = false;
                        Toast.makeText(PostWriteActivity.this, "서버에 연결할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    private void confirmExit() {
        String title = etSubject.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (title.isEmpty() && content.isEmpty()) {
            finish();
            return;
        }

        new AlertDialog.Builder(this)
                .setTitle("작성 취소")
                .setMessage("작성 중인 내용이 사라집니다. 정말 나가시겠어요?")
                .setPositiveButton("나가기", (d, w) -> finish())
                .setNegativeButton("계속 작성", null)
                .show();
    }
}
