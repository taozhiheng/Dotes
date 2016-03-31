package file;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by taozhiheng on 15-3-13.
 *
 */
public class FileOperation {

    public static boolean openFile(Context context, File file)
    {
        if(!file.exists())
            return false;
        return openFile(context, file.getPath());
    }

    public static boolean openFile(Context context, String filePath)
    {
        FileIntent fileIntent = new FileIntent(context, filePath);
        Intent intent = fileIntent.getFileIntent();
        if(intent != null) {
            context.startActivity(intent);
            return true;
        }
        return false;
    }

    public static boolean renameFile(String oldFilePath, String newFilePath)
    {
        return renameFile(new File(oldFilePath), new File(newFilePath));
    }

    public static boolean renameFile(File oldFile, File newFile)
    {
        if(!oldFile.exists())
            return false;
        return oldFile.renameTo(newFile);
    }

    public static boolean deleteFile(String filePath)
    {
        return deleteFile(new File(filePath));
    }

    public static boolean deleteFile(File file)
    {
        if (file.exists())
        { // 判断文件是否存在
            if (file.isFile())
            { // 判断是否是文件
                return file.delete(); // delete()方法 你应该知道 是删除的意思;
            } else if (file.isDirectory())
            { // 否则如果它是一个目录
                File files[] = file.listFiles(); // 声明目录下所有的文件 files[];
                for (int i = 0; i < files.length; i++)
                { // 遍历目录下所有的文件
                    deleteFile(files[i]); // 把每个文件 用这个方法进行迭代
                }
                return file.delete();
            }
        }
        return false;
    }

    public static boolean moveFile(String originFilePath, String toDirPath)
    {
        return moveFile(new File(originFilePath), new File(toDirPath));
    }

    public static boolean moveFile(File originFile, File toDir)
    {
        if(!originFile.exists())
            return false;
        if (!toDir.exists())
            toDir.mkdirs();
        return originFile.renameTo(new File(toDir,  originFile.getName()));
    }

    public static boolean copyFile(String choseFilePath, String toDirPath, Handler handler)
    {
        return copyFile(new File(choseFilePath), toDirPath, handler);
    }

    public static boolean copyFile(File originFile, String toDirPath, final Handler handler)
    {
        final File choseFile = originFile;
        if(!choseFile.exists())
            return false;
        if(choseFile.isDirectory())
        {
            File files[] = choseFile.listFiles(); // 声明目录下所有的文件 files[];
            for (int i = 0; i < files.length; i++)
            { // 遍历目录下所有的文件
                copyFile(files[i], toDirPath + "/" + choseFile.getPath(), handler); // 把每个文件 用这个方法进行迭代
            }
        }
        else
        {
            final File file = new File(toDirPath, choseFile.getName());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        InputStream inputStream = new FileInputStream(choseFile);
                        if (!file.exists())
                            file.createNewFile();
                        OutputStream outputStream = new FileOutputStream(file);
                        byte[] bytes = new byte[1024];
                        int available;
                        while ((available = inputStream.read(bytes)) != -1)
                            outputStream.write(bytes, 0, available);
                        inputStream.close();
                        outputStream.close();
                        Log.e("fileInfo", "copy finish");
                        if(handler != null)
                            handler.obtainMessage(0, file.getName()).sendToTarget();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
        return true;
    }
}
