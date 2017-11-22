package com.baway.monthexam.model;

import com.baway.monthexam.model.bean.JavaBean;
import com.baway.monthexam.net.Api;
import com.baway.monthexam.net.ApiService;
import com.baway.monthexam.net.OnFinish;
import com.baway.monthexam.net.RetrofitUtil;

import rx.Observable;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by 郑文杰 on 2017/11/22.
 */

public class DataModel {

    public void getData(final OnFinish onFinish, String uri){
        ApiService apiService = RetrofitUtil.getInstance().getApiService(Api.url, ApiService.class);
        Observable<JavaBean> observable = apiService.getVideoData(uri);
        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<JavaBean>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(JavaBean javaBean) {
                        onFinish.onSuccess(javaBean);
                    }
                });
    }
}
