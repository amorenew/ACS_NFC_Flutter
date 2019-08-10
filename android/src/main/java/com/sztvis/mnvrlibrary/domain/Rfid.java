package com.sztvis.mnvrlibrary.domain;

import com.sztvis.mnvrlibrary.util.ByteUtil;

/**
 * Created by Administrator on 2019/8/1.
 */

public class Rfid extends DataBase {
    private int rfidId;

    public int getRfidId() {
        return rfidId;
    }

    public void setRfidId(int rfidId) {
        this.rfidId = rfidId;
    }

    @Override
    public void parseBody(byte[] message) {
        this.setRfidId(ByteUtil.subBytesToInteger(message,6,4));
    }
}
