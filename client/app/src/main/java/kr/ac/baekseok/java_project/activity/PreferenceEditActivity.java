package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.request.MemberUpdateRequest;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 음식 취향 수정 화면.
 *
 * 서버 updatePreference API 연동.
 * memberId와 현재 취향을 Intent로 받아서 표시하고, 수정 후 저장.
 */
public class PreferenceEditActivity extends AppCompatActivity {

    public static final String EXTRA_MEMBER_ID = "extra_member_id";
    public static final String EXTRA_PREFERENCE = "extra_preference";
    public static final String EXTRA_NEW_PREFERENCE = "extra_new_preference";

    private EditText etPreference;
    private long memberId;
    private boolean isRequesting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preference_edit);

        memberId = getIntent().getLongExtra(EXTRA_MEMBER_ID, -1);
        String current = getIntent().getStringExtra(EXTRA_PREFERENCE);

        etPreference = findViewById(R.id.et_preference);
        if (current != null) etPreference.setText(current);

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        View btnSave = findViewById(R.id.btn_save);
        if (btnSave != null) btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        if (isRequesting) return;

        if (memberId < 0) {
            Toast.makeText(this, "회원 정보를 찾을 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        String preference = etPreference.getText().toString().trim();
        isRequesting = true;

        MemberUpdateRequest request = new MemberUpdateRequest(preference);

        RetrofitClient.getApi().updatePreference(memberId, request)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {
                        isRequesting = false;
                        if (response.isSuccessful()) {
                            Toast.makeText(PreferenceEditActivity.this,
                                    "음식 취향이 저장되었습니다", Toast.LENGTH_SHORT).show();
                            // 변경 결과를 마이페이지로 전달
                            Intent result = new Intent();
                            result.putExtra(EXTRA_NEW_PREFERENCE, preference);
                            setResult(RESULT_OK, result);
                            finish();
                        } else {
                            Toast.makeText(PreferenceEditActivity.this,
                                    "저장 실패 (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        isRequesting = false;
                        Toast.makeText(PreferenceEditActivity.this,
                                "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
