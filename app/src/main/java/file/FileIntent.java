package file;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.util.Log;

import com.example.taozhiheng.dotes.R;

import java.io.File;

/**
 * 传入context,filePath,获取打开文件的intent
 * */

public class FileIntent {

    private Context mContext;         //context,用于获取资源字符串数组
    private String mFilePath;         //filePath，用于创建文件对象

    public FileIntent(Context context, String path)
    {
        this.mContext = context;
        this.mFilePath = path;
    }

    //检查文件后缀
    private boolean checkEndsWithInStringArray(String checkItsEnd,
                                               String[] fileEndings){
        for(String aEnd : fileEndings){
            if(checkItsEnd.endsWith(aEnd))
                return true;
        }
        return false;
    }

    //获取打开文件的intent
    public Intent getFileIntent()
    {
        return getFileIntent(mContext, mFilePath);
    }

    private  Intent getFileIntent(Context context, String filePath)
    {
        Intent intent;
        Log.e("fileIntent", "path:"+ filePath);
        File file = new File(filePath);
        if(!file.exists())
            return null;
        Resources resources = context.getResources();
        if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingImage))){
            intent = getImageFileIntent(file);
        }else if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingWebText))){
            intent = getHtmlFileIntent(file);
        }else if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingPackage))){
            intent = getApkFileIntent(file);

        }else if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingAudio))){
            intent = getAudioFileIntent(file);
        }else if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingVideo))){
            intent = getVideoFileIntent(file);
        }else if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingText))){
            intent = getTextFileIntent(file);
        }else if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingPdf))){
            intent = getPdfFileIntent(file);
        }else if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingWord))){
            intent = getWordFileIntent(file);
        }else if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingExcel))){
            intent =getExcelFileIntent(file);
        }else if(checkEndsWithInStringArray(filePath, resources.
                getStringArray(R.array.fileEndingPPT))){
            intent = getPPTFileIntent(file);
        }else
        {
            intent = null;
        }
        return intent;
    }

    //android获取一个用于打开HTML文件的intent
    public static Intent getHtmlFileIntent(File file)
    {
        Uri uri = Uri.parse(file.toString()).buildUpon().encodedAuthority("com.android.htmlfileprovider").scheme("content").encodedPath(file.toString()).build();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(uri, "text/html");
        return intent;
    }
    //android获取一个用于打开图片文件的intent
    public static Intent getImageFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "image/*");
        return intent;
    }
    //android获取一个用于打开PDF文件的intent
    public static Intent getPdfFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/pdf");
        return intent;
    }
    //android获取一个用于打开文本文件的intent
    public static Intent getTextFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "text/plain");
        return intent;
    }

    //android获取一个用于打开音频文件的intent
    public static Intent getAudioFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "audio/*");
        return intent;
    }
    //android获取一个用于打开视频文件的intent
    public static Intent getVideoFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("oneshot", 0);
        intent.putExtra("configchange", 0);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "video/*");
        return intent;
    }

    //android获取一个用于打开CHM文件的intent
    public static Intent getChmFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/x-chm");
        return intent;
    }

    //android获取一个用于打开Word文件的intent
    public static Intent getWordFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/msword");
        return intent;
    }

    //android获取一个用于打开Excel文件的intent
    public static Intent getExcelFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-excel");
        return intent;
    }

    //android获取一个用于打开PPT文件的intent
    public static Intent getPPTFileIntent(File file)
    {
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.addCategory("android.intent.category.DEFAULT");
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri = Uri.fromFile(file);
        intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        return intent;
    }

    //android获取一个用于打开apk文件的intent
    public static Intent getApkFileIntent(File file)
    {
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),  "application/vnd.android.package-archive");
        return intent;
    }

}