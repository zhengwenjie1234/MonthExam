package com.baway.monthexam.app;

import android.app.Application;
import android.database.sqlite.SQLiteDatabase;

import com.baway.monthexam.gen.DaoMaster;
import com.baway.monthexam.gen.DaoSession;
import com.facebook.drawee.backends.pipeline.Fresco;

/**
 * Created by 郑文杰 on 2017/11/22.
 */

public class MyApplication extends Application {
    private static MyApplication instances;
    private DaoSession daoSession;

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        instances=this;
        setDataBase();
    }
    public static MyApplication getInstances(){
        return instances;
    }

    private void setDataBase(){
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "mk.db", null);
        SQLiteDatabase database = helper.getReadableDatabase();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }
    public DaoSession getDaoSession(){
        return daoSession;
    }


}
