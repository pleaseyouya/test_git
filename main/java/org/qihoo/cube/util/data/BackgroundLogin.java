package org.qihoo.cube.util.data;

import org.qihoo.cube.activity.MainActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

public class BackgroundLogin {

    public static void stop(Context mContext, int stateNumber){
        Activity activity = (Activity) mContext;

        ApplicationManager application = (ApplicationManager)activity.getApplication();

        if (application.getState() == stateNumber){
            application.setFlagForHome(true);
            application.setState(-1);
        }else{
            application.setFlagForHome(false);
        }
    }

    public static boolean restart(Context mContext){
        Activity activity = (Activity) mContext;

        ApplicationManager application = (ApplicationManager)activity.getApplication();

        if (!application.getFlagForHome()){
            application.setFlagForHome(true);
            return true;
        }else{
            return false;
        }
    }

    public static void resume(Context mContext, int stateNumber){
        Activity activity = (Activity) mContext;

        ApplicationManager application = (ApplicationManager)activity.getApplication();

        application.setState(stateNumber);
    }

}
