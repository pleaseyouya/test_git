package org.qihoo.cube.util.security;

import android.content.Intent;

/**
 * Created by wangjinfa on 2015/8/30.
 */
public class NameGenerator implements Algorithm{
    @Override
    public String encode(String text) {
        return (5*Integer.parseInt(text)) + "";
    }

    @Override
    public String decode(String chiper) {
        return (Integer.parseInt(chiper))/5 + "";
    }
}
