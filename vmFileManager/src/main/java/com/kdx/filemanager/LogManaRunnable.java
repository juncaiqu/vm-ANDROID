package com.kdx.filemanager;

import java.io.File;
import java.util.Calendar;
import java.util.Date;


import com.kdx.filemanager.utils.Tool;
import com.kdx.kdxutils.KdxFileUtil;
import com.kdx.kdxutils.ZipUtils;

public class LogManaRunnable implements Runnable{

		@Override
		public void run() {
			bakLog();
			delLogbak();
			Tool.delFile(new File(KdxFileUtil.getTempDir()) ,10 * 24 * 60 * 60000);//10天
		}
		private synchronized void bakLog(){
			File logFile = new File(KdxFileUtil.getLogsDir());
			bakLog(logFile);
		}
		private synchronized void delLogbak(){
			File logFile = new File(KdxFileUtil.getLogsbakDir());
			delLogbak(logFile);
		}

		private void bakLog(File logFile){
			if(logFile.exists()){
				if(logFile.isDirectory()){
					File[] files = logFile.listFiles();
					for(File file:files){
						bakLog(file);
					}
				}else{
					Calendar lastModifCal = Calendar.getInstance();
					lastModifCal.setTime(new Date(logFile.lastModified()));
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
					destCal.add(Calendar.DAY_OF_YEAR, -5);
					Date destDate = destCal.getTime();

					if(destDate.after(lastModiftime)){
						String str_logPath = logFile.getAbsolutePath();
						int index = logFile.getAbsolutePath().indexOf(KdxFileUtil.getLogsDir())+KdxFileUtil.getLogsDir().length();
						int lastIndexOf = logFile.getAbsolutePath().lastIndexOf(".");
						String lastLog = str_logPath.substring(index,lastIndexOf);
						File bakLogFile = new File(KdxFileUtil.getLogsbakDir()+lastLog+".zip");
						boolean iszip = ZipUtils.zipFiles(logFile.getAbsolutePath(),bakLogFile.getAbsolutePath());
						if(iszip){
							logFile.delete();
						}
					}
				}
			}

		}
	private void delLogbak(File logFile){
		if(logFile.exists()){
			if(logFile.isDirectory()){
				File[] files = logFile.listFiles();
				for(File file:files){
					delLogbak(file);
				}
			}else{
				Calendar lastModifCal = Calendar.getInstance();
				lastModifCal.setTime(new Date(logFile.lastModified()));
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
				destCal.add(Calendar.DAY_OF_YEAR, -90);
				Date destDate = destCal.getTime();

				if(destDate.after(lastModiftime)){
					logFile.delete();
				}
			}
		}

	}
} 