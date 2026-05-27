package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;

import kr.ac.baekseok.java_project.R;

/**
 * 마이 페이지 - 나의 맛집 (이미지 2 좌측)
 */
public class MyPageActivity extends BaseActivity {

    private TextView tvNickname;
    private String currentNickname = "닉네임";

    private ActivityResultLauncher<Intent> nicknameLauncher;
    private ActivityResultLauncher<Intent> profileLauncher;

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
    }

    private void bindViews() {
        tvNickname = findViewById(R.id.tv_nickname);
    }

    private void registerLaunchers() {
        nicknameLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        String newNickname = result.getData().getStringExtra(
                                NicknameChangeActivity.EXTRA_NEW_NICKNAME);
                        if (newNickname != null) {
                            currentNickname = newNickname;
                            tvNickname.setText(newNickname);
                        }
                    }
                });

        profileLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        // 프로필 사진 갱신 처리 (실제로는 Glide 등으로 이미지 로드)
                    }
                });
    }

    private void setupButtons() {
        // 닉네임 변경
        setClickListener(R.id.btn_change_nickname, v -> {
            Intent intent = new Intent(this, NicknameChangeActivity.class);
            intent.putExtra(NicknameChangeActivity.EXTRA_CURRENT_NICKNAME, currentNickname);
            nicknameLauncher.launch(intent);
        });

        // 프로필 변경
        setClickListener(R.id.btn_change_profile, v -> {
            Intent intent = new Intent(this, ProfileChangeActivity.class);
            profileLauncher.launch(intent);
        });

        // 내가 쓴 게시물
        setClickListener(R.id.btn_my_posts, v -> {
            Intent intent = new Intent(this, MyListActivity.class);
            intent.putExtra(MyListActivity.EXTRA_LIST_TYPE, MyListActivity.TYPE_MY_POSTS);
            startActivity(intent);
        });

        // 내가 쓴 답글
        setClickListener(R.id.btn_my_replies, v -> {
            Intent intent = new Intent(this, MyListActivity.class);
            intent.putExtra(MyListActivity.EXTRA_LIST_TYPE, MyListActivity.TYPE_MY_REPLIES);
            startActivity(intent);
        });

        // 나만의 맛집
        setClickListener(R.id.btn_only_my_restaurants, v -> {
            Intent intent = new Intent(this, MyListActivity.class);
            intent.putExtra(MyListActivity.EXTRA_LIST_TYPE, MyListActivity.TYPE_MY_RESTAURANTS);
            startActivity(intent);
        });

        // 앱 소개
        setClickListener(R.id.btn_app_intro, v -> {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra(InfoActivity.EXTRA_INFO_TYPE, InfoActivity.TYPE_APP_INTRO);
            startActivity(intent);
        });

        // 공지사항
        setClickListener(R.id.btn_notice, v -> {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra(InfoActivity.EXTRA_INFO_TYPE, InfoActivity.TYPE_NOTICE);
            startActivity(intent);
        });

        // 고객센터
        setClickListener(R.id.btn_customer_center, v -> {
            Intent intent = new Intent(this, InfoActivity.class);
            intent.putExtra(InfoActivity.EXTRA_INFO_TYPE, InfoActivity.TYPE_CUSTOMER_CENTER);
            startActivity(intent);
        });
    }

    private void setClickListener(int viewId, View.OnClickListener listener) {
        View v = findViewById(viewId);
        if (v != null) v.setOnClickListener(listener);
    }
}
