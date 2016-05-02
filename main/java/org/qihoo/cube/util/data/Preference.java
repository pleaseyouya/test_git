package org.qihoo.cube.util.data;


import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;
import org.qihoo.cube.util.file.FileUtil;
import org.qihoo.cube.util.security.Algorithm;
import org.qihoo.cube.util.security.Base64Utils;
import org.qihoo.cube.util.security.Crypt;
import org.qihoo.cube.util.security.NameGenerator;

import java.util.HashMap;

/**
 * Created by wangjinfa on 2015/8/28.
 */
public class Preference {

    public static final String BASE_PATH = "/mnt/sdcard/cube";

    private Algorithm nameGenerator;

    private KVPreference settings;

    private String password;

    // 当前的相册
    private Gallery currentGallery;
    // 当前的照片
    private Photo currentPhoto;
    // 当前的空间
    private Zone currentZone;

    private static Preference instance = null;
    public static Context applicationContext;

    private Preference() {
        settings = new KVPreference(applicationContext);
        nameGenerator = new NameGenerator();
    }

    public static Preference getInstance() {
        if (instance == null) {
            instance = new Preference();
        }
        return instance;
    }

    public static void setApplicationContext(Context context) {
        applicationContext = context;
    }

    public boolean existZone() {
        if (settings.get(password) == null ) {
            return false;
        }
        return true;
    }

    /**
     *
     *
     * @return
     * 如果password为空或者""，添加密码失败
     *
     */
    public boolean addZone() {
        if (password == null || password.isEmpty()) {
            return false;
        }
        if (existZone()) {
            return true;
        }

        Zone emptyZone = new Zone(password);
        String zonePath =BASE_PATH + "/" + Base64Utils.encode(password.getBytes());
        emptyZone.setZonePath(zonePath);
        settings.set(password, emptyZone.toJson().toString());

        //建立空文件夹
        FileUtil.mkdirs(zonePath);
        return true;
    }

    public Zone getZoneFromPreference() {
        Zone zone = new Zone();
        JSONObject json = null;
        try {
            json = new JSONObject(settings.get(password));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        zone.fromJson(json);
        return zone;
    }

    public String get(String key) {
        return settings.get(key);
    }

    public boolean set(String key, String value) {
        return settings.set(key, value);
    }

    public boolean firstStart() {
        return settings.firstStart();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setCurrentGallery(Gallery gallery) {
        currentGallery = gallery;
    }

    public void setCurrentPhoto(Photo photo) {
        currentPhoto = photo;
    }

    public Gallery getCurrentGallery() {
        return currentGallery;
    }

    public Zone getCurrentZone() {
        if (currentZone != null) {
            return currentZone;
        }
        currentZone = getZoneFromPreference();
        return currentZone;
    }

    public Photo getCurrentPhoto() {
        return currentPhoto;
    }

    public void setCurrentZone(Zone zone) {
        currentZone = zone;
    }

    /**
     * 更新当前空间的数据
     */
    public void updateCurrentZone() {
        JSONObject json = currentZone.toJson();
        settings.set(password, json.toString());
    }
}
