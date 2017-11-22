package com.baway.monthexam.net;

import com.baway.monthexam.model.bean.JavaBean;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by 郑文杰 on 2017/11/22.
 */

public interface ApiService {

    //?uri=vedio
    @GET("iYXEPGn4e9c6dafce6e5cdd23287d2bb136ee7e9194d3e9")
    Observable<JavaBean> getVideoData(@Query("uri")String uri);
}
