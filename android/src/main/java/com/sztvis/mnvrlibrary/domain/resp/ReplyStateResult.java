package com.sztvis.mnvrlibrary.domain.resp;

/**
 * Created by Administrator on 2019/8/2.
 */

public class ReplyStateResult extends RespBase {
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
}
