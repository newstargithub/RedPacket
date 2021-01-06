package com.halo.redpacket.util;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;


import com.halo.redpacket.activity.CrashDisplayActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @title 未捕获异常处理类
 *
 */
public class CrashHandler implements UncaughtExceptionHandler {
	public static final String TAG = "CrashHandler";

	// 系统默认的UncaughtException处理类
	private Thread.UncaughtExceptionHandler mDefaultHandler;
	//CrashHandler实例
	private static CrashHandler INSTANCE = new CrashHandler();
	// 程序的Context对象
	private Context context;
	// 用来存储设备信息和异常信息
	private Map<String, String> infos = new HashMap<String, String>();
	// 用于格式化信息，作为日志文件名的一部分
	private DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss", Locale.US);
	
	// 保证只有一个CrashHandler实例
	private CrashHandler(){

	}
	
	/**  获取CrashHandler实例，单例模式  */
	public static CrashHandler getInstance(){
		return INSTANCE;
	}
	
	/**
	 * 初始化
	 * @param context
	 */
	public void init(Context context){
		this.context = context.getApplicationContext();
		// 获取系统默认的UncaughtException处理器
		mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
		// 设置该CrashHandler为程序的默认处理器
		Thread.setDefaultUncaughtExceptionHandler(this);
	}
	
	/**
	 * 当UncaughtException发生时会转入该函数来处理
	 */
	@Override
	public void uncaughtException(Thread thread, Throwable ex) {
		if(!isPushProcess() && !handleException(ex) && mDefaultHandler !=null){
			// 如果用户没有处理则让系统默认的处理器来处理
			mDefaultHandler.uncaughtException(thread, ex);
		}else{ 
			// 自己处理
			// 展示所捕获的异常信息
			Intent intent = new Intent(context, CrashDisplayActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
			// 退出程序
			Process.killProcess(Process.myPid());
			System.exit(0);
		}
	}
	
	private boolean handleException(Throwable ex){
		if(ex == null){
			return false;
		}
		// 搜集设备参数信息
		collectDeviceInfo(context);
		// 保存日志文件
		saveCrashInfoToFile(ex);
		return true;
	}
	
	/**
	 * 收集设备参数信息
	 * @param ctx
	 */
	private void collectDeviceInfo(Context ctx) {
		//获取versionName，versionCode
		try{
			PackageManager pm = ctx.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
			if(pi != null){
				String versionName = pi.versionName==null?"null":pi.versionName;
				String versionCode = pi.versionCode+"";
				infos.put("versionName", versionName);
				infos.put("versionCode", versionCode);
			}
		}catch(NameNotFoundException e){
			Log.e(TAG, "an error occured when collect package info");
		}
		//获取所有系统信息
		Field[] fields = Build.class.getDeclaredFields();
		for(Field field : fields){
			try {
				field.setAccessible(true);
				infos.put(field.getName(), field.get(null).toString());
				Log.d(TAG, field.getName() + " : " + field.get(null));
			} catch(Exception e){
				Log.e(TAG, "an error occured when collect crash info",e);
			}
		}
	}
	
	/**
	 * 保存错误信息到文件中
	 * @param ex
	 * @return   返回文件名称,便于将文件传送到服务器 
	 */
	private String saveCrashInfoToFile(Throwable ex){
		StringBuilder sb = new StringBuilder();
		for(Map.Entry<String, String> entry : infos.entrySet()){
			String key = entry.getKey();
			String value = entry.getValue();
			sb.append(key).append("=").append(value).append("\n");
		}

		Writer writer = new StringWriter();
		PrintWriter printWriter = new PrintWriter(writer);
		ex.printStackTrace(printWriter);
		Throwable cause = ex.getCause();
		while(cause != null){
			cause.printStackTrace(printWriter);
			cause = cause.getCause();
		}
		printWriter.close();

		String result = writer.toString();
		sb.append(result);
		try {
			//IO流將crash日志保存到文件，注意文件操作权限
			long timestamp = System.currentTimeMillis();
			String time = formatter.format(new Date());
			String fileName = "crash-"+time +"-"+timestamp+".txt";
			if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
				String path = context.getFilesDir() + "/log/crash";
				File dir = new File(path);
				if (!dir.exists()) {
					dir.mkdirs();
				}
				FileOutputStream fos = new FileOutputStream(path + File.separator + fileName);
				fos.write(sb.toString().getBytes());
				fos.close();
			}
			return fileName;
		}catch(Exception e){
			Log.e(TAG,"an error occured while writing file...",e);
		}
		return null;
	}

	/**
	 * 是推送进程
	 * @return
	 */
	private boolean isPushProcess() {
		int pid = android.os.Process.myPid();
		ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		if (activityManager != null) {
			for (ActivityManager.RunningAppProcessInfo appProcessInfo : activityManager.getRunningAppProcesses()) {
				if (appProcessInfo.pid == pid && appProcessInfo.processName.equals("com.wlb.android:wlbservice")) {
					System.out.println(appProcessInfo.processName);
					return true;
				}
			}
		}
		return false;
	}

}
