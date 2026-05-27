package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;

/**
 * 프로필 사진 변경 화면
 *
 * Android 13+ Photo Picker(권한 불필요)로 이미지 선택.
 */
public class ProfileChangeActivity extends AppCompatActivity {

    public static final String EXTRA_NEW_PROFILE_URI = "extra_new_profile_uri";

    private ImageView ivProfile;
    private Uri selectedUri;

    private ActivityResultLauncher<PickVisualMediaRequest> pickMediaLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_change);

        ivProfile = findViewById(R.id.iv_profile);

        registerPicker();
        setupBackButton();
        setupProfilePicker();
        setupSaveButton();
    }

    private void registerPicker() {
        pickMediaLauncher = registerForActivityResult(
                new ActivityResultContracts.PickVisualMedia(),
                uri -> {
                    if (uri == null) return;
                    selectedUri = uri;
                    if (ivProfile != null) ivProfile.setImageURI(uri);
                });
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void setupProfilePicker() {
        View picker = findViewById(R.id.profile_picker);
        if (picker != null) {
            picker.setOnClickListener(v -> launchImagePicker());
        }
    }

    private void launchImagePicker() {
        PickVisualMediaRequest request = new PickVisualMediaRequest.Builder()
                .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly.INSTANCE)
                .build();
        pickMediaLauncher.launch(request);
    }

    private void setupSaveButton() {
        View btnSave = findViewById(R.id.btn_save);
        if (btnSave != null) btnSave.setOnClickListener(v -> save());
    }

    private void save() {
        if (selectedUri == null) {
            Toast.makeText(this, "사진을 선택해주세요", Toast.LENGTH_SHORT).show();
            return;
        }

        // 실제로는 서버에 이미지 업로드
        Intent result = new Intent();
        result.putExtra(EXTRA_NEW_PROFILE_URI, selectedUri.toString());
        setResult(RESULT_OK, result);

        Toast.makeText(this, "프로필이 변경되었습니다", Toast.LENGTH_SHORT).show();
        finish();
    }
}
