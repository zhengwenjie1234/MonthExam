package com.baway.monthexam.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import com.baway.monthexam.R;
import com.baway.monthexam.model.bean.JavaBean;

import java.util.List;

/**
 * Created by 郑文杰 on 2017/11/22.
 */

public class MyAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<JavaBean.DataBean> list;
    private Context context;
    private OnClickItem onClickItem;
    public interface OnClickItem{
        void onItemClick(int position);
        void onLongItemClick(int position);
    }
    public void setOnClickItem(OnClickItem onClickItem){
        this.onClickItem=onClickItem;
    }
    public MyAdapter(List<JavaBean.DataBean> list, Context context) {
        this.list = list;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {
        MyViewHolder myViewHolder= (MyViewHolder) holder;
        JavaBean.DataBean dataBean = list.get(position);
        myViewHolder.tvTitle.setText(dataBean.title);
        myViewHolder.video.setVideoPath(dataBean.vedio_url);
        MediaController mediaController = new MediaController(context);
        myViewHolder.video.setMediaController(mediaController);
        mediaController.setAnchorView(myViewHolder.video);
        mediaController.requestFocus();
        myViewHolder.video.start();
        myViewHolder.ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickItem.onItemClick(position);
            }
        });
        myViewHolder.ll.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                onClickItem.onLongItemClick(position);
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return list!=null?list.size():0;
    }
    class MyViewHolder extends RecyclerView.ViewHolder{

        private final TextView tvTitle;
        private final VideoView video;
        private final LinearLayout ll;

        public MyViewHolder(View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            video = itemView.findViewById(R.id.video);
            ll = itemView.findViewById(R.id.ll);
        }
    }
}
