package com.sztvis.mnvrlibrary.domain.resp;

/**
 * Created by Administrator on 2019/8/1.
 */

public class DeviceStatusResult extends RespBase{
    private short version;
    private int timelong;

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
}
