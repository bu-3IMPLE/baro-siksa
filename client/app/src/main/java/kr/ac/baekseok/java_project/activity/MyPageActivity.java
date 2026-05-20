package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import kr.ac.baekseok.java_project.R;

/**
 * 마이 페이지 - 나의 맛집 (이미지 2 좌측)
 */
public class MyPageActivity extends BaseActivity {

    @Override
    protected int getCurrentTab() {
        return 1;  // 마이 탭
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page);

        setupBottomNavigation();
        setupButtons();
    }

    private void setupButtons() {
        // 닉네임 변경
        setClickToast(R.id.btn_change_nickname, "닉네임 변경 화면으로 이동");

        // 프로필 변경
        setClickToast(R.id.btn_change_profile, "프로필 변경 화면으로 이동");

        // 내가 쓴 게시물
        setClickToast(R.id.btn_my_posts, "내가 쓴 게시물 목록");

        // 내가 쓴 답글
        setClickToast(R.id.btn_my_replies, "내가 쓴 답글 목록");

        // 나만의 맛집
        setClickToast(R.id.btn_only_my_restaurants, "나만의 맛집 목록");

        // 앱 소개
        setClickToast(R.id.btn_app_intro, "앱 소개 화면");

        // 공지사항
        setClickToast(R.id.btn_notice, "공지사항 화면");

        // 고객센터
        setClickToast(R.id.btn_customer_center, "고객센터 화면");
    }

    /**
     * 미구현 화면들은 일단 토스트만 띄운다.
     * 실제 구현 시 startActivity()로 교체.
     */
    private void setClickToast(int viewId, String message) {
        View view = findViewById(viewId);
        if (view != null) {
            view.setOnClickListener(v ->
                    Toast.makeText(this, message, Toast.LENGTH_SHORT).show());
        }
    }
}
