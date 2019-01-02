package com.kdx.brower.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.locks.ReentrantLock;

import SevenZip.Compression.LZMA.Decoder;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.util.Log;

import com.kdx.brower.logger.LoggerSetting;

import org.apache.logging.log4j.Logger;

public class XWalkUtil {
	private static Logger logger = LoggerSetting.getLogger();
	private static final String[] MANDATORY_LIBRARIES = new String[]{"libxwalkcore.so"};
    private static final String TAG = "launcher";
    public static PackageInfo getPackageInfo(Context context){
		PackageManager manager;
		PackageInfo info = null;
		manager = context.getPackageManager();
		try {
			info = manager.getPackageInfo(context.getPackageName(), 0);
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}
		return info;
	}
	public static void delSo(Context context) {
		File cwModelDir = context.getDir("xwalkcore", Context.MODE_PRIVATE);
		File soFile = new File(cwModelDir, "libxwalkcore.so");
		if(!soFile.exists()){
			logger.info(soFile.getPath()+"not exists");
		}

		/*if (soFile.exists()){
			soFile.delete();
		}*/
	}

	public static  boolean decompress(Context context, InputStream inputStream) {
		ReentrantLock reentrantLock = new ReentrantLock();
		File libDir = context.getDir("xwalkcore", Context.MODE_PRIVATE);
		if (libDir.exists() && libDir.isFile())
			libDir.delete();
		if (!libDir.exists() && !libDir.mkdirs())
			return false;

		reentrantLock.lock();

		for (String library : MANDATORY_LIBRARIES) {
			File tmpfile = null;
			InputStream input = null;
			OutputStream output = null;
			try {
				File outfile = new File(libDir, library);
				tmpfile = new File(libDir, library + ".tmp");
				input = new BufferedInputStream(inputStream);
				output = new BufferedOutputStream(new FileOutputStream(tmpfile));
				decodeWithLzma(input, output);
				tmpfile.renameTo(outfile);
			} catch (Resources.NotFoundException e) {
				return false;
			} catch (Exception e) {
				return false;
			} finally {
				reentrantLock.unlock();

				if (output != null) {
					try {
						output.flush();
					} catch (IOException e) {
					}
					try {
						output.close();
					} catch (IOException e) {
					}
				}
				if (input != null) {
					try {
						input.close();
					} catch (IOException e) {
					}
				}
				tmpfile.delete();
			}
		}
		return true;
	}

	public static void decodeWithLzma(InputStream input, OutputStream output)
			throws IOException {
		final int propSize = 5;
		final int outSizeLength = 8;

		byte[] properties = new byte[propSize];
		if (input.read(properties, 0, propSize) != propSize) {
			throw new EOFException("Input .lzma file is too short");
		}

		Decoder decoder = new Decoder();
		if (!decoder.SetDecoderProperties(properties)) {
			Log.w(TAG, "Incorrect stream properties");
		}

		long outSize = 0;
		for (int i = 0; i < outSizeLength; i++) {
			int v = input.read();
			if (v < 0) {
				Log.w(TAG, "Can't read stream size");
			}
			outSize |= ((long) v) << (8 * i);
		}

		if (!decoder.Code(input, output, outSize)) {
			Log.w(TAG, "Error in data stream");
		}
	}
	public static boolean cpFile(String source, String target) {
		File sourcefile = new File(source);
		File targetFile = new File(target);
		File parentFile = targetFile.getParentFile();
		if(!parentFile.exists()){
			parentFile.mkdirs();
		}
		if(!sourcefile.exists()){
			return false;
		}
		try {
			FileInputStream input = new FileInputStream(sourcefile);
			BufferedInputStream inbuff = new BufferedInputStream(input);

			FileOutputStream out = new FileOutputStream(targetFile);
			BufferedOutputStream outbuff = new BufferedOutputStream(out);

			byte[] b = new byte[1024 * 5];
			int len = 0;
			while ((len = inbuff.read(b)) != -1) {
				outbuff.write(b, 0, len);
				outbuff.flush();
			}

			outbuff.flush();

			inbuff.close();
			outbuff.close();
			out.close();
			input.close();
		} catch (IOException e) {
			return false;
		}
		return true;

	}
}
