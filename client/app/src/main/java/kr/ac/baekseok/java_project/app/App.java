package kr.ac.baekseok.java_project.app;

import android.app.Application;

import com.kakao.vectormap.KakaoMapSdk;

import kr.ac.baekseok.java_project.network.RetrofitClient;

public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        RetrofitClient.init(this);
        KakaoMapSdk.init(this, "19d97a2149e07716da2645b426868eb9");
    }
}
