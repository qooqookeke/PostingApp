package com.qooke.postingapp;

import androidx.annotation.RestrictTo;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.qooke.postingapp.api.NetworkClient;
import com.qooke.postingapp.api.UserApi;
import com.qooke.postingapp.config.Config;
import com.qooke.postingapp.model.User;
import com.qooke.postingapp.model.UserRes;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class LoginActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    Button btnLogin;
    TextView txtRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        txtRegister = findViewById(R.id.txtRegister);


        // 회원가입 텍스트뷰 눌렀을때(회원가입 액티비티로 이동)
        txtRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        // 로그인 버튼 눌렀을때()
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "필수 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 이메일 형식이 맞는지 체크(이메일 형식이 맞는지 확인하는 라이브러리 사용, 템플릿)
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if(pattern.matcher(email).matches() == false) {
                    Toast.makeText(LoginActivity.this, "이메일을 정확히 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 길이 체크 4~12자 까지만 허용
                if(password.length() < 4 || password.length() > 12) {
                    Toast.makeText(LoginActivity.this, "비밀번호 길이를 확인하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 네트워크 통신 하여 로그인 API 호출
                showProgress();

                Retrofit retrofit = NetworkClient.getRetrofitClient(LoginActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                User user = new User(email, password);

                Call<UserRes> call = api.login(user);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();

                        if (response.isSuccessful()) {
                            UserRes userRes = response.body();

                            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", userRes.access_token);
                            editor.apply();

                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else if (response.code() == 400) {
                            Toast.makeText(LoginActivity.this, "회원가입이 되지 않은 이메일이거나 비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                            return;

                        } else if (response.code() == 500) {
                            Toast.makeText(LoginActivity.this, "데이터 베이스에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                            return;

                        } else {
                            Toast.makeText(LoginActivity.this, "잠시후 다시 이용하세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();
                        Toast.makeText(LoginActivity.this, "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });



            }
        });
    }

    // 네트워크로 데이터를 처리할때 사용할 다이얼로그
    Dialog dialog;
    private void showProgress() {
        dialog = new Dialog(this);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(new ProgressBar(this));
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    private void dismissProgress() {
        dialog.dismiss();
    }

}