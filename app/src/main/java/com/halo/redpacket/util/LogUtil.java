package com.halo.redpacket.util;

import android.text.TextUtils;
import android.util.Log;

import com.halo.redpacket.BuildConfig;
import com.halo.redpacket.MyApplication;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

/**
 * 日志工具类
 */
public class LogUtil {
    private static final int V = 1;
    private static final String LINE_SEP = "\n";
    private static boolean sLogSwitch = true;
    //日志是否打印到控制台开关
    private static boolean sLog2Console = true;
    //日志是否写入到文件开关
    private static boolean sLog2File = false;
    private static ExecutorService sExecutor;
    private static String fullPath = "log";
    private static String mTag;
    private static int sConsoleFilter;//过滤等级
    private static int sFileFilter;

    private static void d(String msg) {
        d(mTag, msg);
    }

    public static void d(String tag, String msg) {
        if (sLogSwitch) {
            Log.d(tag, msg);
        }
    }

    public static void log(String tag, String msg) {
        if (!sLogSwitch || (!sLog2Console && !sLog2File)) {
            return;
        }
        if (sLog2Console) {
            print2Console(tag, msg);
        }
        if (sLog2File) {
            print2File(tag, msg);
        }
    }

    private static void print2File(final String tag, final String msg) {
        if (sExecutor == null) {
            sExecutor = Executors.newSingleThreadExecutor();
        }
        final String content = "tag\nmsg";
        sExecutor.execute(new Runnable() {
            @Override
            public void run() {
                BufferedWriter bw = null;
                try {
                    bw = new BufferedWriter(new FileWriter(fullPath, true));
                    bw.write(content);
                    Log.d(tag, "log to " + fullPath + " success!");
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d(tag, "log to " + fullPath + " failed!");
                } finally {
                    try {
                        if (bw != null) {
                            bw.close();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    /**
     * 打印到控制台
     * @param tag
     * @param msg
     */
    private static void print2Console(String tag, String msg) {
        if (msg.startsWith("{") || msg.startsWith("[")) {
            msg = formatJson(msg);
        } else if (msg.startsWith("<")) {
            msg = formatXml(msg);
        }
        d(tag, msg);
    }

    /**
     * json格式转换
     * @param json
     * @return
     */
    private static String formatJson(String json) {
        try {
            if (json.startsWith("{")) {
                json = new JSONObject(json).toString(4);
            } else if (json.startsWith("[")) {
                json = new JSONArray(json).toString(4);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * xml格式转换
     * @param xml
     * @return
     */
    private static String formatXml(String xml) {
        try {
            StreamSource xmlInput = new StreamSource(new StringReader(xml));
            StreamResult xmlOutput = new StreamResult(new StringWriter());
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setParameter(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("", "4");
            transformer.transform(xmlInput, xmlOutput);
            xml = xmlOutput.getWriter().toString().replace(">", ">" + LINE_SEP);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return xml;
    }

    /**
     * 设置System Property属性
     */
    public static void setProperty() {
        String log_switch = System.getProperty("persist.danny.log", "null");
        Log.e("TAG", "before log_switch is " + log_switch);

        System.setProperty("persist.danny.log", "true");
        log_switch = System.getProperty("persist.danny.log", "null");
        Log.e("TAG", "after log_switch is " + log_switch);
    }

    public void initLog() {
        LogUtil.Config config = LogUtil.getConfig()
                //设置log总开关，包括输出到控制台，文件，默认开
                .setLogSwitch(BuildConfig.DEBUG)
                //设置输出到控制台，默认开
                .setConsoleSwitch(BuildConfig.DEBUG)
                .setGlobalTag("TAG")
                //当自定义路径为空时。写入应用的/cache/log目录中
                .setDir("")
                //设置输出到文件，默认关
                .setLogFileSwitch(false)
                .setConsoleFilter(LogUtil.V)
                .setFileFilter(LogUtil.V);
        LogUtil.init(config);
        LogUtil.d(config.toString());
    }

    private static void init(Config config) {
        mTag = config.mGlobalTag;
        sLogSwitch = config.mLogSwitch;
        sLog2Console = config.mConsoleSwitch;
        sLog2File = config.mLogFileSwitch;
        String path = MyApplication.getInstance().getCacheDir().getAbsolutePath() + File.separator +  "log";
        fullPath = TextUtils.isEmpty(config.mDir) ? path : config.mDir;
        sConsoleFilter = config.mConsoleFilter;
        sFileFilter = config.mFileFilter;
    }

    private static Config getConfig() {
        return new Config();
    }

    public static class Config {
        private String mGlobalTag;
        private String mDir;
        private boolean mLogSwitch;
        private boolean mConsoleSwitch;
        private boolean mLogFileSwitch;
        private int mConsoleFilter;
        private int mFileFilter;

        //设置打印Tag
        public Config setGlobalTag(String tag) {
            mGlobalTag = tag;
            return this;
        }

        //设置Tag文件保存路径
        public Config setDir(String dir) {
            mDir = dir;
            return this;
        }

        public Config setLogSwitch(boolean logSwitch) {
            mLogSwitch = logSwitch;
            return this;
        }

        public Config setConsoleSwitch(boolean consoleSwitch) {
            mConsoleSwitch = consoleSwitch;
            return this;
        }

        public Config setLogFileSwitch(boolean logFileSwitch) {
            mLogFileSwitch = logFileSwitch;
            return this;
        }

        public Config setConsoleFilter(int level) {
            this.mConsoleFilter = level;
            return this;
        }

        private Config setFileFilter(int level) {
            this.mFileFilter = level;
            return this;
        }
    }

}
