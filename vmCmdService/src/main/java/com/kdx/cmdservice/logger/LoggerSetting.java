package com.kdx.cmdservice.logger;

import com.kdx.kdxutils.KdxFileUtil;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.xml.XmlConfigurationFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Author  :qujuncai
 * DATE    :18/11/30
 * Email   :qjchzq@163.com
 */
public class LoggerSetting {
    private static final String PROJECT_NAME = "cmdservice";
    private static LoggerContext loggerContext = null;


    static {
        loggerContext = loadFile();
    }


    public static Logger getLogger(){
        return loggerContext.getLogger("InfoFile");
    }

    private static LoggerContext loadFile(){

        InputStream is = new ByteArrayInputStream(getLoggerXml().getBytes());
        ConfigurationSource source = null;
        try{
            source = new ConfigurationSource(is);
        } catch (IOException ioe){
            ioe.printStackTrace();
        }
        org.apache.logging.log4j.core.config.Configuration config = XmlConfigurationFactory.getInstance().getConfiguration(source);
        loggerContext = (LoggerContext) LogManager.getContext();
        try {
            loggerContext.start(config);
        } catch (Exception e){
            //e.printStackTrace();
        }
        return loggerContext;
    }

    private static String getLogInfoPath(){
        return KdxFileUtil.getLogsDir()+PROJECT_NAME+"/";
    }

    private static String getLoggerXml(){
        String setStr =
                "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<Configuration status=\"INFO\">\n" +
                        "    <Appenders>\n" +
                        "        <Console name=\"Console\" target=\"SYSTEM_OUT\">\n" +
                        "            <ThresholdFilter level=\"debug\" onMatch=\"ACCEPT\" onMismatch=\"DENY\" />\n" +
                        "            <PatternLayout pattern=\"%d{yyyy-MM-dd HH:mm:ss} %-5level %class{36} %M - %msg%xEx%n\" />\n" +
                        "        </Console>\n" +
                        "        <RollingFile name=\"infoFile\" bufferSize=\"2048\" immediateFlush=\"false\" fileName=\""+getLogInfoPath()+PROJECT_NAME+"_info.log\" " +
                        "                  filePattern=\""+getLogInfoPath()+PROJECT_NAME+"_info_%d{yyyy-MM-dd}_%i.log\" bufferedIO =\"true\">\n" +
                        "            <filters>\n" +
                        "                <ThresholdFilter level=\"error\" onMatch=\"DENY\" onMismatch=\"NEUTRAL\" />\n" +
                        "                <ThresholdFilter level=\"info\" onMatch=\"ACCEPT\" onMismatch=\"DENY\" />\n" +
                        "            </filters>\n" +
                        "            <PatternLayout pattern=\"%d{yyyy-MM-dd HH:mm:ss.SSS} %-5level [%-16t][%10.15C][%-3L] %msg%xEx%n\" />\n" +
                        "            <Policies>\n"+
                        "                   <TimeBasedTriggeringPolicy/>\n" +
                        "                   <SizeBasedTriggeringPolicy size=\"10M\"/>\n" +
                        "            </Policies>\n"+
                        "        </RollingFile>\n" +
                        "\n" +
                        "        <RollingFile name=\"errorFile\" fileName=\""+getLogInfoPath()+PROJECT_NAME+"_error.log\" " +
                        "                    filePattern=\""+getLogInfoPath()+PROJECT_NAME+"_error_%d{yyyy-MM-dd}_%i.log\" bufferedIO =\"true\">\n" +
                        "            <filters>\n" +
                        "                <ThresholdFilter level=\"error\" onMatch=\"ACCEPT\" onMismatch=\"DENY\" />\n" +
                        "            </filters>\n" +
                        "            <PatternLayout pattern=\"%d{yyyy-MM-dd  HH:mm:ss.SSS} %-5level - %msg%xEx%n\" />\n" +
                        "            <Policies>\n"+
                        "                   <TimeBasedTriggeringPolicy/>\n" +
                        "                   <SizeBasedTriggeringPolicy size=\"10M\"/>\n" +
                        "            </Policies>\n"+
                        "        </RollingFile>\n" +
                        "\n" +
                        "    </Appenders>\n" +
                        "    <Loggers>\n" +
                        "        <Logger name=\"InfoFile\" level=\"debug\">\n" +
                        "            <AppenderRef ref=\"infoFile\" />\n" +
                        "            <AppenderRef ref=\"errorFile\" />\n" +
                        "        </Logger>\n" +
                        "    </Loggers>\n" +
                        "</Configuration>";
        return setStr;

    }

}
