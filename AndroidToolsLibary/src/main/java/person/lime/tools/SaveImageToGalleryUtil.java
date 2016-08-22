package person.lime.tools;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 将图片保存到手机并在图库中显示
 * Created by Lime(李振宇) on 2016-08-19.
 */
public class SaveImageToGalleryUtil {

    /**
     * 保存图片
     * @param context 上下文容器
     * @param bmp 图片
     * @param folder 保存的子文件或者路径
     * @return 是否保存成功
     */
    public static boolean save(Context context, Bitmap bmp ,String folder) {
        String fileName = System.currentTimeMillis() + ".jpg";
        return save(context,bmp,folder,fileName);
    }

    /**
     * 保存图片
     * @param context 上下文容器
     * @param bmp 图片
     * @param folder 保存的子文件或者路径
     * @param fileName 保存文件名
     * @return 是否保存成功
     */
    public static boolean save(Context context, Bitmap bmp ,String folder,String fileName){
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), folder);
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(CompressFormat.JPEG, 90, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    file.getAbsolutePath(), fileName, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        }
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
        return true;
    }

    /**
     * 保存图片
     * @param context 上下文容器
     * @param bmp 图片
     * @param folder 保存的子文件或者路径
     * @throws IOException FileNotFoundException,IOException自行处理错误异常
     */
    public static void saveAs(Context context, Bitmap bmp ,String folder) throws IOException {
        String fileName = System.currentTimeMillis() + ".jpg";
        saveAs(context,bmp,folder,fileName);
    }

    /**
     * 保存图片
     * @param context 上下文容器
     * @param bmp 图片
     * @param folder 保存的子文件或者路径
     * @param fileName 保存文件名
     * @throws IOException FileNotFoundException,IOException自行处理错误异常
     */
    public static void saveAs(Context context, Bitmap bmp ,String folder,String fileName) throws IOException {
        // 首先保存图片
        File appDir = new File(Environment.getExternalStorageDirectory(), folder);
        if (!appDir.exists()) {
            appDir.mkdir();
        }

        File file = new File(appDir, fileName);
        FileOutputStream fos = new FileOutputStream(file);
        bmp.compress(CompressFormat.JPEG, 95, fos);
        fos.flush();
        fos.close();

        // 其次把文件插入到系统图库
        MediaStore.Images.Media.insertImage(context.getContentResolver(),
        file.getAbsolutePath(), fileName, null);
        // 最后通知图库更新
        context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file)));
    }
}
