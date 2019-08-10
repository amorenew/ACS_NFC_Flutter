package com.sztvis.mnvrlibrary.domain;

/**
 * Created by Administrator on 2019/8/1.
 */

public class Reply extends DataBase {
    private int replayNo;

    private int replayVal;

    public int getReplayNo() {
        return replayNo;
    }

    public void setReplayNo(int replayNo) {
        this.replayNo = replayNo;
    }

    public int getReplayVal() {
        return replayVal;
    }

    public void setReplayVal(int replayVal) {
        this.replayVal = replayVal;
    }

    @Override
    public void parseBody(byte[] message) {
        this.setReplayNo(message[6]);
        this.setReplayVal(message[7]);
    }
}
