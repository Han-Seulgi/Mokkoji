package com.example.project_test.Mypage.MyContents;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.example.project_test.Content.ContentWithPicture;
import com.example.project_test.Food.FoodContent.FoodActivityContent;
import com.example.project_test.Info.InfoContent.infoActivityContent;
import com.example.project_test.Meet.MeetContent.MeetActivityContent;
import com.example.project_test.R;
import com.example.project_test.Room.RoomContent.RoomContentActivity;
import com.example.project_test.qa.qaContent.qaActivityContent;

import java.util.ArrayList;

public class MyContentsRecyclerAdapter extends RecyclerView.Adapter<MyContentsRecyclerAdapter.MyViewHolder> {
    private ArrayList<MyListData> datas;
    Fragment frg;
    private int MODIFY_POST = 200;
    private int DELETE_POST = 300;

    public void setData(Fragment f, ArrayList<MyListData> list) {
        datas = list;
        frg = f;
    }

    public void updateData(int position, MyListData data){
        datas.set(position, data);
        notifyItemChanged(position);
    }

    public void deleteData(int position) {
        datas.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position, datas.size());
    }

    Intent intent;

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_mycontents, parent, false);

        MyViewHolder holder = new MyViewHolder(view);

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        final MyListData data = datas.get(position);

        final String title = data.getTitle();
        final String id = data.getId();
        final String day = data.getDay();
        final String con = data.getCon();
        final String board = data.getBoard();

        holder.imageView.setImageResource(data.getImg());
        holder.textView.setText(title);
        holder.textView2.setText(data.getBoard());
        holder.textView3.setText(day);
        Log.i("mod", "??????"+data.getBoard());


        holder.itemView.setOnClickListener(new View.OnClickListener() { //??? ?????? ???????????? ???
            @Override
            public void onClick(View v) {
                if(board.equals("??????")) { //??????????????? ??????????????????
                    intent = new Intent(v.getContext(), ContentWithPicture.class);
                }
                else if(board.equals("????????????")) { //??????????????? ??????????????????
                    intent = new Intent(v.getContext(), FoodActivityContent.class);
                }
                else if(board.equals("??????")) { //??????????????? ??????????????????
                    intent = new Intent(v.getContext(), MeetActivityContent.class);
                }
                else if(board.equals("??????Q&A")) { //??????Q&A ??????????????????
                    intent = new Intent(v.getContext(), qaActivityContent.class);
                }
                else if (board.equals("????????????")){ //??????????????? ??????????????????
                    intent = new Intent(v.getContext(), infoActivityContent.class);
                } else if (board.equals("????????????")){
                    intent = new Intent(v.getContext(), RoomContentActivity.class);
                    intent.putExtra("??????", data.getCode());
                }
                else ;
                intent.putExtra("??????", title); //???????????? ??????
                intent.putExtra("?????????", id);
                intent.putExtra("??????", day);
                intent.putExtra("??????", con);
                intent.putExtra("requestmod", MODIFY_POST);
                intent.putExtra("requestdel", DELETE_POST);
                intent.putExtra("position", position);
                frg.startActivityForResult(intent, 777);
            }
        });
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView;//??????..?
        public TextView textView;//??????
        public TextView textView2;//????????? ??????
        public TextView textView3;//??????

        public MyViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.img);
            this.textView = view.findViewById(R.id.title);
            this.textView2 = view.findViewById(R.id.board);
            this.textView3 = view.findViewById(R.id.day);
        }
    }
}
