package kr.ac.baekseok.java_project.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.regex.Pattern;

import kr.ac.baekseok.java_project.R;
import kr.ac.baekseok.java_project.dto.request.MemberLoginRequest;
import kr.ac.baekseok.java_project.dto.request.MemberSignUpRequest;
import kr.ac.baekseok.java_project.dto.response.MemberLoginResponse;
import kr.ac.baekseok.java_project.network.ApiService;
import kr.ac.baekseok.java_project.network.AuthInterceptor;
import kr.ac.baekseok.java_project.network.RetrofitClient;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    // 서버 비밀번호 규칙: 영문 + 숫자 + 특수문자(@$!%*#?&) 포함 8~20자
    private static final Pattern PASSWORD_PATTERN = Pattern.compile(
            "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,20}$");

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

    // 중복 클릭/요청 방지
    private boolean isRequesting = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 저장된 토큰이 있으면 로그인 화면 건너뜀.
        // accessToken이 만료됐더라도 TokenAuthenticator가 첫 API 호출 시 자동 갱신하고,
        // refreshToken까지 만료된 경우엔 Authenticator가 이 화면으로 다시 돌려보낸다.
        if (AuthInterceptor.getToken(this) != null) {
            goToHome();
            return;
        }

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

        // ====== 로그인 버튼: 서버 호출 ======
        login_btn_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
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

        // ====== 회원가입 버튼: 서버 호출 ======
        register_btn_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignUp();
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

    // =========================================================
    //                      로그인 처리
    // =========================================================

    private void doLogin() {
        if (isRequesting) return;

        // login_ID 칸을 이메일로 사용 (서버는 email로 로그인)
        String email = login_ID.getText().toString().trim();
        String password = login_Password.getText().toString();

        // 입력 검증
        if (email.isEmpty()) {
            Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
            login_ID.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "올바른 이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
            login_ID.requestFocus();
            return;
        }
        if (password.isEmpty()) {
            Toast.makeText(this, "비밀번호를 입력하세요", Toast.LENGTH_SHORT).show();
            login_Password.requestFocus();
            return;
        }

        setRequesting(true);

        ApiService api = RetrofitClient.getApi();
        MemberLoginRequest request = new MemberLoginRequest(email, password);

        api.login(request).enqueue(new Callback<MemberLoginResponse>() {
            @Override
            public void onResponse(@NonNull Call<MemberLoginResponse> call,
                                   @NonNull Response<MemberLoginResponse> response) {
                setRequesting(false);

                if (response.isSuccessful() && response.body() != null) {
                    MemberLoginResponse body = response.body();

                    // 토큰 저장 → 이후 모든 요청에 자동 첨부
                    AuthInterceptor.saveToken(MainActivity.this, body.accessToken);
                    saveRefreshToken(body.refreshToken);

                    Toast.makeText(MainActivity.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                    goToHome();
                } else if (response.code() == 401 || response.code() == 400) {
                    Toast.makeText(MainActivity.this,
                            "이메일 또는 비밀번호가 올바르지 않습니다",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "로그인 실패 (오류 코드 " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MemberLoginResponse> call,
                                  @NonNull Throwable t) {
                setRequesting(false);
                Toast.makeText(MainActivity.this,
                        "서버에 연결할 수 없습니다.\n서버 실행 여부와 주소를 확인하세요.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // =========================================================
    //                      회원가입 처리
    // =========================================================

    private void doSignUp() {
        if (isRequesting) return;

        String nickname = register_Nickname.getText().toString().trim();
        String name = register_Name.getText().toString().trim();
        String phone = register_Number.getText().toString().trim();
        String email = register_ID.getText().toString().trim();   // ID 칸 = 이메일
        String password = register_Password.getText().toString();

        // 입력 검증
        if (nickname.isEmpty()) {
            Toast.makeText(this, "닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
            register_Nickname.requestFocus();
            return;
        }
        if (email.isEmpty()) {
            Toast.makeText(this, "이메일을 입력하세요", Toast.LENGTH_SHORT).show();
            register_ID.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, "올바른 이메일 형식이 아닙니다", Toast.LENGTH_SHORT).show();
            register_ID.requestFocus();
            return;
        }
        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            Toast.makeText(this,
                    "비밀번호는 영문, 숫자, 특수문자(@$!%*#?&)를 포함해 8~20자여야 합니다",
                    Toast.LENGTH_LONG).show();
            register_Password.requestFocus();
            return;
        }

        setRequesting(true);

        ApiService api = RetrofitClient.getApi();
        // 서버 username에는 닉네임을 보낸다. role은 USER 고정.
        // (이름/전화번호 칸은 현재 서버 스키마에 대응 필드가 없어 전송하지 않음)
        MemberSignUpRequest request =
                new MemberSignUpRequest(nickname, email, password, "USER");

        api.signUp(request).enqueue(new Callback<Long>() {
            @Override
            public void onResponse(@NonNull Call<Long> call,
                                   @NonNull Response<Long> response) {
                setRequesting(false);

                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this,
                            "회원가입이 완료되었습니다. 로그인해 주세요.",
                            Toast.LENGTH_SHORT).show();

                    // 가입 성공 → 로그인 화면으로 전환하고 이메일 자동 입력
                    register_In.setVisibility(View.GONE);
                    login_In.setVisibility(View.VISIBLE);
                    login_ID.setText(email);
                    login_Password.setText("");
                } else if (response.code() == 409) {
                    Toast.makeText(MainActivity.this,
                            "이미 가입된 이메일입니다",
                            Toast.LENGTH_SHORT).show();
                } else if (response.code() == 400) {
                    Toast.makeText(MainActivity.this,
                            "입력 정보를 다시 확인해 주세요",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this,
                            "회원가입 실패 (오류 코드 " + response.code() + ")",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Long> call, @NonNull Throwable t) {
                setRequesting(false);
                Toast.makeText(MainActivity.this,
                        "서버에 연결할 수 없습니다.\n서버 실행 여부와 주소를 확인하세요.",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    // =========================================================
    //                      헬퍼
    // =========================================================

    private void goToHome() {
        Intent intent = new Intent(MainActivity.this, HomeActivity.class);
        // 로그인 후 뒤로가기로 로그인 화면에 다시 못 오게 스택 정리
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * 요청 중에는 버튼을 비활성화해서 중복 요청을 막는다.
     */
    private void setRequesting(boolean requesting) {
        isRequesting = requesting;
        if (login_btn_final != null) login_btn_final.setEnabled(!requesting);
        if (register_btn_final != null) register_btn_final.setEnabled(!requesting);
    }

    /**
     * refreshToken을 SharedPreferences에 저장.
     * 토큰 만료 시 reissue API로 새 accessToken을 받을 때 사용.
     */
    private void saveRefreshToken(String refreshToken) {
        if (refreshToken == null) return;
        getSharedPreferences("auth_prefs", MODE_PRIVATE)
                .edit()
                .putString("refresh_token", refreshToken)
                .apply();
    }
}
