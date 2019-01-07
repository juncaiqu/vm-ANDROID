package com.kdx.kdxutils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.Writer;

/**
 * Author  :qujuncai
 * DATE    :18/12/10
 * Email   :qjchzq@163.com
 */
public class FileUtil {
    public static boolean fileExits(String name) {
        File file = new File(name);
        if (file.exists()) {
            return true;
        } else {
            return false;
        }
    }
    public static void copyDirectiory(String sourceDir, String targetDir) throws Exception {
        File sorce = new File(sourceDir);
        if (!sorce.exists()) {
            return;
        }
        File target = new File(targetDir);
        if (!target.exists()) {
            target.mkdirs();
        }
        File[] file = (new File(sourceDir)).listFiles();
        if(file == null) return;
        for (int i = 0; i < file.length; i++) {
            if (file[i].isFile()) {
                File sourceFile = file[i];
                File targetFile = new File(new File(targetDir).getAbsolutePath() + File.separator + file[i].getName());
                copyFile(sourceFile, targetFile);
            }

            if (file[i].isDirectory()) {
                String dir1 = sourceDir + sorce.separator + file[i].getName();
                String dir2 = targetDir + sorce.separator + file[i].getName();
                copyDirectiory(dir1, dir2);
            }
        }

    }
    public static void copyFile(File sourcefile, File targetFile) throws Exception {
        FileInputStream input = null;
        BufferedInputStream inbuff = null;
        FileOutputStream out = null;
        BufferedOutputStream outbuff = null;
        try {
            input = new FileInputStream(sourcefile);
            inbuff = new BufferedInputStream(input);
            out = new FileOutputStream(targetFile);
            outbuff = new BufferedOutputStream(out);
            byte[] b = new byte[1024 * 5];
            int len = 0;
            while ((len = inbuff.read(b)) != -1) {
                outbuff.write(b, 0, len);
            }
            outbuff.flush();

        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }finally {
            try {if(inbuff != null){inbuff.close();} } catch (IOException e) { e.printStackTrace();}
            try {if(outbuff != null){outbuff.close();} } catch (IOException e) { e.printStackTrace();}
            try {if(out != null){out.close();} } catch (IOException e) { e.printStackTrace();}
            try {if(input != null){input.close();} } catch (IOException e) { e.printStackTrace();}
        }

    }

    public static void deleteDirectory(File directory) throws IOException {
        if(directory.exists()) {
            if(!isSymlink(directory)) {
                cleanDirectory(directory);
            }

            if(!directory.delete()) {
                String message = "Unable to delete directory " + directory + ".";
                throw new IOException(message);
            }
        }
    }
    public static boolean deleteFile(File directory) {
        if(directory.exists()) {
           return directory.delete();
        }else{
            return false;
        }
    }
    public static boolean isSymlink(File file) throws IOException {
        if(file == null) {
            throw new NullPointerException("File must not be null");
        } else {
            File fileInCanonicalDir = null;
            if(file.getParent() == null) {
                fileInCanonicalDir = file;
            } else {
                File canonicalDir = file.getParentFile().getCanonicalFile();
                fileInCanonicalDir = new File(canonicalDir, file.getName());
            }

            return !fileInCanonicalDir.getCanonicalFile().equals(fileInCanonicalDir.getAbsoluteFile());
        }
    }

    public static void forceDelete(File file) throws IOException {
        if(file.isDirectory()) {
            deleteDirectory(file);
        } else {
            boolean filePresent = file.exists();
            if(!file.delete()) {
                if(!filePresent) {
                    throw new FileNotFoundException("File does not exist: " + file);
                }

                String message = "Unable to delete file: " + file;
                throw new IOException(message);
            }
        }

    }
    public static void cleanDirectory(File directory) throws IOException {
        String var9;
        if(!directory.exists()) {
            var9 = directory + " does not exist";
            throw new IllegalArgumentException(var9);
        } else if(!directory.isDirectory()) {
            var9 = directory + " is not a directory";
            throw new IllegalArgumentException(var9);
        } else {
            File[] files = directory.listFiles();
            if(files == null) {
                throw new IOException("Failed to list contents of " + directory.getPath());
            } else {
                IOException exception = null;
                File[] arr$ = files;
                int len$ = files.length;

                for(int i$ = 0; i$ < len$; ++i$) {
                    File file = arr$[i$];

                    try {
                        forceDelete(file);
                    } catch (IOException var8) {
                        exception = var8;
                    }
                }

                if(null != exception) {
                    throw exception;
                }
            }
        }
    }
    public static float readUsage() {
        RandomAccessFile reader = null;
        try {
            reader = new RandomAccessFile("/proc/stat", "r");
            String load = reader.readLine();
            String[] toks = load.split(" ");
            long idle1 = Long.parseLong(toks[5]);
            long cpu1 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
                    + Long.parseLong(toks[4]) + Long.parseLong(toks[6])
                    + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            try {
                Thread.sleep(360);
            } catch (Exception e) {
                e.printStackTrace();
            }
            reader.seek(0);
            load = reader.readLine();
            toks = load.split(" ");

            long idle2 = Long.parseLong(toks[5]);
            long cpu2 = Long.parseLong(toks[2]) + Long.parseLong(toks[3])
                    + Long.parseLong(toks[4]) + Long.parseLong(toks[6])
                    + Long.parseLong(toks[7]) + Long.parseLong(toks[8]);

            return (int) (100 * (cpu2 - cpu1) / ((cpu2 + idle2) - (cpu1 + idle1)));

        } catch (IOException ex) {
            ex.printStackTrace();
        }finally {
           if(reader != null){
               try {
                   reader.close();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }
        }
        return 0;
    }
    /**
     * 读取数据
     *
     * @return
     */
    public static String readFile(String path) {
        StringBuffer content = new StringBuffer();
        File file = new File(path);
        BufferedReader br = null;
        if (file.exists()&&file.isFile()) {
            try {
                br = new BufferedReader(new InputStreamReader(new FileInputStream(path)));
                String data = br.readLine();
                while (data != null) {
                    content.append(data);
                    data = br.readLine();
                }
                br.close();
            } catch (IOException e) {
            }finally {
                if(br != null){
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return content + "";
    }

    public static void writeFile(String path, String content) {
        FileWriter fw = null;
        try {
            fw = new FileWriter(path, false);
            String c = content + "\r\n";
            fw.write(c);
            fw.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            closeOut(fw);
        }

    }

    public static void closeIn(InputStream inputStream){
        if(inputStream != null){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void closeOut(OutputStream outputStream){
        if(outputStream != null){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public static void closeOut(Writer writer){
        if(writer != null){
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
