package org.qihoo.cube.util.image;

import java.util.List;

import org.qihoo.cube.util.data.Gallery;
import org.qihoo.cube.util.data.Photo;
import org.qihoo.cube.util.data.Preference;
import org.qihoo.cube.util.security.AESFunc;
import org.qihoo.cube.util.view.Thumbnail;

import android.os.AsyncTask;
import android.util.Log;

public class ImageLoadTask extends AsyncTask<Object, Void, Boolean>{

    private List<String> selectedDataList;

    private AsyncTaskListener listener;

    public ImageLoadTask(List<String> selectedDataList, AsyncTaskListener listener) {
        // TODO Auto-generated constructor stub
        this.selectedDataList = selectedDataList;
        this.listener = listener;
    }
    @Override
    protected Boolean doInBackground(Object... params) {
        // TODO Auto-generated method stub
        // 新增照片
        Preference userDetail = Preference.getInstance();
        Gallery gallery = userDetail.getCurrentGallery();
        for (String srcPath: selectedDataList) {
            // todo 设置照片的原路径, 缩略图路径, 加密文件路径，解密文件路径,加入相册   
            Photo photo = new Photo();
            photo.setOriginPath(srcPath);
            photo.setSrcPath(gallery.getSrcPath() + "/" + System.currentTimeMillis());
            photo.setThumbPath(gallery.getThumbPath() + "/" + System.currentTimeMillis());
            photo.setEncryPath(gallery.getEncryPath() + "/" + System.currentTimeMillis());
            gallery.addPhoto(photo);

            Log.i("Add Photo", "Origin:" + photo.getOriginPath());
            Log.i("Add Photo", "Src:" + photo.getSrcPath());
            Log.i("Add Photo", "Thumb:" + photo.getThumbPath());
            Log.i("Add Photo", "Encry:" + photo.getEncryPath());

            // todo 生成缩略图
            Thumbnail.thumbnailMaker(srcPath, photo.getThumbPath());

            // todo 加密照片
            AESFunc.encryptFile(srcPath, userDetail.getPassword(), photo.getEncryPath());
        }
        // 更新用户数据
        userDetail.updateCurrentZone();
        return true;
    }

    @Override
    protected void onPostExecute(Boolean result) {
        // TODO Auto-generated method stub
        super.onPostExecute(result);

        listener.onAsyncTaskComplete();
    }


}
