package com.example.project_test.Food.FoodContent;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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
import com.example.project_test.Modify.FoodModifyActivity;
import com.example.project_test.PostList;
import com.example.project_test.R;
import com.example.project_test.likeCheck;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FoodActivityContent extends AppCompatActivity implements OnMapReadyCallback {
    MapFragment mapFragment;
    GoogleMap gMap;

    double latitude, longitude;

    Toolbar toolbar;

    private RecyclerView recyclerView;
    public RecyclerView.LayoutManager layoutManager;
    //private RecyclerView.Adapter adapter;
    TextView text1, writer, contents, textLikenum, lct;
    int postcode, likenum, position;
    ImageButton like, modify, delete;
    String title, content, location,cmt_con, id, day;
    EditText editTextName1;
    Button push;

    private AlertDialog dialog;

    private CommentRecyclerAdapter adapter;
    ArrayList<CommentListData> data;

    private final int MOD = 1000;

    String rtitle, rcon, rlct;

    boolean mod = false;

    boolean validatelk;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_foodcontent);

            //?????????
            toolbar = findViewById(R.id.toolbar);
            TextView t = findViewById(R.id.title);
            t.setText("???????????????");
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowCustomEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ???????????? ???????????? ???????????? ??????(?????? ???????????? ???????????? ?????????)
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbtn);
            getSupportActionBar().setDisplayShowTitleEnabled(false);

            mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);

            //??????
            recyclerView = findViewById(R.id.recyclerView);
            recyclerView.setHasFixedSize(true);
            adapter = new CommentRecyclerAdapter();

            text1 = findViewById(R.id.text1);
            writer = findViewById(R.id.id_day);
            contents = findViewById(R.id.con);
            like = findViewById(R.id.like);
            textLikenum = findViewById(R.id.textLikenum);
            modify = findViewById(R.id.modify);
            delete = findViewById(R.id.delete);
            editTextName1 = findViewById(R.id.editTextName1);
            push = findViewById(R.id.push);

            lct = findViewById(R.id.lct);

            data = new ArrayList<>();

            //????????????
            Intent intent = getIntent();
            title = intent.getStringExtra("??????");
            id = intent.getStringExtra("?????????");
            day = intent.getStringExtra("??????");
            content = intent.getStringExtra("??????");

            //setText
            text1.setText(title);
            writer.setText(id+"\n"+day);
            contents.setText(content);

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

                            AlertDialog.Builder builder = new AlertDialog.Builder(FoodActivityContent.this);
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
            //?????? ???????????? ??????????????? ????????????
            final Api api = Api.Factory.INSTANCE.create();

            //???????????? ??????
            api.getcontent(title).enqueue(new Callback<PostList>() {
                @Override
                public void onResponse(Call<PostList> call, Response<PostList> response) {
                    PostList postlist = response.body();
                    postcode = postlist.pcode;

                    //????????? ????????? ?????? ????????????
                    api.getlocation(postcode).enqueue(new Callback<FoodList>() {
                        @Override
                        public void onResponse(Call<FoodList> call, Response<FoodList> response) {
                            FoodList foodList = response.body();
                            location = foodList.lct;
                            lct.setText(location);

                            Log.i("abcdef", location+"");
                        }

                        @Override
                        public void onFailure(Call<FoodList> call, Throwable t) {
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
                }

                @Override
                public void onFailure(Call<PostList> call, Throwable t) {
                }
            });


            //?????? ??????
            like.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    /*//????????? ?????? ???????????? ?????????????????? ??????
                    api.validateLike(LoginActivity.user_ac, postcode).enqueue(new Callback<likeCheck>() {
                        @Override
                        public void onResponse(Call<likeCheck> call, Response<likeCheck> response) {
                            likeCheck likecheck = response.body();
                            boolean validatelk = likecheck.validatelk;*/

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
//                        @Override
//                        public void onFailure(Call<likeCheck> call, Throwable t) {
//
//                        }
//                    });
//                }
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
                        Intent intent = new Intent(FoodActivityContent.this, FoodModifyActivity.class);
                        intent.putExtra("??????", title); //???????????? ??????
                        intent.putExtra("??????", content); //???????????? ??????
                        intent.putExtra("??????", location); //??????
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

            layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);

        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent rdata) {
        super.onActivityResult(requestCode, resultCode, rdata);
        Log.i("foodcontents", "requestcode: " + requestCode + "resultcode" + resultCode);
        //if (resultCode == RESULT_OK) {
        switch (requestCode) {
            case MOD: {
                if (resultCode == RESULT_OK) {
                    Log.i("refresh", "???????????????");
                    rtitle = rdata.getStringExtra("title");
                    rcon = rdata.getStringExtra("con");
                    rlct = rdata.getStringExtra("lct");

                    text1.setText(rtitle);
                    contents.setText(rcon);
                    lct.setText(rlct);

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
        intent.putExtra("board", "????????????");
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
                    intent.putExtra("board", "????????????");
                    intent.putExtra("rc", 1);
                    setResult(RESULT_OK, intent);
                    Log.i("refresh", "????????? ????????????");
                    finish();}
                else finish();
                return true;
        }
        return true;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        gMap = googleMap;
        gMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        gMap.getUiSettings().setZoomControlsEnabled(true);

        final Api api = Api.Factory.INSTANCE.create();
        //???????????? ??????
        api.getcontent(title).enqueue(new Callback<PostList>() {
            @Override
            public void onResponse(Call<PostList> call, Response<PostList> response) {
                PostList postlist = response.body();
                postcode = postlist.pcode;

                //????????? ????????? ?????? ????????????
                api.getlocation(postcode).enqueue(new Callback<FoodList>() {
                    @Override
                    public void onResponse(Call<FoodList> call, Response<FoodList> response) {
                        FoodList foodList = response.body();
                        location = foodList.lct;
                        List<Address> list = null;
                        try {
                            Geocoder geocoder = new Geocoder(FoodActivityContent.this);
                            list = geocoder.getFromLocationName(location, 10);
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.e("test", "????????? ?????? - ???????????? ??????????????? ????????????");
                        }

                        if (list != null) {
                            if (list.size() == 0) {
                                Toast.makeText(FoodActivityContent.this, "???????????? ?????? ????????? ????????????", Toast.LENGTH_LONG);
                            } else {
                                latitude = list.get(0).getLatitude(); //??????
                                longitude = list.get(0).getLongitude(); // ??????
                                LatLng loc = new LatLng(latitude, longitude); //?????? ????????? ????????????
                                gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(loc, 15));
                            }
                        }

                        LatLng food = new LatLng(latitude, longitude);
                        LatLng latLng[] = new LatLng[]{food};
                        String titles[] = new String[]{title};

                        //?????? ??????
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(latLng[0]);
                        markerOptions.alpha(0.5f);
                        gMap.addMarker(markerOptions);

                        Log.i("foodlocation", location + "");
                    }

                    @Override
                    public void onFailure(Call<FoodList> call, Throwable t) {
                        Log.i("food", t.getMessage());
                    }
                });
            }

            @Override
            public void onFailure(Call<PostList> call, Throwable t) {
            }
        });
    }
}

