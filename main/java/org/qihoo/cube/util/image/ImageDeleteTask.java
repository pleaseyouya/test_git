package org.qihoo.cube.util.image;

/**
 * Created by wangjinfa on 2015/9/3.
 */

import android.content.Intent;
import android.os.AsyncTask;

import org.qihoo.cube.util.data.Gallery;
import org.qihoo.cube.util.data.Photo;

/**
 * 异步删除照片或将照片移出空间
 */
public class ImageDeleteTask  extends AsyncTask<Object, Integer, Boolean>{

    // 删除photo对象指向的照片
    private Photo photo;
    // photo所属的相册gallery
    private Gallery gallery;
    // 是否删除照片, true表示删除, false表示移出空间
    private Boolean force;

    // 监听删除图片
    private AsyncTaskListener taskListener;

    public ImageDeleteTask(AsyncTaskListener taskListener) {
        this.taskListener = taskListener;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Object... params) {
        photo = (Photo) params[0];
        gallery = (Gallery)params[1];
        force = (Boolean) params[2];

        return gallery.deletePhotoFile(photo, force);
    }

    @Override
    protected void onPostExecute(Boolean deleted) {
        super.onPostExecute(deleted);
        // 删除成功
        if (deleted) {
            taskListener.onAsyncTaskComplete(photo);
        }

        // todo 如果有某一个图片删除失败了怎么处理
    }
}
