package com.qooke.postingapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.qooke.postingapp.adapter.PostingAdapter;
import com.qooke.postingapp.api.NetworkClient;
import com.qooke.postingapp.api.PostingApi;
import com.qooke.postingapp.api.UserApi;
import com.qooke.postingapp.config.Config;
import com.qooke.postingapp.model.Posting;
import com.qooke.postingapp.model.PostingList;
import com.qooke.postingapp.model.Res;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;

    Fragment firstFragment;
    Fragment secondFragment;
    Fragment thirdFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 토큰 확인하고 없으면 회원가입 액티비티 띄우기
        SharedPreferences sp = getSharedPreferences(Config.PREFERENCE_NAME, MODE_PRIVATE);
        String token = sp.getString("token", "");

        if (token.isEmpty()) {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
            return;
        }

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        firstFragment = new FirstFragment();
        secondFragment = new SecondFragment();
        thirdFragment = new ThirdFragment();


        // 바텀 네비게이션 뷰 클릭 했을때
        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();
                Fragment fragment = null;

                if (itemId == R.id.firstFragment) {
                    // 내 포스팅 리스트 보여주는 화면
                    fragment = firstFragment;

                } else if (itemId == R.id.secondFragment) {
                    fragment = secondFragment;

                } else if (itemId == R.id.thirdFragment) {
                    fragment = thirdFragment;

                }
                return loadFragment(fragment);
            }
        });

    }

    boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment, fragment).commit();
            return true;
        } else {
            return false;
        }
    }


}