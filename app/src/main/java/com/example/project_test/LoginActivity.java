package com.example.project_test;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import com.example.project_test.Mypage.KeywordList;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    public static String user_ac;

    Button loginBtn, jnBtn;
    EditText idEt, pwEt;
    static public String strID;
    String strPW;
    AlertDialog.Builder loginfail;
    ArrayList<String> kw = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginBtn = findViewById(R.id.loginBtn);
        jnBtn = findViewById(R.id.jnBtn);
        idEt = findViewById(R.id.idEt);
        pwEt = findViewById(R.id.pwEt);

    }

    public void btnClick(View v) {
        switch (v.getId()) {
            case R.id.loginBtn:

                Api api = Api.Factory.INSTANCE.create();

                //입력한 아이디와 패스워드 값 가져오기
                strID = idEt.getText().toString();
                strPW = pwEt.getText().toString();

                //입력한 아이디가 db에 존재하는 확인
                api.getID(strID).enqueue(new Callback<UserIdCheck>() {
                    @Override
                    public void onResponse(Call<UserIdCheck> call, Response<UserIdCheck> response) {
                        UserIdCheck id = response.body();
                        boolean ckid = id.ckid;

                        //입력한 아이디 존재
                        if(ckid) {

                            //입력한 패스워드와 서버에서 받아온 패스워드가 일치하는지 확인
                            final Api api = Api.Factory.INSTANCE.create();
                            api.getUser(strID).enqueue(new Callback<User>() {
                                public void onResponse(Call<User> call, Response<User> response) {
                                    User user = response.body();
                                    String pw = user.password;
                                    String name = user.name;

                                    //패스워드 일치
                                    if(pw.equals(strPW)) {
                                        user_ac = strID;

                                        //SharedPreferences에 로그인한 아이디 저장
                                        SharedPreferences preferences = getSharedPreferences("lastID", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = preferences.edit();
                                        editor.putString("IDkey", user_ac);
                                        editor.putString("NAMEkey", name);
                                        editor.commit();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        idEt.setText("");
                                        pwEt.setText("");

                                        //키워드 알림
                                        api.getkeywordpost(LoginActivity.user_ac).enqueue(new Callback<KeywordList>() {
                                            @Override
                                            public void onResponse(Call<KeywordList> call, Response<KeywordList> response) {
                                                KeywordList kl = response.body();
                                                Log.i("키워드알림", kl.checkpost+"");

                                                if (kl.checkpost) {
                                                    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                                                    NotificationCompat.Builder builder = null;

                                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                                        String channelID = "channel_01";
                                                        String channelName = "MyChannel01";

                                                        NotificationChannel channel = new NotificationChannel(channelID, channelName, NotificationManager.IMPORTANCE_DEFAULT);

                                                        notificationManager.createNotificationChannel(channel);
                                                        builder = new NotificationCompat.Builder(LoginActivity.this, channelID);
                                                    } else {
                                                        builder = new NotificationCompat.Builder(LoginActivity.this, null);
                                                    }
                                                    builder.setSmallIcon(android.R.drawable.ic_menu_view);

                                                    builder.setContentText("설정한 키워드가 포함된 게시물이 올라왔습니다");
                                                    builder.setContentTitle("키워드 알림");
                                                    Bitmap bm = BitmapFactory.decodeResource(getResources(), R.mipmap.mainicon);
                                                    builder.setLargeIcon(bm);

                                                    Notification notification = builder.build();

                                                    notificationManager.notify(1, notification);
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<KeywordList> call, Throwable t) {
                                                Log.i("실패", t.getMessage());
                                            }
                                        });
                                    }
                                    //패스워드 불일치
                                    else {
                                        loginfail = new AlertDialog.Builder(LoginActivity.this);
                                        loginfail.setTitle("비밀번호 불일치");
                                        loginfail.setMessage("잘못된 비밀번호입니다");
                                        loginfail.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                            }
                                        });
                                        loginfail.show();
                                    }

                                }

                                public void onFailure(Call<User> call, Throwable t) {
                                    Log.i("abcdefg", t.getMessage());
                                    loginfail = new AlertDialog.Builder(LoginActivity.this);
                                    loginfail.setTitle("서버 연결 실패");
                                    loginfail.setMessage("서버에 연결되지 않았습니다");
                                    loginfail.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                        }
                                    });
                                    loginfail.show();
                                }
                            });
                        }
                        //아이디 없음
                        else {
                            loginfail = new AlertDialog.Builder(LoginActivity.this);
                            loginfail.setTitle("아이디 없음");
                            loginfail.setMessage("존재하지 않는 아이디입니다");
                            loginfail.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                            loginfail.show();
                        }
                    }
                    @Override
                    public void onFailure(Call<UserIdCheck> call, Throwable t) {
                        Log.i("abcdefg", t.getMessage());
                        loginfail = new AlertDialog.Builder(LoginActivity.this);
                        loginfail.setTitle("서버 연결 실패");
                        loginfail.setMessage("서버에 연결되지 않았습니다");
                        loginfail.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        loginfail.show();
                    }
                });

                break;
            case R.id.jnBtn:             //회원가입 버튼 누르면 회원가입 화면으로
                Intent intent = new Intent(LoginActivity.this, JoinActivity.class);
                startActivity(intent);
                break;
        }
    }
}