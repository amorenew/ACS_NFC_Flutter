package com.sztvis.mnvrlibrary.domain;

/**
 * Created by Administrator on 2019/8/1.
 */

public class IO extends DataBase{
    private int ioNo;

    private int ioVal;

    public int getIoNo() {
        return ioNo;
    }

    public void setIoNo(int ioNo) {
        this.ioNo = ioNo;
    }

    public int getIoVal() {
        return ioVal;
    }

    public void setIoVal(int ioVal) {
        this.ioVal = ioVal;
    }

    @Override
    public void parseBody(byte[] message) {
        this.setIoNo(message[6]);
        this.setIoVal(message[7]);
    }
}
