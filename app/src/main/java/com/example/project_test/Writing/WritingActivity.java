package com.example.project_test.Writing;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_test.Api;
import com.example.project_test.LoginActivity;
import com.example.project_test.R;
import com.example.project_test.Recipe.PostData;
import com.example.project_test.Recipe.RecipePostList;
import com.example.project_test.Write;
import com.example.project_test.likeCheck;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class WritingActivity extends AppCompatActivity {

    Toolbar toolbar;
    Button writing;
    EditText tedit, cedit, pedit, nedit, cedit2;
    TextView tv0, tv1 , title2;
    String post_title, post_con, cook_src, cook_rcp;
    ImageButton imgup;
    int board_code;

    private AlertDialog dialog;
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_IMAGE=2;

    private static Uri mImageCaptureUri;
    private String imgPath;
    private Bitmap photoBitmap;
    String folderName = "cameraTemp";
    File mediaFile = null;
    String fileName = "talkphoto.jpg";

    LinearLayout imgLayout;

    private String realPath;
    String imgString2;
    String[] imgString;
    ArrayList<String> imgs = new ArrayList<>();

    private RecyclerView rv;
    private LinearLayoutManager layoutManager;
    ArrayList<ThumbnailListData> imglist;
    RecyclerAdapterThumbnail adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.write_recipe);

        writing = findViewById(R.id.writing);
        tedit = findViewById(R.id.tedit);
        cedit = findViewById(R.id.cedit);
        //pedit = findViewById(R.id.pedit);
        nedit = findViewById(R.id.nedit);
        cedit2 = findViewById(R.id.cedit2);
        title2 = findViewById(R.id.title2);
        imgup = findViewById(R.id.imgup);

        tv0 = findViewById(R.id.tv0);
        tv1 = findViewById(R.id.tv1);

        rv = findViewById(R.id.rvT);
        adapter = new RecyclerAdapterThumbnail();
        imglist = new ArrayList<>();
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);

        imgLayout = findViewById(R.id.imgLayout);
        //?????????
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowCustomEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true); // ???????????? ???????????? ???????????? ??????(?????? ???????????? ???????????? ?????????)
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.backbtn);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        imgup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(WritingActivity.this);
                builder.setMessage("????????? ??????");
//                DialogInterface.OnClickListener cameraListener = new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        takePhotoAction();
//                    }
//                };
                DialogInterface.OnClickListener albumListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        takeAlbumAction();
                    }
                };
                DialogInterface.OnClickListener cancleListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                };
                builder.setTitle("???????????? ????????? ??????");
                //builder.setPositiveButton("????????????", cameraListener);
                builder.setPositiveButton("????????????", albumListener);
                builder.setNegativeButton("??????", cancleListener);
                builder.show();
            }
        });

        int request = getIntent().getIntExtra("request", -1);
        switch (request) {
            case 0: AlertDialog.Builder builder = new AlertDialog.Builder(WritingActivity.this);
                dialog = builder.setMessage("?????? ??????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
                dialog.show(); break;
            case 100:
            writing.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    post_title = tedit.getText().toString();
                    post_con = cedit2.getText().toString();
                    cook_src = nedit.getText().toString();
                    cook_rcp = cedit.getText().toString();

                    if (title2.getText().toString().equals("???????????????")) {
                        board_code = 11;
                    }
                    Log.i("?????????", LoginActivity.user_ac + post_title + post_con + board_code);

                    if (post_title.equals("") || post_con.equals("") || cook_src.equals("")) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(WritingActivity.this);
                        dialog = builder.setMessage("??? ????????? ???????????? ???????????????.").setNegativeButton("??????", null)
                                .create();
                        dialog.show();
                        return;
                    } else {
                        final Api api = Api.Factory.INSTANCE.create();
                        api.Write(LoginActivity.user_ac, post_title, post_con, board_code).enqueue(new Callback<Write>() {
                            public void onResponse(Call<Write> call, final Response<Write> response) {
                                final Write write = response.body();
                                api.CookWrite(cook_src, cook_rcp).enqueue(new Callback<CookWrite>() {
                                    public void onResponse(Call<CookWrite> call, Response<CookWrite> response) {
                                        Log.i("rcp", "?????????:" + response.toString());
                                    }

                                    public void onFailure(Call<CookWrite> call, Throwable t) {
                                        Log.i("rcpfail", t.getMessage());
                                    }
                                });


                                if (imgString2 != null) {
                                    api.imgupload(LoginActivity.user_ac, imgString).enqueue(new Callback<ResponseBody>() {
                                        @Override
                                        public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                                            try {
                                                Log.i("img", response.body().string().trim());
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                            //Img i = response.body();
                                            //String d = i.img_data;
                                            //boolean upload = i.insert;

                                            //if (upload==true){
                                            Log.i("img", "????????? ??????");
                                            //}
                                        }

                                        @Override
                                        public void onFailure(Call<ResponseBody> call, Throwable t) {
                                            Log.i("img", "????????? ??????" + t.getMessage());
                                        }
                                    });
                                } else Log.i("img", "?????? ?????? ??????");

                                AlertDialog.Builder builder = new AlertDialog.Builder(WritingActivity.this);
                                dialog = builder.setMessage("?????? ?????????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Log.i("result", "refresh result");
                                        finish();
                                    }
                                }).create();
                                dialog.show();
                                returnResult();

                                api.newlike().enqueue(new Callback<likeCheck>() {
                                    @Override
                                    public void onResponse(Call<likeCheck> call, Response<likeCheck> response) {
                                        likeCheck lc = response.body();
                                        boolean newlk = lc.newlike;
                                        Log.i("aaaa", newlk + "");
                                    }

                                    @Override
                                    public void onFailure(Call<likeCheck> call, Throwable t) {

                                    }
                                });
                            }

                            public void onFailure(Call<Write> call, Throwable t) {
                                Log.i("onfailure", t.getMessage());
                                AlertDialog.Builder builder = new AlertDialog.Builder(WritingActivity.this);
                                dialog = builder.setMessage("?????? ??????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                }).create();
                                dialog.show();
                            }
                        });
                    }
                }
            }); break;
        }
    }

    private void returnResult() {
        final Api api = Api.Factory.INSTANCE.create();
        api.getRecipeList(11).enqueue(new Callback<RecipePostList>() {
            @Override
            public void onResponse(Call<RecipePostList> call, Response<RecipePostList> response) {
                RecipePostList postList = response.body();
                List<PostData> postData = postList.items;

                ArrayList<String> title1 = new ArrayList<>();
                ArrayList<String> day1 = new ArrayList<>();
                ArrayList<String> id1 = new ArrayList<>();
                ArrayList<String> con1 = new ArrayList<>();
                ArrayList<Integer> code1 = new ArrayList<>();
                ArrayList<String> imgdata1 = new ArrayList<>();

                //???????????? ??????, ??????, ????????? ????????? ??????
                for (PostData d : postData) {
                    title1.add(d.title);
                    day1.add(d.day);
                    id1.add(d.id);
                    con1.add(d.con);
                    code1.add(d.pcode);
                    if(d.img_data.equals("")) imgdata1.add("none");
                    else imgdata1.add(d.img_data);
                    Log.i("abc", "?????? All: " + d.toString());
                }

                //???????????? ????????? ?????????, ????????? ?????? ??????
                final String[] title = title1.toArray(new String[title1.size()]);
                String[] day = day1.toArray(new String[day1.size()]);
                String[] id = id1.toArray(new String[id1.size()]);
                String[] con = con1.toArray(new String[con1.size()]);
                final Integer[] code = code1.toArray(new Integer[code1.size()]);
                final String[] img_data = imgdata1.toArray(new String[imgdata1.size()]);

                Intent intent = new Intent();
                intent.putExtra("title", title[0]);
                intent.putExtra("day", day[0]);
                intent.putExtra("id", id[0]);
                intent.putExtra("con", con[0]);
                intent.putExtra("code", code[0]);
                intent.putExtra("img", img_data[0]);

                setResult(RESULT_OK, intent);
                //finish();
            }

            public void onFailure(Call<RecipePostList> call, Throwable t) {
                Log.i("onfailure", t.getMessage());
                AlertDialog.Builder builder = new AlertDialog.Builder(WritingActivity.this);
                dialog = builder.setMessage("?????? ??????").setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).create();
                dialog.show();
            }
        });
    }

    //????????????
    private void takePhotoAction() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri =  Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));
        /*File photoFile = new File(Environment.getExternalStorageDirectory(),url);
        mImageCaptureUri = FileProvider.getUriForFile(getBaseContext(),"com.example.project_test", photoFile);*/

        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        startActivityForResult(cameraIntent, PICK_FROM_CAMERA);

    }

    //??????????????? ????????????
    private void takeAlbumAction() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(albumIntent, PICK_FROM_ALBUM);
    }

    public String getRealPathFromURI(Uri contentUri){
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri,proj,null,null,null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return  cursor.getString(column_index);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode != RESULT_OK)
            return;

        switch (requestCode) {
            case PICK_FROM_ALBUM: {
                mImageCaptureUri = data.getData();
                Log.e("?????????????????????", mImageCaptureUri.getPath().toString());
                imgPath = getRealPathFromURI(mImageCaptureUri);//??????????????????
                realPath = getRealPathFromURI(mImageCaptureUri);
                Log.e("?????????????????????",imgPath);
            }
            case PICK_FROM_CAMERA: {
                Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri,"image/*");

                intent.putExtra("outputX", 200);//???????????? x?????????
                intent.putExtra("outputY", 115);//y?????????
                intent.putExtra("aspectX", 1.67);//????????? x?????????
                intent.putExtra("aspectY", 1);//????????? y?????????
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_IMAGE);
                break;
            }
            case CROP_FROM_IMAGE: {
                //????????? ????????? ??????
                if (resultCode != RESULT_OK) return;

                final Bundle extras = data.getExtras();

                //????????? ???????????? ?????? file ?????? ??????
                String filePath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/talkImg/"+
                        System.currentTimeMillis()+".jpg";
                Log.e("mImageCaptureUri: ", "Croped" + filePath);


                if (extras != null) {
                    photoBitmap = extras.getParcelable("data");//???????????? ????????? ?????? bitmap?????????
                    /*ImageView iv = new ImageView(getBaseContext());
                    iv.setImageBitmap(photoBitmap);
                    imgLayout.addView(iv, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);*/

                    imglist.add(new ThumbnailListData(photoBitmap));
                    adapter.setData(imglist);
                    rv.setAdapter(adapter);
                    rv.setLayoutManager(layoutManager);

                    imgPath = filePath;
                    saveCropImage(photoBitmap,imgPath);//?????? ???????????? ???????????????, ????????? ??????

                    //base64 encoding
                    ByteArrayOutputStream outStream = new ByteArrayOutputStream();

                    Bitmap bm = BitmapFactory.decodeFile(imgPath); //???????????? ?????????
                    bm.compress(Bitmap.CompressFormat.JPEG,100,outStream);
                    byte bytes[] = outStream.toByteArray();
                    imgString2 = Base64.encodeToString(bytes, Base64.NO_WRAP);
                    imgs.add(imgString2);
                    Log.i("imgArray", String.valueOf(imgs.size()));
                    imgString = imgs.toArray(new String[imgs.size()]);

                    break;
                }
                //???????????? ??????
                File f = new File(mImageCaptureUri.getPath());
                if (f.exists()) f.delete();


            }
        }
    }

    private void saveCropImage(Bitmap bitmap, String filePath) {
        String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/talkImg";
        File dir_talkImg = new File(dirPath);
        if (!dir_talkImg.exists()) dir_talkImg.mkdir();

        File copyFile = new File(filePath);
        BufferedOutputStream bos = null;
        try{
            copyFile.createNewFile();
            bos = new BufferedOutputStream(new FileOutputStream((copyFile)));
            bitmap.compress(Bitmap.CompressFormat.JPEG,100,bos);

            sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(copyFile)));
            bos.flush();
            bos.close();
        } catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                AlertDialog.Builder logout = new AlertDialog.Builder(WritingActivity.this);
                logout.setTitle("????????????");
                logout.setMessage("????????? ?????????????????????????");
                logout.setPositiveButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(RESULT_CANCELED);
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
}



