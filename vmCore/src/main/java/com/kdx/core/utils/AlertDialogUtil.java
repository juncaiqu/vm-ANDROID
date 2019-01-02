package com.kdx.core.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.kdx.core.R;

/**
 * Author  :qujuncai
 * DATE    :18/12/17
 * Email   :qjchzq@163.com
 */
public class AlertDialogUtil {
    private static AlertDialogUtil mAlertDialogUtil;
    public static AlertDialog.Builder builder;
    private AlertDialog dialog;

    private AlertDialogUtil(){}
    public static AlertDialogUtil getInstance(Context mContent){
        if (mAlertDialogUtil==null){
            synchronized(AlertDialogUtil.class){
                if (mAlertDialogUtil==null)
                    mAlertDialogUtil=new AlertDialogUtil();
                builder = new AlertDialog.Builder(mContent);
            }
        }
        return mAlertDialogUtil;
    }

    /**
     * 创建一个对话框
     */
    public void showDialog(String title,String msg,String posBtn,String negBtn,DialogInterface.OnClickListener pListenner,DialogInterface.OnClickListener nListenner) {
        dismiss();
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(posBtn,pListenner);
        // 点击取消
        builder.setNegativeButton(negBtn,nListenner);
        dialog = builder.create();
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    public void showDialog(String title,String msg,String posBtn,String negBtn,DialogInterface.OnClickListener pListenner) {
        dismiss();
        builder.setIcon(R.mipmap.ic_launcher);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(posBtn,pListenner);
        dialog = builder.create();
        dialog.setCancelable(true);
        dialog.setCanceledOnTouchOutside(true);
        dialog.show();
    }
    public void dismiss(){
        if(dialog != null && dialog.isShowing()){
            dialog.dismiss();
        }
    }

}
