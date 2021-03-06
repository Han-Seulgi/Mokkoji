package com.example.project_test.Mypage.MyContents;

import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_test.Api;
import com.example.project_test.Content.ContentWithPicture;
import com.example.project_test.Delete.DeleteCmt;
import com.example.project_test.Food.FoodContent.FoodActivityContent;
import com.example.project_test.Info.InfoContent.infoActivityContent;
import com.example.project_test.LoginActivity;
import com.example.project_test.Meet.MeetContent.MeetActivityContent;
import com.example.project_test.R;
import com.example.project_test.qa.qaContent.qaActivityContent;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyCmtRecyclerAdapter extends RecyclerView.Adapter<MyCmtRecyclerAdapter.CmtViewHolder> {
    private ArrayList<MyCmtListData> datas;
    public void setData(ArrayList<MyCmtListData> list) { datas = list; }
    Intent intent;

    @NonNull
    @Override
    public MyCmtRecyclerAdapter.CmtViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mycontents2,parent,false);

        CmtViewHolder holder = new CmtViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CmtViewHolder holder, final int position) {
        MyCmtListData data = datas.get(position);

        final int code = data.getCmtcode();
        final String cmtcon = data.getCmtcon();
        final String cmtday = data.getCmtday();
        final String id = data.getId();
        final String ptitle = data.getPosttitle();
        final String pcon = data.getPostcon();
        final String pday = data.getPostday();
        final int board = data.getBoard();

        holder.iv.setImageResource(data.getImg());
        holder.title.setText(ptitle);
        holder.cmt.setText(cmtcon);
        holder.day.setText(cmtday);

        holder.itemView.setOnClickListener(new View.OnClickListener() { //??? ?????? ???????????? ???
            @Override
            public void onClick(View v) {
                if(board == 11) { //??????????????? ??????????????????
                    intent = new Intent(v.getContext(), ContentWithPicture.class);
                }
                else if(board == 22) { //??????????????? ??????????????????
                    intent = new Intent(v.getContext(), FoodActivityContent.class);
                }
                else if(board == 33) { //??????????????? ??????????????????
                    intent = new Intent(v.getContext(), MeetActivityContent.class);
                }
                else if(board == 66) { //??????Q&A ??????????????????
                    intent = new Intent(v.getContext(), qaActivityContent.class);
                }
                else if (board == 77){ //??????????????? ??????????????????
                    intent = new Intent(v.getContext(), infoActivityContent.class);
                } else ;
                intent.putExtra("??????", ptitle); //???????????? ??????
                intent.putExtra("?????????", id);
                intent.putExtra("??????", pday);
                intent.putExtra("??????", pcon);
                v.getContext().startActivity(intent);
            }
        });

        //???????????? ?????????
        if( id.equals(LoginActivity.user_ac)) {
            holder.delete.setVisibility(View.VISIBLE);
            Log.i("recyclerview","??? ?????????: "+id+"???????????????: "+LoginActivity.user_ac);
        }
        else {
            holder.delete.setVisibility(View.GONE);
        }
        //??????
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                builder.setNegativeButton("??????", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                builder.setMessage("????????? ?????????????????????????").setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Log.i("delete", "?????? ????????????" + code);

                                Api api = Api.Factory.INSTANCE.create();

                                api.deletecmt(code).enqueue(new Callback<DeleteCmt>() {
                                    @Override
                                    public void onResponse(Call<DeleteCmt> call, Response<DeleteCmt> response) {
                                        Log.i("delete", "??????" + response);
                                        datas.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, datas.size());
                                        Log.i("delete", "????????? ??????");
                                    }
                                    @Override
                                    public void onFailure(Call<DeleteCmt> call, Throwable t) {
                                        Log.i("delete",t.getMessage());
                                    }
                                });
                            }
                        }
                ).create();
                builder.show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    public class CmtViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv;
        public TextView cmt, title, day;
        public ImageButton delete;

        public CmtViewHolder(@NonNull View itemView) {
            super(itemView);

            iv = itemView.findViewById(R.id.img);
            cmt = itemView.findViewById(R.id.cmt);
            title = itemView.findViewById(R.id.title);
            day = itemView.findViewById(R.id.day);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}


