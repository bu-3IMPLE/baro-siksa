package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;

import kr.ac.baekseok.java_project.R;

/**
 * 지도/주소 화면 (이미지 2 우측)
 *
 * MapView 실제 구현은 사용 SDK에 따라 처리:
 * - Google Maps: SupportMapFragment를 map_container에 add
 * - 네이버 지도: NaverMap onMapReady()에서 좌표 마커 추가
 * - 카카오 지도: KakaoMapReadyCallback에서 라벨 추가
 */
public class MapActivity extends BaseActivity {

    public static final String EXTRA_ADDRESS = "extra_address";
    public static final String EXTRA_ADDRESS_DETAIL = "extra_address_detail";

    @Override
    protected int getCurrentTab() {
        return -1;  // 어느 탭에도 속하지 않음 (상세에서 진입)
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        setupBottomNavigation();
        loadIntentExtras();
        setupMap();
    }

    private void loadIntentExtras() {
        String address = getIntent().getStringExtra(EXTRA_ADDRESS);
        String addressDetail = getIntent().getStringExtra(EXTRA_ADDRESS_DETAIL);

        if (address != null) {
            TextView tvAddress = findViewById(R.id.tv_address);
            if (tvAddress != null) tvAddress.setText(address);
        }
        if (addressDetail != null) {
            TextView tvDetail = findViewById(R.id.tv_address_detail);
            if (tvDetail != null) tvDetail.setText(addressDetail);
        }
    }

    private void setupMap() {
        // TODO: 실제 지도 SDK 초기화
        //
        // Google Maps 예시:
        //   SupportMapFragment mapFragment = (SupportMapFragment)
        //       getSupportFragmentManager().findFragmentById(R.id.map_container);
        //   mapFragment.getMapAsync(googleMap -> {
        //       LatLng pos = new LatLng(36.815, 127.114); // 천안
        //       googleMap.addMarker(new MarkerOptions().position(pos).title("00칼국수"));
        //       googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(pos, 15f));
        //   });
        //
        // 네이버 지도 예시:
        //   MapView mapView = findViewById(R.id.map_view);
        //   mapView.getMapAsync(naverMap -> {
        //       CameraPosition cam = new CameraPosition(new LatLng(36.815, 127.114), 15);
        //       naverMap.setCameraPosition(cam);
        //       Marker marker = new Marker();
        //       marker.setPosition(new LatLng(36.815, 127.114));
        //       marker.setMap(naverMap);
        //   });
    }
}
