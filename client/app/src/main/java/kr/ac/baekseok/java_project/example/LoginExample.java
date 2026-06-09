package kr.ac.baekseok.java_project.example;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import kr.ac.baekseok.java_project.dto.request.MemberLoginRequest;
import kr.ac.baekseok.java_project.dto.response.MemberLoginResponse;
import kr.ac.baekseok.java_project.network.ApiService;
import kr.ac.baekseok.java_project.network.AuthInterceptor;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * 로그인 API 호출 예시.
 *
 * 실제 Activity(SignInActivity 등)에서 이 패턴을 그대로 쓰면 된다.
 * 핵심 흐름:
 *   1) 입력값으로 요청 DTO 생성
 *   2) ApiService 메서드 호출 → enqueue로 비동기 실행
 *   3) onResponse에서 성공/실패 분기
 *   4) 성공 시 토큰을 저장 (AuthInterceptor.saveToken)
 */
public class LoginExample {

    public interface LoginCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public static void login(Context context, String email, String password,
                             LoginCallback callback) {
        ApiService api = RetrofitClient.getApi();
        MemberLoginRequest request = new MemberLoginRequest(email, password);

        api.login(request).enqueue(new Callback<MemberLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<MemberLoginResponse> call,
                                   @NonNull Response<MemberLoginResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    MemberLoginResponse body = response.body();

                    // 받은 토큰을 저장 → 이후 모든 요청에 자동 첨부됨
                    AuthInterceptor.saveToken(context, body.accessToken);
                    // refreshToken도 따로 저장하고 싶으면 SharedPreferences에 보관

                    callback.onSuccess();
                } else if (response.code() == 401) {
                    callback.onFailure("이메일 또는 비밀번호가 올바르지 않습니다");
                } else {
                    callback.onFailure("로그인 실패 (코드 " + response.code() + ")");
                }
            }

            @Override
            public void onFailure(@NonNull Call<MemberLoginResponse> call,
                                  @NonNull Throwable t) {
                // 네트워크 자체가 실패 (서버 꺼짐, 주소 틀림, 인터넷 없음 등)
                callback.onFailure("서버에 연결할 수 없습니다: " + t.getMessage());
            }
        });
    }

    /**
     * Activity에서 호출하는 예:
     *
     * LoginExample.login(this, email, password, new LoginExample.LoginCallback() {
     *     @Override public void onSuccess() {
     *         startActivity(new Intent(MyActivity.this, HomeActivity.class));
     *         finish();
     *     }
     *     @Override public void onFailure(String message) {
     *         Toast.makeText(MyActivity.this, message, Toast.LENGTH_SHORT).show();
     *     }
     * });
     */
}
