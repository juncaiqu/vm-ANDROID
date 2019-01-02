package com.kdx.cmdservice.net;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.text.TextUtils;
import android.util.Log;

import com.kdx.kdxutils.FileUtil;
import com.kdx.kdxutils.KdxFileUtil;

public class HttpDownload {
	public static final String TAG = "HttpDownload";
	private static HttpDownload download = new HttpDownload();
//	public static final String FILEDIR = KdxFileUtil.getTempDir();
	private HttpDownload() {
	}

	public static HttpDownload getInstance() {
		return download;
	}

	/**
	 * @param httpUri
	 *            请求文件的绝对路径
	 * @param localUri
	 *            本地位置 例: /mnt/sdcard/Ubox/temp/Vclient.apk
	 * @return
	 * @throws ExecutionException
	 * @throws InterruptedException
	 */

	public boolean down(String httpUri, String localUri) {
		boolean isSuccess = false;
		try {
			URL url = new URL(httpUri);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(30*1000);
			int code = conn.getResponseCode();
			if (code == 200) {
				int len = conn.getContentLength();
				RandomAccessFile file = new RandomAccessFile(localUri, "rwd");
				// 设置本地文件大小跟服务器的文件大小一致
				file.setLength(len);

				// 线程1 0~ blocksize 线程2 1*bolocksize ~ 2*blocksize 线程3
				// 2*blocksize ~文件末尾
				// 默认开启3个线程
				int threadnumber = 3;
				int blocksize = len / threadnumber;

				ExecutorService service = Executors.newFixedThreadPool(threadnumber);
				List<Future> futures = new ArrayList<Future>();
				for (int i = 0; i < threadnumber; i++) {
					int startposition = i * blocksize;
					int endpositon = (i + 1) * blocksize;
					if (i == (threadnumber - 1)) {
						// 最后一个线程
						endpositon = len;
					}
					Future<Boolean> future = service.submit(new DownLoadRunnable(i, httpUri, startposition, endpositon, localUri)); // 不会阻塞
					futures.add(future);
				}
				service.shutdown(); // 线程停止 : 不会影响之前提交的线程运行
				for (Future future : futures) {
					if (!(Boolean) future.get()) { // 阻塞
						return false;
					}
					isSuccess = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			isSuccess = false;
		}
		return isSuccess;
	}

	private class DownLoadRunnable implements Callable<Boolean> {

		private int threadid;
		private String filepath;
		private int startposition;
		private int endpositon;
		private String localUri;

		public DownLoadRunnable(int threadid, String filepath,
				int startposition, int endpositon, String localUri) {
			this.threadid = threadid;
			this.filepath = filepath;
			this.startposition = startposition;
			this.endpositon = endpositon;
			this.localUri = localUri;
		}

		@Override
		public Boolean call() {
			int currentPostion = 0;
			String filename = localUri.substring(localUri.lastIndexOf("/") + 1, localUri.lastIndexOf("."));
			File postionfile = new File(KdxFileUtil.getTempDir() + filename + "_" + threadid + ".txt");
			Log.d(TAG, "postionfile name is " + postionfile.getName());
			try {
				URL url = new URL(filepath);
				HttpURLConnection conn = (HttpURLConnection) url.openConnection();

				if (postionfile.exists()) {
					String str = FileUtil.readFile(postionfile.getAbsolutePath());
					if (!TextUtils.isEmpty(str)) {
						int newstartposition = Integer.parseInt(str);
						if (newstartposition > startposition) {
							startposition = newstartposition;
						}
					}
				}
				Log.i(TAG, "线程" + threadid + "正在下载 " + "开始位置 : "+ startposition + "结束位置 " + endpositon);
				conn.setRequestProperty("Range", "bytes=" + startposition + "-" + endpositon);
				conn.setRequestMethod("GET");
				conn.setConnectTimeout(30*1000);
				conn.setRequestProperty("User-Agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
				InputStream is = conn.getInputStream();
				RandomAccessFile file = new RandomAccessFile(localUri, "rwd");
				// 设置 数据从文件哪个位置开始写
				file.seek(startposition);
				byte[] buffer = new byte[1024];
				int len = 0;
				// 代表当前读到的服务器数据的位置 ,同时这个值已经存储的文件的位置
				currentPostion = startposition;

				while ((len = is.read(buffer)) != -1) {
					file.write(buffer, 0, len);
					currentPostion += len;
					// Log.d(TAG, "线程 "+threadid + "currentPostion = "
					// +currentPostion);
				}
				file.close();
				conn.disconnect();
				Log.i(TAG, "线程" + threadid + "下载完毕");
				// 当线程下载完毕后 把文件删除掉
				if (postionfile.exists()) {
					postionfile.delete();
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				Log.e(TAG, "下载异常保存当前进度 -> " + currentPostion);
				setDownloadPosition(filename, threadid, currentPostion);
				return false;
			}
		}
	}

	/**
	 * 下载数据
	 */
	public boolean downloadRes(String url, String file) {
		HttpURLConnection conn = null;
		FileOutputStream threadFile = null;
		InputStream inStream = null;
		
		File f = new File(file);
		String fileName = f.getName();
		String fileDir = f.getParent();
		
		if(fileName.startsWith("/")){
			fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
		}

		File dir = new File(fileDir);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		
		File existFile = new File(fileDir, fileName);
		if(existFile.exists()){
			return true;
		}
		// 开始单个文件的下载
		File tempFile = new File(fileDir, fileName + ".tmp");
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(30*1000);
			conn.setRequestProperty("Accept", "*/*");
			conn.setRequestProperty("Accept-Language", "zh-CN");
			conn.setRequestProperty("Charset", "UTF-8");
			conn.setRequestProperty("Connection", "Keep-Alive");
			conn.setRequestProperty("Range", "bytes=" + tempFile.length() + "-");
			conn.connect();
			inStream = conn.getInputStream();
			byte[] buffer = new byte[1024];
			int offset = 0;
			threadFile = new FileOutputStream(tempFile, true);
			while ((offset = inStream.read(buffer, 0, 1024)) != -1) {
				threadFile.write(buffer, 0, offset);
			}
			File newFile = new File(fileDir, fileName);
			if (newFile.exists()) {
				newFile.delete();
			}
			tempFile.renameTo(newFile);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}finally {
			try {
				if(threadFile != null){threadFile.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(inStream != null){inStream.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
			conn.disconnect();
		}
	}
	
	/**
	 * 更新文件中的进度值
	 * 
	 * @param
	 * @param pos
	 */
	private static void setDownloadPosition(String fileName, int threadid, int pos) {

		FileOutputStream fos = null;
		try {
			File file = createFile(fileName, threadid);
			fos = new FileOutputStream(file);
			String postion = pos + "";
			fos.write(postion.getBytes());
			fos.flush();
		} catch (IOException e) {
			e.printStackTrace();
			Log.e(TAG, "文件进度存储异常");
		}finally {
			try {
				if(fos != null){fos.close();}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static File createFile(String filename, int threadid) {
		File fileDir = new File(KdxFileUtil.getTempDir());
		if (!fileDir.exists()) {
			fileDir.mkdir();
		}
		return new File(fileDir, filename + "_" + threadid + ".txt");
	}

}
