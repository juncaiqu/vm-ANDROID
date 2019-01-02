package com.kdx.core.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

import com.kdx.core.R;

public class FileAsyncTask extends AsyncTask<Void, Void, Boolean> {
    private ProgressDialog dialog = null;
    private AsyncTaskCallback asyncTaskCallback;
    public FileAsyncTask( Context context) {
        dialog = new ProgressDialog(context);
    }


    public void setAsyncTaskCallback(AsyncTaskCallback asyncTaskCallback) {
        this.asyncTaskCallback = asyncTaskCallback;
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        if(asyncTaskCallback != null){
            return asyncTaskCallback.doInBackground();
        }
        return false;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        dialog.dismiss();
    }

    @Override
    protected void onPreExecute() {
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);// 设置进度条的形式为圆形转动的进度条
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("提示");
        dialog.setMessage("正在拷贝文件");
        dialog.show();
    }
    interface AsyncTaskCallback{
        boolean doInBackground();
    }
}