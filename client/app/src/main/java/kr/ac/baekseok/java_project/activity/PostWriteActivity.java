package kr.ac.baekseok.java_project.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;

/**
 * 게시물 작성 화면
 *
 * 등록 시 RESULT_OK로 종료. 호출한 Activity는
 * onActivityResult 또는 ActivityResultLauncher로 결과를 받아
 * 게시판 목록을 갱신할 수 있다.
 */
public class PostWriteActivity extends AppCompatActivity {

    public static final String EXTRA_NEW_SUBJECT = "extra_new_subject";
    public static final String EXTRA_NEW_CONTENT = "extra_new_content";

    private EditText etSubject;
    private EditText etContent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_write);

        etSubject = findViewById(R.id.et_subject);
        etContent = findViewById(R.id.et_content);

        setupBackButton();
        setupSubmitButton();
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> confirmExit());
        }
    }

    private void setupSubmitButton() {
        View btnSubmit = findViewById(R.id.btn_submit);
        if (btnSubmit != null) {
            btnSubmit.setOnClickListener(v -> submit());
        }
    }

    private void submit() {
        String subject = etSubject.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (subject.isEmpty()) {
            Toast.makeText(this, "제목을 입력하세요", Toast.LENGTH_SHORT).show();
            etSubject.requestFocus();
            return;
        }
        if (content.isEmpty()) {
            Toast.makeText(this, "내용을 입력하세요", Toast.LENGTH_SHORT).show();
            etContent.requestFocus();
            return;
        }

        // 실제로는 서버 POST 요청 → 성공 후 finish
        android.content.Intent result = new android.content.Intent();
        result.putExtra(EXTRA_NEW_SUBJECT, subject);
        result.putExtra(EXTRA_NEW_CONTENT, content);
        setResult(RESULT_OK, result);

        Toast.makeText(this, "게시물이 등록되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    public void onBackPressed() {
        confirmExit();
    }

    /**
     * 작성 중인 내용이 있으면 확인 대화상자를 띄운다.
     */
    private void confirmExit() {
        String subject = etSubject.getText().toString().trim();
        String content = etContent.getText().toString().trim();

        if (subject.isEmpty() && content.isEmpty()) {
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
