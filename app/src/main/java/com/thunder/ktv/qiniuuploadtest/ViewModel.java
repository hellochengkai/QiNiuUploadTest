package com.thunder.ktv.qiniuuploadtest;

import android.databinding.BaseObservable;
import android.databinding.Bindable;

public class ViewModel extends BaseObservable {
    private String info;
    private boolean upload = false;

    @Bindable
    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
        notifyChange();
    }

    public void changeUpload() {
        this.upload = !upload;
        notifyChange();
    }

    @Bindable
    public String getButtonInfo() {
        return !upload ?"开始上传":"暂停上传";
    }

    public boolean isUpload() {
        return upload;
    }
}
