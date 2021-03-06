package com.example.project_test.Meet.MeetContent;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_test.Api;
import com.example.project_test.Comment.Cmt;
import com.example.project_test.Comment.CmtData;
import com.example.project_test.Comment.CmtList;
import com.example.project_test.Comment.CommentListData;
import com.example.project_test.Comment.CommentRecyclerAdapter;
import com.example.project_test.Delete.DeletePost;
import com.example.project_test.LoginActivity;
import com.example.project_test.Modify.MeetModifyActivity;
import com.example.project_test.PostList;
import com.example.project_test.R;
import com.example.project_test.likeCheck;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetActivityContent extends AppCompatActivity {
    Toolbar toolbar;

    private RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    private CommentRecyclerAdapter adapter;
    int postcode, likenum, pnum , img1, position;
    TextView text1, writer, contents, textLikenum, locationtv, numtv, datetv;
    ImageView iv;
    ImageButton like, modify, delete;
    String title, content, location, date , cmt_con, tag, day, id;
    EditText editTextName1;
    Button push;

    ArrayList<CommentListData> data;

    private AlertDialog dialog;

    private final int MOD = 1000;
    String rtitle, rcon, rtag, rlct;
    int rpnum;
    //String rdate;
    boolean mod = false;

    boolean validatelk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meetcontent);

        //?????????
        toolbar = findViewById(R.id.toolbar);
        TextView t = findViewById(R.id.title);
        t.setText("???????????????");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ???????????? ???????????? ???????????? ??????(?????? ???????????? ???????????? ?????????)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbtn);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //??????
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        adapter = new CommentRecyclerAdapter();
        data = new ArrayList<>();

        text1 = findViewById(R.id.text1);
        writer = findViewById(R.id.id_day);
        contents = findViewById(R.id.con);
        locationtv = findViewById(R.id.location);
        numtv = findViewById(R.id.num);
        datetv = findViewById(R.id.date);
        like = findViewById(R.id.like);
        textLikenum = findViewById(R.id.textLikenum);
        modify = findViewById(R.id.modify);
        delete = findViewById(R.id.delete);
        editTextName1 = findViewById(R.id.editTextName1);
        push = findViewById(R.id.push);
        iv = findViewById(R.id.iv);

        //????????????
        Intent intent = getIntent();
        title = intent.getStringExtra("??????");
        id = intent.getStringExtra("?????????");
        day = intent.getStringExtra("??????");
        //int img = intent.getIntExtra("??????", 0);
        content = intent.getStringExtra("??????");

        //set
        text1.setText(title);
        writer.setText(id+"\n"+day);
        contents.setText(content);

        //?????? ????????? ???????????? ??? ???????????? ????????? ??????, ?????? ??????
        if( id.equals(LoginActivity.user_ac)) {
            modify.setVisibility(View.VISIBLE);
            delete.setVisibility(View.VISIBLE);
            Log.i("recyclerview","??? ?????????: "+id+"???????????????: "+LoginActivity.user_ac);
        }
        else {
            modify.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }

        //?????? ??????
        push.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cmt_con = editTextName1.getText().toString();

                Api api = Api.Factory.INSTANCE.create();
                api.Cmt(LoginActivity.user_ac,postcode,cmt_con).enqueue(new Callback<Cmt>() {
                    public void onResponse(Call<Cmt> call, Response<Cmt> response) {
                        Cmt cmt = response.body();
                        List<CmtData> cmtData = cmt.cmtdatas;

                        final ArrayList<Integer> cmtcode = new ArrayList<>();
                        final ArrayList<String> cmtday = new ArrayList<>();
                        for (CmtData d:cmtData) {
                            cmtcode.add(d.cmt_code);
                            cmtday.add(d.cmt_day);
                        }

                        Log.i("comment" , String.valueOf(cmtcode.get(0))+cmtday.get(0));

                        AlertDialog.Builder builder = new AlertDialog.Builder(MeetActivityContent.this);
                        dialog = builder.setMessage("?????????????????????????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                adapter.addData(new CommentListData(cmtcode.get(0), LoginActivity.user_ac, cmt_con, cmtday.get(0)));
                                editTextName1.setText("");
                                editTextName1.clearFocus();
                            }
                        })
                                .create();
                        dialog.show();
                    }
                    public void onFailure(Call<Cmt> call, Throwable t) {
                        Log.i("????????????", t.getMessage());
                    }

                });

            }
        });

        //?????? ???????????? ??????????????? ????????????
        final Api api = Api.Factory.INSTANCE.create();

        //???????????? ??????
        api.getcontent(title).enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                PostList postlist = response.body();
                postcode = postlist.pcode;

                //????????? ????????? ????????????, ??????, ?????????, ?????? ????????????
                api.getmeetday(postcode).enqueue(new Callback<MeetList>() {
                    @Override
                    public void onResponse(Call<MeetList> call, Response<MeetList> response) {
                        MeetList meetList = response.body();

                        tag = meetList.tag;
                        location = meetList.lct;
                        date = meetList.day;
                        pnum = meetList.pnum;
                        locationtv.setText("??????:   "+location);
                        numtv.setText("??????:   "+pnum);
                        datetv.setText("??????:   "+date);

                        //????????? ?????? ?????? ??????
                        if(tag.equals("??????")) img1=R.drawable.meeting2;
                        else if(tag.equals("??????")) img1=R.drawable.meeting4;
                        else if(tag.equals("??????")) img1=R.drawable.meeting6;
                        else if(tag.equals("??????")) img1=R.drawable.meeting1;
                        else if(tag.equals("??????/??????")) img1=R.drawable.meeting3;
                        else if(tag.equals("??????")) img1=R.drawable.meeting5;
                        iv.setImageResource(img1);
                    }

                    @Override
                    public void onFailure(Call<MeetList> call, Throwable t) {
                    }
                });

                //?????? ????????????
                api.getComments(postcode).enqueue(new Callback<CmtList>() {
                    @Override
                    public void onResponse(Call<CmtList> call, Response<CmtList> response) {
                        CmtList cmtList = response.body();
                        List<CmtData> cmtData = cmtList.items;

                        ArrayList<Integer> cmt_code1 = new ArrayList<>();
                        ArrayList<String> cmt_id1 = new ArrayList<>();
                        ArrayList<String> cmt_con1 = new ArrayList<>();
                        ArrayList<String> cmt_day1 = new ArrayList<>();

                        for(CmtData d:cmtData) {
                            cmt_code1.add(d.cmt_code);
                            cmt_id1.add(d.id);
                            cmt_con1.add(d.cmt_con);
                            cmt_day1.add(d.cmt_day);
                            Log.i("cmt",d.toString());
                        }

                        Integer[] cmt_code = cmt_code1.toArray(new Integer[cmt_code1.size()]);
                        String[] cmt_id = cmt_id1.toArray(new String[cmt_id1.size()]);
                        String[] cmt_con = cmt_con1.toArray(new String[cmt_con1.size()]);
                        String[] cmt_day = cmt_day1.toArray(new String[cmt_day1.size()]);

                        int i = 0;
                        while (i<cmt_code.length){
                            data.add(new CommentListData(cmt_code[i],cmt_id[i],cmt_con[i],cmt_day[i]));
                            i++;
                        }
                        adapter.setData(data);
                        recyclerView.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(Call<CmtList> call, Throwable t) {

                    }
                });


                //????????? ?????? ???????????? ?????????????????? ??????, ????????? ???????????? ?????? ??????
                api.validateLike(LoginActivity.user_ac, postcode).enqueue(new Callback<likeCheck>() {
                    @Override
                    public void onResponse(Call<likeCheck> call, Response<likeCheck> response) {
                        likeCheck likecheck = response.body();
                        validatelk = likecheck.validatelk;

                        //?????? ???
                        if (validatelk)
                            like.setImageResource(R.drawable.ic_like2);

                            //?????? ??????
                        else
                            like.setImageResource(R.drawable.ic_like);
                    }
                    @Override
                    public void onFailure(Call<likeCheck> call, Throwable t) {

                    }
                });

                //????????? ??????
                api.getlikenum(postcode).enqueue(new Callback<likeCheck>() {
                    @Override
                    public void onResponse(Call<likeCheck> call, Response<likeCheck> response) {
                        likeCheck likecheck = response.body();
                        likenum = likecheck.likenum;
                        //????????? ?????? ??????
                        textLikenum.setText("" + likenum);
                    }

                    @Override
                    public void onFailure(Call<likeCheck> call, Throwable t) {
                    }
                });
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
            }
        });


        //?????? ??????
        like.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //????????? ?????? ???????????? ?????????????????? ??????


                //?????????????????? ????????? ?????? ????????? likes ???????????? ??????
                if (!validatelk) {
                    Log.i("abcdefg", validatelk + "????????????");
                    api.addlike(LoginActivity.user_ac, postcode).enqueue(new Callback<likeCheck>() {
                        @Override
                        public void onResponse(Call<likeCheck> call, Response<likeCheck> response) {
                            likeCheck likecheck = response.body();
                            boolean cklk = likecheck.ckaddlk;
                            Log.i("abcdefg", cklk + "??????????????????");

                            //likes ???????????? ????????????, id, ?????????????????? ??????????????????
                            if (cklk) {
                                //like_num ???????????? ????????? +1
                                api.addlikenum(postcode).enqueue(new Callback<likeCheck>() {
                                    @Override
                                    public void onResponse(Call<likeCheck> call, Response<likeCheck> response) {
                                        likeCheck likecheck = response.body();
                                        boolean cklknum = likecheck.ckaddlikenum;

                                        Log.i("abcdefg", cklknum + "?????????????????????");

                                        //????????? ?????? ??????
                                        likenum++;
                                        textLikenum.setText("" + likenum);

                                        like.setImageResource(R.drawable.ic_like2);
                                        validatelk = true;
                                    }

                                    @Override
                                    public void onFailure(Call<likeCheck> call, Throwable t) {
                                    }
                                });
                            } else {
                                Log.i("abcdefg", "?????????????????? ?????? ??????");
                            }
                        }
                        @Override
                        public void onFailure(Call<likeCheck> call, Throwable t) {
                        }
                    });
                }

                //????????? ??????????????? ?????? ?????? ??????
                else {
                    Log.i("abcdefg", "?????? ????????? ????????? ?????? ??????");
                    api.deletelike(LoginActivity.user_ac, postcode).enqueue(new Callback<likeCheck>() {
                        @Override
                        public void onResponse(Call<likeCheck> call, Response<likeCheck> response) {
                            likeCheck likecheck = response.body();
                            boolean cklk = likecheck.ckdellk;
                            Log.i("abcdefg", cklk + "??????????????????");
                            like.setImageResource(R.drawable.ic_like);

                            //likes ??????????????? ?????? ?????? ????????????
                            if (cklk) {
                                //like_num ???????????? ????????? - 1
                                api.sublikenum(postcode).enqueue(new Callback<likeCheck>() {
                                    @Override
                                    public void onResponse(Call<likeCheck> call, Response<likeCheck> response) {
                                        likeCheck likecheck = response.body();
                                        boolean cklknum = likecheck.cksublikenum;

                                        Log.i("abcdefg", cklknum + "?????????????????????");

                                        //????????? ?????? ??????
                                        likenum--;
                                        textLikenum.setText("" + likenum);

                                        like.setImageResource(R.drawable.ic_like);
                                        validatelk = false;
                                    }

                                    @Override
                                    public void onFailure(Call<likeCheck> call, Throwable t) {
                                    }
                                });
                            } else {
                                Log.i("abcdefg", "?????????????????? ?????? ??????");
                            }
                        }
                        @Override
                        public void onFailure(Call<likeCheck> call, Throwable t) {
                        }
                    });
                }
            }
//                    @Override
//                    public void onFailure(Call<likeCheck> call, Throwable t) {
//
//                    }
            //});
            // }
        });

        //?????? ??????
        int request = getIntent().getIntExtra("requestmod", -1);
        int request2 = getIntent().getIntExtra("requestdel", -1);
        position = getIntent().getIntExtra("position",-1);
        Log.i("modifyrequest", String.valueOf(request));
        if (request == 200) {
            modify.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MeetActivityContent.this, MeetModifyActivity.class);
                    intent.putExtra("??????", title); //???????????? ??????
                    intent.putExtra("??????", content); //???????????? ??????
                    intent.putExtra("??????", tag);  //????????????
                    intent.putExtra("??????", location);  //??????
                    intent.putExtra("??????", pnum);  //??????
                    intent.putExtra("??????", date);  //??????
                    intent.putExtra("???????????????", postcode); //???????????? ??????
                    intent.putExtra("request", MOD);
                    startActivityForResult(intent, MOD);
                }
            });
        }
        if (request2 == 300) {
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    AlertDialog dialog;
                    dialog = builder.setMessage("???????????? ?????????????????????????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Api api = Api.Factory.INSTANCE.create();

                                    api.deletepost(title).enqueue(new Callback<DeletePost>() {
                                        @Override
                                        public void onResponse(Call<DeletePost> call, Response<DeletePost> response) {

                                            Intent intent = new Intent();
                                            intent.putExtra("position", position);
                                            intent.putExtra("rc", 2);
                                            setResult(RESULT_OK, intent);
                                            Log.i("refresh", "????????????");
                                            finish();
                                        }

                                        @Override
                                        public void onFailure(Call<DeletePost> call, Throwable t) {
                                            Log.i("delete", t.getMessage());
                                        }
                                    });
                                }
                            }
                    ).create();
                    dialog.show();
                }
            });
        }

        //??????
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent rdata) {
        super.onActivityResult(requestCode, resultCode, rdata);
        Log.i("meetcontents", "requestcode: " + requestCode + "resultcode" + resultCode);
        //if (resultCode == RESULT_OK) {
        switch (requestCode) {
            case MOD: {
                if (resultCode == RESULT_OK) {
                    Log.i("refresh", "???????????????");
                    rtitle = rdata.getStringExtra("title");
                    rcon = rdata.getStringExtra("con");
                    rtag = rdata.getStringExtra("tag");
                    rlct = rdata.getStringExtra("lct");
                    //rdate = rdata.getStringExtra("date");
                    rpnum = rdata.getIntExtra("pnum", 0);

                    //????????? ?????? ?????? ??????
                    if(rtag.equals("??????")) img1=R.drawable.meeting2;
                    else if(rtag.equals("??????")) img1=R.drawable.meeting4;
                    else if(rtag.equals("??????")) img1=R.drawable.meeting6;
                    else if(rtag.equals("??????")) img1=R.drawable.meeting1;
                    else if(rtag.equals("??????/??????")) img1=R.drawable.meeting3;
                    else if(rtag.equals("??????")) img1=R.drawable.meeting5;
                    iv.setImageResource(img1);

                    text1.setText(rtitle);
                    contents.setText(rcon);
                    locationtv.setText("??????:   "+rlct);
                    numtv.setText("??????:   "+rpnum);
                    //datetv.setText("??????:   "+rdate);
                    mod = true;
                }
                break;
            }
        }
        //}
    }

    @Override
    public void onBackPressed() {
        if(mod){
            Intent intent = new Intent();
            intent.putExtra("position", position);
            intent.putExtra("title", rtitle);
            intent.putExtra("id", id);
            intent.putExtra("day", day);
            intent.putExtra("con", rcon);
            intent.putExtra("img", img1);
            intent.putExtra("board", "??????");
            intent.putExtra("rc", 1);
            setResult(RESULT_OK, intent);
            Log.i("refresh", "????????????");
            finish();}
        else finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int gid = item.getItemId();
        switch (gid) {
            case android.R.id.home:
                if(mod){
                    Intent intent = new Intent();
                    intent.putExtra("position", position);
                    intent.putExtra("title", rtitle);
                    intent.putExtra("id", id);
                    intent.putExtra("day", day);
                    intent.putExtra("con", rcon);
                    intent.putExtra("img", img1);
                    intent.putExtra("board", "??????");
                    intent.putExtra("rc", 1);
                    setResult(RESULT_OK, intent);
                    Log.i("refresh", "????????????");
                    finish();}
                else finish();
                return true;
        }
        return true;
    }
}
