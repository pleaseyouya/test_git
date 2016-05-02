package org.qihoo.cube.util.data;

import android.content.Context;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by wangjinfa on 2015/8/31.
 */
public class Zone implements JsonSerializable{

    private String zonePath;

    private List<Gallery> gallerys;
    private String password;

    private OnZoneUpdateListener zoneUpdateListener;

    public interface OnZoneUpdateListener {
        public void onZoneUpdate();
    }

    public Zone(String password) {
        this.password = password;
        gallerys = new ArrayList<Gallery>();
    }

    public Zone() {
        gallerys = new ArrayList<Gallery>();
    }

    public void setZonePath(String zonePath) {
        this.zonePath = zonePath;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addGallery(Gallery gallery) {
        gallerys.add(gallery);
    }

    public List<Gallery> getGallerys() {
        return gallerys;
    }

    public String getZonePath() {
        return zonePath;
    }

    public String getPassword() {
        return password;
    }

    public void setOnZoneUpdateListener(OnZoneUpdateListener zoneUpdateListener) {
        this.zoneUpdateListener = zoneUpdateListener;
    }

    /**
     * 全选或者取消全选当前Zone的相册
     * @param checked
     *      true: 全选
     *      false: 取消全选
     */
    public void setAllGallerysChecked(boolean checked) {
        for (Gallery gallery: gallerys) {
            gallery.setChecked(checked);
        }
    }

    /**
     * 判断当前Zone的相册是否被全选
     * @return
     *      true: 相册被全选
     *      false: 未全选
     */
    public boolean isAllGalleryChecked() {
        for (Gallery gallery: gallerys) {
            if (!gallery.isChecked()) {
                return false;
            }
        }
        return true;
    }

    /**
     * 计数当前被选中的相册
     * @return
     */
    public int countCheckedGallerys() {
        int count = 0;
        for (Gallery gallery: gallerys) {
            if (gallery.isChecked()) {
                count++;
            }
        }
        return count;
    }

    /**
     * 删除选中的相册
     */
    public void deleteCheckedGallerys() {
        // todo: 这个删除对象的方法可能是不安全的
        Iterator<Gallery> iterator = gallerys.iterator();
        while (iterator.hasNext()) {
            Gallery gallery = iterator.next();
            // 未被选中,不做处理
            if (!gallery.isChecked()) {
                continue;
            }
            // 清空相册
            gallery.destroy();
            // 删除gallery对象
            iterator.remove();
        }

        // 更新界面
        zoneUpdateListener.onZoneUpdate();
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();

        try {
            json.put("zonePath", zonePath);
            json.put("password", password);
            JSONArray jsonArray = new JSONArray();
            for (Gallery gallery: gallerys) {
                jsonArray.put(gallery.toJson());
            }
            json.put("gallerys", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    @Override
    public void fromJson(JSONObject json) {
        try {
            setZonePath(json.getString("zonePath"));
            setPassword(json.getString("password"));
            JSONArray jsonArray = json.getJSONArray("gallerys");
            for (int i = 0; i < jsonArray.length(); ++i) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                Gallery gallery = new Gallery(jsonObject.getString("name"));
                gallery.fromJson(jsonObject);
                addGallery(gallery);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 判断是否存在同名相册
     */
    public boolean existSameGallery(String galleryName) {
        for (Gallery gallery: gallerys) {
            if (gallery.getName().equals(galleryName)) {
                return true;
            }
        }
        return false;
    }
}
