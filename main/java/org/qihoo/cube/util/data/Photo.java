package org.qihoo.cube.util.data;

import android.os.Environment;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by wangjinfa on 2015/9/1.
 */
public class Photo implements JsonSerializable{

    // 解密后的图片路径
    private String srcPath;
    // 缩略图路径
    private String thumbPath;
    // 图片加密后的路径
    private String encryPath;
    // 图片原来的路径
    private  String originPath;

    // 图片是否被选中
    public boolean checked;

    public void setSrcPath(String srcPath) {
        this.srcPath = srcPath;
    }

    public void setThumbPath(String thumbPath) {
        this.thumbPath = thumbPath;
    }

    public void setEncryPath(String encryPath) {
        this.encryPath = encryPath;
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

    public String getOriginPath() {
        return originPath;
    }

    public void setOriginPath(String originPath) {
        this.originPath = originPath;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public boolean isChecked() {
        return checked;
    }

    @Override
    public JSONObject toJson() {
        JSONObject json = new JSONObject();
        try {
            json.put("srcPath", srcPath);
            json.put("thumbPath", thumbPath);
            json.put("encryPath", encryPath);
            json.put("originPath", originPath);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return json;
    }

    @Override
    public void fromJson(JSONObject json) {
        try {
            setSrcPath(json.getString("srcPath"));
            setThumbPath(json.getString("thumbPath"));
            setEncryPath(json.getString("encryPath"));
            setOriginPath(json.getString("originPath"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
