package com.example.project_test;
import android.Manifest;
import android.app.ActivityGroup;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.example.project_test.Emergency.EmergencyActivity;
import com.example.project_test.Event.EventActivity;
import com.example.project_test.Room.RoomActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends ActivityGroup {

    int page;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //탭 만들기
        createTab();

        //안전관리에서 긴급전화 버튼 눌렀을 때 전화 하기 위해 권한 요청+위치
        if(Build.VERSION.SDK_INT >= 23) {
            if(checkPermission()) {}
            else { requestPermission(); }
        }

        //새로운 쪽지가 있는지 확인
        Api api = Api.Factory.INSTANCE.create();
        api.checknote(LoginActivity.user_ac).enqueue(new Callback<CheckNote>() {
            @Override
            public void onResponse(Call<CheckNote> call, Response<CheckNote> response) {
                CheckNote cknote = response.body();
                Boolean checknt = cknote.checknote;

                Log.i("abccc", checknt+"");
                if(checknt)
                {
                    //쪽지 다이얼로그 띄우기
                    NoteDialog customDialog = new NoteDialog(MainActivity.this);
                    customDialog.callFunction();
                }
            }
            @Override
            public void onFailure(Call<CheckNote> call, Throwable t) {
                Log.i("abccc", t.getMessage());
            }
        });


/*        Api api = Api.Factory.INSTANCE.create();

        api.getUser("test").enqueue(new Callback<User>() {
            public void onResponse(Call<User> call, Response<User> response) {
                User user = response.body();
                Log.i("abcdefg", user.toString());
            }

            public void onFailure(Call<User> call, Throwable t) {
                Log.i("abcdefg", t.getMessage());
            }
        });

        api.getAllUser().enqueue(new Callback<UserFeed>() {
            public void onResponse(Call<UserFeed> call, Response<UserFeed> response) {

                Log.i("abcdefg",response.toString());

                UserFeed userFeed = response.body();
                List<User> users = userFeed.items;

                for(User user: users){
                    Log.i("abcdefg", "All : " + user.toString());
                }
            }

            public void onFailure(Call<UserFeed> call, Throwable t) {
                Log.i("abcdefg", t.getMessage());
            }
        });*/
    }

    private boolean checkPermission() {
        int result = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CALL_PHONE);
        int result2 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);
        int result3 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int result4 = ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.SEND_SMS);
        if(result == PackageManager.PERMISSION_GRANTED && result2==PackageManager.PERMISSION_GRANTED && result3==PackageManager.PERMISSION_GRANTED && result4==PackageManager.PERMISSION_GRANTED) { // 이전에 요청 허락 했으면
            return true;
        }
        else { // 이전에 요청 허락 하지 않았으면
            return false;
        }
    }

    private void requestPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this,android.Manifest.permission.CALL_PHONE)) {
            Toast.makeText(MainActivity.this, "일부 서비스를 이용하지 못 할 수 있습니다",Toast.LENGTH_SHORT).show();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)){
            Toast.makeText(MainActivity.this, "앱의 주요기능을 이용하실 수 없습니다",Toast.LENGTH_SHORT).show();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)){
            Toast.makeText(MainActivity.this, "앱의 주요기능을 이용하실 수 없습니다",Toast.LENGTH_SHORT).show();
        }
        else if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, Manifest.permission.SEND_SMS)){
            Toast.makeText(MainActivity.this, "일부 서비스를 이용하지 못 할 수 있습니다",Toast.LENGTH_SHORT).show();
        }
        else {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CALL_PHONE, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.SEND_SMS},1000);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch(requestCode) {
            case 1000 :
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "권한동의 완료", Toast.LENGTH_SHORT).show();
                }
                else { Toast.makeText(MainActivity.this,"권한거부", Toast.LENGTH_SHORT).show(); }
                break;
        }
        return;
    }

    private void createTab() {
        //tabHost 위젯과 연결
        TabHost tabHost = (TabHost) findViewById(R.id.tabhost);
        //TabHost를 설정할 때 가장 먼저 호출
        tabHost.setup(getLocalActivityManager());

        //탭 아이콘
        ImageView tab1 = new ImageView(this);
        tab1.setImageResource(R.drawable.selector1);
        ImageView tab2 = new ImageView(this);
        tab2.setImageResource(R.drawable.selector2);
        ImageView tab3 = new ImageView(this);
        tab3.setImageResource(R.drawable.selector3);
        ImageView tab4 = new ImageView(this);
        tab4.setImageResource(R.drawable.selector4);

        //Tab Spec생성 및 Tab 추가
        tabHost.addTab(tabHost.newTabSpec("TAB1").setIndicator(tab1) //화면에 보여주는 Tab명
                .setContent(new Intent(this,BoardActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("TAB2").setIndicator(tab2)
                .setContent(new Intent(this, RoomActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("TAB3").setIndicator(tab3)
                .setContent(new Intent(this, EventActivity.class)));
        tabHost.addTab(tabHost.newTabSpec("TAB4").setIndicator(tab4)
                .setContent(new Intent(this, EmergencyActivity.class)));


//        //흔들어서 열면 안전지키미 선택되도록 설정
//        Intent it = new Intent(getIntent());
//        page = it.getIntExtra("page", -1);
//
//        if(page == 1) {
//            tabHost.setCurrentTab(3);
//        } else {
//            tabHost.setCurrentTab(0);
//        }
    }
}

