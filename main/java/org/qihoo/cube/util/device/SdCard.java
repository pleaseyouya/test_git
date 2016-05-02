package org.qihoo.cube.util.device;

import android.os.Environment;
import android.text.AndroidCharacter;

/**
 * Created by wangjinfa on 2015/8/28.
 */
public class SdCard {

    /**
     * 获取sd卡路径
     * @return 如果不存在sd卡，返回null
     */
    public static String getSdPath() {
        String path = null;
         if (isSdExist()) {
             path = Environment.getExternalStorageDirectory().toString();
         }
        return path;
    }

    public static boolean isSdExist() {
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }
}
