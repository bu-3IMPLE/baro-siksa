package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import org.w3c.dom.Text;

import kr.ac.baekseok.java_project.R;

public class MainActivity extends AppCompatActivity {

    Button login_btn_1;
    Button register_btn_1;
    LinearLayout login_register_home;
    LinearLayout login_In;
    LinearLayout register_In;


    TextView back_home_1;
    EditText login_ID;
    ImageView login_ID_View;
    EditText login_Password;
    ImageView login_Password_View;
    TextView findPassword;
    Button login_btn_final;
    TextView register_btn_2;
    boolean login_ID_Text_View = false;
    boolean login_Password_Text_View = false;


    TextView back_home_2;
    EditText register_Name;
    EditText register_Nickname;
    EditText register_Number;
    EditText register_ID;
    ImageView register_ID_View;
    EditText register_Password;
    ImageView register_Password_View;
    Button register_btn_final;
    TextView login_btn_2;
    boolean register_ID_Text_View = false;
    boolean register_Password_Text_View = false;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        login_btn_1=(Button)findViewById(R.id.login_btn_1);
        register_btn_1=(Button)findViewById(R.id.register_btn_1);
        login_register_home=(LinearLayout)findViewById(R.id.login_register_home);
        login_In=(LinearLayout)findViewById(R.id.login_In);
        register_In=(LinearLayout)findViewById(R.id.register_In);


        back_home_1=(TextView)findViewById(R.id.back_home_1);
        login_ID=(EditText)findViewById(R.id.login_ID);
        login_ID_View=(ImageView)findViewById(R.id.login_ID_View);
        login_Password=(EditText)findViewById(R.id.login_Password);
        login_Password_View=(ImageView)findViewById(R.id.login_Password_View);
        findPassword=(TextView)findViewById(R.id.findPassword); //비밀번호 찾기 추가필요
        login_btn_final=(Button)findViewById(R.id.login_btn_final); //최종 로그인 버튼
        register_btn_2=(TextView)findViewById(R.id.register_btn_2);


        back_home_2=(TextView)findViewById(R.id.back_home_2);
        register_Name=(EditText)findViewById(R.id.register_Name);
        register_Nickname=(EditText)findViewById(R.id.register_Nickname);
        register_Number=(EditText)findViewById(R.id.register_Number);
        register_ID=(EditText)findViewById(R.id.register_ID);
        register_ID_View=(ImageView)findViewById(R.id.register_ID_View);
        register_Password=(EditText)findViewById(R.id.register_Password);
        register_Password_View=(ImageView)findViewById(R.id.register_Password_View);
        register_btn_final=(Button)findViewById(R.id.register_btn_final); //최종 회원가입 버튼
        login_btn_2=(TextView)findViewById(R.id.login_btn_2);



        login_btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_register_home.setVisibility(View.GONE);
                login_In.setVisibility(View.VISIBLE);
                login_ID.setText("");
                login_Password.setText("");
            }
        });
        register_btn_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_register_home.setVisibility(View.GONE);
                register_In.setVisibility(View.VISIBLE);
                register_ID.setText("");
                register_Password.setText("");
                register_Name.setText("");
                register_Nickname.setText("");
                register_Number.setText("");
            }
        });


        back_home_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_In.setVisibility(View.GONE);
                login_register_home.setVisibility(View.VISIBLE);
            }
        });
        login_ID_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!login_ID_Text_View)
                {
                    login_ID.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    login_ID_Text_View = true;
                }
                else
                {
                    login_ID.setTransformationMethod(null);
                    login_ID_Text_View = false;
                }
                login_ID.setSelection(login_ID.getText().length());
            }
        });
        login_Password_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!login_Password_Text_View)
                {
                    login_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    login_Password_Text_View = true;
                }
                else
                {
                    login_Password.setTransformationMethod(null);
                    login_Password_Text_View = false;
                }
                login_Password.setSelection(login_Password.getText().length());
            }
        });
        findPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //비밀번호 찾기
            }
        });
        login_btn_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        register_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login_In.setVisibility(View.GONE);
                register_In.setVisibility(View.VISIBLE);
                register_ID.setText("");
                register_Password.setText("");
                register_Name.setText("");
                register_Nickname.setText("");
                register_Number.setText("");
            }
        });


        back_home_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_In.setVisibility(View.GONE);
                login_register_home.setVisibility(View.VISIBLE);
            }
        });
        register_ID_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!register_ID_Text_View)
                {
                    register_ID.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    register_ID_Text_View = true;
                }
                else
                {
                    register_ID.setTransformationMethod(null);
                    register_ID_Text_View = false;
                }
                register_ID.setSelection(register_ID.getText().length());
            }
        });
        register_Password_View.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!register_Password_Text_View)
                {
                    register_Password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    register_Password_Text_View = true;
                }
                else
                {
                    register_Password.setTransformationMethod(null);
                    register_Password_Text_View = false;
                }
                register_Password.setSelection(register_Password.getText().length());
            }
        });
        login_btn_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                register_In.setVisibility(View.GONE);
                login_In.setVisibility(View.VISIBLE);
                login_ID.setText("");
                login_Password.setText("");
            }
        });
    }
}