package org.qihoo.cube.util.security;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by wangjinfa on 2015/8/28.
 */
public class Crypt {

    /**
     *
     * @param srcPath
     * @param decode
     * @param res
     * 返回值是dstPath
     */
    public static void decodePubKeys(String srcPath, String decode, JSONObject res) {
        decodeKey(srcPath, decode,res);
    }

    public static void decodePrikey(String srcPath, String decode, JSONObject res) {
        decodeKey(srcPath, decode, res);
    }

    public static void encodePubKeys(JSONObject src, String encode, String dstPath) {
        encodeKey(src, encode, dstPath);
    }

    public static void encodePriKey(JSONObject src, String encode, String dstPath) {
        encodeKey(src, encode, dstPath);
    }

    public static void encodeKey(JSONObject src, String encode, String dstPath) {

    }

    public static void decodeKey(String srcPath, String decode, JSONObject res) {

    }

    public static void encodeFile(String srcPath, String encode, String dstPath) {

    }

    public static String decodeFile(String srcPath, String decode, String dstPath) {
        return "";
    }

    public static void generateKey(String key) {

    }

    public static void generatePubPriKey(String pub, String pri) {
        generateKey(pub);
        generateKey(pri);
    }
}
