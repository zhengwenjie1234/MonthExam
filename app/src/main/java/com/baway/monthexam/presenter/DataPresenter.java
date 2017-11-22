package com.baway.monthexam.presenter;

import com.baway.monthexam.model.DataModel;
import com.baway.monthexam.model.bean.JavaBean;
import com.baway.monthexam.net.OnFinish;
import com.baway.monthexam.view.DataView;

/**
 * Created by 郑文杰 on 2017/11/22.
 */

public class DataPresenter implements OnFinish {

    private DataView dataView;
    private final DataModel dataModel;

    public DataPresenter(DataView dataView) {
        this.dataView=dataView;
        dataModel = new DataModel();
    }

    public void relevance(String uri){
        dataModel.getData(this,uri);
    }

    @Override
    public void onSuccess(JavaBean javaBean) {
        dataView.getData(javaBean);
    }
}
