package com.qooke.postingapp;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

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

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FirstFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FirstFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FirstFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FirsrFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FirstFragment newInstance(String param1, String param2) {
        FirstFragment fragment = new FirstFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }


    // 메인 액티비티에 있던 멤버변수

    Button btnAdd;
    ProgressBar progressBar;


    // 리사이클러뷰 관련 멤버변수
    RecyclerView recyclerView;
    PostingAdapter adapter;
    ArrayList<Posting> postingArrayList = new ArrayList<>();


    // 페이징 관련 처리 함수
    int offset;
    int limit = 20;
    int count;


    // 멤버변수화
    String token;


    // 여기만 수정하면 됨(자바랑 화면 연결하는 부분, 화면에서 처리할 코드를 여기서 수정한다.)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_first, container, false);

        btnAdd = rootView.findViewById(R.id.btnSave);
        progressBar = rootView.findViewById(R.id.progressBar);

        recyclerView = rootView.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true); // 고정사이즈:true, 변동사이즈:false
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 리사이클러뷰 페이징 처리하는 함수
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                int lastPosition = ((LinearLayoutManager)recyclerView.getLayoutManager()).findLastCompletelyVisibleItemPosition();
                int totalCount = recyclerView.getAdapter().getItemCount();

                if(lastPosition + 1 == totalCount) {
                    // 네트워크 통해서 데이터를 더 불러온다.
                    if (limit == count) {
                        // DB에 데이터가 더 존재할 수 있으니까, 데이터를 불러온다.(네트워크 낭비 제한)
                        // 네트워크 통하는 함수 만들기(페이징 처리)
                        addNetworkData();
                    }
                }
            }
        });


        // 포스팅 생성 버튼 눌렀을때
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddActivity.class);
                startActivity(intent);
            }
        });

        return rootView;
    }


    // 포스팅 생성하고 메인 액티비티로 오면 새롭게 업로드한 포스팅 보여지게 하는 함수
    @Override
    public void onResume() {
        super.onResume();

        getNetworkData();
    }

    private void getNetworkData() {

        // 변수 초기화
        offset = 0;
        count = 0;

        // 네트워크로 API 호출한다.
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");
        token = "Bearer " + token;

        Call<PostingList> call = api.getMyPosting(token, offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    PostingList postingList = response.body();

                    count = postingList.count;

                    postingArrayList.clear();

                    postingArrayList.addAll(postingList.items);

                    adapter = new PostingAdapter(getActivity(), postingArrayList);
                    recyclerView.setAdapter(adapter);

                } else {

                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);

            }
        });

    }

    // 데이터 페이징하는 함수 만들기
    private void addNetworkData() {

        progressBar.setVisibility(View.VISIBLE);

        // 네트워크로 API 호출한다.
        Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
        PostingApi api = retrofit.create(PostingApi.class);

        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
        String token = sp.getString("token", "");

        // 오프셋을 count만큼 증가시킬 수 있도록 셋팅
        offset = offset + count;

        Call<PostingList> call = api.getMyPosting(token, offset, limit);
        call.enqueue(new Callback<PostingList>() {
            @Override
            public void onResponse(Call<PostingList> call, Response<PostingList> response) {
                progressBar.setVisibility(View.GONE);

                if(response.isSuccessful()) {

                    PostingList postingList = response.body();
                    postingArrayList.addAll(postingList.items);
                    count = postingList.count;

                    adapter.notifyDataSetChanged();

                } else {

                }
            }

            @Override
            public void onFailure(Call<PostingList> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
            }
        });
    }


    // 액션바에 아이콘 나오게 하기
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.main, menu);
    }


    // 액션바 아이콘 눌렀을때 처리할 행동(액티비티 이동하기)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.menuLogout){
            // 저장된 토큰을 삭제하고 로그아웃처리(로그인 액티비티로 이동)
            Retrofit retrofit = NetworkClient.getRetrofitClient(getActivity());
            UserApi api = retrofit.create(UserApi.class);

            Call<Res> call = api.logout(token);
            call.enqueue(new Callback<Res>() {
                @Override
                public void onResponse(Call<Res> call, Response<Res> response) {
                    if(response.isSuccessful()) {
                        // 쉐어드 프리퍼런스의 token을 없애야 한다.
                        SharedPreferences sp = getActivity().getSharedPreferences(Config.PREFERENCE_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("token", "");
                        editor.apply();

                        // 로그인 액티비티를 띄우고
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);

                        // 메인 액티비티를 종료
                        getActivity().finish();

                    } else {

                    }
                }

                @Override
                public void onFailure(Call<Res> call, Throwable t) {

                }
            });

        }
        return super.onOptionsItemSelected(item);
    }

}