package com.sztvis.mnvrlibrary.domain;

import com.sztvis.mnvrlibrary.util.ByteUtil;

/**
 * Created by Administrator on 2019/8/1.
 */

public class GSensor extends DataBase {
    private int accelerationX;
    private int accelerationY;
    private int accelerationZ;

    public int getAccelerationX() {
        return accelerationX;
    }

    public void setAccelerationX(int accelerationX) {
        this.accelerationX = accelerationX;
    }

    public int getAccelerationY() {
        return accelerationY;
    }

    public void setAccelerationY(int accelerationY) {
        this.accelerationY = accelerationY;
    }

    public int getAccelerationZ() {
        return accelerationZ;
    }

    public void setAccelerationZ(int accelerationZ) {
        this.accelerationZ = accelerationZ;
    }

    @Override
    public void parseBody(byte[] message) {
        this.setAccelerationX(ByteUtil.subBytesToInteger(message,6,6));
        this.setAccelerationY(ByteUtil.subBytesToInteger(message,10,4));
        this.setAccelerationZ(ByteUtil.subBytesToInteger(message,14,4));
    }
}
