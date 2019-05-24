package com.thunder.ktv.qiniuuploadtest;

import android.databinding.DataBindingUtil;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.common.QiniuException;
import com.qiniu.common.Zone;
import com.qiniu.storage.BucketManager;
import com.qiniu.storage.Configuration;
import com.qiniu.util.Auth;
import com.thunder.ktv.qiniuuploadtest.databinding.ActivityMainBinding;

import java.io.File;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    BucketManager bucketManager;
    Auth auth;
    private int threadNum = 30;
    private static UpladInfo upladInfo;
    private static final String AccessKey = "hzytXmzU2iBvbLCv9oOL4xg40_RERxo9kDWYNRXf";
    private static final String SecretKey = "iw2WA9UOQznNnUoZtx0bqbZ1oVNGUh1euy-TfU_B";
    private static final String Bucket = "hello7niu";
    private static final String TAG = "MainActivity";
    //    private static final String token = "hzytXmzU2iBvbLCv9oOL4xg40_RERxo9kDWYNRXf:eqo44MRTVllV9H3I7fwdyQXGKig=:eyJzY29wZSI6ImhlbGxvN25pdSIsImRlYWRsaW5lIjoxNTU4Njg2OTQ5fQ==";
    private String token;
    private TextView textView;
    ActivityMainBinding mainBinding;
    ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        viewModel = new ViewModel();
        mainBinding.setModel(viewModel);
        initDate();
    }

    private void initDate() {
        upladInfo = new UpladInfo();
        auth = Auth.create(AccessKey, SecretKey);
        token = auth.uploadToken(Bucket);
        Log.d(TAG, "onCreate: token == " + token);
        bucketManager = new BucketManager(auth, new Configuration(Zone.zone0()));

        for (int i = 0; i < threadNum; i++) {
            new UploadThread(i).start();
        }
    }

    private void updateView() {
        String info = "上传线程数:" + threadNum + "\n" + upladInfo;
        viewModel.setInfo(info);
    }

    @Override
    public void onClick(View v) {
        viewModel.changeUpload();
        updateView();
    }

    class UploadThread extends Thread {
        private int index = 0;

        public UploadThread(int index) {
            this.index = index;
        }

        @Override
        public void run() {
            super.run();
            File file = new File("/sdcard/Music/test.mp3");
            String upladFileName = index + "_" + file.getName();
            while (true) {
                while (!viewModel.isUpload()){
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                upladInfo.plusUploadNum();
                UploadManager uploadManager = new UploadManager();
                ResponseInfo info = uploadManager.syncPut(file, upladFileName, token, null);
                boolean isOk = (info != null) ? info.isOK() : false;
                Log.d(TAG, "run: " + info + isOk);
                if (isOk) {
                    upladInfo.plusUploadOKNum();
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    try {
                        bucketManager.delete(Bucket, upladFileName);
                    } catch (QiniuException e) {
                        e.printStackTrace();
                        Log.d(TAG, "run: delete " + upladFileName + "失败");
                        break;
                    }
                }
                updateView();
            }
            threadNum--;
            updateView();
        }
    }
}
