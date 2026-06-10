package kr.ac.baekseok.java_project.activity;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import kr.ac.baekseok.java_project.R;

/**
 * 모든 하단 네비게이션이 있는 Activity의 부모 클래스.
 * 자식 Activity는 setContentView 호출 후 setupBottomNavigation()을 호출하면 된다.
 */
public abstract class BaseActivity extends AppCompatActivity {

    protected abstract int getCurrentTab();

    /**
     * setContentView 이후에 호출해야 한다.
     */
    protected void setupBottomNavigation() {
        applySystemBarInsets(this);

        View bottomNav = findViewById(R.id.bottom_nav_include);
        if (bottomNav == null) return;

        LinearLayout navHome = bottomNav.findViewById(R.id.nav_home);
        LinearLayout navMy = bottomNav.findViewById(R.id.nav_my);
        LinearLayout navBoard = bottomNav.findViewById(R.id.nav_board);

        if (navHome != null) navHome.setOnClickListener(v -> navigateTo(HomeActivity.class, 0));
        if (navMy != null) navMy.setOnClickListener(v -> navigateTo(MyPageActivity.class, 1));
        if (navBoard != null) navBoard.setOnClickListener(v -> navigateTo(BoardActivity.class, 2));
    }

    /**
     * targetSdk 35+ 강제 edge-to-edge 대응.
     * setContentView() 직후에 호출한다.
     * 루트 뷰에 상단(status bar) / 하단(navigation bar) 패딩을 자동으로 적용한다.
     */
    public static void applySystemBarInsets(Activity activity) {
        WindowCompat.setDecorFitsSystemWindows(activity.getWindow(), false);

        ViewGroup contentFrame = activity.findViewById(android.R.id.content);
        if (contentFrame == null || contentFrame.getChildCount() == 0) return;
        View root = contentFrame.getChildAt(0);

        ViewCompat.setOnApplyWindowInsetsListener(root, (v, windowInsets) -> {
            Insets insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(
                    v.getPaddingLeft(),
                    insets.top,
                    v.getPaddingRight(),
                    insets.bottom
            );
            return WindowInsetsCompat.CONSUMED;
        });
    }

    private void navigateTo(Class<?> target, int tabIndex) {
        if (tabIndex == getCurrentTab()) return;
        Intent intent = new Intent(this, target);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}
