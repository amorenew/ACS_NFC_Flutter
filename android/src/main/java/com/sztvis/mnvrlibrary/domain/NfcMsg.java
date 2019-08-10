package com.sztvis.mnvrlibrary.domain;

import com.sztvis.mnvrlibrary.util.ByteUtil;

/**
 * Created by Administrator on 2019/8/2.
 */

public class NfcMsg extends DataBase {
    private String nfcId;
    private byte[] nfcByte;
    public String getNfcId() {
        return nfcId;
    }

    public void setNfcId(String nfcId) {
        this.nfcId = nfcId;
    }

    public byte[] getNfcByte() {
        return nfcByte;
    }

    public void setNfcByte(byte[] nfcByte) {
        this.nfcByte = nfcByte;
    }

    @Override
    public void parseBody(byte[] message) {
        byte[] nfcIdByte = ByteUtil.subBytes(message,6,28);
        this.setNfcId(new String(nfcIdByte));
        this.setNfcByte(nfcIdByte);
    }
}
