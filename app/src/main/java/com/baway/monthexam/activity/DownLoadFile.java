package com.baway.monthexam.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;

import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by 郑文杰 on 2017/11/22.
 */

public class DownLoadFile {

    private static final String SP_NAME = "download_file";
    private static final String CURR_LENGTH = "curr_length";
    private static final int DEFAULT_THREAD_COUNT = 4;//默认下载线程数
    //以下为线程状态
    private static final String DOWNLOAD_INIT = "1";
    private static final String DOWNLOAD_ING = "2";
    private static final String DOWNLOAD_PAUSE = "3";

    private Context mContext;

    private String loadUrl;//网络获取的url
    private String filePath;//下载到本地的path
    private int threadCount = DEFAULT_THREAD_COUNT;//下载线程数

    private int fileLength;//文件总大小
    //使用volatile防止多线程不安全
    private volatile int currLength;//当前总共下载的大小
    private volatile int runningThreadCount;//正在运行的线程数
    private Thread[] mThreads;
    private String stateDownload = DOWNLOAD_INIT;//当前线程状态

    private DownLoadListener mDownLoadListener;

    public void setOnDownLoadListener(DownLoadListener mDownLoadListener) {
        this.mDownLoadListener = mDownLoadListener;
    }

    interface DownLoadListener {
        //返回当前下载进度的百分比
        void getProgress(int progress);

        void onComplete();

        void onFailure();
    }

    public DownLoadFile(Context mContext, String loadUrl, String filePath) {
        this(mContext, loadUrl, filePath, DEFAULT_THREAD_COUNT, null);
    }

    public DownLoadFile(Context mContext, String loadUrl, String filePath, DownLoadListener mDownLoadListener) {
        this(mContext, loadUrl, filePath, DEFAULT_THREAD_COUNT, mDownLoadListener);
    }

    public DownLoadFile(Context mContext, String loadUrl, String filePath, int threadCount) {
        this(mContext, loadUrl, filePath, threadCount, null);
    }

    public DownLoadFile(Context mContext, String loadUrl, String filePath, int threadCount, DownLoadListener mDownLoadListener) {
        this.mContext = mContext;
        this.loadUrl = loadUrl;
        this.filePath = filePath;
        this.threadCount = threadCount;
        runningThreadCount = 0;
        this.mDownLoadListener = mDownLoadListener;
    }

    /**
     * 开始下载
     */
    protected void downLoad() {
        //在线程中运行，防止anr
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //初始化数据
                    if (mThreads == null)
                        mThreads = new Thread[threadCount];

                    //建立连接请求
                    URL url = new URL(loadUrl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(5000);
                    conn.setRequestMethod("GET");
                    int code = conn.getResponseCode();//获取返回码
                    if (code == 200) {//请求成功,根据文件大小开始分多线程下载
                        fileLength = conn.getContentLength();
                        RandomAccessFile raf = new RandomAccessFile(filePath, "rwd");
                        raf.setLength(fileLength);
                        raf.close();
                        //计算各个线程下载的数据段
                        int blockLength = fileLength / threadCount;

                        SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
                        //获取上次取消下载的进度，若没有则返回0
                        currLength = sp.getInt(CURR_LENGTH, 0);
                        for (int i = 0; i < threadCount; i++) {
                            //开始位置，获取上次取消下载的进度，默认返回i*blockLength,即第i个线程开始下载的位置
                            int startPosition = sp.getInt(SP_NAME + (i + 1), i * blockLength);
                            //结束位置，-1是为了防止上一个线程和下一个线程重复下载衔接处数据
                            int endPosition = (i + 1) * blockLength - 1;
                            //将最后一个线程结束位置扩大，防止文件下载不完全，大了不影响，小了文件失效
                            if ((i + 1) == threadCount)
                                endPosition = endPosition * 2;

                            mThreads[i] = new DownThread(i + 1, startPosition, endPosition);
                            mThreads[i].start();
                        }
                    } else {
                        handler.sendEmptyMessage(FAILURE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.sendEmptyMessage(FAILURE);
                }
            }
        }).start();
    }

    /**
     * 取消下载
     */
    protected void cancel() {
        if (mThreads != null) {
            //若线程处于等待状态，则while循环处于阻塞状态，无法跳出循环，必须先唤醒线程，才能执行取消任务
            if (stateDownload.equals(DOWNLOAD_PAUSE))
                onStart();
            for (Thread dt : mThreads) {
                ((DownThread) dt).cancel();
            }
        }
    }

    /**
     * 暂停下载
     */
    protected void onPause() {
        if (mThreads != null)
            stateDownload = DOWNLOAD_PAUSE;
    }

    /**
     * 继续下载
     */
    protected void onStart() {
        if (mThreads != null)
            synchronized (DOWNLOAD_PAUSE) {
                stateDownload = DOWNLOAD_ING;
                DOWNLOAD_PAUSE.notifyAll();
            }
    }

    protected void onDestroy() {
        if (mThreads != null)
            mThreads = null;
    }

    private class DownThread extends Thread {

        private boolean isGoOn = true;//是否继续下载
        private int threadId;
        private int startPosition;//开始下载点
        private int endPosition;//结束下载点
        private int currPosition;//当前线程的下载进度

        private DownThread(int threadId, int startPosition, int endPosition) {
            this.threadId = threadId;
            this.startPosition = startPosition;
            currPosition = startPosition;
            this.endPosition = endPosition;
            runningThreadCount++;
        }

        @Override
        public void run() {
            SharedPreferences sp = mContext.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
            try {
                URL url = new URL(loadUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");
                conn.setRequestProperty("Range", "bytes=" + startPosition + "-" + endPosition);
                conn.setConnectTimeout(5000);
                //若请求头加上Range这个参数，则返回状态码为206，而不是200
                if (conn.getResponseCode() == 206) {
                    InputStream is = conn.getInputStream();
                    RandomAccessFile raf = new RandomAccessFile(filePath, "rwd");
                    raf.seek(startPosition);//跳到指定位置开始写数据
                    int len;
                    byte[] buffer = new byte[1024];
                    while ((len = is.read(buffer)) != -1) {
                        //是否继续下载
                        if (!isGoOn)
                            break;
                        //回调当前进度
                        if (mDownLoadListener != null) {
                            currLength += len;
                            int progress = (int) ((float) currLength / (float) fileLength * 100);
                            handler.sendEmptyMessage(progress);
                        }

                        raf.write(buffer, 0, len);
                        //写完后将当前指针后移，为取消下载时保存当前进度做准备
                        currPosition += len;
                        synchronized (DOWNLOAD_PAUSE) {
                            if (stateDownload.equals(DOWNLOAD_PAUSE)) {
                                DOWNLOAD_PAUSE.wait();
                            }
                        }
                    }
                    is.close();
                    raf.close();
                    //线程计数器-1
                    runningThreadCount--;
                    //若取消下载，则直接返回
                    if (!isGoOn) {
                        //此处采用SharedPreferences保存每个线程的当前进度，和三个线程的总下载进度
                        if (currPosition < endPosition) {
                            sp.edit().putInt(SP_NAME + threadId, currPosition).apply();
                            sp.edit().putInt(CURR_LENGTH, currLength).apply();
                        }
                        return;
                    }
                    if (runningThreadCount == 0) {
                        sp.edit().clear().apply();
                        handler.sendEmptyMessage(SUCCESS);
                        handler.sendEmptyMessage(100);
                        mThreads = null;
                    }
                } else {
                    sp.edit().clear().apply();
                    handler.sendEmptyMessage(FAILURE);
                }
            } catch (Exception e) {
                sp.edit().clear().apply();
                e.printStackTrace();
                handler.sendEmptyMessage(FAILURE);
            }
        }

        public void cancel() {
            isGoOn = false;
        }
    }

    private final int SUCCESS = 0x00000101;
    private final int FAILURE = 0x00000102;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {

            if (mDownLoadListener != null) {
                if (msg.what == SUCCESS) {
                    mDownLoadListener.onComplete();
                } else if (msg.what == FAILURE) {

                    mDownLoadListener.onFailure();
                } else {
                    mDownLoadListener.getProgress(msg.what);
                }
            }
        }
    };
}
