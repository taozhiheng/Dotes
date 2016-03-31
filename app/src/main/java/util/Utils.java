package util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.example.taozhiheng.dotes.MainActivity;
import com.example.taozhiheng.dotes.R;

public class Utils  
{  
    private static int sTheme = -1;
    /** 
     * Set the theme of the Activity, and restart it by creating a new Activity 
     * of the same type. 
     */  
    public static void changeToTheme(Activity activity, int theme)  
    {  
        sTheme = theme;  
        activity.finish();  
  
        activity.startActivity(new Intent(activity, activity.getClass()));  
    }  
  
    /** Set the theme of the activity, according to the configuration. */  
    public static void onActivityCreateSetTheme(Activity activity)  
    {
        if(sTheme == -1)
            sTheme = activity.getSharedPreferences(Constant.SHARED_PREF, Context.MODE_PRIVATE).getInt(Constant.APPLICATION_THEME_ID, 1);
        switch (sTheme)  
        {  
            case 0:
                activity.setTheme(R.style.AppTheme_BLUE_DARK);
            case 1:
                activity.setTheme(R.style.AppTheme_BLUE_LIGHT);
                break;
            case 2:
                activity.setTheme(R.style.AppTheme_ORANGE_DARK);
                break;
            case 3:
                activity.setTheme(R.style.AppTheme_ORANGE_LIGHT);
                break;
        }  
    }  
}  