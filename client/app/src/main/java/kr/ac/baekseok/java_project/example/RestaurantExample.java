package kr.ac.baekseok.java_project.example;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import kr.ac.baekseok.java_project.dto.request.ReservationCreateRequest;
import kr.ac.baekseok.java_project.dto.request.ReservationMenuItemRequest;
import kr.ac.baekseok.java_project.dto.response.MenuResponse;
import kr.ac.baekseok.java_project.dto.response.RestaurantResponse;
import kr.ac.baekseok.java_project.network.ApiService;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 식당 상세 + 메뉴 조회 + 예약 생성 예시.
 *
 * 기존에 만든 RestaurantDetailActivity / SeatSelectActivity / ReservationConfirmActivity의
 * 더미 데이터 부분을 이 호출들로 교체하면 실제 서버 연동이 된다.
 */
public class RestaurantExample {

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onFailure(String message);
    }

    /**
     * 식당 상세 조회.
     * RestaurantDetailActivity.bindBasicInfo() 대신 사용.
     */
    public static void loadRestaurant(long restaurantId,
                                      DataCallback<RestaurantResponse> callback) {
        ApiService api = RetrofitClient.getApi();
        api.getRestaurant(restaurantId).enqueue(new Callback<RestaurantResponse>() {
            @Override
            public void onResponse(@NonNull Call<RestaurantResponse> call,
                                   @NonNull Response<RestaurantResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("식당 정보를 불러오지 못했습니다 (" + response.code() + ")");
                }
            }
            @Override
            public void onFailure(@NonNull Call<RestaurantResponse> call, @NonNull Throwable t) {
                callback.onFailure("서버 연결 실패: " + t.getMessage());
            }
        });
    }

    /**
     * 식당 메뉴 목록 조회.
     * RestaurantDetailActivity.loadRestaurantInfo()의 더미 menus 대신 사용.
     * MenuResponse.inDangerous로 알레르기 위험 메뉴를 표시할 수 있다.
     */
    public static void loadMenus(long restaurantId,
                                 DataCallback<List<MenuResponse>> callback) {
        ApiService api = RetrofitClient.getApi();
        api.getMenus(restaurantId).enqueue(new Callback<List<MenuResponse>>() {
            @Override
            public void onResponse(@NonNull Call<List<MenuResponse>> call,
                                   @NonNull Response<List<MenuResponse>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure("메뉴를 불러오지 못했습니다 (" + response.code() + ")");
                }
            }
            @Override
            public void onFailure(@NonNull Call<List<MenuResponse>> call, @NonNull Throwable t) {
                callback.onFailure("서버 연결 실패: " + t.getMessage());
            }
        });
    }

    /**
     * 예약 생성.
     * ReservationConfirmActivity.confirmReservation()의 더미 처리 대신 사용.
     *
     * @param restaurantId  식당 id
     * @param isoDateTime   예약 시간 (ISO 형식, 예: "2025-12-01T18:30:00")
     * @param menuId        예약할 메뉴 id
     * @param quantity      수량(=인원수 등)
     */
    public static void createReservation(long restaurantId, String isoDateTime,
                                         long menuId, int quantity,
                                         DataCallback<Long> callback) {
        ApiService api = RetrofitClient.getApi();

        List<ReservationMenuItemRequest> items = new ArrayList<>();
        items.add(new ReservationMenuItemRequest(menuId, quantity));

        ReservationCreateRequest request =
                new ReservationCreateRequest(isoDateTime, items);

        api.createReservation(restaurantId, request).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call,
                                   @NonNull Response<Long> response) {
                if (response.isSuccessful() && response.body() != null) {
                    callback.onSuccess(response.body());  // 생성된 reservationId
                } else if (response.code() == 401) {
                    callback.onFailure("로그인이 필요합니다");
                } else {
                    callback.onFailure("예약 실패 (" + response.code() + ")");
                }
            }
            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                callback.onFailure("서버 연결 실패: " + t.getMessage());
            }
        });
    }
}
