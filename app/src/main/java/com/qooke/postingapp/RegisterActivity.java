package com.qooke.postingapp;

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

public class RegisterActivity extends AppCompatActivity {

    EditText editEmail;
    EditText editPassword;
    Button btnRegister;
    TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = findViewById(R.id.editEmail);
        editPassword =findViewById(R.id.editPassword);
        btnRegister = findViewById(R.id.btnRegister);
        txtLogin = findViewById(R.id.txtLogin);


        // 로그인 텍스트뷰 눌렀을때(로그인 액티비티로 이동)
        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        });


        // 회원 가입 버튼 눌렀을때
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = editEmail.getText().toString().trim();
                String password = editPassword.getText().toString().trim();

                if (email.isEmpty() || password.isEmpty()) {
                    Toast.makeText(RegisterActivity.this, "필수 항목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 이메일 형식이 맞는지 확인
                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (pattern.matcher(email).matches() == false) {
                    Toast.makeText(RegisterActivity.this, "이메일을 정확히 입력하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 형식이 맞는지 확인
                if (password.length() < 4 || password.length() > 12) {
                    Toast.makeText(RegisterActivity.this, "비밀번호 길이를 확인하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 네트워크로 회원가입 API 불러오기
                showProgress();

                Retrofit retrofit = NetworkClient.getRetrofitClient(RegisterActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                User user = new User(email, password);

                Call<UserRes> call = api.register(user);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        dismissProgress();

                        if(response.isSuccessful()) {
                            UserRes userRes = response.body();

                            SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString("token", userRes.access_token);
                            editor.apply();

                            // 데이터가 이상 없으므로 메인 액티비티를 실행한다.
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else if (response.code() == 400) {
                            // 정상동작 안할때 코드 작성(에러코드로 구분, api명세서에서 확인)
                            Toast.makeText(RegisterActivity.this, "이메일과 비밀번호 형식이 올바르지 않습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else if (response.code() == 500) {
                            Toast.makeText(RegisterActivity.this, "데이터 베이스에 문제가 있습니다.", Toast.LENGTH_SHORT).show();
                            return;
                        } else {
                            Toast.makeText(RegisterActivity.this, "잠시후 다시 이용하세요.", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        dismissProgress();

                        Toast.makeText(RegisterActivity.this, "네트워크 연결 오류", Toast.LENGTH_SHORT).show();
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