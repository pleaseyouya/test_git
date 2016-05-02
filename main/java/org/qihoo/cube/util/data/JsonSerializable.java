package org.qihoo.cube.util.data;

import org.json.JSONObject;

/**
 * Created by wangjinfa on 2015/9/5.
 */
public interface JsonSerializable {

    // 对象转化为Json
    public JSONObject toJson();

    // 将Json转化为对象
    public void fromJson(JSONObject  json);
}
