package org.qihoo.cube.util.data;

import java.util.Set;

/**
 * Created by wangjinfa on 2015/8/27.
 */
public interface KVDataSet {

    public boolean set(String key, String value);

    public String get(String key);

    public boolean remove(String key);

    public boolean clear();

    public boolean delete();
}
