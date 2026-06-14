package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.request.MemberPasswordUpdateRequest;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 비밀번호 변경 화면.
 * 서버 changePassword API 연동 (현재 비번 + 새 비번).
 */
public class PasswordChangeActivity extends AppCompatActivity {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$");

    private EditText etCurrent, etNew;
    private boolean isRequesting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_change);

        etCurrent = findViewById(R.id.et_current_password);
        etNew = findViewById(R.id.et_new_password);

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        View btnSave = findViewById(R.id.btn_save);
        if (btnSave != null) btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        if (isRequesting) return;

        String current = etCurrent.getText().toString();
        String newPw = etNew.getText().toString();

        if (current.isEmpty()) {
            Toast.makeText(this, "현재 비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!PASSWORD_PATTERN.matcher(newPw).matches()) {
            Toast.makeText(this,
                    "새 비밀번호는 영문, 숫자, 특수문자(@$!%*#?&)를 포함해 8~20자여야 합니다",
                    Toast.LENGTH_LONG).show();
            return;
        }
        if (current.equals(newPw)) {
            Toast.makeText(this, "현재 비밀번호와 다른 비밀번호를 입력하세요",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        isRequesting = true;
        MemberPasswordUpdateRequest request =
                new MemberPasswordUpdateRequest(current, newPw);

        RetrofitClient.getApi().changePassword(request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {
                        isRequesting = false;
                        if (response.isSuccessful()) {
                            Toast.makeText(PasswordChangeActivity.this,
                                    "비밀번호가 변경되었습니다", Toast.LENGTH_SHORT).show();
                            finish();
                        } else if (response.code() == 400 || response.code() == 401) {
                            Toast.makeText(PasswordChangeActivity.this,
                                    "현재 비밀번호가 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(PasswordChangeActivity.this,
                                    "변경 실패 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        isRequesting = false;
                        Toast.makeText(PasswordChangeActivity.this,
                                "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
