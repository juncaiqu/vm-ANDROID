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
import java.io.UnsupportedEncodingException;
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

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}finally {
			if(zipout != null){
				try {
					zipout.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
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
		InputStream inputStream = null;
		FileOutputStream fileOut = null;
		try {

			File folder = new File(folderPath);
			if (!folder.exists()) {
				boolean mkResult = folder.mkdirs();
				if (!mkResult) {
					return false;
				}
			}

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
		}finally {
			FileUtil.closeIn(inputStream);
			FileUtil.closeOut(fileOut);
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
			String rootpath) throws UnsupportedEncodingException {
		rootpath = rootpath
				+ (rootpath.trim().length() == 0 ? "" : File.separator)
				+ resFile.getName();
		rootpath = new String(rootpath.getBytes("8859_1"), "GB2312");

		if (resFile.isDirectory()) {
			File[] fileList = resFile.listFiles();
			for (File file : fileList) {
				zipFile(file, zipout, rootpath);
			}
			Log.i("zip_result", "zipFile -- finish");
		} else {
			byte buffer[] = new byte[BUFF_SIZE];
			BufferedInputStream in = null;
			try {
				in = new BufferedInputStream(new FileInputStream(resFile), BUFF_SIZE);
				zipout.putNextEntry(new ZipEntry(rootpath));
				int realLength;
				while ((realLength = in.read(buffer)) != -1) {
					zipout.write(buffer, 0, realLength);
				}
				zipout.flush();
			} catch (Exception e) {
				e.printStackTrace();
			}finally {
				FileUtil.closeIn(in);
				if(zipout != null){
					try {
						zipout.closeEntry();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}


		}
	}



}
