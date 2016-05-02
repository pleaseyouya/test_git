package org.qihoo.cube.util.data;

/**
 * Created by wangjinfa on 2015/8/30.
 */
public class AppChecker {

    public static boolean isAppFirstUsed(Preference preference) {
        return preference.firstStart();
    }
}
