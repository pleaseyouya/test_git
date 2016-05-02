package org.qihoo.cube.layout;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * Created by wangjinfa on 2015/8/27.
 */
public abstract class LayoutAbstract {

    protected Context context;

    protected LayoutInflater inflater;

    protected EventListener listener;

    public abstract void setListener(EventListener listener);

    public abstract View getView();

    public abstract void inflate();

}
