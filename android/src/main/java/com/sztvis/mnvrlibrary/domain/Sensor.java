package com.sztvis.mnvrlibrary.domain;

import com.sztvis.mnvrlibrary.util.ByteUtil;

/**
 * Created by Administrator on 2019/8/1.
 */

public class Sensor extends DataBase {
    private int sersorType;
    private short sersorId;
    private int param1;
    private int param2;
    private int powerLower;

    public int getPowerLower() {
        return powerLower;
    }

    public void setPowerLower(int powerLower) {
        this.powerLower = powerLower;
    }

    public int getSersorType() {
        return sersorType;
    }

    public void setSersorType(int sersorType) {
        this.sersorType = sersorType;
    }

    public short getSersorId() {
        return sersorId;
    }

    public void setSersorId(short sersorId) {
        this.sersorId = sersorId;
    }

    public int getParam1() {
        return param1;
    }

    public void setParam1(int param1) {
        this.param1 = param1;
    }

    public int getParam2() {
        return param2;
    }

    public void setParam2(int param2) {
        this.param2 = param2;
    }

    @Override
    public void parseBody(byte[] message) {
        this.setSersorType(message[6]);
        this.setSersorId(ByteUtil.subBytesToShort(message,7,2));
        if(message[6] == 0x66){
            this.setParam1(message[9] - 40);
        }else
            this.setParam1(message[9]);
        this.setParam2(message[10]);
        this.setPowerLower(message[11]);

    }
}
