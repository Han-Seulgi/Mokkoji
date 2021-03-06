package com.example.project_test.Recipe;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.project_test.Api;
import com.example.project_test.Content.ContentWithPicture;
import com.example.project_test.PostList;
import com.example.project_test.R;

import java.util.Arrays;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RecipeRandom extends Activity {
    TextView tv;
    ImageView iv;
    ImageButton btnClose;

    TextView showContent;
    TextView click;

    //int[] img;
    String[] img;
    int size, random;
    String title[];

    String id;
    String day;
    String con;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);//타이틀바x
        setContentView(R.layout.activity_random);

        iv=findViewById(R.id.img);
        tv=findViewById(R.id.title);
        showContent = findViewById(R.id.showContent);
        click = findViewById(R.id.click);

        Intent intent = getIntent();
        img = intent.getStringArrayExtra("img"); // img[] 라는 배열에 게시물 이미지 저장
        //img = intent.getIntArrayExtra("img"); // img[] 라는 배열에 게시물 이미지 저장
        title = intent.getStringArrayExtra("title"); // title[]이라는 배열에 게시물 제목 저장
        size = intent.getIntExtra("size", 0);
        Log.i("random","index: " + size);

        btnClose = findViewById(R.id.btnClose);
        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void mOnRec(View v) { // 추천받기(다시추천) 버튼
        Random r = new Random();
        random = r.nextInt(size); // 게시물의 갯수 만큼의 범위에서 랜덤값 추출

        Log.i("dddd", img[random]);
        if(img[random].equals("none"))
            iv.setImageResource(R.drawable.food2);

        else {
            byte[] encodeByte = Base64.decode(img[random], Base64.NO_WRAP);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            //bitmap;
            iv.setImageBitmap(bitmap);
        }
        //iv.setImageResource(img[random]);
        tv.setText(title[random]);
        tv.setVisibility(View.VISIBLE);

        showContent.setVisibility(View.VISIBLE);
        click.setText("다시 추천받기");
        click.setTextSize(20);
    }

    public void mOnShow(final View v){ //레시피 보러가기 버튼
        final Api api = Api.Factory.INSTANCE.create();

        api.getcontent(title[random]).enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                PostList postlist = response.body();
                id = postlist.id;
                con = postlist.con;
                day = postlist.day;

                Intent intent = new Intent(v.getContext(), ContentWithPicture.class);
                intent.putExtra("제목", title[random]); //게시물의 제목
                intent.putExtra("탭이름", "자취앤집밥"); //게시판의 제목
                intent.putExtra("작성자", id);
                intent.putExtra("날짜", day);
                intent.putExtra("내용", con);
                v.getContext().startActivity(intent);
                finish();
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {

            }
        });

    }
}
