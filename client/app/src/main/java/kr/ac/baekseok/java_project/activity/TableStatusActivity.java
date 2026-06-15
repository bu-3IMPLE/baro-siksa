package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.adapter.TableAdapter;
import kr.ac.baekseok.java_project.dto.request.TableStatusUpdateRequest;
import kr.ac.baekseok.java_project.dto.response.TableResponse;
import kr.ac.baekseok.java_project.network.RetrofitClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;

public class TableStatusActivity extends AppCompatActivity {

    public static final String EXTRA_RESTAURANT_ID = "extra_restaurant_id";
    public static final String EXTRA_RESTAURANT_NAME = "extra_restaurant_name";
    public static final String EXTRA_IS_OWNER = "extra_is_owner";

    private static final int GRID_SPAN = 2;

    private long restaurantId;
    private String restaurantName;
    private boolean isOwner;

    private RecyclerView rvTables;
    private TextView tvWsStatus;
    private TextView tvEmpty;
    private TextView tvOwnerHint;
    private TableAdapter adapter;
    private final List<TableResponse> tables = new ArrayList<>();

    private StompClient stompClient;
    private CompositeDisposable compositeDisposable;

    private final Handler mainHandler = new Handler(Looper.getMainLooper());
    private final Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_table_status);
        BaseActivity.applySystemBarInsets(this);

        restaurantId = getIntent().getLongExtra(EXTRA_RESTAURANT_ID, -1);
        restaurantName = getIntent().getStringExtra(EXTRA_RESTAURANT_NAME);
        isOwner = getIntent().getBooleanExtra(EXTRA_IS_OWNER, false);

        bindViews();
        setupBackButton();
        setupRecyclerView();
        loadTables();
        connectWebSocket();
    }

    private void bindViews() {
        tvWsStatus = findViewById(R.id.tv_ws_status);
        tvEmpty = findViewById(R.id.tv_empty);
        rvTables = findViewById(R.id.rv_tables);
        tvOwnerHint = findViewById(R.id.tv_owner_hint);

        TextView tvName = findViewById(R.id.tv_restaurant_name);
        if (tvName != null && restaurantName != null) tvName.setText(restaurantName + " 테이블");

        if (tvOwnerHint != null) {
            tvOwnerHint.setVisibility(isOwner ? View.VISIBLE : View.GONE);
        }
    }

    private void setupBackButton() {
        View btn = findViewById(R.id.btn_back);
        if (btn != null) btn.setOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        adapter = new TableAdapter(tables, this::onTableSelected);
        if (isOwner) {
            adapter.setLongClickListener(this::onTableLongPressed);
        }
        rvTables.setLayoutManager(new GridLayoutManager(this, GRID_SPAN));
        rvTables.setAdapter(adapter);
    }

    private void loadTables() {
        RetrofitClient.getApi().getTables(restaurantId).enqueue(new Callback<List<TableResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<TableResponse>> call,
                                   @NonNull Response<List<TableResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    tables.clear();
                    tables.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    tvEmpty.setVisibility(tables.isEmpty() ? View.VISIBLE : View.GONE);
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<TableResponse>> call, @NonNull Throwable t) {
                Toast.makeText(TableStatusActivity.this, "테이블 정보를 불러오지 못했습니다", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void connectWebSocket() {
        String wsUrl = RetrofitClient.BASE_URL
                .replaceFirst("^http", "ws") + "ws";

        compositeDisposable = new CompositeDisposable();
        stompClient = Stomp.over(Stomp.ConnectionProvider.OKHTTP, wsUrl);

        compositeDisposable.add(
                stompClient.lifecycle()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(event -> {
                            switch (event.getType()) {
                                case OPENED:
                                    tvWsStatus.setText("실시간 연결됨");
                                    break;
                                case CLOSED:
                                    tvWsStatus.setText("연결 끊김");
                                    break;
                                case ERROR:
                                    tvWsStatus.setText("연결 오류");
                                    break;
                                default:
                                    break;
                            }
                        })
        );

        compositeDisposable.add(
                stompClient.topic("/topic/restaurant/" + restaurantId + "/tables")
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(message -> {
                            TableResponse updated = gson.fromJson(message.getPayload(), TableResponse.class);
                            adapter.updateTable(updated);
                        }, throwable -> {
                            // 구독 오류 무시
                        })
        );

        stompClient.connect();
    }

    private void onTableSelected(TableResponse table) {
        if (isOwner) return; // 업주 모드에서는 일반 클릭 무시
        Intent intent = new Intent(this, ReservationFormActivity.class);
        intent.putExtra(ReservationFormActivity.EXTRA_RESTAURANT_ID, restaurantId);
        intent.putExtra(ReservationFormActivity.EXTRA_RESTAURANT_NAME, restaurantName);
        intent.putExtra(ReservationFormActivity.EXTRA_TABLE_ID, table.tableId);
        intent.putExtra(ReservationFormActivity.EXTRA_TABLE_NUMBER, table.tableNumber);
        startActivity(intent);
    }

    private void onTableLongPressed(TableResponse table) {
        if ("RESERVED".equals(table.status)) {
            Toast.makeText(this, "예약된 테이블은 직접 변경할 수 없습니다", Toast.LENGTH_SHORT).show();
            return;
        }

        boolean currentlyOccupied = "OCCUPIED".equals(table.status);
        String newStatus = currentlyOccupied ? "AVAILABLE" : "OCCUPIED";
        String message = currentlyOccupied
                ? "테이블 " + table.tableNumber + "을(를) 비어 있는 상태로 변경하시겠습니까?"
                : "테이블 " + table.tableNumber + "에 홀 손님이 착석 중으로 처리하시겠습니까?\n앱 예약이 불가능해집니다.";
        String confirmLabel = currentlyOccupied ? "해제" : "잠금";

        new AlertDialog.Builder(this)
                .setTitle("테이블 상태 변경")
                .setMessage(message)
                .setPositiveButton(confirmLabel, (dialog, which) ->
                        doUpdateStatus(table, newStatus))
                .setNegativeButton("취소", null)
                .show();
    }

    private void doUpdateStatus(TableResponse table, String newStatus) {
        RetrofitClient.getApi()
                .updateTableStatus(restaurantId, table.tableId, new TableStatusUpdateRequest(newStatus))
                .enqueue(new Callback<Void>() {
                    @Override
                    public void onResponse(@NonNull Call<Void> call,
                                           @NonNull Response<Void> response) {
                        if (response.isSuccessful()) {
                            String msg = "OCCUPIED".equals(newStatus) ? "테이블을 잠갔습니다" : "테이블을 해제했습니다";
                            Toast.makeText(TableStatusActivity.this, msg, Toast.LENGTH_SHORT).show();
                            // WebSocket broadcast가 자동으로 UI 업데이트
                        } else {
                            Toast.makeText(TableStatusActivity.this,
                                    "변경에 실패했습니다 (" + response.code() + ")", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<Void> call, @NonNull Throwable t) {
                        Toast.makeText(TableStatusActivity.this,
                                "서버에 연결할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTables();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (compositeDisposable != null) compositeDisposable.dispose();
        if (stompClient != null) stompClient.disconnect();
    }
}
