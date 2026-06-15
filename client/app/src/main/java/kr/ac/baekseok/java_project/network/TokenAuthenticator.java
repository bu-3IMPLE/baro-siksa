package kr.ac.baekseok.java_project.network;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import java.io.IOException;

import kr.ac.baekseok.java_project.activity.MainActivity;
import kr.ac.baekseok.java_project.dto.request.TokenReissueRequest;
import kr.ac.baekseok.java_project.dto.response.MemberLoginResponse;
import okhttp3.Authenticator;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.MediaType;

/**
 * accessToken 만료(401) 시 refreshToken으로 자동 갱신 후 원래 요청을 재시도한다.
 *
 * 갱신 실패(refreshToken도 만료됐거나 서버 오류)이면 토큰을 모두 지우고
 * 로그인 화면으로 이동한다.
 */
public class TokenAuthenticator implements Authenticator {

    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_REFRESH_TOKEN = "refresh_token";

    private final Context context;

    public TokenAuthenticator(Context context) {
        this.context = context.getApplicationContext();
    }

    @Nullable
    @Override
    public Request authenticate(@Nullable Route route, @NonNull Response response) throws IOException {
        // 연속 401 방지: 재시도 요청도 401이면 포기
        if (responseCount(response) >= 2) {
            clearTokensAndGoToLogin();
            return null;
        }

        String refreshToken = getRefreshToken();
        if (refreshToken == null || refreshToken.isEmpty()) {
            clearTokensAndGoToLogin();
            return null;
        }

        // reissue API를 동기 호출 (Authenticator는 백그라운드 스레드에서 실행됨)
        MemberLoginResponse newTokens = reissueSync(refreshToken);
        if (newTokens == null || newTokens.accessToken == null) {
            clearTokensAndGoToLogin();
            return null;
        }

        // 새 토큰 저장
        AuthInterceptor.saveToken(context, newTokens.accessToken);
        if (newTokens.refreshToken != null) {
            saveRefreshToken(newTokens.refreshToken);
        }

        // 원래 요청에 새 accessToken 붙여서 재시도
        return response.request().newBuilder()
                .header("Authorization", "Bearer " + newTokens.accessToken)
                .build();
    }

    private MemberLoginResponse reissueSync(String refreshToken) {
        try {
            Gson gson = new Gson();
            TokenReissueRequest req = new TokenReissueRequest(refreshToken);
            String json = gson.toJson(req);

            okhttp3.RequestBody body = RequestBody.create(
                    json, MediaType.parse("application/json; charset=utf-8"));

            // 인터셉터 없는 별도 클라이언트로 호출해야 무한 루프를 방지할 수 있다
            OkHttpClient plain = new OkHttpClient();
            okhttp3.Request request = new okhttp3.Request.Builder()
                    .url(RetrofitClient.BASE_URL + "api/members/reissue")
                    .post(body)
                    .build();

            try (okhttp3.Response resp = plain.newCall(request).execute()) {
                if (!resp.isSuccessful() || resp.body() == null) return null;
                return gson.fromJson(resp.body().string(), MemberLoginResponse.class);
            }
        } catch (IOException e) {
            return null;
        }
    }

    private String getRefreshToken() {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_REFRESH_TOKEN, null);
    }

    private void saveRefreshToken(String token) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_REFRESH_TOKEN, token)
                .apply();
    }

    private void clearTokensAndGoToLogin() {
        AuthInterceptor.clearToken(context);
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_REFRESH_TOKEN)
                .apply();

        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    private int responseCount(Response response) {
        int count = 1;
        while ((response = response.priorResponse()) != null) {
            count++;
        }
        return count;
    }
}
