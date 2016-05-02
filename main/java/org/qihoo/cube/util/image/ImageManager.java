package org.qihoo.cube.util.image;



import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.provider.MediaStore;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

import org.qihoo.cube.R;
import org.qihoo.cube.util.data.ApplicationManager;

public class ImageManager {

    protected ImageLoader loader;
    protected DisplayImageOptions options;

    public ImageManager(){
        loader = ImageLoader.getInstance();

        options = new DisplayImageOptions.Builder()
                .imageScaleType(ImageScaleType.EXACTLY)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .showImageOnLoading(R.mipmap.pic_loading)
                .cacheInMemory(true)
                .cacheOnDisc(true)
                .build();
    }

    public ImageLoader getLoader(){
        return loader;
    }

    public DisplayImageOptions getOptions(){
        return options;
    }

    /**
     * 更新图库
     * @param imgPath
     */
    public static void insertImage(String imgPath){
        Context context = ApplicationManager.getInstance();
        ContentResolver contentResolver = context.getContentResolver();
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
        values.put(MediaStore.Images.Media.ORIENTATION, 0);
        values.put(MediaStore.Images.Media.DATA, imgPath);

        contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

}
