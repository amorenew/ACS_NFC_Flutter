package com.sztvis.mnvrlibrary.listener;


import com.sztvis.mnvrlibrary.domain.DeviceStatus;
import com.sztvis.mnvrlibrary.domain.GSensor;
import com.sztvis.mnvrlibrary.domain.GpsInfo;
import com.sztvis.mnvrlibrary.domain.HeartBeat;
import com.sztvis.mnvrlibrary.domain.IO;
import com.sztvis.mnvrlibrary.domain.NfcMsg;
import com.sztvis.mnvrlibrary.domain.Reply;
import com.sztvis.mnvrlibrary.domain.Rfid;
import com.sztvis.mnvrlibrary.domain.Sensor;

/**
 * Created by Administrator on 2019/8/1.
 */

public interface OnDataListener {

    void OnHeartBeatMsgReceive(HeartBeat heartBeat);

    void OnGpsMsgReceive(GpsInfo gpsInfo);

    void OnGsensorMsgReceive(GSensor gSensor);

    void OnIoStateChange(IO io);

    void OnReplyChange(Reply reply);

    void OnRfidMsgReceive(Rfid rfid);

    void OnDeviceStatusMsgReceive(DeviceStatus deviceStatus);

    void OnSensorMsgReceive(Sensor sensor);

    void onNfcMsgReceive(NfcMsg nfcMsg);

}
