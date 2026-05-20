package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;

/**
 * 모든 하단 네비게이션이 있는 Activity의 부모 클래스.
 * 자식 Activity는 setContentView 호출 후 setupBottomNavigation()을 호출하면 된다.
 */
public abstract class BaseActivity extends AppCompatActivity {

    /**
     * 자식 Activity가 어떤 탭에 속하는지 반환.
     * 0 = 홈, 1 = 마이, 2 = 게시판, -1 = 어느 탭에도 속하지 않음(상세 화면 등)
     */
    protected abstract int getCurrentTab();

    /**
     * setContentView 이후에 호출해야 한다.
     */
    protected void setupBottomNavigation() {
        View bottomNav = findViewById(R.id.bottom_nav_include);
        if (bottomNav == null) return;

        LinearLayout navHome = bottomNav.findViewById(R.id.nav_home);
        LinearLayout navMy = bottomNav.findViewById(R.id.nav_my);
        LinearLayout navBoard = bottomNav.findViewById(R.id.nav_board);

        if (navHome != null) {
            navHome.setOnClickListener(v -> navigateTo(HomeActivity.class, 0));
        }
        if (navMy != null) {
            navMy.setOnClickListener(v -> navigateTo(MyPageActivity.class, 1));
        }
        if (navBoard != null) {
            navBoard.setOnClickListener(v -> navigateTo(BoardActivity.class, 2));
        }
    }

    private void navigateTo(Class<?> target, int tabIndex) {
        // 현재 탭과 같은 곳을 누르면 무시
        if (tabIndex == getCurrentTab()) return;

        Intent intent = new Intent(this, target);
        // 동일한 Activity가 스택에 쌓이지 않도록 처리
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
