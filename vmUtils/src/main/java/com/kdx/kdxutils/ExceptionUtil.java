package com.kdx.kdxutils;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * Author  :qujuncai
 * DATE    :19/1/7
 * Email   :qjchzq@163.com
 */
public class ExceptionUtil {
    public static String errorConvert(Throwable ex){
        Writer writer = null;
        PrintWriter pw = null;
        StringBuffer sb = new StringBuffer();
        try {
            writer = new StringWriter();
            pw = new PrintWriter(writer);
            ex.printStackTrace(pw);
            Throwable cause = ex.getCause();
            while (cause != null) {
                cause.printStackTrace(pw);
                cause = cause.getCause();
            }
            String result = writer.toString();
            sb.append(result);
        }catch (Exception e){
            e.printStackTrace();
        }finally {

            FileUtil.closeOut(writer);
            FileUtil.closeOut(pw);
        }

        return sb.toString();
    }
}
