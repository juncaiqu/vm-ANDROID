package com.kdx.filemanager.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;


public class Tool {



	public static void delFile(File file, long jet) {
		if (!file.exists()) {
			return;
		} else {
			// 如果file是文件并且属于在被删除的条件
			if (file.isFile() && delFileOfDate(file, jet)) {
				file.delete();
				return;
			}
			if (file.isDirectory()) {
				File[] childFile = file.listFiles();
				if (childFile == null || childFile.length == 0) {
					file.delete();
					return;
				}
				for (File f : childFile) {
					delFile(f, jet);
				}
			}
		}
	}

	/**
	 * 判断是否是数字
	 *
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		return pattern.matcher(str).matches();
	}

	/**
	 * @param file
	 * @param file
	 *            年份相同的月差值
	 * @param jet
	 *            年份不同的月差值
	 * @return
	 */
	public static boolean delFileOfDate(File file, long jet) {
		long lastModified_time = file.lastModified();// 返回文件最后修改时间，是以个long型毫秒数
		long now_time = System.currentTimeMillis();
		long jet_time = now_time - lastModified_time;
		if (lastModified_time > 0 && jet_time > jet)
			return true;
		else
			return false;
	}




	/**
	 * 获取文件
	 *
	 * @param filePath
	 * @return
	 */
	public static List<File> getFiles(String filePath) {
		File file = new File(filePath);
		List<File> list = new ArrayList<File>();
		if (file.exists()) {
			File[] listFiles = file.listFiles();
			if(listFiles == null){
				return null;
			}
			for (File f : listFiles) {
				if (f.isFile()) {
					list.add(f);
				}
			}
		}
		return list;
	}

}
