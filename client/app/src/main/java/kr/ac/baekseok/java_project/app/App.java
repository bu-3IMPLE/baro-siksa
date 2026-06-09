package kr.ac.baekseok.java_project.app;

import android.app.Application;

import kr.ac.baekseok.java_project.network.RetrofitClient;

/**
 * 앱 전역 초기화.
 *
 * AndroidManifest.xml의 <application> 태그에 등록해야 한다:
 *   android:name=".app.App"
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        // Retrofit + 인증 인터셉터 초기화 (앱 전체에서 1회)
        RetrofitClient.init(this);
    }
}
