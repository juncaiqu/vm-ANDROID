package com.kdx.filemanager;

import java.io.File;
import java.io.FileFilter;
import java.util.Calendar;
import java.util.Date;

import android.util.Log;

import com.kdx.filemanager.utils.Tool;
import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.ZipUtils;

public class LogManaRunnable implements Runnable{

		@Override
		public void run() {
			bakLog();
			delLogbak();
			//emmc中保存的log
			Tool.delFile(new File(KdxFileUtil.getLogsDir()), 20 * 24 * 60 * 60000L);//20天
			// 定时清除uboxservice中断点续传遗留的文件
			Tool.delFile(new File(KdxFileUtil.getTempDir()) ,10 * 24 * 60 * 60000);//10天
		}
		public synchronized void bakLog(){
			File logFile = new File(KdxFileUtil.getLogsDir());
			if(logFile.isDirectory()){
				File[] listFiles = logFile.listFiles();
				for(File listFile:listFiles){
					Log.i("qujc", listFile.getPath());
					if(listFile.isDirectory()){
						File[] logFiles = listFile.listFiles(new FileFilter() {
							@Override
							public boolean accept(File pathname) {
								try {
									Calendar lastModifCal = Calendar.getInstance();
									lastModifCal.setTime(new Date(pathname.lastModified()));
									lastModifCal.set(Calendar.HOUR_OF_DAY, 0);
									lastModifCal.set(Calendar.MINUTE, 0);
									lastModifCal.set(Calendar.SECOND, 0);
									lastModifCal.set(Calendar.MILLISECOND, 0);
									Date lastModiftime = lastModifCal.getTime();
									//-----------------------------------------
									
									Calendar destCal = Calendar.getInstance();
									destCal.set(Calendar.HOUR_OF_DAY, 0);
									destCal.set(Calendar.MINUTE, 0);
									destCal.set(Calendar.SECOND, 0);
									destCal.set(Calendar.MILLISECOND, 0);
									destCal.add(Calendar.DAY_OF_YEAR, -6);
									Date destDate = destCal.getTime();
									
									return destDate.after(lastModiftime);
								} catch (Exception e) {
									return false;
								}
							}
						});
						for(File log:logFiles){
							ZipUtils.zipFiles(log.getPath(), KdxFileUtil.getLogsbakDir()+listFile.getName()+"/"+log.getName().replace(".log", ".zip"));
						}
					}
					
				}
					
			}
		}
		public synchronized void delLogbak(){
			File logFile = new File(KdxFileUtil.getLogsbakDir());
			if(logFile.isDirectory()){
				File[] listFiles = logFile.listFiles();
				for(File listFile:listFiles){
					Log.i("qujc", listFile.getPath());
					if(listFile.isDirectory()){
						File[] logFiles = listFile.listFiles(new FileFilter() {
							@Override
							public boolean accept(File pathname) {
								try {
									Calendar lastModifCal = Calendar.getInstance();
									lastModifCal.setTime(new Date(pathname.lastModified()));
									lastModifCal.set(Calendar.HOUR_OF_DAY, 0);
									lastModifCal.set(Calendar.MINUTE, 0);
									lastModifCal.set(Calendar.SECOND, 0);
									lastModifCal.set(Calendar.MILLISECOND, 0);
									Date lastModiftime = lastModifCal.getTime();
									
									Calendar destCal = Calendar.getInstance();
									destCal.set(Calendar.HOUR_OF_DAY, 0);
									destCal.set(Calendar.MINUTE, 0);
									destCal.set(Calendar.SECOND, 0);
									destCal.set(Calendar.MILLISECOND, 0);
									destCal.add(Calendar.DAY_OF_YEAR, -365);
									Date destDate = destCal.getTime();
									
									return destDate.after(lastModiftime);
								} catch (Exception e) {
									return false;
								}
							}
						});
						for(File log:logFiles){
							Log.i("qujc",log.getName());
							log.delete();
						}
					}
					
				}
					
			}
		}
} 