package kr.ac.baekseok.java_project;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainHome extends AppCompatActivity {

    TextView tvMore;
    NavigationBarView bottomNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_home);

        tvMore=(TextView)findViewById(R.id.tvMore);
        bottomNav=(NavigationBarView)findViewById(R.id.bottomNav);


        //오늘 동네 맛집 더보기
        tvMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        //bottomNav.setOnItemSelectedListener();


    }
}
