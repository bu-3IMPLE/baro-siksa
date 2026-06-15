package kr.ac.baekseok.java_project.network;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit 싱글톤 (인증 토큰 자동 첨부 포함).
 *
 * 사용법:
 *   // Application 또는 첫 Activity에서 한 번 초기화
 *   RetrofitClient.init(getApplicationContext());
 *
 *   // 이후 어디서든
 *   ApiService api = RetrofitClient.getApi();
 *
 * 서버 주소 규칙:
 *  - 끝에 슬래시(/) 필수
 *  - 에뮬레이터 → 내 PC: 10.0.2.2 사용 (127.0.0.1 아님)
 *  - 실제 기기 → PC 내부 IP (예: 192.168.0.10)
 */
public class RetrofitClient {

    // ===== 서버 주소 (환경에 맞게 수정) =====
    public static final String BASE_URL = "http://10.0.2.2:8080/";
    // 실제 기기: public static final String BASE_URL = "http://192.168.0.10:8080/";

    private static Retrofit retrofit = null;
    private static ApiService apiService = null;

    private RetrofitClient() {
    }

    /**
     * 앱 시작 시 한 번 호출. AuthInterceptor가 Context를 필요로 한다.
     */
    public static void init(Context context) {
        if (retrofit != null) return;

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(context))  // 토큰 자동 첨부
                .authenticator(new TokenAuthenticator(context)) // 401 시 자동 갱신
                .addInterceptor(logging)
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(15, TimeUnit.SECONDS)
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();

        // 서버의 날짜 형식(ISO-8601: 2025-12-01T18:30:00)에 맞춤
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create();

        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public static Retrofit getInstance() {
        if (retrofit == null) {
            throw new IllegalStateException(
                    "RetrofitClient.init(context)를 먼저 호출하세요 " +
                    "(보통 Application 클래스 또는 첫 Activity onCreate에서).");
        }
        return retrofit;
    }

    /**
     * API 인터페이스 반환.
     * 예: ApiService api = RetrofitClient.getApi();
     */
    public static ApiService getApi() {
        if (apiService == null) {
            throw new IllegalStateException(
                    "RetrofitClient.init(context)를 먼저 호출하세요.");
        }
        return apiService;
    }
}
