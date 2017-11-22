package com.baway.monthexam.activity;

import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.baway.monthexam.R;
import com.baway.monthexam.adapter.MyAdapter;
import com.baway.monthexam.app.FrescoLoad;
import com.baway.monthexam.app.MyApplication;
import com.baway.monthexam.gen.UserDao;
import com.baway.monthexam.model.bean.JavaBean;
import com.baway.monthexam.model.bean.User;
import com.baway.monthexam.presenter.DataPresenter;
import com.baway.monthexam.view.DataView;
import com.youth.banner.Banner;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements DataView {

    @BindView(R.id.banner)
    Banner mBanner;
    @BindView(R.id.rv)
    RecyclerView mRv;
    @BindView(R.id.pb)
    ProgressBar mPb;
    @BindView(R.id.tvInfo)
    TextView mTvInfo;
    private List<String> imgList = new ArrayList<>();
    private DownLoadFile downLoadFile;
    private boolean flag=true;
    private UserDao userDao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        DataPresenter dataPresenter = new DataPresenter(this);
        dataPresenter.relevance("vedio");
        userDao = MyApplication.getInstances().getDaoSession().getUserDao();
    }

    @Override
    public void getData(JavaBean javaBean) {
        final List<JavaBean.DataBean> data = javaBean.data;
        for (int i = 0; i < data.size(); i++) {
            String image_url = data.get(i).image_url;
            imgList.add(image_url);
        }
        mBanner.setImages(imgList).setImageLoader(new FrescoLoad()).setDelayTime(1500).isAutoPlay(true).start();
        MyAdapter adapter = new MyAdapter(data, this);
        mRv.setAdapter(adapter);

        adapter.setOnClickItem(new MyAdapter.OnClickItem() {
            @Override
            public void onItemClick(int position) {

                if (flag){

                    Toast.makeText(MainActivity.this, "开始" + position, Toast.LENGTH_SHORT).show();
                    String vedio_url = data.get(position).vedio_url;
                    String filePath = Environment.getExternalStorageDirectory() + "/" + data.get(position).title + ".mp4";
                    downLoadFile = new DownLoadFile(MainActivity.this, vedio_url, filePath, 3);
                    downLoadFile.setOnDownLoadListener(new DownLoadFile.DownLoadListener() {
                        @Override
                        public void getProgress(int progress) {
                            mPb.setProgress(progress);
                            mTvInfo.setText("当前进度："+progress+"%");
                        }

                        @Override
                        public void onComplete() {
                            Toast.makeText(MainActivity.this,"下载完成",Toast.LENGTH_SHORT).show();
                            mPb.setProgress(0);
                        }

                        @Override
                        public void onFailure() {
                            Toast.makeText(MainActivity.this,"下载失败",Toast.LENGTH_SHORT).show();
                        }
                    });

                    downLoadFile.downLoad();
                }else{
                    downLoadFile.onStart();
                }


            }

            @Override
            public void onLongItemClick(int position) {
                Toast.makeText(MainActivity.this, "暂停" + position, Toast.LENGTH_SHORT).show();
                downLoadFile.onPause();
                flag=false;
                int progress = mPb.getProgress();
                String downloadsize = String.valueOf(progress);
                Log.e("6666", String.valueOf(progress));

                User user = new User(downloadsize+"%");
                userDao.insert(user);
            }
        });

    }

    @Override
    protected void onDestroy() {
        downLoadFile.onDestroy();
        super.onDestroy();
    }
}