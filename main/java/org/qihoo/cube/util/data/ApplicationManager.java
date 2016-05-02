package org.qihoo.cube.util.data;

import android.app.Activity;
import android.app.Application;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by wangjinfa on 2015/9/6.
 */
public class ApplicationManager extends Application {

    // 判断activity是否active
    private HashMap<String, Boolean> activityMap = new HashMap<String, Boolean>();

    public static ApplicationManager application = null;

    @Override
    public void onCreate() {
        super.onCreate();
        application = this;
    }

    /**
     * 把Activity和状态放到List中管理
     * @param activity
     * @param isAlive
     */
    public void addActivityStatus(Activity activity, boolean isAlive) {
        if (activityMap.containsKey(activity.getClass().getName())) {
            activityMap.remove(activity.getClass().getName());
            activityMap.put(activity.getClass().getName(), isAlive);
        } else {
            activityMap.put(activity.getClass().getName(), isAlive);
        }
    }

    /**
     * 判断是否有Activity是Active状态
      * @return
     *      true:应用处于前台
     *      false: 应用处于后台
     */
    public boolean isAllActivityAlive() {
        boolean res = false;
        Iterator iter = activityMap.entrySet().iterator();
        while (iter.hasNext()) {
            Map.Entry entry = (Map.Entry) iter.next();
            Object key = entry.getKey();
            boolean value = (Boolean) entry.getValue();
            if (value) {
                return true;
            }
        }
        return false;
    }

    public static ApplicationManager getInstance() {
        return application;
    }

    private int state = -1;
    private boolean flagForHome = true;

    public void setState(int state){
        this.state = state;
    }

    public int getState(){
        return state;
    }

    public void setFlagForHome(boolean flagForHome){
        this.flagForHome = flagForHome;
    }

    public boolean getFlagForHome(){
        return flagForHome;
    }

    private int stateForEndActivity = -1;

    public void setStateForEndActivity(int stateForEndActivity){
        this.stateForEndActivity = stateForEndActivity;
    }

    public int getStateForEndActivity(){
        return stateForEndActivity;
    }
}
