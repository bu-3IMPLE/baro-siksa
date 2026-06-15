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

/**
 * 마이 페이지 - 실제 서버 데이터(getMyInfo) 연동.
 *
 * 서버에서 가져오는 것: username(닉네임), email, foodPreference
 *
 * 주의:
 *  - 닉네임(username) 변경, 음식취향, 비밀번호 변경 가능.
 *  - 프로필 사진 API 없어서 보류.
 */
public class MyPageActivity extends BaseActivity {

    private TextView tvNickname;

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

        // 화면 진입 시 서버에서 내 정보 로드
        loadMyInfo();
    }

    private void bindViews() {
        tvNickname = findViewById(R.id.tv_nickname);
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

    /**
     * 서버에서 내 정보를 가져와 화면에 반영한다.
     * 로그인 시 저장한 토큰이 AuthInterceptor로 자동 첨부된다.
     */
    private void loadMyInfo() {
        // 토큰이 없으면 로그인 안 된 상태 → 로그인 화면으로
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
                    // 토큰 만료/무효 → 로그인 화면으로
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
            public void onFailure(@NonNull Call<MemberResponse> call,
                                  @NonNull Throwable t) {
                Toast.makeText(MyPageActivity.this,
                        "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                if (tvNickname != null) tvNickname.setText("닉네임");
            }
        });
    }

    private void bindMyInfo() {
        if (myInfo == null) return;
        // 닉네임(username) 표시
        if (tvNickname != null) {
            tvNickname.setText(myInfo.username != null ? myInfo.username : "닉네임");
        }
    }

    private void setupButtons() {
        setClickListener(R.id.btn_change_nickname, v -> {
            Intent intent = new Intent(this, NicknameChangeActivity.class);
            intent.putExtra(NicknameChangeActivity.EXTRA_CURRENT_NICKNAME,
                    myInfo != null ? myInfo.username : "");
            nicknameLauncher.launch(intent);
        });

        // 프로필 변경 → 서버에 API 없음. 안내만.
        setClickListener(R.id.btn_change_profile, v ->
                Toast.makeText(this,
                        "프로필 사진 변경은 현재 지원되지 않습니다",
                        Toast.LENGTH_SHORT).show());

        // 내가 쓴 게시물 → 리뷰로 대체 예정 (다음 단계)
        setClickListener(R.id.btn_my_posts, v ->
                Toast.makeText(this, "준비 중입니다 (리뷰 연동 예정)",
                        Toast.LENGTH_SHORT).show());

        // 내가 쓴 답글 → 준비 중
        setClickListener(R.id.btn_my_replies, v ->
                startActivity(new Intent(this, OwnerRestaurantActivity.class)));
                /*Toast.makeText(this, "준비 중입니다",
                        Toast.LENGTH_SHORT).show());*/

        // 나만의 맛집(음식취향) → 음식취향 수정 화면
        setClickListener(R.id.btn_only_my_restaurants, v -> openPreferenceEdit());

        // 앱 소개 / 공지 / 고객센터 → 정적 화면 (기존 InfoActivity 재사용)
        setClickListener(R.id.btn_app_intro, v ->
                openInfo(InfoActivity.TYPE_APP_INTRO));
        setClickListener(R.id.btn_notice, v ->
                openInfo(InfoActivity.TYPE_NOTICE));
        setClickListener(R.id.btn_customer_center, v ->
                openInfo(InfoActivity.TYPE_CUSTOMER_CENTER));
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
