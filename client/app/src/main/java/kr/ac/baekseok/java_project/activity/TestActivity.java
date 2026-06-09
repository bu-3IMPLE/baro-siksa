package kr.ac.baekseok.java_project.activity;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import kr.ac.baekseok.java_project.R;


public class TestActivity extends AppCompatActivity {

    TextView test_id;
    TextView test_password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        test_id=(TextView)findViewById(R.id.test_id);
        test_password=(TextView)findViewById(R.id.test_password);


    }
}
