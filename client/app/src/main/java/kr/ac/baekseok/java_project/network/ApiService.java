package kr.ac.baekseok.java_project.network;

import java.util.List;

import kr.ac.baekseok.java_project.dto.request.*;
import kr.ac.baekseok.java_project.dto.response.*;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * 바로식사 백엔드 API 전체 엔드포인트.
 *
 * Swagger 명세(openapi 3.1.0)를 그대로 매핑했다.
 * 응답이 없는 API(200 OK만)는 Call<Void>로 처리.
 * 생성 API는 보통 생성된 id(Long)를 반환.
 */
public interface ApiService {

    // ========================= 회원 (Member) =========================

    /** 회원가입 → 생성된 memberId 반환 */
    @POST("api/members/signup")
    Call<Long> signUp(@Body MemberSignUpRequest request);

    /** 로그인 → accessToken + refreshToken */
    @POST("api/members/login")
    Call<MemberLoginResponse> login(@Body MemberLoginRequest request);

    /** 토큰 재발급 */
    @POST("api/members/reissue")
    Call<MemberLoginResponse> reissue(@Body TokenReissueRequest request);

    /** 내 정보 조회 */
    @GET("api/members/me")
    Call<MemberResponse> getMyInfo();

    /** 회원 탈퇴 */
    @retrofit2.http.HTTP(method = "DELETE", path = "api/members/me", hasBody = true)
    Call<Void> withdraw(@Body MemberWithdrawRequest request);

    /** 음식 취향 수정 */
    @PATCH("api/members/{memberId}/preference")
    Call<Void> updatePreference(@Path("memberId") long memberId,
                                @Body MemberUpdateRequest request);

    /** 닉네임 변경 */
    @PATCH("api/members/me/nickname")
    Call<Void> updateNickname(@Body NicknameUpdateRequest request);

    /** 비밀번호 변경 */
    @PATCH("api/members/me/password")
    Call<Void> changePassword(@Body MemberPasswordUpdateRequest request);

    // ========================= 식당 (Restaurant) =========================

    /**
     * 식당 조건 검색 (페이징)
     * 조건과 페이징 파라미터를 쿼리스트링으로 전달.
     * 예: ?name=칼국수&category=KOREAN&page=0&size=20
     */
    @GET("api/restaurants")
    Call<PageRestaurantResponse> searchRestaurants(
            @Query("name") String name,
            @Query("category") String category,
            @Query("page") int page,
            @Query("size") int size);

    /** 주변 식당 검색 (위치 기반) */
    @GET("api/restaurants/nearby")
    Call<List<RestaurantResponse>> getNearbyRestaurants(
            @Query("latitude") double latitude,
            @Query("longitude") double longitude,
            @Query("radius") double radius);

    /** 식당 단건 조회 */
    @GET("api/restaurants/{restaurantId}")
    Call<RestaurantResponse> getRestaurant(@Path("restaurantId") long restaurantId);

    /** 식당 등록 (OWNER) → restaurantId */
    @POST("api/restaurants")
    Call<Long> createRestaurant(@Body RestaurantCreateRequest request);

    /** 식당 수정 (OWNER) */
    @PUT("api/restaurants/{restaurantId}")
    Call<Void> updateRestaurant(@Path("restaurantId") long restaurantId,
                                @Body RestaurantUpdateRequest request);

    /** 식당 삭제 (OWNER) */
    @DELETE("api/restaurants/{restaurantId}")
    Call<Void> deleteRestaurant(@Path("restaurantId") long restaurantId);

    // ========================= 메뉴 (Menu) =========================

    /** 식당 메뉴 목록 */
    @GET("api/restaurants/{restaurantId}/menus")
    Call<List<MenuResponse>> getMenus(@Path("restaurantId") long restaurantId);

    /** 메뉴 등록 (OWNER) → menuId */
    @POST("api/restaurants/{restaurantId}/menus")
    Call<Long> createMenu(@Path("restaurantId") long restaurantId,
                          @Body MenuCreateRequest request);

    /** 메뉴 수정 (OWNER) */
    @PUT("api/restaurants/{restaurantId}/menus/{menuId}")
    Call<Void> updateMenu(@Path("restaurantId") long restaurantId,
                          @Path("menuId") long menuId,
                          @Body MenuUpdateRequest request);

    /** 메뉴 삭제 (OWNER) */
    @DELETE("api/restaurants/{restaurantId}/menus/{menuId}")
    Call<Void> deleteMenu(@Path("restaurantId") long restaurantId,
                          @Path("menuId") long menuId);

    // ========================= 예약 (Reservation) =========================

    /** 예약 생성 (USER) → reservationId */
    @POST("api/restaurants/{restaurantId}/reservations")
    Call<Long> createReservation(@Path("restaurantId") long restaurantId,
                                 @Body ReservationCreateRequest request);

    /** 내 예약 목록 (USER) */
    @GET("api/restaurants/{restaurantId}/reservations/me")
    Call<List<ReservationResponse>> getMyReservations(
            @Path("restaurantId") long restaurantId);

    /** 식당 예약 목록 (OWNER) */
    @GET("api/restaurants/{restaurantId}/reservations")
    Call<List<OwnerReservationResponse>> getReservationsForOwner(
            @Path("restaurantId") long restaurantId);

    /** 예약 수정 (USER) */
    @PUT("api/restaurants/{restaurantId}/reservations/{reservationId}")
    Call<Void> updateReservation(@Path("restaurantId") long restaurantId,
                                 @Path("reservationId") long reservationId,
                                 @Body ReservationUpdateRequest request);

    /** 예약 취소 (USER) */
    @DELETE("api/restaurants/{restaurantId}/reservations/{reservationId}")
    Call<Void> deleteReservation(@Path("restaurantId") long restaurantId,
                                 @Path("reservationId") long reservationId);

    /** 예약 상태 변경 (OWNER) */
    @PATCH("api/restaurants/{restaurantId}/reservations/{reservationId}/status")
    Call<Void> updateReservationStatus(@Path("restaurantId") long restaurantId,
                                       @Path("reservationId") long reservationId,
                                       @Body ReservationStatusUpdateRequest request);

    // ========================= 리뷰 (Review) =========================

    /** 식당 리뷰 목록 (최신순) */
    @GET("api/restaurants/{restaurantId}/reviews")
    Call<List<ReviewResponse>> getRestaurantReviews(
            @Path("restaurantId") long restaurantId);

    /** 리뷰 등록 (USER) → reviewId */
    @POST("api/restaurants/{restaurantId}/reviews")
    Call<Long> createReview(@Path("restaurantId") long restaurantId,
                            @Body ReviewCreateRequest request);

    /** 리뷰 수정 (작성자 본인) */
    @PATCH("api/restaurants/{restaurantId}/reviews/{reviewId}")
    Call<Void> updateReview(@Path("restaurantId") long restaurantId,
                            @Path("reviewId") long reviewId,
                            @Body ReviewUpdateRequest request);

    /** 리뷰 삭제 (작성자 본인) */
    @DELETE("api/restaurants/{restaurantId}/reviews/{reviewId}")
    Call<Void> deleteReview(@Path("restaurantId") long restaurantId,
                            @Path("reviewId") long reviewId);

    // ========================= 식당 식재료 (Restaurant Ingredient) =========================

    /** 식당 식재료 목록 */
    @GET("api/restaurants/{restaurantId}/ingredients")
    Call<List<RestaurantIngredientResponse>> getRestaurantIngredients(
            @Path("restaurantId") long restaurantId);

    /** 식당 식재료 등록 (OWNER) → restaurantIngredientId */
    @POST("api/restaurants/{restaurantId}/ingredients")
    Call<Long> createRestaurantIngredient(
            @Path("restaurantId") long restaurantId,
            @Body RestaurantIngredientCreateRequest request);

    /** 식당 식재료 수정 (OWNER) */
    @PUT("api/restaurants/{restaurantId}/ingredients/{restaurantIngredientId}")
    Call<Void> updateRestaurantIngredient(
            @Path("restaurantId") long restaurantId,
            @Path("restaurantIngredientId") long restaurantIngredientId,
            @Body RestaurantIngredientUpdateRequest request);

    /** 식당 식재료 삭제 (OWNER) */
    @DELETE("api/restaurants/{restaurantId}/ingredients/{restaurantIngredientId}")
    Call<Void> deleteRestaurantIngredient(
            @Path("restaurantId") long restaurantId,
            @Path("restaurantIngredientId") long restaurantIngredientId);

    // ========================= 마스터 재료 (Ingredient) =========================

    /** 마스터 재료 조회/검색 (keyword 선택) */
    @GET("api/ingredients")
    Call<List<IngredientResponse>> searchIngredients(@Query("keyword") String keyword);

    /** 마스터 재료 생성 (ADMIN) → ingredientId */
    @POST("api/ingredients")
    Call<Long> createIngredient(@Body IngredientCreateRequest request);

    // ========================= 게시판 (Post) =========================

    /** 게시글 목록 (최신순 페이징) */
    @GET("api/posts")
    Call<PagePostResponse> getPosts(@Query("page") int page, @Query("size") int size);

    /** 게시글 상세 */
    @GET("api/posts/{postId}")
    Call<PostDetailResponse> getPost(@Path("postId") long postId);

    /** 게시글 작성 → postId */
    @POST("api/posts")
    Call<Long> createPost(@Body PostCreateRequest request);

    /** 게시글 삭제 (작성자 본인) */
    @DELETE("api/posts/{postId}")
    Call<Void> deletePost(@Path("postId") long postId);

    /** 댓글 목록 */
    @GET("api/posts/{postId}/comments")
    Call<List<PostCommentResponse>> getComments(@Path("postId") long postId);

    /** 댓글 작성 → commentId */
    @POST("api/posts/{postId}/comments")
    Call<Long> createComment(@Path("postId") long postId,
                             @Body PostCommentCreateRequest request);

    /** 댓글 삭제 (작성자 본인) */
    @DELETE("api/posts/{postId}/comments/{commentId}")
    Call<Void> deleteComment(@Path("postId") long postId,
                             @Path("commentId") long commentId);
}
