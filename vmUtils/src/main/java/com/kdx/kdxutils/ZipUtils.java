package com.kdx.kdxutils;

import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

public class ZipUtils {
	
	private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

	/**
	 * 批量压缩文件（夹）
	 * 
	 * @param resFilePath
	 *            要压缩的文件（夹）路径
	 * @param zipFilePath
	 *            生成的压缩文件文件路径
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	public static Boolean zipFiles(String resFilePath, String zipFilePath) {
		ZipOutputStream zipout = null;
		try {
			File resFile = new File(resFilePath);
			String tempZipFilePath = zipFilePath + ".temp";
			File tempZipFile = new File(tempZipFilePath);
			File saveZipDir = new File(tempZipFile.getParent());
			if (!saveZipDir.exists()) {
				boolean mkResult = saveZipDir.mkdirs();
				if (!mkResult) {
					return false;
				}
			}
			zipout = new ZipOutputStream(
					new BufferedOutputStream(new FileOutputStream(tempZipFile),
							BUFF_SIZE));
			Log.i("zip_result",
					"resFile.getAbsolutePath() = " + resFile.getAbsolutePath());
			zipFile(resFile, zipout, "");
			Log.i("zip_result", "zipFiles finish ");
			tempZipFile.renameTo(new File(zipFilePath));
			zipout.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally {

		}
		return true;
	}

	/**
	 * 解压缩一个文件
	 * 
	 * @param zipFileName
	 *            压缩文件
	 * @param folderPath
	 *            解压缩的目标目录
	 * @return
	 * @throws IOException
	 *             当解压缩过程出错时抛出
	 */
	public static boolean unZipFile(String zipFileName, String folderPath) {
		System.out.println("unZipFile start");
		byte[] buf = new byte[512];
		int readedBytes;
		try {
			FileOutputStream fileOut;
			File folder = new File(folderPath);
			if (!folder.exists()) {
				boolean mkResult = folder.mkdirs();
				if (!mkResult) {
					return false;
				}
			}
			InputStream inputStream;
			ZipFile zipFile = new ZipFile(zipFileName);

			Log.i("zip_result", "unZipfile == " + zipFileName);
			for (Enumeration entries = zipFile.entries(); entries
					.hasMoreElements();) {
				ZipEntry entry = (ZipEntry) entries.nextElement();
				File file = new File(folderPath + File.separator
						+ entry.getName());
				if (entry.isDirectory()) {
					file.mkdirs();
				} else {
					// 如果指定文件的目录不存在,则创建之
					File parent = file.getParentFile();
					if (!parent.exists()) {
						parent.mkdirs();
					}
					inputStream = zipFile.getInputStream(entry);
					fileOut = new FileOutputStream(file);
					while ((readedBytes = inputStream.read(buf)) > 0) {
						fileOut.write(buf, 0, readedBytes);
					}
					fileOut.close();
					inputStream.close();
				}
			}
			zipFile.close();
		} catch (ZipException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * 压缩文件
	 * 
	 * @param resFile
	 *            需要压缩的文件（夹）
	 * @param zipout
	 *            压缩的目的文件
	 * @param rootpath
	 *            压缩的文件路径
	 * @throws FileNotFoundException
	 *             找不到文件时抛出
	 * @throws IOException
	 *             当压缩过程出错时抛出
	 */
	private static void zipFile(File resFile, ZipOutputStream zipout,
			String rootpath) throws IOException {
		rootpath = rootpath
				+ (rootpath.trim().length() == 0 ? "" : File.separator)
				+ resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");

		Log.i("zip_result",
				"zipFile -- resFile.getAbsolutePath() = "
						+ resFile.getAbsolutePath() + "---rootpath = "
						+ rootpath);

		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
				zipFile(file, zipout, rootpath);
			}
			Log.i("zip_result", "zipFile -- finish");
		} else {
			byte buffer[] = new byte[BUFF_SIZE];
			BufferedInputStream in = new BufferedInputStream(
					new FileInputStream(resFile), BUFF_SIZE);
			zipout.putNextEntry(new ZipEntry(rootpath));
			int realLength;
			while ((realLength = in.read(buffer)) != -1) {
				zipout.write(buffer, 0, realLength);
			}
			in.close();
			zipout.flush();
			zipout.closeEntry();
		}
	}

	public static byte[] getBytes(InputStream is) throws Exception {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = is.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		is.close();
		bos.flush();
		byte[] result = bos.toByteArray();
		return result;
	}

	/**
	 * 
	 * @param files
	 *            要压缩的文件集合
	 * @param zipFilePath
	 *            生成zip文件的绝对路径
	 * @return
	 */
	public static File zipFiles(List<File> files, String zipFilePath) {
		File tempZipFile = null;
		try {
			tempZipFile = new File(zipFilePath);
			byte[] buf = new byte[1024];
			// ZipOutputStream类：完成文件或文件夹的压缩
			ZipOutputStream out = new ZipOutputStream(new FileOutputStream(
					tempZipFile));
			for (int i = 0; i < files.size(); i++) {
				zipFile(files.get(i), out, "");
			}
			out.close();
			System.out.println("压缩完成.");
		} catch (Exception e) {
			e.printStackTrace();
			StringBuffer sb = new StringBuffer();
	        Writer writer = new StringWriter();
	        PrintWriter pw = new PrintWriter(writer);
	        e.printStackTrace(pw);
	       
	        Throwable cause = e.getCause();
	        while (cause != null) {
	                cause.printStackTrace(pw);
	                cause = cause.getCause();
	        }
	        pw.close();
	        String result = writer.toString();
	        sb.append(result);
//	        System.out.println("mUncaughtExceptionHandler ---- ex === " + sb);
		}
		return tempZipFile;
	}


}
