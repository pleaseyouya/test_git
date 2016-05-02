package org.qihoo.cube.util.security;

import android.util.Log;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.crypto.spec.IvParameterSpec;




public class AESFunc {
    /**
     * 转化key
     * @param seed
     *          原始key.getBytes()
     * @return
     *          转化后的key
     * @throws Exception
     */
    private static byte[] getRawKey(byte[] seed) throws Exception {
        KeyGenerator kgen = KeyGenerator.getInstance("AES");
        SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");
        sr.setSeed(seed);
        kgen.init(128, sr); // 192 and 256 bits may not be available
        SecretKey skey = kgen.generateKey();
        byte[] raw = skey.getEncoded();
        return raw;
    }

    /**
     * 加密函数
     * @param clear
     * * @param rawKey
     * @return
     * @throws Exception
     */
    private static byte[] encrypt( byte[] clear,byte[] rawKey) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, skeySpec,new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] encrypted = cipher.doFinal(clear);
        return encrypted;
    }

    /**
     * 解密函数
     * @param encrypted
     * @param rawKey
     * @return
     * @throws Exception
     */
    private static byte[] decrypt( byte[] encrypted,byte[] rawKey) throws Exception {
        SecretKeySpec skeySpec = new SecretKeySpec(rawKey, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec,new IvParameterSpec(new byte[cipher.getBlockSize()]));
        byte[] decrypted = cipher.doFinal(encrypted);
        return decrypted;
    }

    /**
     * 加密指定文件
     * @param inputFile
     * 			待加密的文件
     * @param key
     * 			加密用的密钥，字符串格式
     * @param outputPath
     * 			加密后文件的路径
     */
    public static void encryptFile(String inputFile, String key, String outputPath){
        try {
            byte[] fileBytes;
            fileBytes = SerialUtil.fileToBytes(inputFile);

            // 把源地址路径补上
            byte[] pathBytes = inputFile.getBytes();
            int len = pathBytes.length;
            byte[] lenBytes = SerialUtil.intToBytes(len);
            int totalLen = lenBytes.length + pathBytes.length + fileBytes.length;
            byte[] raw = Arrays.copyOf(lenBytes, totalLen);
            // lenBytes.length == 4
            System.arraycopy(pathBytes, 0, raw, 4, pathBytes.length);
            System.arraycopy(fileBytes, 0, raw, 4 + pathBytes.length, fileBytes.length);

            byte[] rawKey = getRawKey(key.getBytes());
            byte[] encryptResult = encrypt(raw, rawKey);

            SerialUtil.bytesToFile(encryptResult, outputPath);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.err.println("文件读取错误，请检查输入文件是否存在");
            e.printStackTrace();
        }
    }
    /**
     * 解密指定的文件
     * @param inputFile
     * 			待解密的文件
     * @param key
     * 			解密用的密钥，字符串格式
     * @param outputPath
     * 			解密后的文件路径
     * @return
     * 			返回该文件被加密之前的原始路径
     */
    public static String decryptFile2File(String inputFile, String key, String outputPath){
        try {
            byte[] raw = SerialUtil.fileToBytes(inputFile);
            byte[] rawKey = getRawKey(key.getBytes());
            byte[] result = decrypt(raw, rawKey);
            byte[] lenBytes = Arrays.copyOf(result, 4);
            int pathLen = SerialUtil.bytesToInt(lenBytes, 0);
            byte[] pathBytes = new byte[pathLen];
            System.arraycopy(result, 4, pathBytes, 0, pathLen);
            String path = new String(pathBytes);
            String[] parts = path.split("\\.");
            // 尝试从原始路径中获取文件后缀名补充到输出路径上
//            if (parts.length >= 2) {
//                String postFix = "." + parts[parts.length - 1];
//                if (!outputPath.endsWith(postFix)) {
//                    outputPath += postFix;
//                }
//            }

            int fileLen = result.length - 4 - pathLen;
            byte[] fileBytes = new byte[fileLen];
            System.arraycopy(result, 4 + pathLen, fileBytes, 0, fileLen);
            SerialUtil.bytesToFile(fileBytes, outputPath);
            return path;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            System.err.println("文件读取错误，请检查输入文件是否存在");
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 加密方法
     *
     * @param content
     *            需要加密的内容
     * @param password
     *            加密密码
     * @return
     */
    /*
    public static byte[] encrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器

            //byte[] byteContent = content.getBytes("utf-8");
            cipher.init(Cipher.ENCRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    */
    /**
     * 解密方法
     *
     * @param content
     *            待解密内容
     * @param password
     *            解密密钥
     * @return
     */
    /*
    public static byte[] decrypt(byte[] content, String password) {
        try {
            KeyGenerator kgen = KeyGenerator.getInstance("AES");
            kgen.init(128, new SecureRandom(password.getBytes()));
            SecretKey secretKey = kgen.generateKey();
            byte[] enCodeFormat = secretKey.getEncoded();
            SecretKeySpec key = new SecretKeySpec(enCodeFormat, "AES");
            Cipher cipher = Cipher.getInstance("AES");// 创建密码器
            cipher.init(Cipher.DECRYPT_MODE, key);// 初始化
            byte[] result = cipher.doFinal(content);
            return result; // 加密
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        }
        return null;
    }
    */

}
