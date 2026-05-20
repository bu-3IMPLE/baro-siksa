package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;

/**
 * 검색 입력 화면 (이미지 3 좌측)
 *
 * 키보드는 EditText 포커스로 자동으로 올라옴.
 * 검색 실행 시 SearchResultActivity로 키워드 전달.
 */
public class SearchActivity extends AppCompatActivity {

    private EditText etSearch;
    private ImageView btnClear;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        etSearch = findViewById(R.id.et_search);
        btnClear = findViewById(R.id.btn_clear);

        setupBackButton();
        setupSearchInput();
        setupClearButton();

        // 화면 진입 시 자동으로 키보드 올리기
        autoShowKeyboard();
    }

    private void setupBackButton() {
        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) {
            btnBack.setOnClickListener(v -> finish());
        }
    }

    private void setupSearchInput() {
        if (etSearch == null) return;

        etSearch.requestFocus();

        // 키보드 검색 버튼 → 검색 실행
        etSearch.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch();
                return true;
            }
            return false;
        });

        // 입력에 따라 X 버튼 보임/숨김
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (btnClear == null) return;
                btnClear.setVisibility(s.length() > 0 ? View.VISIBLE : View.INVISIBLE);
            }

            @Override public void afterTextChanged(Editable s) {}
        });

        // 초기 상태는 빈 검색어 → X 숨김
        if (btnClear != null) btnClear.setVisibility(View.INVISIBLE);
    }

    private void setupClearButton() {
        if (btnClear == null) return;
        btnClear.setOnClickListener(v -> {
            if (etSearch != null) etSearch.setText("");
        });
    }

    private void autoShowKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        if (etSearch != null) {
            etSearch.post(() -> {
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                if (imm != null) imm.showSoftInput(etSearch, InputMethodManager.SHOW_IMPLICIT);
            });
        }
    }

    private void performSearch() {
        if (etSearch == null) return;
        String keyword = etSearch.getText().toString().trim();
        if (keyword.isEmpty()) return;

        Intent intent = new Intent(this, SearchResultActivity.class);
        intent.putExtra(SearchResultActivity.EXTRA_KEYWORD, keyword);
        startActivity(intent);
    }
}
