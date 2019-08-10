package com.sztvis.mnvrlibrary.util;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.ByteBuffer;

/**
 * Created by Administrator on 2019/8/1.
 */

public class ByteUtil
{
    public static String subBytesToString(byte[] src, int begin, int count){
        return new String(subBytes(src,begin,count));
    }
    public static double subBytesToDouble(byte[] src, int begin, int count){
        return bytes2Double(subBytes(src,begin,count));
    }

    public static int subBytesToInteger(byte[] src, int begin, int count) {
        byte[] bytes = subBytes(src, begin, count);
        return (bytes[0] & 0xff) << 24
                | (bytes[1] & 0xff) << 16
                | (bytes[2] & 0xff) << 8
                | (bytes[3] & 0xff);

    }

    public static short subBytesToShort(byte[] src, int begin, int count) {
        byte[] bytes = subBytes(src, begin, count);
        short n = (short) (((bytes[0] & 0xff) << 8) | (bytes[1] & 0xff));
        return n;
    }

    public static String bytesToTimeString(byte[] bytes,int begin,int count){
        byte[] byte2 = subBytes(bytes,begin,count);
        String timeString = "";
        for(int i=0;i<byte2.length;i++){
            timeString+=(byte2[i]==0x00?"00":(byte2[i]<10?"0"+byte2[i]:byte2[i]+""))+(i==2?" ":(i>2?":":"-"));
        }
        String utcTime = "20"+timeString.substring(0,timeString.length()-1);
        return TimeUtil.utc2Local(utcTime);
    }

    public static String bytesToTimeString(byte[] bytes){
        byte[] byte2 = subBytes(bytes,20,6);
        String timeString = "";
        for(int i=0;i<byte2.length;i++){
            timeString+=(byte2[i]==0x00?"00":(byte2[i]<10?"0"+byte2[i]:byte2[i]+""))+(i==2?" ":(i>2?":":"-"));
        }
        String utcTime = "20"+timeString.substring(0,timeString.length()-1);
        return TimeUtil.utc2Local(utcTime);
    }

    public static double bytes2Double(byte[] arr) {
        long value = 0;
        for (int i = 0; i < 8; i++) {
            value |= ((long) (arr[i] & 0xff)) << (8 * i);
        }
        return Double.longBitsToDouble(value);
    }
    public static long subBytesToLong(byte[] src, int begin, int count){
        ByteBuffer buffer = ByteBuffer.allocate(8);
        byte[] bytes = subBytes(src,begin,count);
        buffer.put(bytes,0,bytes.length);
        return buffer.getLong();
    }

    public static byte[] subBytes(byte[] src, int begin, int count) {
        byte[] bs = new byte[count];
        System.arraycopy(src, begin, bs, 0, count);
        return bs;
    }

    public static byte[] Int2ByteArray(int val) {
        return ByteBuffer.allocate(4).putInt(val).array();
    }
    /**
     * short转换为byte[]
     * @param number
     * @return byte[]
     */
    public static byte[] shortToByte(short number) {
        int temp = number;
        byte[] b = new byte[2]; // 将最低位保存在最低位
        b[0] = (byte)(temp & 0xff);
        temp = temp >> 8; // 向右移8位
        b[1] = (byte)(temp & 0xff);
        return b;
    }

    /**
     * byte数组转hex
     * @param bytes
     * @return
     */
    public static String byteToHex(byte[] bytes){
        String strHex = "";
        StringBuilder sb = new StringBuilder("");
        for (int n = 0; n < bytes.length; n++) {
            strHex = Integer.toHexString(bytes[n] & 0xFF);
            sb.append((strHex.length() == 1) ? "0" + strHex : strHex); // 每个字节由两个字符表示，位数不够，高位补0
            sb.append(" ");
        }
        return sb.toString().trim();
    }


}
