package org.qihoo.cube.util.data;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.qihoo.cube.util.file.FileUtil;
import org.qihoo.cube.util.image.AsyncTaskListener;
import org.qihoo.cube.util.image.ImageManager;
import org.qihoo.cube.util.security.AESFunc;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangjinfa on 2015/8/31.
 */
public class Gallery implements AsyncTaskListener, JsonSerializable{

    private String name;
    private String galleryPath;
    private String srcPath;
    private String thumbPath;
    private String encryPath;

    private List<Photo> photos;

    private int photoCheckedCount = 0;
    private int photoDeletedCount = 0;

    // gallery是否被选中
    private boolean checked = false;

    // 相册数据变化时的监听接口
    private OnGalleryUpdateListener updateListener;

    public interface OnGalleryUpdateListener {
        public void onGalleryUpdate();
    }

    public void setUpdateListener(OnGalleryUpdateListener updateListener) {
        this.updateListener = updateListener;
    }

    public Gallery(String name) {
        this.name = name;
        photos = new ArrayList<Photo>();
    }

    public void setGalleryPath(String galleryPath) {
        this.galleryPath = galleryPath;
    }

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public void setEncryPath(String encryPath) {
        this.encryPath = encryPath;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getSrcPath() {
        return srcPath;
    }

    public String getThumbPath() {
        return thumbPath;
    }

    public String getEncryPath() {
        return encryPath;
    }

    public boolean isChecked() {
        return checked;
    }

    public boolean empty() {
        return FileUtil.childCount(thumbPath) == 0;
    }

    public List<String> getThumbs() {
        File thumbDir = new File(thumbPath);
        return Arrays.asList(thumbDir.list());
    }

    public void addPhoto(Photo photo) {
        photos.add(photo);
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    /**
     * 从List<Photo>中删除photo对象
     * @param photo
     * @return
     */
    public boolean removePhoto(Photo photo) {
        if (!photos.contains(photo)) {
            return false;
        }
        return photos.remove(photo);
    }

    /**
     *
     * @param photo
     *      当前要删除的photo对象啊
     * @param force
     *      true:删掉照片
     *      false:把照片从当前Cube移出到原来的位置
     * @return
     *      true: 操作成功
     *      false: 当前相册并不含有该照片
     */
    public boolean deletePhoto(Photo photo, boolean force) {
        // 删除photo对象指向的文件
        deletePhotoFile(photo, force);

        // 当前相册删除photo对象
        removePhoto(photo);
        // 取消当前照片设置
        Preference.getInstance().setCurrentPhoto(null);
        return true;
    }

    /**
     * 删除photo对象指向的图片文件
     * @param photo
     *      当前要删除的photo指向的文件
     * @param force
     *      true:删掉照片
     *      false:把照片从当前Cube移出到原来的位置
     * @return
     *      true: 操作成功
     *      false: 当前相册并不含有该照片
     */
    public boolean deletePhotoFile(Photo photo, boolean force) {
        if (!photos.contains(photo)) {
            return false;
        }

        if (force) {
            // 删除图片
            FileUtil.delete(photo.getSrcPath());
        } else {
            // 当前被移出的照片只有缩略图,则直接解密图片
            String srcPath = photo.getSrcPath();
            String originPath = photo.getOriginPath();
            if (!FileUtil.exists(srcPath) || !FileUtil.isFile(srcPath)) {
                String password = Preference.getInstance().getPassword();
                AESFunc.decryptFile2File(photo.getEncryPath(), password, originPath);
            } else {
                // 剪切图片
                FileUtil.moveFile(srcPath, originPath);
            }

            // 更新图库
            ImageManager.insertImage(originPath);
        }

        // 删除加密文件
        FileUtil.delete(photo.getEncryPath());
        // 删除缩略图
        FileUtil.delete(photo.getThumbPath());
        return true;
    }

    /**
     * 全选或者取消全选当前相册照片
     * @param check
     *      true: 全选
     *      false: 取消全选
     */
    public void checkAllPhotos(boolean check) {
        for (Photo photo : photos) {
            photo.setChecked(check);
        }
    }

    /**
     * 统计当前相册中被选中的照片
     * @return
     *      返回当前相册被选中的照片数
     */
    public int countCheckedPhotos() {
        int count = 0;
        for (Photo photo : photos) {
            if (photo.isChecked()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 删除被选中的照片
     * @return
     */
    public boolean deleteCheckedPhotos(boolean force) {
        photoCheckedCount = countCheckedPhotos();
        Iterator<Photo> iterator = photos.iterator();
        while (iterator.hasNext()) {
            Photo photo = iterator.next();
            // 当前照片未被选中, 不做任何处理
            if (!photo.isChecked()) {
                continue;
            }
            deletePhotoFile(photo, force);
            iterator.remove();

//            deletePhotoFile(photo, force);
//            onAsyncTaskComplete(null);
            // 否则, 开一个删除照片的异步任务，删除照片文件
//            ImageDeleteTask imageDeleteTask = new ImageDeleteTask(this);
//            imageDeleteTask.execute(photo, this, force);
        }
        updateListener.onGalleryUpdate();
        return true;
    }

    /**
     * 执行异步任务之后的回调
     * @param objects
     *      异步任务返回的结果
     */
    @Override
    public void onAsyncTaskComplete(Object... objects) {
        photoDeletedCount++;
        if (photoDeletedCount == photoCheckedCount) {
            removeCheckedPhotos();
            updateListener.onGalleryUpdate();
        }
    }

    /**
     * 删除被选中的photo对象
     */
    public void removeCheckedPhotos() {
        List<Photo> checkedPhotos = new ArrayList<Photo>();
        for (Photo photo: photos) {
            if (photo.isChecked()) {
                checkedPhotos.add(photo);
            }
        }
        photos.removeAll(checkedPhotos);
        checkedPhotos.clear();
    }

    /**
     * 清空相册,同时删除该相册的文件夹
     */
    public void destroy() {
        for (Photo photo: photos) {
            // 删除文件
            deletePhotoFile(photo, true);
        }

        // 清空photo对象列表
        photos.clear();

        // 删除缩略图文件夹,原图解密文件夹和加密文件夹
        FileUtil.delete(thumbPath);
        FileUtil.delete(srcPath);
        FileUtil.delete(encryPath);

        // 删除相册根目录
        FileUtil.delete(galleryPath);
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("name", name);
            json.put("galleryPath", galleryPath);
            json.put("srcPath", srcPath);
            json.put("thumbPath", thumbPath);
            json.put("encryPath", encryPath);

            JSONArray jsonArray = new JSONArray();
            for (Photo photo: photos) {
                jsonArray.put(photo.toJson());
            }
            json.put("photos", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void fromJson(JSONObject json) {
        try {
            setName(json.getString("name"));
            setGalleryPath(json.getString("galleryPath"));
            setSrcPath(json.getString("srcPath"));
            setThumbPath(json.getString("thumbPath"));
            setEncryPath(json.getString("encryPath"));
            JSONArray jsonArray = json.getJSONArray("photos");
            for (int i = 0; i < jsonArray.length(); ++i) {
                Photo photo = new Photo();
                photo.fromJson(jsonArray.getJSONObject(i));
                addPhoto(photo);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
