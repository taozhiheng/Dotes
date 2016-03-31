package com.example.taozhiheng.dotes;

import android.app.Activity;
import android.app.Application;

import java.util.Stack;

import sql.DBOperate;
import util.Constant;

/**
 * Created by taozhiheng on 15-4-9.
 *
 */
public class MyApplication extends Application {

    private static Stack<Activity> activityStack;
    private static MyApplication singleton;

    @Override
    public void onTerminate() {
        super.onTerminate();
        DBOperate.getInstance(this).close();
    }

    @Override
    public void onCreate()
    {
        int which = getSharedPreferences(Constant.SHARED_PREF, MODE_PRIVATE).getInt(Constant.SHARED_PREF, 1);
        setApplicationTheme(which);
        super.onCreate();
        singleton=this;
    }
    // Returns the application instance
    public static MyApplication getInstance() {
        return singleton;
    }

    public void setApplicationTheme(int which)
    {
        switch(which) {
            case 0:
                setTheme(R.style.AppTheme_BLUE_DARK);
                break;
            case 1:
                setTheme(R.style.AppTheme_BLUE_LIGHT);
                break;
            case 2:
                setTheme(R.style.AppTheme_ORANGE_DARK);
                break;
            case 3:
                setTheme(R.style.AppTheme_ORANGE_LIGHT);
                break;
        }
    }

    /**
     * add Activity 添加Activity到栈
     */
    public void addActivity(Activity activity){
        if(activityStack ==null){
            activityStack =new Stack<Activity>();
        }
        activityStack.add(activity);
    }
    /**
     * get current Activity 获取当前Activity（栈中最后一个压入的）
     */
    public Activity currentActivity() {
        Activity activity = activityStack.lastElement();
        return activity;
    }
    /**
     * 结束当前Activity（栈中最后一个压入的）
     */
    public void finishActivity() {
        Activity activity = activityStack.lastElement();
        finishActivity(activity);
    }

    /**
     * 结束指定的Activity
     */
    public void finishActivity(Activity activity) {
        if (activity != null) {
            activityStack.remove(activity);
            activity.finish();
            activity = null;
        }
    }

    /**
     * 结束指定类名的Activity
     */
    public void finishActivity(Class<?> cls) {
        for (Activity activity : activityStack) {
            if (activity.getClass().equals(cls)) {
                finishActivity(activity);
            }
        }
    }

    /**
     * 结束所有Activity
     */
    public void finishAllActivity() {
        for (int i = 0, size = activityStack.size(); i < size; i++) {
            if (null != activityStack.get(i)) {
                activityStack.get(i).finish();
            }
        }
        activityStack.clear();
    }

    /**
     * 退出应用程序
     */
    public void AppExit() {
        try {
            finishAllActivity();
        } catch (Exception e) {
        }
    }

}
