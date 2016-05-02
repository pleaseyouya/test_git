package org.qihoo.cube.util.security;

import org.json.JSONObject;

/**
 * Created by wangjinfa on 2015/8/28.
 */
public class KeyUtil {

    public static final String ENCRY_PRIKEY_PATH = "/sdcard/encry_prikey.txt";
    public static final String ENCRY_PUBKEY_PATH = "/sdcard/encry_pubkey.txt";

    public static final String PRIKEY_PATH = "/sdcard/prikey.txt";
    public static final String PUBKEY_PATH = "/sdcard/pubkey.txt";

    public static final String DECODE = "dasdkfajlfjasda";
    public static final String ENCODE = "kskadkafljldaf";

    public static void getCurCodes(JSONObject pubRes, JSONObject priRes) {
        Crypt.decodePubKeys(ENCRY_PUBKEY_PATH, DECODE, pubRes);
        Crypt.decodePrikey(ENCRY_PRIKEY_PATH, DECODE, priRes);
    }

    public static void updateCurCodes(JSONObject pubRes, JSONObject priRes) {
        Crypt.encodePubKeys(pubRes, ENCODE, ENCRY_PUBKEY_PATH);
        Crypt.encodePriKey(priRes, ENCODE, ENCRY_PRIKEY_PATH);
    }

}
