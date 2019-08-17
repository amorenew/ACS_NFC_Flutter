package com.sztvis.mnvrlibrary.listener;


/**
 * Created by Administrator on 2019/8/1.
 */

public interface OnConnectLinstener {

    public void OnConnectFailed(int errorCode,String msg);

    public void onConnectSuccess();
}
