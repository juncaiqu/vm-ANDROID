package com.kdx.core.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PixelFormat;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.Adapter;
import android.widget.Button;
import android.widget.ListAdapter;

import com.kdx.core.R;

/**
 * 控制显示的Manager
 * @author Administrator
 *
 */
public class PromptManager {
	/**
	 * 显示界面上浮动的Button-->用于返回维护模式界面
	 */
	public static void initFloatView(Context ctx , String msg ,final View view1 , final View view2 ,int position1 , int position2) {
		// 获取WindowManager
		final WindowManager wm = (WindowManager) ctx.getApplicationContext().getSystemService(
				Context.WINDOW_SERVICE);
		// 设置LayoutParams(全局变量）相关参数
		WindowManager.LayoutParams wmParams = new WindowManager.LayoutParams();

		wmParams.type = LayoutParams.TYPE_PHONE; // 设置window type
		wmParams.format = PixelFormat.RGBA_8888; // 设置图片格式，效果为背景透明
		// 设置Window flag
		wmParams.flags = LayoutParams.FLAG_NOT_TOUCH_MODAL
				| LayoutParams.FLAG_NOT_FOCUSABLE;

		// 以屏幕左上角为原点，设置x、y初始值
		wmParams.x = 0;
		wmParams.y = 0;
		// 设置悬浮窗口长宽数据
		wmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
		wmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
		final Button btn = new Button(ctx);
		btn.setText(msg);
		btn.setBackgroundResource(R.mipmap.ic_launcher);
		// 调整悬浮窗口
		wmParams.gravity = position1 | position2;
		// 显示myFloatView图像
		wm.addView(btn, wmParams);

		btn.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				view1.setVisibility(View.VISIBLE);
				view2.setVisibility(View.GONE);
				if (btn != null)
					wm.removeView(btn);
			}
		});
	}
	
	public static void createInfoDialog(Activity context ,Adapter adapter, String title, int width,
			int height, boolean isBig) {
		Dialog dialog = new AlertDialog.Builder(context).setTitle(title)
				.setIcon(R.mipmap.ic_launcher)
				.setAdapter((ListAdapter) adapter, null).create();
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(true);
		dialog.show();

	}
	
	/**
	 * 创建一个显示listView的dialog
	 */
	public static void createListDialog(Activity context ,String[] arr, String title,
			DialogInterface.OnClickListener mOnClickListener) {
		Dialog dialog = new AlertDialog.Builder(context).setTitle(title)
				.setIcon(R.drawable.bt_background).setItems(arr, mOnClickListener)
				.create();
		dialog.show();
	}

}
