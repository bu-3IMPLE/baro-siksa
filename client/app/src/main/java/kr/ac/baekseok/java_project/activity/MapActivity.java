package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.kakao.vectormap.KakaoMap;
import com.kakao.vectormap.KakaoMapReadyCallback;
import com.kakao.vectormap.LatLng;
import com.kakao.vectormap.MapLifeCycleCallback;
import com.kakao.vectormap.MapView;
import com.kakao.vectormap.camera.CameraUpdateFactory;
import com.kakao.vectormap.label.LabelOptions;
import com.kakao.vectormap.label.LabelStyle;
import com.kakao.vectormap.label.LabelStyles;

import kr.ac.baekseok.java_project.R;

public class MapActivity extends BaseActivity {

    public static final String EXTRA_ADDRESS = "extra_address";
    public static final String EXTRA_ADDRESS_DETAIL = "extra_address_detail";
    public static final String EXTRA_LAT = "extra_lat";
    public static final String EXTRA_LNG = "extra_lng";
    public static final String EXTRA_NAME = "extra_name";

    // 위치 정보 없을 때 기본값 (백석대학교)
    private static final double DEFAULT_LAT = 36.8154;
    private static final double DEFAULT_LNG = 127.1139;

    private MapView mapView;
    private double lat;
    private double lng;

    @Override
    protected int getCurrentTab() {
        return -1;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        setupBottomNavigation();

        lat = getIntent().getDoubleExtra(EXTRA_LAT, DEFAULT_LAT);
        lng = getIntent().getDoubleExtra(EXTRA_LNG, DEFAULT_LNG);

        String address = getIntent().getStringExtra(EXTRA_ADDRESS);
        String addressDetail = getIntent().getStringExtra(EXTRA_ADDRESS_DETAIL);
        String name = getIntent().getStringExtra(EXTRA_NAME);

        if (address != null) {
            TextView tvAddress = findViewById(R.id.tv_address);
            if (tvAddress != null) tvAddress.setText(address);
        }
        if (addressDetail != null) {
            TextView tvDetail = findViewById(R.id.tv_address_detail);
            if (tvDetail != null) tvDetail.setText(addressDetail);
        }

        mapView = findViewById(R.id.map_view);
        mapView.start(new MapLifeCycleCallback() {
            @Override
            public void onMapDestroy() {}

            @Override
            public void onMapError(Exception e) {
                Toast.makeText(MapActivity.this, "지도를 불러오지 못했습니다.", Toast.LENGTH_SHORT).show();
            }
        }, new KakaoMapReadyCallback() {
            @Override
            public void onMapReady(@NonNull KakaoMap kakaoMap) {
                LatLng pos = LatLng.from(lat, lng);

                kakaoMap.moveCamera(CameraUpdateFactory.newCenterPosition(pos, 15));

                String label = (name != null && !name.isEmpty()) ? name : "식당 위치";
                kakaoMap.getLabelManager()
                        .getLayer()
                        .addLabel(LabelOptions.from(label, pos)
                                .setStyles(LabelStyles.from(LabelStyle.from(R.drawable.ic_location))));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mapView != null) mapView.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mapView != null) mapView.pause();
    }
}
