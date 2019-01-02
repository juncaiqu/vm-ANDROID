package com.kdx.brower.net;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.os.SystemClock;

import com.kdx.brower.logger.LoggerSetting;

import org.apache.logging.log4j.Logger;

public class VmBoxConnection {
	private static Logger logger = LoggerSetting.getLogger();
	private ExecutorService workThread = Executors.newFixedThreadPool(1);
	private ResultCallback resultCalback;
	public void setCallBack(ResultCallback resultCallback){
		this.resultCalback = resultCallback;
	}
	public void connectVbox(String urlstr){
		final String address = urlstr;
		workThread.submit(new Runnable() {

			@Override
            public void run() {

				connect(address);
			}
		});
	}

	private synchronized void connect(final String urlstr){
		int count=0;
		while(true){
			HttpURLConnection conn = null;
			try {
				URL url = new URL(urlstr);
				conn = (HttpURLConnection) url.openConnection();
				conn.setConnectTimeout(5000); //设置连接超时为5秒
				conn.setReadTimeout(5000);
				conn.setRequestMethod("GET"); //设定请求方式
				conn.connect();
				if(count % 100 == 0){
					logger.info("launcher --> VmBoxConnection connect() conn.getResponseCode():" + conn.getResponseCode() + ",resultCalback:" + (resultCalback != null));
					count = 0;
				}

				if(resultCalback != null){
					if(conn.getResponseCode() == HttpURLConnection.HTTP_OK){
						resultCalback.onResult(conn.getURL().toExternalForm());
						return;
					}

				}
			} catch (Exception e) {
				if(count % 100 == 0){
					e.printStackTrace();
					count = 0;
				}
				e.printStackTrace();
			} finally {
				if (conn != null) {
					conn.disconnect(); //中断连接
				}
				count++;
			}
			SystemClock.sleep(3000);
		}

	}
}
