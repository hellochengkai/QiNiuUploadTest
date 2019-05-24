package com.thunder.ktv.qiniuuploadtest;

public class UpladInfo {
    private int uploadNum = 0;
    private int uploadOKNum = 0;

    public synchronized int getUploadNum() {
        return uploadNum;
    }

    public synchronized void plusUploadNum() {
        this.uploadNum++;
    }

    public synchronized int getUploadOKNum() {
        return uploadOKNum;
    }

    public synchronized void plusUploadOKNum() {
        this.uploadOKNum++;
    }

    @Override
    public synchronized String toString() {
        return "上传统计(Try/Ok)(" + uploadNum + "/" + uploadOKNum + ")";
    }
}
