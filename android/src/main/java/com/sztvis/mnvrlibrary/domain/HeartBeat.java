package com.sztvis.mnvrlibrary.domain;

import com.sztvis.mnvrlibrary.util.ByteUtil;


/**
 * Created by Administrator on 2019/8/1.
 */

public class HeartBeat extends DataBase{
    private short version;
    private int timelong;
    private String deviceCode;

    public short getVersion() {
        return version;
    }

    public void setVersion(short version) {
        this.version = version;
    }

    public int getTimelong() {
        return timelong;
    }

    public void setTimelong(int timelong) {
        this.timelong = timelong;
    }

    public String getDeviceCode() {
        return deviceCode;
    }

    public void setDeviceCode(String deviceCode) {
        this.deviceCode = deviceCode;
    }

    @Override
    public void parseBody(byte[] message) {
        this.setVersion(ByteUtil.subBytesToShort(message,6,2));
        this.setTimelong(ByteUtil.subBytesToInteger(message,8,4));
        this.setDeviceCode(ByteUtil.subBytesToString(message,12,20).replaceAll("\\u0000",""));
    }
}
