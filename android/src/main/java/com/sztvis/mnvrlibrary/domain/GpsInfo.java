package com.sztvis.mnvrlibrary.domain;

import android.util.Log;

import com.sztvis.mnvrlibrary.Msdk;
import com.sztvis.mnvrlibrary.util.ByteUtil;

import java.lang.annotation.Documented;
import java.text.DecimalFormat;

import java.util.StringTokenizer;

/**
 * Created by Administrator on 2019/8/1.
 */

public class GpsInfo extends DataBase {
    private String time;

    private double longitude;

    private double latitude;

    private int lonhemisphere;

    private int lathemisphere;

    private double speed;

    private double derection;

    private double aititude;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public int getLonhemisphere() {
        return lonhemisphere;
    }

    public void setLonhemisphere(int lonhemisphere) {
        this.lonhemisphere = lonhemisphere;
    }

    public int getLathemisphere() {
        return lathemisphere;
    }

    public void setLathemisphere(int lathemisphere) {
        this.lathemisphere = lathemisphere;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getDerection() {
        return derection;
    }

    public void setDerection(double derection) {
        this.derection = derection;
    }

    public double getAititude() {
        return aititude;
    }

    public void setAititude(double aititude) {
        this.aititude = aititude;
    }

    @Override
    public void parseBody(byte[] message) {
        String nmea = new String(ByteUtil.subBytes(message, 6, message.length - 10));
        if (nmea.indexOf("$GNRMC") > -1 || nmea.indexOf("$GNGGA") > -1) {
            Log.d("mnvrlibrary-nmea", nmea);
            Msdk.gpsList.add(nmea);
        }
        this.parseGPRMC();

    }

    public int parseBody2(byte[] message){
        String nmea = new String(ByteUtil.subBytes(message, 6, message.length - 10));
        if (nmea.indexOf("$GNRMC") > -1 || nmea.indexOf("$GNGGA") > -1) {
            Log.d("mnvrlibrary-nmea", nmea);
            Msdk.gpsList.add(nmea);
        }
        return this.parseGPRMC();
    }

    /*
     * 解析串口接收到的字节串（RMC）推荐定位信息
     * $GPRMC,013946,A,3202.1855,N,11849.0769,E,0.05,218.30,111105,4.5,W,A*20..
     * $GPRMC,<1> ,2,<3> ,4,<5> ,6,<7> ,<8> ,<9> ,10,11,12*hh<CR><LF>
     * <1>UTC时间，hhmmss（时分秒）格式 <2> 定位状态，A=有效定位，V=无效定位
     * <3>纬度ddmm.mmmm（度分）格式（前面的0也将被传输） <4> 纬度半球N（北半球）或S（南半球）
     * <5>经度dddmm.mmmm（度分）格式（前面的0也将被传输） <6> 经度半球E（东经）或W（西经）
     * <7>地面速率（000.0~999.9节，前面的0也将被传输） <8> 地面航向（000.0~359.9度，以真北为参考基准，前面的0也将被传输）
     * <9> UTC日期，ddmmyy（日月年）格式 <10> 磁偏角（000.0~180.0度，前面的0也将被传输）
     * <11>磁偏角方向，E（东）或W（西） <12> 模式指示（仅NMEA0183 3.00版本输出，A=自主定位，D=差分，E=估算，N=数据无效）
     *
     *
     * 返回值 0 正确 1校验失败 2非GPRMC信息 3无效定位 4格式错误 5校验错误
     */
    public int parseGPRMC() {
        if(1+1==2)
            return 0;
        if (Msdk.gpsList.size() == 2) {
            for (int i = 0; i < Msdk.gpsList.size(); i++) {
                String[] bodys = Msdk.gpsList.get(i).split(",");
                if (bodys[0].equals("$GNRMC") && bodys[2].equals("A")) {
                    String ymd = bodys[9];
                    String hms = bodys[1];
                    this.setTime("20" + ymd.substring(4, 6) + "-" + ymd.substring(2, 4) + "-" + ymd.substring(0, 2) + " " + hms.substring(0, 2) + ":" + hms.substring(2, 4) + ":" + hms.substring(4, 6));
                    this.setLongitude(Double.valueOf(bodys[5].substring(0, 3)) + (Double.valueOf(bodys[5].substring(3, bodys[5].length() - 3)) / 60));
                    this.setLatitude(Double.valueOf(bodys[3].substring(0, 2)) + (Double.valueOf(bodys[3].substring(2, bodys[3].length() - 2)) / 60));
                    this.setSpeed((bodys[7].equals("") || bodys[7] == null) ? 0 : (int) (Double.valueOf(bodys[7]) * 1.852));
                    this.setDerection((bodys[8].equals("") || bodys[8] == null) ? 0 : Double.valueOf(bodys[8]));
                    this.setLonhemisphere(bodys[6].equals("E") ? 1 : 2);
                    this.setLathemisphere(bodys[4].equals("N") ? 1 : 2);
                }
                if (bodys[0].equals("$GNGGA")) {
                    this.setAititude(Double.valueOf(bodys[9]));
                }
            }
            Msdk.gpsList.clear();
            return 0;
        }
        return 1;
    }

    private boolean checksum(byte[] b) {
        byte chk = 0;// 校验和
        byte cb = b[1];// 当前字节
        int i = 0;
        if (b[0] != '$')
            return false;
        for (i = 2; i < b.length; i++)//计算校验和
        {
            if (b[i] == '*')
                break;
            cb = (byte) (cb ^ b[i]);
        }
        if (i != b.length - 3)//校验位不正常
            return false;
        i++;
        byte[] bb = new byte[2];//用于存放语句后两位
        bb[0] = b[i++];
        bb[1] = b[i];
        try {
            chk = (byte) Integer.parseInt(new String(bb), 16);//后两位转换为一个字节
        } catch (Exception e)//后两位无法转换为一个字节，格式错误
        {
            return false;
        }
        System.out.println("校验信息");
        System.out.println("    原文：" + chk);
        System.out.println("    计算：" + cb);
        return chk == cb;
    }


}
