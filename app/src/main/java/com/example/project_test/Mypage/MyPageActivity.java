package com.example.project_test.Mypage;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.*;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.example.project_test.Api;
import com.example.project_test.LoginActivity;
import com.example.project_test.MainActivity;
import com.example.project_test.Mypage.MyContents.MyContentsActivity;
import com.example.project_test.R;
import com.example.project_test.User;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyPageActivity extends AppCompatActivity {
    Button userSetBtn, logout, addkwd;
    ImageButton gomycon, btn1,btn2, btn3, btn4, btn5;
    ConstraintLayout mplayout;
    TextView textName, kwdaad;
    Toolbar toolbar;
    RecyclerView kwdlist;

    private RecyclerView recyclerView;
    public GridLayoutManager layoutManager;
    private KwRecyclerAdapter adapter;

    ArrayList<String> kw = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mypage);


        //?????????
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ???????????? ???????????? ???????????? ??????(?????? ???????????? ???????????? ?????????)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbtn);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        textName = findViewById(R.id.name);
        mplayout = (ConstraintLayout) findViewById(R.id.mplayout);
        recyclerView = findViewById(R.id.kwdlist);
        recyclerView.setHasFixedSize(true);
        adapter = new KwRecyclerAdapter();
        kwdaad = findViewById(R.id.kwdadd);
        kwdlist = findViewById(R.id.kwdlist);



        layoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(layoutManager);

        //???????????? ????????? ?????? ?????? ??????
        Api api = Api.Factory.INSTANCE.create();
        api.getUser(LoginActivity.user_ac).enqueue(new Callback<User>() {
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                textName.setText(user.name+" ???");
            }

            public void onFailure(Call<User> call, Throwable t) {

            }
        });

        //????????? ???????????? ??????
        api.getkeyword(LoginActivity.user_ac).enqueue(new Callback<KeywordList>() {
            @Override
            public void onResponse(Call<KeywordList> call, Response<KeywordList> response) {
                KeywordList kl = response.body();
                List<KeywordData> kData = kl.items;

                for (KeywordData d:kData) {
                    kw.add(d.keyword);
                }
                adapter.setData(kw);
                recyclerView.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<KeywordList> call, Throwable t) {

            }
        });


    }



    public void mypageClick(View v) {
        switch (v.getId()) {
            case R.id.userSetBtn:          //????????? ?????? ??????
                Intent intent = new Intent(MyPageActivity.this, MyInfoSetActivity.class);
                startActivity(intent);
                break;
            case R.id.logout:          //????????????
                AlertDialog.Builder logout = new AlertDialog.Builder(MyPageActivity.this);
                logout.setTitle("????????????");
                logout.setMessage("???????????? ???????????????????");
                logout.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(MyPageActivity.this , LoginActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        startActivity(i);
                    }
                });
                logout.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                logout.show();

                break;
            case R.id.addkwd:           //????????? ??????
                AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                View view = LayoutInflater.from(MyPageActivity.this).inflate(R.layout.dialog_keyword, null, false);
                builder.setView(view);

                final Button cancel = view.findViewById(R.id.cancel);
                final Button submit = view.findViewById(R.id.submit);
                final EditText pw1 = view.findViewById(R.id.pw1);

                final AlertDialog dialog = builder.create();
                //?????? ?????? ??????
                cancel.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });

                //?????? ?????? ??????
                submit.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        //????????? ?????? ?????????
                        String abc = pw1.getText().toString();
                        abc = abc.replace(" ", "");
                        abc = abc.replace("\n", "");

                        if(abc.equals(""))
                        {
                            AlertDialog dialog2;
                            AlertDialog.Builder builder = new AlertDialog.Builder(MyPageActivity.this);
                            dialog2 = builder.setMessage("???????????? ???????????????").setNegativeButton("??????", null).create();
                            dialog2.show();
                        }

                        else {
                            //???????????? ?????? ????????????????????? ??????
                            kw.add(abc);
                            adapter.notifyDataSetChanged();
                            //adapter.notifyItemInserted(0);

                            //db??? ??????
                            Api api = Api.Factory.INSTANCE.create();
                            api.addkeyword(abc, LoginActivity.user_ac).enqueue(new Callback<KeywordList>() {
                                @Override
                                public void onResponse(Call<KeywordList> call, Response<KeywordList> response) {
                                    KeywordList kl = response.body();
                                    Boolean update = kl.update;

                                    if (update) {
                                        Log.i("??????", update + "");
                                    }
                                }

                                @Override
                                public void onFailure(Call<KeywordList> call, Throwable t) {

                                }
                            });
                            Log.i("??????", kw.get(0));
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();

                break;
            case R.id.go:             //?????? ?????? ?????? ??????
                intent = new Intent(getApplicationContext(), MyContentsActivity.class);
                startActivity(intent);
                break;

            case R.id.btn2:
                mplayout.setBackgroundColor(getResources().getColor(R.color.red));
                break;

        }
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                return true;
        }
        return true;
    }
}
