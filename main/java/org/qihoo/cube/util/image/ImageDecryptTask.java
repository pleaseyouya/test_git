package org.qihoo.cube.util.image;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import org.qihoo.cube.R;
import org.qihoo.cube.util.data.Preference;
import org.qihoo.cube.util.file.FileUtil;
import org.qihoo.cube.util.security.AESFunc;
import org.qihoo.cube.util.view.Thumbnail;

/**
 * Created by wangjinfa on 2015/9/1.
 */
public class ImageDecryptTask extends AsyncTask<String, Integer, Bitmap> {

    private ImageRefreshListener listener;

    public interface ImageRefreshListener {
        public void refreshImage(Bitmap bitmap);
    }

    public ImageDecryptTask(ImageRefreshListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        // todo 显示进度条
    }

    @Override
    protected Bitmap doInBackground(String... params) {
        String encryPath = params[0];
        String srcPath = params[1];

        // 解密图片
        AESFunc.decryptFile2File(encryPath, Preference.getInstance().getPassword(), srcPath);
        // 获取图片
        Bitmap bitmap = BitmapFactory.decodeFile(srcPath);
        return bitmap;
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);
        // todo 更新进度条
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
        listener.refreshImage(bitmap);
    }

}
