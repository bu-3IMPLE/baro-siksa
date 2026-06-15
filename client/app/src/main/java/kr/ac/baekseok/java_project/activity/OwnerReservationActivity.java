package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.OwnerReservationAdapter;
import kr.ac.baekseok.java_project.dto.request.ReservationStatusUpdateRequest;
import kr.ac.baekseok.java_project.dto.response.OwnerReservationResponse;
import kr.ac.baekseok.java_project.network.RetrofitClient;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 사장님 예약 관리 화면.
 * WebSocket으로 새 예약 실시간 알림 + REST로 목록 조회/상태 변경.
 */
public class OwnerReservationActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";

    private RecyclerView rvReservations;
    private ProgressBar progress;
    private TextView tvEmpty;
    private TextView tvWsStatus;

    private final List<OwnerReservationResponse> reservations = new ArrayList<>();
    private OwnerReservationAdapter adapter;

    private long restaurantId = -1;

    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_owner_reservation);
        BaseActivity.applySystemBarInsets(this);

        restaurantId = getIntent().getLongExtra(EXTRA_RESTAURANT_ID, -1);
        if (restaurantId < 0) {
            Toast.makeText(this, "식당 정보가 없습니다", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        rvReservations = findViewById(R.id.rv_reservations);
        progress = findViewById(R.id.progress);
        tvEmpty = findViewById(R.id.tv_empty);
        tvWsStatus = findViewById(R.id.tv_ws_status);

        View btnBack = findViewById(R.id.btn_back);
        if (btnBack != null) btnBack.setOnClickListener(v -> finish());

        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        adapter = new OwnerReservationAdapter(reservations, this::changeStatus);
        rvReservations.setAdapter(adapter);

        loadReservations();
        connectWebSocket();
    }

    private void connectWebSocket() {
        String wsUrl = RetrofitClient.BASE_URL.replaceFirst("^http", "ws") + "ws";
        compositeDisposable = new CompositeDisposable();
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);

        compositeDisposable.add(
                stompClient.lifecycle()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(event -> {
                            if (tvWsStatus == null) return;
                            switch (event.getType()) {
                                case OPENED:
                                    tvWsStatus.setText("실시간 알림 켜짐");
                                    break;
                                case CLOSED:
                                    tvWsStatus.setText("알림 꺼짐");
                                    break;
                                case ERROR:
                                    tvWsStatus.setText("연결 오류");
                                    break;
                                default:
                                    break;
                            }
                        })
        );

        // 새 예약 발생 시 목록 자동 갱신
        compositeDisposable.add(
                stompClient.topic("/topic/restaurant/" + restaurantId + "/reservations")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(message -> {
                            toast("새 예약이 들어왔습니다!");
                            loadReservations();
                        }, throwable -> {})
        );

        stompClient.connect();
    }

    private void loadReservations() {
        progress.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        RetrofitClient.getApi().getReservationsForOwner(restaurantId)
                .enqueue(new Callback<List<OwnerReservationResponse>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<OwnerReservationResponse>> call,
                                           @NonNull Response<List<OwnerReservationResponse>> response) {
                        progress.setVisibility(View.GONE);
                        if (response.isSuccessful() && response.body() != null) {
                            reservations.clear();
                            reservations.addAll(response.body());
                            adapter.notifyDataSetChanged();
                            updateEmpty();
                        } else if (response.code() == 403) {
                            toast("OWNER 권한이 필요합니다");
                            updateEmpty();
                        } else {
                            toast("예약을 불러오지 못했습니다 (" + response.code() + ")");
                            updateEmpty();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<OwnerReservationResponse>> call,
                                          @NonNull Throwable t) {
                        progress.setVisibility(View.GONE);
                        toast("서버에 연결할 수 없습니다");
                        updateEmpty();
                    }
                });
    }

    private void changeStatus(OwnerReservationResponse r, String newStatus) {
        ReservationStatusUpdateRequest req = new ReservationStatusUpdateRequest(newStatus);

        RetrofitClient.getApi()
                .updateReservationStatus(restaurantId, r.reservationId, req)
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            toast("상태가 변경되었습니다");
                            loadReservations();
                        } else if (response.code() == 403) {
                            toast("OWNER 권한이 필요합니다");
                        } else {
                            toast("변경 실패 (" + response.code() + ")");
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        toast("서버에 연결할 수 없습니다");
                    }
                });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) compositeDisposable.dispose();
        if (stompClient != null) stompClient.disconnect();
    }

    private void updateEmpty() {
        tvEmpty.setVisibility(reservations.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void toast(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
