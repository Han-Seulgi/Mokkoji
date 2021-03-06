package com.example.project_test;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project_test.Content.ContentWithPicture;
import com.example.project_test.Food.FoodActivity;
import com.example.project_test.Food.FoodContent.FoodActivityContent;
import com.example.project_test.Info.InfoActivity;
import com.example.project_test.Info.InfoContent.infoActivityContent;
import com.example.project_test.Meet.MeetActivity;
import com.example.project_test.Meet.MeetContent.MeetActivityContent;
import com.example.project_test.Mypage.MyPageActivity;
import com.example.project_test.Recipe.RecipeBoardActivity;
import com.example.project_test.Room.RoomContent.RoomContentActivity;
import com.example.project_test.Room.RoomData;
import com.example.project_test.Room.RoomList;
import com.example.project_test.SharenRent.SharenRentActivity;
import com.example.project_test.qa.qaActivity;
import com.example.project_test.qa.qaContent.qaActivityContent;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BoardActivity extends AppCompatActivity {
    ViewFlipper vflip1, vflip2, vflip3;
    View[] vflipview1;
    View[] vflipview2 = new View[2];
    ImageButton btn1, btn2, btn3, btn4, btn5, btn6, vimg1, vimg2;
    ImageButton imageButtons[] = {btn1, btn2, btn3, btn4, btn5, btn6};
    TextView v1txt1, v1txt2, v2txt1, v2txt2;
    Button changebtn;
    Toolbar toolbar;
    Intent intent;
    public String[] roomlist;
    Random rnd;

    int i = 0, t = 0, j=0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_board_main);

        //?????????
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ???????????? ???????????? ???????????? ??????(?????? ???????????? ???????????? ?????????)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.mypage);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //????????????
        vflip1 = findViewById(R.id.vflip1);
        vflip2 = findViewById(R.id.vflip2);
        vflip3 = findViewById(R.id.vflip3);
        vimg1 = findViewById(R.id.vimg1);
        vimg2 = findViewById(R.id.vimg2);
        v1txt1 = findViewById(R.id.v1txt1);
        v1txt2 = findViewById(R.id.v1txt2);
        v2txt1 = findViewById(R.id.v2txt1);
        v2txt2 = findViewById(R.id.v2txt2);

        changebtn = findViewById(R.id.changebtn);

        imageButtons[0] = findViewById(R.id.btn1);
        imageButtons[1] = findViewById(R.id.btn2);
        imageButtons[2] = findViewById(R.id.btn3);
        imageButtons[3] = findViewById(R.id.btn4);
        imageButtons[4] = findViewById(R.id.btn5);
        imageButtons[5] = findViewById(R.id.btn6);

        rnd = new Random();

        //??? ?????? ?????? ?????? ????????? ??? ?????????
        final Api api = Api.Factory.INSTANCE.create();

        api.CheckCook().enqueue(new Callback<PopularPost>() {
            @Override
            public void onResponse(Call<PopularPost> call, Response<PopularPost> response) {

                PopularPost popularPost = response.body();
                List<PopularData> popularData = popularPost.items;

                final ArrayList<String> title1 = new ArrayList<>();
                ArrayList<String> con1 = new ArrayList<>();

                //???????????? ??????, ?????? ??????
                for (PopularData d:popularData) {
                    title1.add(d.post_title);
                    con1.add(d.post_con);
                }

                //???????????? ????????? ?????????, ????????? ?????? ??????
                final String[] title = title1.toArray(new String[title1.size()]);
                final String[] con = con1.toArray(new String[con1.size()]);
                final Integer[] img = new Integer[title1.size()];

                for (int i = 0; i < img.length; i++) {

                    vflipview1 = new View[img.length];
                    vflipview1[i] = (View) View.inflate(BoardActivity.this, R.layout.view_item, null);
                    //???????????? ?????? ????????? ??? ????????????

                    //????????? ??????
                    TextView textView1 = vflipview1[i].findViewById(R.id.view_title);
                    textView1.setText(title[i]);
                    TextView textView2 = vflipview1[i].findViewById(R.id.view_content);
                    textView2.setText(con[i]);
                    final ImageView imageView = vflipview1[i].findViewById(R.id.view_img);
                    final int finalI = i;
                    api.getcontent(title[i]).enqueue(new Callback<PostList>() {
                        @Override
                        public void onResponse(Call<PostList> call, Response<PostList> response) {
                            PostList postlist = response.body();
                            int  bcode = postlist.bcode;

                            if (bcode == 11) { //??????????????? ??????????????????
                                img[finalI] = R.drawable.vflip2;
                            } else if (bcode == 22) { //??????????????? ??????????????????
                                img[finalI] = R.drawable.vflip3;
                            } else if (bcode == 33) { //??????????????? ??????????????????
                                img[finalI] = R.drawable.vflip1;
                            } else if (bcode == 66) { //??????Q&A ??????????????????
                                img[finalI] = R.drawable.vflip5;
                            } else { //??????????????? ??????????????????
                                img[finalI] = R.drawable.vflip4;
                            }
                            imageView.setBackgroundResource(img[finalI]);
                        }
                        @Override
                        public void onFailure(Call<PostList> call, Throwable t) {
                        }

                    });

                    //??????????????? ??? ??????
                    vflip1.addView(vflipview1[i]);

                    vflip1.setOnClickListener(new View.OnClickListener() { //??? ????????? ???????????? ???
                        @Override
                        public void onClick(View v) {
                            final int i = vflip1.getDisplayedChild();    //?????? ????????? ????????????

                            api.getcontent(title[i]).enqueue(new Callback<PostList>() {
                                public void onResponse(Call<PostList> call, Response<PostList> response) {
                                    PostList postlist = response.body();
                                    String id = postlist.id;
                                    String day = postlist.day;
                                    int  bcode = postlist.bcode;

                                    if(bcode == 11) { //??????????????? ??????????????????
                                        intent = new Intent(getApplicationContext(), ContentWithPicture.class);
                                    }
                                    else if(bcode == 22) { //??????????????? ??????????????????
                                        intent = new Intent(getApplicationContext(), FoodActivityContent.class);
                                    }
                                    else if(bcode == 33) { //??????????????? ??????????????????
                                        intent = new Intent(getApplicationContext(), MeetActivityContent.class);
                                    }
                                    else if(bcode == 66) { //??????Q&A ??????????????????
                                        intent = new Intent(getApplicationContext(), qaActivityContent.class);
                                    }
                                    else { //??????????????? ??????????????????
                                        intent = new Intent(getApplicationContext(), infoActivityContent.class);
                                    }
                                    intent.putExtra("??????", title[i]);
                                    intent.putExtra("?????????", id);
                                    intent.putExtra("??????", day);
                                    intent.putExtra("??????", con[i]);
                                    startActivity(intent);

                                }
                                public void onFailure(Call<PostList> call, Throwable t) {
                                    Log.i("??????", t.getMessage());
                                }

                            });
                        }
                    });
                }

            }

            @Override
            public void onFailure(Call<PopularPost> call, Throwable t) {
                Log.i("??????", t.getMessage());
            }

        });

        //???????????? ??????, ??????????????? ??????
        vflip1.setFlipInterval(4000);   // ??? ??? ?????? ???????????? ??????????????????(1000 ??? 1???)
        vflip1.setAutoStart(true);      //??????????????????(true:??????)
        vflip1.setInAnimation(BoardActivity.this, android.R.anim.slide_in_left); //animation
        vflip1.setOutAnimation(BoardActivity.this, android.R.anim.slide_out_right); //animation

        //??? ?????? ??? 6?????? ?????????
        for (int i = 0; i < imageButtons.length; i++) {
            imageButtons[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (v.getId() == R.id.btn1) { //??????????????? ?????????
                        Intent intent = new Intent(BoardActivity.this, RecipeBoardActivity.class);
                        startActivity(intent);
                    } else if (v.getId() == R.id.btn2) { //??????????????? ?????????
                        Intent intent = new Intent(BoardActivity.this, FoodActivity.class);
                        startActivity(intent);

                    } else if (v.getId() == R.id.btn3) { //??????????????? ?????????
                        Intent intent = new Intent(BoardActivity.this, MeetActivity.class);
                        startActivity(intent);

                    } else if (v.getId() == R.id.btn4) { //??????????????? ?????????
                        Intent intent = new Intent(BoardActivity.this, InfoActivity.class);
                        startActivity(intent);

                    } else if (v.getId() == R.id.btn5) {  //???????????? ?????????
                        Intent intent = new Intent(BoardActivity.this, SharenRentActivity.class);
                        startActivity(intent);

                    } else if (v.getId() == R.id.btn6) { //??????Q&A ?????????
                        Intent intent = new Intent(BoardActivity.this, qaActivity.class);
                        startActivity(intent);
                    }
                }
            });
        }

        //??? ?????? ?????? ??? ????????? ????????? ??? ?????????
        final int f2_images[] = {R.drawable.roomimg1, R.drawable.roomimg2, R.drawable.roomimg3, R.drawable.roomimg4}; //??? ???????????? ????????? ?????????

        api.getAllRoom().enqueue(new Callback<RoomList>() {
            @Override
            public void onResponse(Call<RoomList> call, Response<RoomList> response) {

                RoomList rlist = response.body();
                List<RoomData> rooms = rlist.items;

                roomlist = new String[rooms.size()];

                final ArrayList<Integer> post_code = new ArrayList<>();
                final ArrayList<String> post_title = new ArrayList<>();
                final ArrayList<String> post_con = new ArrayList<>();
                final ArrayList<String> id = new ArrayList<>();
                final ArrayList<String> room_lct = new ArrayList<>();
                final ArrayList<String> room_p = new ArrayList<>();
                final ArrayList<String> room_day = new ArrayList<>();
                final ArrayList<String> post_day = new ArrayList<>();

                for(RoomData d:rooms) {
                    post_code.add(d.post_code);
                    post_title.add(d.post_title);
                    post_con.add(d.post_con);
                    id.add(d.id);
                    room_lct.add(d.room_lct);
                    room_p.add(d.room_p);
                    room_day.add(d.room_day);
                    post_day.add(d.post_day);
                    Log.e("roomlist", d.toString());
                }
                final Integer[] img = {R.drawable.roomimg1, R.drawable.roomimg2};
                final String[] post_title1 = post_title.toArray(new String[post_title.size()]);
                final String[] room_lct1 = room_lct.toArray(new String[room_lct.size()]);

                for(int i=0; i<post_con.size(); i++) {
                    vflipview2 = new View[post_con.size()];
                    vflipview2[i] = (View) View.inflate(BoardActivity.this, R.layout.view_item, null);

                    TextView textView1 = vflipview2[i].findViewById(R.id.view_title);
                    textView1.setText(post_title1[i]);
                    TextView textView2 = vflipview2[i].findViewById(R.id.view_content);
                    textView2.setText(room_lct1[i]);
                    int ran = rnd.nextInt(img.length);
                    final ImageView imageView = vflipview2[i].findViewById(R.id.view_img);
                    imageView.setBackgroundResource(img[ran]);
                    vflip2.addView(vflipview2[i]);

                }

                vflip2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //??????, ?????????, ????????????, ??????
                        final int k = vflip2.getDisplayedChild();
                        Intent intent = new Intent(BoardActivity.this, RoomContentActivity.class);
                        intent.putExtra("??????", post_title.get(k));
                        intent.putExtra("?????????", id.get(k));
                        intent.putExtra("????????????", room_day.get(k));
                        intent.putExtra("??????", post_con.get(k));
                        intent.putExtra("??????",room_lct.get(k));
                        intent.putExtra("??????",post_code.get(k));
                        intent.putExtra("??????",post_day.get(k));
                        intent.putExtra("??????",room_p.get(k));
                        startActivity(intent);
                    }
                });

            }

            @Override
            public void onFailure(Call<RoomList> call, Throwable t) {
                Log.i("??????", t.getMessage());
            }
        });


        //???????????? ??????, ??????????????? ??????
        vflip2.setFlipInterval(4000);   // ??? ??? ?????? ???????????? ??????????????????(1000 ??? 1???)
        vflip2.setAutoStart(true);      //??????????????????(true:??????)
        vflip2.setInAnimation(this, android.R.anim.slide_in_left); //animation
        vflip2.setOutAnimation(this, android.R.anim.slide_out_right); //animation*/


        //??? ?????? ?????? ?????? HOT ??? ?????????
        final int f3_images[] = {R.drawable.shareimg1, R.drawable.shareimg2, R.drawable.shareimg3, R.drawable.shareimg4, R.drawable.shareimg5, R.drawable.shareimg6}; //??? ???????????? ????????? ?????????
        final String f3_text1[] = {"??????", "??????", "??????", "?????????", "??????", "??????"};  //??? ???????????? ????????? ?????????(??????)
        final String f3_text2[] = {"1000???", "2000???", "599???", "20000???", "3000???", "1500???"};  //??? ???????????? ????????? ?????????(??????)

        //??? ?????? ??? ?????? HOT?????? ?????? ????????? ????????? ???
        changebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //i, t, j??? 0?????? ?????????????????????
                //????????? ?????? ??? ????????? ???????????? ??????
                vimg1.setImageResource(f3_images[i++]);
                v1txt1.setText(f3_text1[t++]);
                v1txt2.setText(f3_text2[j++]);

                vimg2.setImageResource(f3_images[i++]);
                v2txt1.setText(f3_text1[t++]);
                v2txt2.setText(f3_text2[j++]);
                if (i >= f3_images.length) {   //????????? ????????? ????????? 0?????? ??????????????? ????????????
                    i = 0;
                    t = 0;
                    j=0;
                }
            }
        });

    }

    //????????? ??????
            @Override
            public boolean onCreateOptionsMenu(Menu menu) {
                super.onCreateOptionsMenu(menu);
                getMenuInflater().inflate(R.menu.top_menu, menu);
                return true;
            }

    //???????????? --home:??????????????? --message:?????????
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
                //????????? ??????
                Intent note_itnt = new Intent(getApplicationContext(), NoteActivity.class);
                startActivity(note_itnt);
                return true;
        }
        return true;
    }

}
