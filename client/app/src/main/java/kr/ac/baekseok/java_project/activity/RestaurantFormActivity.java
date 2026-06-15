package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.ApiTime;
import kr.ac.baekseok.java_project.dto.request.RestaurantCreateRequest;
import kr.ac.baekseok.java_project.dto.request.RestaurantUpdateRequest;
import kr.ac.baekseok.java_project.dto.response.RestaurantResponse;
import kr.ac.baekseok.java_project.network.RetrofitClient;
import kr.ac.baekseok.java_project.util.OwnerStore;
import kr.ac.baekseok.java_project.util.TimePickerHelper;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 식당 등록 / 수정 화면 (OWNER 전용).
 *
 * 모드:
 *  - 등록: EXTRA_RESTAURANT_ID 없이 진입 → createRestaurant
 *  - 수정: EXTRA_RESTAURANT_ID 전달 → 기존 정보 로드 후 updateRestaurant
 *
 * 카테고리 enum: KOREAN, JAPANESE, CHINESE, WESTERN, ASIAN, CAFE, ETC
 */
public class RestaurantFormActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";

    // 카테고리 코드와 화면 표시용 한글 (순서 일치)
    private static final String[] CATEGORY_CODES =
            {"KOREAN", "JAPANESE", "CHINESE", "WESTERN", "ASIAN", "CAFE", "ETC"};
    private static final String[] CATEGORY_LABELS =
            {"한식", "일식", "중식", "양식", "아시안", "카페", "기타"};

    private EditText etName, etAddress, etLatitude, etLongitude,
            etPhone, etClosedDays, etDescription;
    private Spinner spinnerCategory;
    private TextView tvScreenTitle, btnSubmit, tvOpenTime, tvCloseTime;

    private TimePickerHelper openTimeHelper, closeTimeHelper;

    private boolean isEditMode = false;
    private long restaurantId = -1;
    private boolean isRequesting = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_form);
        BaseActivity.applySystemBarInsets(this);

        restaurantId = getIntent().getLongExtra(EXTRA_RESTAURANT_ID, -1);
        isEditMode = restaurantId >= 0;

        bindViews();
        setupCategorySpinner();
        setupTimePickers();

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        if (isEditMode) {
            tvScreenTitle.setText("식당 정보 수정");
            btnSubmit.setText("수정");
            loadExisting();
        } else {
            tvScreenTitle.setText("식당 등록");
            btnSubmit.setText("등록");
        }

        btnSubmit.setOnClickListener(v -> submit());
    }

    private void bindViews() {
        etName = findViewById(R.id.et_name);
        etAddress = findViewById(R.id.et_address);
        etLatitude = findViewById(R.id.et_latitude);
        etLongitude = findViewById(R.id.et_longitude);
        etPhone = findViewById(R.id.et_phone);
        etClosedDays = findViewById(R.id.et_closed_days);
        etDescription = findViewById(R.id.et_description);
        spinnerCategory = findViewById(R.id.spinner_category);
        tvScreenTitle = findViewById(R.id.tv_screen_title);
        btnSubmit = findViewById(R.id.btn_submit);
        tvOpenTime = findViewById(R.id.tv_open_time);
        tvCloseTime = findViewById(R.id.tv_close_time);
    }

    private void setupCategorySpinner() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, CATEGORY_LABELS);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerCategory.setAdapter(adapter);
    }

    private void setupTimePickers() {
        openTimeHelper = new TimePickerHelper(tvOpenTime);
        closeTimeHelper = new TimePickerHelper(tvCloseTime);
        openTimeHelper.attach(this);
        closeTimeHelper.attach(this);
    }

    /** 수정 모드: 기존 식당 정보를 불러와 입력칸 채우기 */
    private void loadExisting() {
        RetrofitClient.getApi().getRestaurant(restaurantId)
                .enqueue(new Callback<RestaurantResponse>() {
                    @Override
                    public void onResponse(@NonNull Call<RestaurantResponse> call,
                                           @NonNull Response<RestaurantResponse> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            fillForm(response.body());
                        } else {
                            Toast.makeText(RestaurantFormActivity.this,
                                    "정보를 불러오지 못했습니다 (" + response.code() + ")",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<RestaurantResponse> call,
                                          @NonNull Throwable t) {
                        Toast.makeText(RestaurantFormActivity.this,
                                "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void fillForm(RestaurantResponse r) {
        etName.setText(r.name);
        etAddress.setText(r.address);
        etLatitude.setText(String.valueOf(r.latitude));
        etLongitude.setText(String.valueOf(r.longitude));
        if (r.phoneNumber != null) etPhone.setText(r.phoneNumber);
        if (r.closedDays != null) etClosedDays.setText(r.closedDays);
        if (r.description != null) etDescription.setText(r.description);

        openTimeHelper.setValue(r.openTime);
        closeTimeHelper.setValue(r.closeTime);

        // 카테고리 선택
        int idx = indexOfCategory(r.category);
        if (idx >= 0) spinnerCategory.setSelection(idx);
    }

    private int indexOfCategory(String code) {
        if (code == null) return -1;
        for (int i = 0; i < CATEGORY_CODES.length; i++) {
            if (CATEGORY_CODES[i].equals(code)) return i;
        }
        return -1;
    }

    private void submit() {
        if (isRequesting) return;

        String name = etName.getText().toString().trim();
        String address = etAddress.getText().toString().trim();
        String latStr = etLatitude.getText().toString().trim();
        String lngStr = etLongitude.getText().toString().trim();

        // 필수값 검증
        if (name.isEmpty()) { toast("상호명을 입력하세요"); return; }
        if (address.isEmpty()) { toast("주소를 입력하세요"); return; }
        if (latStr.isEmpty() || lngStr.isEmpty()) { toast("위도/경도를 입력하세요"); return; }
        if (!openTimeHelper.hasValue()) { toast("오픈 시간을 선택하세요"); return; }
        if (!closeTimeHelper.hasValue()) { toast("마감 시간을 선택하세요"); return; }

        double lat, lng;
        try {
            lat = Double.parseDouble(latStr);
            lng = Double.parseDouble(lngStr);
        } catch (NumberFormatException e) {
            toast("위도/경도는 숫자로 입력하세요");
            return;
        }

        String category = CATEGORY_CODES[spinnerCategory.getSelectedItemPosition()];
        String phone = etPhone.getText().toString().trim();
        String closedDays = etClosedDays.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        ApiTime openTime = openTimeHelper.getValue();
        ApiTime closeTime = closeTimeHelper.getValue();

        isRequesting = true;
        btnSubmit.setEnabled(false);

        if (isEditMode) {
            doUpdate(name, category, address, lat, lng, phone, description,
                    openTime, closeTime, closedDays);
        } else {
            doCreate(name, category, address, lat, lng, phone, description,
                    openTime, closeTime, closedDays);
        }
    }

    private void doCreate(String name, String category, String address,
                          double lat, double lng, String phone, String description,
                          ApiTime openTime, ApiTime closeTime, String closedDays) {
        RestaurantCreateRequest req = new RestaurantCreateRequest();
        req.name = name;
        req.category = category;
        req.address = address;
        req.latitude = lat;
        req.longitude = lng;
        req.phoneNumber = phone.isEmpty() ? null : phone;
        req.description = description.isEmpty() ? null : description;
        req.openTime = openTime.toServerString();
        req.closeTime = closeTime.toServerString();
        req.closedDays = closedDays.isEmpty() ? null : closedDays;

        RetrofitClient.getApi().createRestaurant(req).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call, @NonNull Response<Long> response) {
                isRequesting = false;
                btnSubmit.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    long newId = response.body();
                    // 내 식당으로 저장
                    OwnerStore.saveRestaurantId(RestaurantFormActivity.this, newId);
                    Toast.makeText(RestaurantFormActivity.this,
                            "식당이 등록되었습니다", Toast.LENGTH_SHORT).show();
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

    private void doUpdate(String name, String category, String address,
                          double lat, double lng, String phone, String description,
                          ApiTime openTime, ApiTime closeTime, String closedDays) {
        RestaurantUpdateRequest req = new RestaurantUpdateRequest();
        req.name = name;
        req.category = category;
        req.address = address;
        req.latitude = lat;
        req.longitude = lng;
        req.phoneNumber = phone.isEmpty() ? null : phone;
        req.description = description.isEmpty() ? null : description;
        req.openTime = openTime.toServerString();
        req.closeTime = closeTime.toServerString();
        req.closedDays = closedDays.isEmpty() ? null : closedDays;

        RetrofitClient.getApi().updateRestaurant(restaurantId, req)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {
                        isRequesting = false;
                        btnSubmit.setEnabled(true);
                        if (response.isSuccessful()) {
                            Toast.makeText(RestaurantFormActivity.this,
                                    "수정되었습니다", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else if (response.code() == 403) {
                            toast("OWNER 권한이 필요합니다");
                        } else {
                            toast("수정 실패 (" + response.code() + ")");
                        }
                    }
                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        isRequesting = false;
                        btnSubmit.setEnabled(true);
                        toast("서버에 연결할 수 없습니다");
                    }
                });
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
