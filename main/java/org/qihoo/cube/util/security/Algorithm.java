package org.qihoo.cube.util.security;

/**
 * Created by wangjinfa on 2015/8/30.
 */
public interface Algorithm {

    public String encode(String text);

    public String decode(String chiper);
}
