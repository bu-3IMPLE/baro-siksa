package kr.ac.baekseok.java_project.activity;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.request.MenuCreateRequest;
import kr.ac.baekseok.java_project.dto.request.MenuUpdateRequest;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 메뉴 등록 / 수정 / 삭제 화면 (OWNER 전용).
 *
 * 모드:
 *  - 등록: EXTRA_MENU_ID 없이 진입 → createMenu
 *  - 수정: EXTRA_MENU_ID + 기존 값(이름/가격/설명) 전달 → updateMenu, 삭제 버튼 표시
 *
 * restaurantId는 필수.
 */
public class MenuFormActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";
    public static final String EXTRA_MENU_ID = "extra_menu_id";
    public static final String EXTRA_MENU_NAME = "extra_menu_name";
    public static final String EXTRA_MENU_PRICE = "extra_menu_price";
    public static final String EXTRA_MENU_DESC = "extra_menu_desc";

    private EditText etName, etPrice, etDesc;
    private TextView tvScreenTitle, btnSubmit, btnDelete;

    private long restaurantId = -1;
    private long menuId = -1;
    private boolean isEditMode = false;
    private boolean isRequesting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_form);

        restaurantId = getIntent().getLongExtra(EXTRA_RESTAURANT_ID, -1);
        menuId = getIntent().getLongExtra(EXTRA_MENU_ID, -1);
        isEditMode = menuId >= 0;

        if (restaurantId < 0) {
            Toast.makeText(this, "식당 정보가 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        bindViews();

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (isEditMode) {
            tvScreenTitle.setText("메뉴 수정");
            btnSubmit.setText("수정");
            btnDelete.setVisibility(View.VISIBLE);
            // 기존 값 채우기
            etName.setText(getIntent().getStringExtra(EXTRA_MENU_NAME));
            int price = getIntent().getIntExtra(EXTRA_MENU_PRICE, 0);
            etPrice.setText(String.valueOf(price));
            String desc = getIntent().getStringExtra(EXTRA_MENU_DESC);
            if (desc != null) etDesc.setText(desc);
        } else {
            tvScreenTitle.setText("메뉴 등록");
            btnSubmit.setText("등록");
            btnDelete.setVisibility(View.GONE);
        }

        btnSubmit.setOnClickListener(v -> submit());
        btnDelete.setOnClickListener(v -> confirmDelete());
    }

    private void bindViews() {
        etName = findViewById(R.id.et_menu_name);
        etPrice = findViewById(R.id.et_menu_price);
        etDesc = findViewById(R.id.et_menu_desc);
        tvScreenTitle = findViewById(R.id.tv_screen_title);
        btnSubmit = findViewById(R.id.btn_submit);
        btnDelete = findViewById(R.id.btn_delete);
    }

    private void submit() {
        if (isRequesting) return;

        String name = etName.getText().toString().trim();
        String priceStr = etPrice.getText().toString().trim();
        String desc = etDesc.getText().toString().trim();

        if (name.isEmpty()) { toast("메뉴명을 입력하세요"); return; }
        if (priceStr.isEmpty()) { toast("가격을 입력하세요"); return; }

        int price;
        try {
            price = Integer.parseInt(priceStr);
        } catch (NumberFormatException e) {
            toast("가격은 숫자로 입력하세요");
            return;
        }
        if (price < 0) { toast("가격은 0 이상이어야 합니다"); return; }

        isRequesting = true;
        btnSubmit.setEnabled(false);

        if (isEditMode) {
            MenuUpdateRequest req = new MenuUpdateRequest();
            req.name = name;
            req.price = price;
            req.description = desc.isEmpty() ? null : desc;
            req.restaurantIngredientIds = null;  // 재료 연결은 식재료 관리에서 별도

            RetrofitClient.getApi().updateMenu(restaurantId, menuId, req)
                    .enqueue(voidCallback("수정되었습니다"));
        } else {
            MenuCreateRequest req = new MenuCreateRequest();
            req.name = name;
            req.price = price;
            req.description = desc.isEmpty() ? null : desc;
            req.restaurantIngredientIds = null;

            RetrofitClient.getApi().createMenu(restaurantId, req)
                    .enqueue(new Callback<Long>() {
                        @Override
                        public void onResponse(@NonNull Call<Long> call,
                                               @NonNull Response<Long> response) {
                            isRequesting = false;
                            btnSubmit.setEnabled(true);
                            if (response.isSuccessful()) {
                                Toast.makeText(MenuFormActivity.this,
                                        "메뉴가 등록되었습니다", Toast.LENGTH_SHORT).show();
                                setResult(RESULT_OK);
                                finish();
                            } else if (response.code() == 403) {
                                toast("OWNER 권한이 필요합니다");
                            } else {
                                toast("등록 실패 (" + response.code() + ")");
                            }
                        }
                        @Override
                        public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                            isRequesting = false;
                            btnSubmit.setEnabled(true);
                            toast("서버에 연결할 수 없습니다");
                        }
                    });
        }
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("메뉴 삭제")
                .setMessage("이 메뉴를 삭제하시겠습니까?")
                .setPositiveButton("삭제", (d, w) -> doDelete())
                .setNegativeButton("취소", null)
                .show();
    }

    private void doDelete() {
        if (isRequesting) return;
        isRequesting = true;

        RetrofitClient.getApi().deleteMenu(restaurantId, menuId)
                .enqueue(voidCallback("삭제되었습니다"));
    }

    /** 수정/삭제 공통 콜백 (Void 응답) */
    private Callback<Void> voidCallback(String successMsg) {
        return new Callback<Void>() {
            @Override
            public void onResponse(@NonNull Call<Void> call,
                                   @NonNull Response<Void> response) {
                isRequesting = false;
                btnSubmit.setEnabled(true);
                if (response.isSuccessful()) {
                    Toast.makeText(MenuFormActivity.this, successMsg,
                            Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else if (response.code() == 403) {
                    toast("OWNER 권한이 필요합니다");
                } else {
                    toast("실패 (" + response.code() + ")");
                }
            }
            @Override
            public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                isRequesting = false;
                btnSubmit.setEnabled(true);
                toast("서버에 연결할 수 없습니다");
            }
        };
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
