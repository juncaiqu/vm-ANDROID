package com.kdx.kdxutils;

import android.app.ActivityManager;
import android.content.Context;
import android.os.StatFs;
import android.text.format.Formatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SdcardUtils {
/**
 * 获取sdcard可用空间
 * @return
 */
	public static String getAvailaSize(String path) {
		// 取得sdcard文件路径
		StatFs statfs = new StatFs(path);
		// 获取block的SIZE
		long blocSize = statfs.getBlockSize();
		// 获取BLOCK数量
		long totalBlocks = statfs.getBlockCount();
		// 己使用的Block的数量
		long availaBlock = statfs.getAvailableBlocks();

		return formatM(availaBlock * blocSize);
	}
	/**
	 * 获取sdcard全部空间大小
	 * @return
	 */
	public static String getTotalSize(String path) {
		// 取得sdcard文件路径
		StatFs statfs = new StatFs(path);
		// 获取block的SIZE
		long blocSize = statfs.getBlockSize();
		// 获取BLOCK数量
		long totalBlocks = statfs.getBlockCount();
		// 己使用的Block的数量
		long availaBlock = statfs.getAvailableBlocks();
		
		return formatM(totalBlocks * blocSize);
	}
	public static String getSdRoom() {
		String availaSize = getAvailaSize(KdxFileUtil.getSdcardDir());
		String totalSize = getTotalSize(KdxFileUtil.getSdcardDir());
		return availaSize + "/" + totalSize;
	}
	/**
	 * 返回数据大小对应的文本
	 */
	public static String formatM(long length) {
		String show = "";
		int sub_index = 0;
		if (length >= 1073741824) {
			sub_index = (String.valueOf((float) length / 1073741824))
					.indexOf(".");
			show = ((float) length / 1073741824 + "000").substring(0,
					sub_index + 3) + "GB";
		} else if (length >= 1048576) {
			sub_index = (String.valueOf((float) length / 1048576)).indexOf(".");
			show = ((float) length / 1048576 + "000").substring(0,
					sub_index + 3) + "MB";
		} else if (length >= 1024) {
			sub_index = (String.valueOf((float) length / 1024)).indexOf(".");
			show = ((float) length / 1024 + "000").substring(0, sub_index + 3)
					+ "KB";
		} else if (length < 1024) {
			show = String.valueOf(length) + "B";
		}

		return show;
	}

	public static String getAvailMemory(Context context) {
		ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		ActivityManager.MemoryInfo mi = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(mi);
		// mi.availMem; 当前系统的可用内存
		return Formatter.formatFileSize(context, mi.availMem);// 将获取的内存大小规格化
	}

	/**
	 * 系统总内存大小
	 *
	 * @param
	 * @return
	 */
	public static String getTotalMemory(Context context) {
		String str1 = "/proc/meminfo";// 系统内存信息文件
		String str2;
		String[] arrayOfString;
		long initial_memory = 0;
		FileReader localFileReader = null;
		BufferedReader localBufferedReader = null;
		try {
			localFileReader = new FileReader(str1);
			localBufferedReader = new BufferedReader(localFileReader, 8192);
			str2 = localBufferedReader.readLine();// 读取meminfo第一行，系统总内存大小
			arrayOfString = str2.split("\\s+");
			initial_memory = Integer.valueOf(arrayOfString[1]).intValue() * 1024L;// 获得系统总内存，单位是KB，乘以1024转换为Byte

		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(localFileReader != null){
				try {
					localFileReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(localBufferedReader != null){
				try {
					localBufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
		return Formatter.formatFileSize(context, initial_memory);// Byte转换为KB或者MB，内存大小规格化
	}
	public static String testSD(String path){
		boolean isTrue = false;
		File file = new File(path);
		try {
			boolean success = file.createNewFile();
			if(success){
				isTrue = true;
			}
			file.delete();

			if(file.exists())
				isTrue = false;

			return isTrue ? "normal" : "exception";
		} catch (IOException e) {
			e.printStackTrace();
			return "exception";
		}
	}

}
