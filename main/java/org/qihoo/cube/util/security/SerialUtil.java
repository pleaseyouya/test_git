package org.qihoo.cube.util.security;
/**
 * Created by luanxinglong-xy on 2015/9/1.
 */
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;


public class SerialUtil {
    private static String RSA = "RSA";

    /**
     * Mapped File way MappedByteBuffer 将文件读取到byte数组
     *
     * @param filename
     * @return
     * @throws IOException
     */
    public static byte[] fileToBytes(String filename) throws IOException {

        FileChannel fc = null;
        try {
            Log.i("读文件", "进入读取文件");
            fc = new RandomAccessFile(filename, "r").getChannel();
            MappedByteBuffer byteBuffer = fc.map(MapMode.READ_ONLY, 0,
                    fc.size()).load();
            //System.out.println(byteBuffer.isLoaded());
            byte[] result = new byte[(int) fc.size()];
            if (byteBuffer.remaining() > 0) {
                // System.out.println("remain");
                byteBuffer.get(result, 0, byteBuffer.remaining());
            }
            return result;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                fc.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    /**
     * 将解密后得到的byte数组写入指定路径的文件
     * @param bytes
     * @param outPath
     */
    public static void bytesToFile(byte[] bytes, String outPath){
        try {
            File file = new File(outPath);
            OutputStream out = new FileOutputStream(file);
            out.write(bytes);
            out.flush();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    /**
     * int转为byte数组，固定长度为4
     * @param value
     * @return
     */
    public static byte[] intToBytes( int value )
    {
        byte[] src = new byte[4];
        src[3] =  (byte) ((value>>24) & 0xFF);
        src[2] =  (byte) ((value>>16) & 0xFF);
        src[1] =  (byte) ((value>>8) & 0xFF);
        src[0] =  (byte) (value & 0xFF);
        return src;
    }
    /**
     * byte转为数组，offset默认为0
     * @param ary
     * @param offset
     * 			本程序里用0
     * @return
     */
    public static int bytesToInt(byte[] ary, int offset) {
        int value;
        value = (int) ((ary[offset]&0xFF)
                | ((ary[offset+1]<<8) & 0xFF00)
                | ((ary[offset+2]<<16)& 0xFF0000)
                | ((ary[offset+3]<<24) & 0xFF000000));
        return value;
    }
}
