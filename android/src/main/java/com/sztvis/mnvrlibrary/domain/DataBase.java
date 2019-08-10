package com.sztvis.mnvrlibrary.domain;

import com.sztvis.mnvrlibrary.util.ByteUtil;

import java.io.Serializable;
import java.text.DecimalFormat;


/**
 * Created by Administrator on 2019/8/1.
 */

public class DataBase implements Serializable {
    protected DecimalFormat df = new DecimalFormat("#.0");
    public void parseBody(byte[] message){

    }

    protected String getTime(byte[] bytes,int begin,int end) {
       return ByteUtil.bytesToTimeString(bytes, begin, end);
    }
}
