package com.baway.monthexam.model.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;

/**
 * Created by 郑文杰 on 2017/11/22.
 */
@Entity
public class User {
    private String loadSize;

    @Generated(hash = 783970311)
    public User(String loadSize) {
        this.loadSize = loadSize;
    }

    @Generated(hash = 586692638)
    public User() {
    }

    public String getLoadSize() {
        return this.loadSize;
    }

    public void setLoadSize(String loadSize) {
        this.loadSize = loadSize;
    }
    

}
