package com.woodys.record.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.os.Build;

import java.util.List;

/********
 * @author woodys
 */
public class StackProcessManager {
    private static final StackProcessManager instance = new StackProcessManager();

    private StackProcessManager() {
    }

    public static StackProcessManager get() {
        return instance;
    }


    /**
     * 判断程序是否在前台运行
     *
     * @param context
     * @return
     */
    public boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            if (null == runningProcesses) return isInBackground;
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (null!=processInfo && processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    if (null == processInfo.pkgList) return isInBackground;
                    for (String activeProcess : processInfo.pkgList) {
                        if (null!=activeProcess && activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            if (null != taskInfo) {
                ActivityManager.RunningTaskInfo runningTaskInfo = taskInfo.get(0);
                if(null != runningTaskInfo) {
                    ComponentName componentInfo = taskInfo.get(0).topActivity;
                    if (null != componentInfo && componentInfo.getPackageName().equals(context.getPackageName())) {
                        isInBackground = false;
                    }
                }
            }
        }

        return isInBackground;
    }


}
