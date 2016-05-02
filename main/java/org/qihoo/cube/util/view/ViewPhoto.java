package org.qihoo.cube.util.view;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.DisplayMetrics;

/**
 * Created by zhangqi1-pd on 2015/9/7.
 */
public class ViewPhoto {

    public static Bitmap getFitPhoto(Bitmap photo){


        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;


        // 获取屏幕的宽和高
        DisplayMetrics dm = new DisplayMetrics();
        //getWindowManager().getDefaultDisplay().getMetrics(dm);
        int screenWidth = dm.widthPixels;
        int screenHeigh = dm.heightPixels;

        // 获取这个图片的宽和高
        int photoWidth = photo.getWidth();
        int photoHeigth = photo.getHeight();

        float widthScale = 1;
        float heightScale = 1;
        float scale = 1;

        if(photoWidth > photoHeigth && photoWidth > screenWidth) {
            widthScale = (screenWidth / photoWidth);
            scale = widthScale;
        }else if (photoWidth < photoHeigth && photoHeigth > screenHeigh) {
            heightScale = (screenHeigh / photoHeigth);
            scale = widthScale;
        }

        options.inJustDecodeBounds = false;
        Matrix matrix = new Matrix();
        matrix.postScale(scale, scale);
        Bitmap bitmap = Bitmap.createBitmap(photo, 0, 0, (int)(photoWidth/widthScale), (int)(photoHeigth/heightScale));
        return bitmap;
    }
}
