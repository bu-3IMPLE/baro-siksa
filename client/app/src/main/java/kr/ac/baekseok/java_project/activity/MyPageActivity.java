package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.response.MemberResponse;
import kr.ac.baekseok.java_project.network.AuthInterceptor;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends BaseActivity {

    private TextView tvNickname;
    private View sectionOwner;

    private MemberResponse myInfo;

    private ActivityResultLauncher<Intent> preferenceLauncher;
    private ActivityResultLauncher<Intent> nicknameLauncher;

    @Override
    protected int getCurrentTab() {
        return 1;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        setupBottomNavigation();
        bindViews();
        registerLaunchers();
        setupButtons();
        loadMyInfo();
    }

    private void bindViews() {
        tvNickname = findViewById(R.id.tv_nickname);
        sectionOwner = findViewById(R.id.section_owner);
    }

    private void registerLaunchers() {
        preferenceLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String newPref = result.getData().getStringExtra(
                                PreferenceEditActivity.EXTRA_NEW_PREFERENCE);
                        if (myInfo != null) myInfo.foodPreference = newPref;
                    }
                });

        nicknameLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String newNickname = result.getData().getStringExtra(
                                NicknameChangeActivity.EXTRA_NEW_NICKNAME);
                        if (newNickname != null) {
                            if (myInfo != null) myInfo.username = newNickname;
                            tvNickname.setText(newNickname);
                        }
                    }
                });
    }

    private void loadMyInfo() {
        if (AuthInterceptor.getToken(this) == null) {
            Toast.makeText(this, "로그인이 필요합니다", Toast.LENGTH_SHORT).show();
            goToLogin();
            return;
        }

        if (tvNickname != null) tvNickname.setText("불러오는 중...");

        RetrofitClient.getApi().getMyInfo().enqueue(new Callback<MemberResponse>() {
            @Override
            public void onResponse(@NonNull Call<MemberResponse> call,
                                   @NonNull Response<MemberResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    myInfo = response.body();
                    bindMyInfo();
                } else if (response.code() == 401) {
                    Toast.makeText(MyPageActivity.this,
                            "로그인이 만료되었습니다. 다시 로그인해 주세요.",
                            Toast.LENGTH_SHORT).show();
                    AuthInterceptor.clearToken(MyPageActivity.this);
                    goToLogin();
                } else {
                    Toast.makeText(MyPageActivity.this,
                            "정보를 불러오지 못했습니다 (" + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                    if (tvNickname != null) tvNickname.setText("닉네임");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MemberResponse> call, @NonNull Throwable t) {
                Toast.makeText(MyPageActivity.this,
                        "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                if (tvNickname != null) tvNickname.setText("닉네임");
            }
        });
    }

    private void bindMyInfo() {
        if (myInfo == null) return;

        if (tvNickname != null) {
            tvNickname.setText(myInfo.username != null ? myInfo.username : "닉네임");
        }

        // OWNER / ADMIN 계정이면 업주 관리 섹션 표시
        if (sectionOwner != null) {
            sectionOwner.setVisibility(myInfo.isOwner() ? View.VISIBLE : View.GONE);
        }
    }

    private void setupButtons() {
        setClickListener(R.id.btn_change_nickname, v -> {
            Intent intent = new Intent(this, NicknameChangeActivity.class);
            intent.putExtra(NicknameChangeActivity.EXTRA_CURRENT_NICKNAME,
                    myInfo != null ? myInfo.username : "");
            nicknameLauncher.launch(intent);
        });

        setClickListener(R.id.btn_change_profile, v ->
                Toast.makeText(this,
                        "프로필 사진 변경은 현재 지원되지 않습니다",
                        Toast.LENGTH_SHORT).show());

        setClickListener(R.id.btn_my_reservations, v ->
                startActivity(new Intent(this, MyReservationActivity.class)));

        setClickListener(R.id.btn_my_posts, v ->
                startActivity(new Intent(this, MyPostsActivity.class)));

        setClickListener(R.id.btn_my_replies, v ->
                Toast.makeText(this, "준비 중입니다", Toast.LENGTH_SHORT).show());

        setClickListener(R.id.btn_only_my_restaurants, v -> openPreferenceEdit());

        // 업주 전용 - 내 식당 관리
        setClickListener(R.id.btn_owner_restaurant, v ->
                startActivity(new Intent(this, OwnerRestaurantActivity.class)));

        setClickListener(R.id.btn_app_intro, v -> openInfo(InfoActivity.TYPE_APP_INTRO));
        setClickListener(R.id.btn_notice, v -> openInfo(InfoActivity.TYPE_NOTICE));
        setClickListener(R.id.btn_customer_center, v -> openInfo(InfoActivity.TYPE_CUSTOMER_CENTER));

        setClickListener(R.id.btn_logout, v -> logout());
    }

    private void openPreferenceEdit() {
        if (myInfo == null) {
            Toast.makeText(this, "정보를 불러오는 중입니다", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(this, PreferenceEditActivity.class);
        intent.putExtra(PreferenceEditActivity.EXTRA_MEMBER_ID, myInfo.id);
        intent.putExtra(PreferenceEditActivity.EXTRA_PREFERENCE, myInfo.foodPreference);
        preferenceLauncher.launch(intent);
    }

    private void openInfo(int type) {
        Intent intent = new Intent(this, InfoActivity.class);
        intent.putExtra(InfoActivity.EXTRA_INFO_TYPE, type);
        startActivity(intent);
    }

    private void logout() {
        new android.app.AlertDialog.Builder(this)
                .setTitle("로그아웃")
                .setMessage("로그아웃 하시겠습니까?")
                .setPositiveButton("로그아웃", (dialog, which) -> {
                    AuthInterceptor.clearToken(this);
                    goToLogin();
                })
                .setNegativeButton("취소", null)
                .show();
    }

    private void goToLogin() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void setClickListener(int viewId, View.OnClickListener listener) {
        View v = findViewById(viewId);
        if (v != null) v.setOnClickListener(listener);
    }
}
