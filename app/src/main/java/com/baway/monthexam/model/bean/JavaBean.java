package com.baway.monthexam.model.bean;

import java.util.List;

/**
 * Created by 郑文杰 on 2017/11/22.
 */

public class JavaBean {

    /**
     * code : 200
     * data : [{"content":"习近平举行仪式欢迎加蓬总统访华","id":10000,"image_url":"http://pic32.nipic.com/20130817/9745430_101836881000_2.jpg","title":"今日头条","type":1,"vedio_url":"http://2449.vod.myqcloud.com/2449_bfbbfa3cea8f11e5aac3db03cda99974.f20.mp4"},{"content":"习近平举行仪式欢迎加蓬总统访华","id":10001,"image_url":"http://pic15.nipic.com/20110630/6322714_105943746342_2.jpg","title":"往期故事","type":2,"vedio_url":"https://wdl.wallstreetcn.com/41aae4d2-390a-48ff-9230-ee865552e72d.mp4"},{"content":"这个市的书记市长双双被约谈 引发震动","id":10002,"image_url":"http://pic48.nipic.com/file/20140916/2531170_195153248000_2.jpg","title":"讨论天下","type":3,"vedio_url":"http://video.jiecao.fm/5/1/%E8%87%AA%E5%8F%96%E5%85%B6%E8%BE%B1.mp4"},{"content":"\u201c乔丹\u201d商标是否侵权 4年官司迎最高法判决 调查","id":10003,"image_url":"http://img.taopic.com/uploads/allimg/140626/240469-1406261S24553.jpg","title":"我的珍爱","type":4,"vedio_url":"http://2449.vod.myqcloud.com/2449_bfbbfa3cea8f11e5aac3db03cda99974.f20.mp4"},{"content":"黑龙江稻农收入暴涨组团进城买房 有人买十多套","id":10004,"image_url":"http://pic77.nipic.com/file/20150911/21721561_155058651000_2.jpg","title":"我的未来","type":5,"vedio_url":"http://video.jiecao.fm/5/1/%E8%87%AA%E5%8F%96%E5%85%B6%E8%BE%B1.mp4"},{"content":"印尼6.4级地震致97死 专家称当地防震意识差","id":10005,"image_url":"http://img4.duitang.com/uploads/item/201603/18/20160318103156_cziuY.jpeg","title":"东芝","type":6,"vedio_url":"http://ips.ifeng.com/video19.ifeng.com/video09/2014/06/16/1989823-102-086-0009.mp4"}]
     * msg : 成功
     */

    public int code;
    public String msg;
    public List<DataBean> data;

    public static class DataBean {
        /**
         * content : 习近平举行仪式欢迎加蓬总统访华
         * id : 10000
         * image_url : http://pic32.nipic.com/20130817/9745430_101836881000_2.jpg
         * title : 今日头条
         * type : 1
         * vedio_url : http://2449.vod.myqcloud.com/2449_bfbbfa3cea8f11e5aac3db03cda99974.f20.mp4
         */

        public String content;
        public int id;
        public String image_url;
        public String title;
        public int type;
        public String vedio_url;
    }
}
