package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;

import java.util.Locale;

/**
 * 닉네임 변경 화면
 *
 * RESULT_OK + EXTRA_NEW_NICKNAME 으로 변경된 닉네임 반환
 */
public class NicknameChangeActivity extends AppCompatActivity {

    public static final String EXTRA_CURRENT_NICKNAME = "extra_current_nickname";
    public static final String EXTRA_NEW_NICKNAME = "extra_new_nickname";

    private static final int MAX_LEN = 10;
    private static final int MIN_LEN = 2;

    private EditText etNickname;
    private TextView tvHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nickname_change);
        BaseActivity.applySystemBarInsets(this);

        etNickname = findViewById(R.id.et_nickname);
        tvHelper = findViewById(R.id.tv_helper);

        setupBackButton();
        loadCurrentNickname();
        setupNicknameWatcher();
        setupSaveButton();
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void loadCurrentNickname() {
        String current = getIntent().getStringExtra(EXTRA_CURRENT_NICKNAME);
        if (current != null && etNickname != null) {
            etNickname.setText(current);
            etNickname.setSelection(current.length());
        }
        updateHelper();
    }

    private void setupNicknameWatcher() {
        if (etNickname == null) return;
        etNickname.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                updateHelper();
            }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void updateHelper() {
        if (tvHelper == null || etNickname == null) return;
        int len = etNickname.getText().length();
        tvHelper.setText(String.format(Locale.getDefault(),
                "%d/%d자", len, MAX_LEN));
    }

    private void setupSaveButton() {
        View btnSave = findViewById(R.id.btn_save);
        if (btnSave != null) btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        String nickname = etNickname.getText().toString().trim();

        if (nickname.length() < MIN_LEN) {
            Toast.makeText(this,
                    String.format(Locale.getDefault(),
                            "닉네임은 %d자 이상이어야 합니다", MIN_LEN),
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // 실제로는 서버에 PATCH 요청
        Intent result = new Intent();
        result.putExtra(EXTRA_NEW_NICKNAME, nickname);
        setResult(RESULT_OK, result);

        Toast.makeText(this, "닉네임이 변경되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }
}
