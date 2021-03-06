package com.example.project_test.Modify;

import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.example.project_test.Api;
import com.example.project_test.MySpinnerAdapter;
import com.example.project_test.R;
import com.example.project_test.Write;
import com.example.project_test.Writing.MeetWritingActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MeetModifyActivity extends AppCompatActivity {
    Toolbar toolbar;
    Button writing;
    EditText tedit, cedit, nedit, wedit, dedit;
    TextView tv0;
    String post_title, post_con, meet_lct, meet_day, meet_tag;
    int post_code, meet_p;

    private AlertDialog dialog;

    Spinner spinner;

    Calendar calendar = Calendar.getInstance();
    DatePickerDialog.OnDateSetListener datepicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateLabel(R.id.dedit);
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_meet);

        writing = findViewById(R.id.writing);
        tedit = findViewById(R.id.tedit);
        cedit = findViewById(R.id.cedit);
        wedit = findViewById(R.id.wedit);
        nedit = findViewById(R.id.nedit);
        dedit = findViewById(R.id.dedit);
        dedit.setFocusable(false);
        tv0 = findViewById(R.id.tv0);

        spinner = findViewById(R.id.meetSpinner);

        //?????? ????????????
        final ArrayList<String> items = new ArrayList<>();

        Api api = Api.Factory.INSTANCE.create();
        api.getModifyCategory(33).enqueue(new Callback<ModifyCategoryData>() {
            @Override
            public void onResponse(Call<ModifyCategoryData> call, Response<ModifyCategoryData> response) {
                Log.i("abced","??????");

                ModifyCategoryData cgList = response.body();
                List<Category> cg = cgList.items;

                //???????????? ??????
                for (Category s:cg) {
                    items.add(s.tag);
                }
                items.add("??????");

                MySpinnerAdapter adapter = new MySpinnerAdapter(MeetModifyActivity.this, android.R.layout.simple_spinner_item,items);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
                spinner.setSelection(adapter.getCount());

                int cnt=0;
                for (String i:items) {
                    if(i.equals(meet_tag)) {
                        spinner.setSelection(cnt);
                    }
                    cnt++;
                }
            }

            @Override
            public void onFailure(Call<ModifyCategoryData> call, Throwable t) {
                Log.i("abced",t.getMessage());
            }
        });

        //?????????
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ???????????? ???????????? ???????????? ??????(?????? ???????????? ???????????? ?????????)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbtn);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        tv0.setText("????????????");

        //??? ??????, ??????
        Intent intent = getIntent();
        post_title = intent.getStringExtra("??????");
        post_con = intent.getStringExtra("??????");
        meet_tag = intent.getStringExtra("??????");
        meet_lct = intent.getStringExtra("??????");
        meet_p = intent.getIntExtra("??????", 0);
        meet_day = intent.getStringExtra("??????");
        post_code = intent.getIntExtra("???????????????", 0);

        //set
        tedit.setText(post_title);
        cedit.setText(post_con);
        nedit.setText(meet_p+"");
        wedit.setText(meet_lct);
        dedit.setText(meet_day);

        //?????? ?????? ??????
        dedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(MeetModifyActivity.this, datepicker, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        //????????? _?????????
        int request = getIntent().getIntExtra("request", -1);
        Log.i("modifyrequest", String.valueOf(request));
        switch (request) {
//            case 0: AlertDialog.Builder builder = new AlertDialog.Builder(RecipeModifyActivity.this);
//                dialog = builder.setMessage("?????? ??????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        finish();
//                    }
//                }).create();
//                dialog.show(); break;
            case 1000:
        writing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                post_title = tedit.getText().toString();
                post_con = cedit.getText().toString();
                meet_lct = wedit.getText().toString();
                String p = nedit.getText().toString();
                meet_tag = spinner.getSelectedItem().toString();
                meet_day = dedit.getText().toString();


                if (post_title.equals("") || post_con.equals("") || meet_lct.equals("") || p.equals("") || meet_day.equals("") || meet_tag.equals("")) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MeetModifyActivity.this);
                    dialog = builder.setMessage("??? ????????? ???????????? ???????????????.").setNegativeButton("??????", null)
                            .create();
                    dialog.show();
                    return;
                } else {
                    meet_p = Integer.parseInt(p);

                    final Api api = Api.Factory.INSTANCE.create();

                    api.Modify(post_title, post_con, post_code).enqueue(new Callback<Write>() {
                        public void onResponse(Call<Write> call, Response<Write> response) {

                            Write write = response.body();

                            api.ModifyMeet(meet_tag, meet_lct, meet_p, meet_day, post_code).enqueue(new Callback<Write>() {
                                @Override
                                public void onResponse(Call<Write> call, Response<Write> response) {
                                    Write write = response.body();

                                    AlertDialog.Builder builder = new AlertDialog.Builder(MeetModifyActivity.this);
                                    dialog = builder.setMessage("?????? ?????????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            returnResult();
                                            finish();
                                        }
                                    })
                                            .create();
                                    dialog.show();
                                }

                                @Override
                                public void onFailure(Call<Write> call, Throwable t) {
                                    Log.i("????????????", t.getMessage());
                                }
                            });
                        }

                        public void onFailure(Call<Write> call, Throwable t) {
                            Log.i("????????????", t.getMessage());
                        }

                    });
                }

            }
        });break;}

    }

    private void returnResult() {
        post_title = tedit.getText().toString();
        post_con = cedit.getText().toString();
        meet_lct = wedit.getText().toString();
        String p = nedit.getText().toString();
        meet_p = Integer.parseInt(p);

        meet_tag = spinner.getSelectedItem().toString();

        Intent intent = new Intent();
        intent.putExtra("title", post_title);
        intent.putExtra("con", post_con);
        intent.putExtra("tag", meet_tag);
        intent.putExtra("lct", meet_lct);
        intent.putExtra("pnum", meet_p);

        setResult(RESULT_OK, intent);
        Log.i("meetmodifyact", "??????");
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                AlertDialog.Builder logout = new AlertDialog.Builder(MeetModifyActivity.this);
                logout.setTitle("????????????");
                logout.setMessage("????????? ?????????????????????????");
                logout.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
                logout.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                logout.show();

                return true;
        }
        return true;

    }

    private void updateLabel(int id) {
        String myFormat = "yyyy-MM-dd";  //????????????
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.KOREA);

        EditText edit = findViewById(id);
        edit.setText(sdf.format(calendar.getTime()));
    }
}
