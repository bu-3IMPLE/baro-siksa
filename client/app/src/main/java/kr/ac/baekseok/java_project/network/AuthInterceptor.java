package kr.ac.baekseok.java_project.network;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * 로그인 후 받은 토큰(JWT 등)을 모든 요청 헤더에 자동으로 붙여주는 인터셉터.
 *
 * 토큰은 SharedPreferences에 저장한다고 가정.
 * 조원의 서버가 토큰 인증(Authorization: Bearer ...)을 쓰는 경우 사용한다.
 *
 * 만약 서버가 토큰 대신 세션 쿠키를 쓰거나 인증이 없으면 이 인터셉터는 빼도 된다.
 */
public class AuthInterceptor implements Interceptor {

    private static final String PREFS_NAME = "auth_prefs";
    private static final String KEY_TOKEN = "access_token";

    private final Context context;

    public AuthInterceptor(Context context) {
        // application context로 보관 (메모리 누수 방지)
        this.context = context.getApplicationContext();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request original = chain.request();

        String token = getToken();
        if (token == null || token.isEmpty()) {
            // 토큰이 없으면 원래 요청 그대로 진행 (로그인/회원가입 등)
            return chain.proceed(original);
        }

        // Authorization 헤더 추가
        Request withAuth = original.newBuilder()
                .header("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(withAuth);
    }

    private String getToken() {
        SharedPreferences prefs =
                context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return prefs.getString(KEY_TOKEN, null);
    }

    // ===== 토큰 저장/삭제 헬퍼 (다른 곳에서 호출) =====

    public static void saveToken(Context context, String token) {
        context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_TOKEN, token)
                .apply();
    }

    public static void clearToken(Context context) {
        context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit()
                .remove(KEY_TOKEN)
                .apply();
    }

    public static String getToken(Context context) {
        return context.getApplicationContext()
                .getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getString(KEY_TOKEN, null);
    }
}
