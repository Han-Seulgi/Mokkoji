package com.example.project_test.Info;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SearchView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_test.Api;
import com.example.project_test.Mypage.MyPageActivity;
import com.example.project_test.NoteActivity;
import com.example.project_test.R;
import com.example.project_test.Writing.InfoWritingActivity;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InfoActivity extends AppCompatActivity {
    Toolbar toolbar;

    private RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    private InfoRecyclerAdapter adapter;

    SearchView search;

    ArrayList<InfoListData> data;
    //검색을 위한 전체 데이터 리스트 복사본
    ArrayList<InfoListData> cdata;

    Activity act;
    private final int WRITE_POST = 100;
    private final int MODIFY_POST = 1;
    private final int DELETE_POST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //상단탭
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ↓툴바의 홈버튼의 이미지를 변경(기본 이미지는 뒤로가기 화살표)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.mypage);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        Button writing = findViewById(R.id.writing); //글쓰기 버튼
        search = findViewById(R.id.search);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new InfoRecyclerAdapter();

        data = new ArrayList<>();
        //복사본 리스트 생성
        cdata = new ArrayList<>();
        act = InfoActivity.this;

        //서버 연결
        Api api = Api.Factory.INSTANCE.create();
        api.getInfoList(77).enqueue(new Callback<InfoPostList>() {
            @Override
            public void onResponse(Call<InfoPostList> call, Response<InfoPostList> response) {
                InfoPostList postList = response.body();
                List<PostData> postData = postList.items;

                ArrayList<String> title1 = new ArrayList<>();
                ArrayList<String> day1 = new ArrayList<>();
                ArrayList<String> id1 = new ArrayList<>();
                ArrayList<String> con1 = new ArrayList<>();

                //리스트에 제목, 날짜, 작성자 아이디 넣기
                for (PostData d:postData) {
                    title1.add(d.post_title);
                    day1.add(d.post_day);
                    id1.add(d.id);
                    con1.add(d.post_con);
                    Log.i("abc","정보 All: " + d.toString());
                }

                //리스트를 배열로 바꾸기, 이미지 배열 생성
                String[] title = title1.toArray(new String[title1.size()]);
                String[] day = day1.toArray(new String[day1.size()]);
                String[] id = id1.toArray(new String[id1.size()]);
                String[] con = con1.toArray(new String[con1.size()]);
                Integer[] img = new Integer[title1.size()];

                //넘어온 데이터의 사이즈에 맞춰 이미지 생성(?), 리사이클러뷰 데이터파일에 데이터 넘기기
                int i = 0;
                while (i < title.length) {
                    img[i] = R.drawable.information;
                    data.add(new InfoListData(img[i], title[i], day[i], id[i], con[i]));
                    i++;
                }
                adapter.setData(act, data);
                recyclerView.setAdapter(adapter);

                //복사본에 모든 데이터 저장
                cdata.addAll(data);
            }

            @Override
            public void onFailure(Call<InfoPostList> call, Throwable t) {
                    Log.i("abcdef", t.getMessage());
            }
        }); //서버연결

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //글쓰기버튼 이벤트
        writing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(act, InfoWritingActivity.class);
                intent.putExtra("request", WRITE_POST);
                startActivityForResult(intent, WRITE_POST);
            }
        });

        //검색
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.i("search", query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                Log.i("search", newText);
                msearch(newText);
                return true;
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent rdata) {
        super.onActivityResult(requestCode, resultCode, rdata);
//        if (resultCode == RESULT_OK) {
        Log.i("refresh", "requestcode: "+requestCode);
        Log.i("rbact", "requestcode: "+requestCode+"resultcode"+resultCode);
        switch (requestCode) {
            case WRITE_POST: if(resultCode == RESULT_OK){
                Log.i("refresh", "갱신");
                int img = R.drawable.information;
                String title = rdata.getStringExtra("title");
                String day = rdata.getStringExtra("day");
                String id = rdata.getStringExtra("id");
                String con = rdata.getStringExtra("con");

                adapter.addData(new InfoListData(img, title, day, id, con));
                adapter.notifyDataSetChanged();
                Log.i("WRITE_POST", "올리기 갱신: "+title+day+id+con);

            }break;

            case 777:
                if(resultCode == RESULT_OK){
                int position = rdata.getIntExtra("position", 0);
                int rc = rdata.getIntExtra("rc", 0);
                if (rc == MODIFY_POST) {
                    int img = R.drawable.information;
                    String title = rdata.getStringExtra("title");
                    String id = rdata.getStringExtra("id");
                    String day = rdata.getStringExtra("day");
                    String con = rdata.getStringExtra("con");
                    adapter.updateData(position, new InfoListData(img, title, day, id, con));
                } else if (rc == DELETE_POST) {
                    adapter.deleteData(position);
                } else Log.i("mod/del fail", "실패");
                } else Log.i("mod/del fail", "아무것도 안함");
        }
    }

    //상단탭 메뉴
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.top_menu, menu);
        return true;
    }

    //메뉴액션 --home:마이페이지 --message:쪽지함
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                Intent mypage_itnt = new Intent(getApplicationContext(), MyPageActivity.class);
                startActivity(mypage_itnt);
                return true;
            case R.id.mail:
                //쪽지함 화면
                Intent note_itnt = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(note_itnt);
                return true;
        }
        return true;
    }

    //검색을 수행하는 메소드
    public void msearch(String txt) {

        //문자 입력시마다 리스트를 지우고 새로 뿌림
        data.clear();

        //문자 입력이 없을때는 모든 데이터 보여줌
        if(txt.length() == 0) {
            data.addAll(cdata);
        }

        //문자 입력
        else{
            //데이터 리스트 복사본의 모든 데이터 검색
            for(int i = 0; i<cdata.size(); i++) {
                //모든 데이터의 입력받은 단어가 포함되어 있으면
                if(cdata.get(i).getTitle().contains(txt)) {
                    //검색된 데이터를 리스트에 추가
                    data.add(cdata.get(i));
                }
            }
        }
        //리스트 데이터가 변경되었으므로 어댑터 갱신
        adapter.notifyDataSetChanged();
    }
}

