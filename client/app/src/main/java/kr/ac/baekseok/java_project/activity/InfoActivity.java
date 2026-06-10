package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;

/**
 * 정보 표시용 공용 Activity (앱 소개 / 공지사항 / 고객센터)
 */
public class InfoActivity extends AppCompatActivity {

    public static final String EXTRA_INFO_TYPE = "extra_info_type";

    public static final int TYPE_APP_INTRO = 1;
    public static final int TYPE_NOTICE = 2;
    public static final int TYPE_CUSTOMER_CENTER = 3;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        BaseActivity.applySystemBarInsets(this);

        int type = getIntent().getIntExtra(EXTRA_INFO_TYPE, TYPE_APP_INTRO);

        setupBackButton();
        showContent(type);
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());
    }

    private void showContent(int type) {
        TextView tvTitle = findViewById(R.id.tv_title);
        TextView tvContent = findViewById(R.id.tv_content);

        switch (type) {
            case TYPE_APP_INTRO:
                tvTitle.setText("앱 소개");
                tvContent.setText(getAppIntro());
                break;
            case TYPE_NOTICE:
                tvTitle.setText("공지사항");
                tvContent.setText(getNoticeContent());
                break;
            case TYPE_CUSTOMER_CENTER:
                tvTitle.setText("고객센터");
                tvContent.setText(getCustomerCenterContent());
                break;
        }
    }

    private String getAppIntro() {
        return "맛집 공유 앱\n"
                + "버전 1.0.0\n\n"
                + "이 앱은 동네 맛집 정보를 공유하고 추천받을 수 있는 서비스입니다.\n\n"
                + "■ 주요 기능\n"
                + "  · 동네 맛집 둘러보기\n"
                + "  · 음식명으로 가게 검색\n"
                + "  · 맛집 공유 게시판\n"
                + "  · 나만의 맛집 저장\n\n"
                + "■ 제작\n"
                + "  백석대학교 자바 프로젝트\n";
    }

    private String getNoticeContent() {
        return "[중요] 서비스 점검 안내\n"
                + "2025년 12월 1일 새벽 2시~4시 정기 점검이 진행됩니다.\n\n"
                + "─────────────────\n\n"
                + "[업데이트] v1.0.0 출시\n"
                + "맛집 공유 앱이 정식 출시되었습니다. "
                + "동네 맛집 정보를 공유하고 추천받아 보세요!\n\n"
                + "─────────────────\n\n"
                + "[이벤트] 첫 게시물 작성 이벤트\n"
                + "첫 게시물 작성 시 뱃지를 드립니다.\n";
    }

    private String getCustomerCenterContent() {
        return "고객 문의\n\n"
                + "■ 이메일\n"
                + "  support@example.com\n\n"
                + "■ 운영 시간\n"
                + "  평일 09:00 ~ 18:00 (점심시간 12:00~13:00)\n"
                + "  주말 및 공휴일 휴무\n\n"
                + "■ 자주 묻는 질문\n"
                + "  Q. 게시물은 어떻게 삭제하나요?\n"
                + "  A. 마이페이지 → 내가 쓴 게시물에서 길게 눌러 삭제할 수 있습니다.\n\n"
                + "  Q. 닉네임은 몇 번까지 바꿀 수 있나요?\n"
                + "  A. 닉네임 변경에는 제한이 없습니다.\n\n"
                + "  Q. 영업시간이나 메뉴 정보가 잘못되었어요.\n"
                + "  A. 가게 상세 화면에서 신고 기능으로 제보해 주세요.\n";
    }
}
