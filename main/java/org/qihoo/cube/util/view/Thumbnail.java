package org.qihoo.cube.util.view;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;
import android.graphics.Matrix;


import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by zhangqi1-pd on 2015/9/1.
 */
public class Thumbnail {

    public static boolean thumbnailMaker(String photoPath,String thumbnailPath) {

        //将原图压缩，得到压缩后的图
        Bitmap photo = getThumbnail(photoPath);

        //将压缩后的缩略图放入指定的目录文件夹中
        try {
            File file = new File(thumbnailPath);

            FileOutputStream out = new FileOutputStream(file);
            if (photo.compress(Bitmap.CompressFormat.PNG, 100, out)) {
                out.flush();
                out.close();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 压缩图片，并得到缩略图
     * params photoPath
     * return string thumbnail.
     */
    private static Bitmap getThumbnail(String photoPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options); //此时返回bm为空
        //计算缩放比
        int scale = (int) (options.outHeight / (float) 200);

        if (scale <= 0)
            scale = 1;
        options.inSampleSize = scale;

        //重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
        options.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(photoPath, options);

        return bitmap;
    }
}
