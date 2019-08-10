package com.sztvis.mnvrlibrary.decoder;

import android.util.Log;

import com.sztvis.mnvrlibrary.domain.GSensor;
import com.sztvis.mnvrlibrary.domain.GpsInfo;
import com.sztvis.mnvrlibrary.domain.HeartBeat;
import com.sztvis.mnvrlibrary.domain.IO;
import com.sztvis.mnvrlibrary.domain.NfcMsg;
import com.sztvis.mnvrlibrary.domain.Reply;
import com.sztvis.mnvrlibrary.domain.Rfid;
import com.sztvis.mnvrlibrary.domain.Sensor;
import com.sztvis.mnvrlibrary.domain.resp.ControlReplyResult;
import com.sztvis.mnvrlibrary.domain.resp.DeviceStatusResult;
import com.sztvis.mnvrlibrary.domain.resp.IoResult;
import com.sztvis.mnvrlibrary.domain.resp.ReplyStateResult;
import com.sztvis.mnvrlibrary.domain.resp.RespBase;
import com.sztvis.mnvrlibrary.domain.resp.RfidDistanceResult;
import com.sztvis.mnvrlibrary.listener.OnCommandResultListener;
import com.sztvis.mnvrlibrary.listener.OnDataListener;
import com.sztvis.mnvrlibrary.util.ByteUtil;

/**
 * Created by Administrator on 2019/8/1.
 */

public class MessageDecoder {
    private byte[] message;
    private OnDataListener onDataListener;
    private OnCommandResultListener onCommandResultListener;
    int crc = 0;

    public MessageDecoder(byte[] message, OnDataListener onDataListener, OnCommandResultListener onCommandResultListener) {
        Log.i("mnvrlibrary", ByteUtil.byteToHex(message).toUpperCase());
        this.message = message;
        this.onDataListener = onDataListener;
        this.onCommandResultListener = onCommandResultListener;
        for (int i = 0; i < message.length; i++) {
            message[i] &= 0xFF;
        }
        crc = ((message[message.length - 4] << 8) & 0xFF00) | (message[message.length - 3] & 0xFF);
        message[message.length - 4] = 0x00;
        message[message.length - 3] = 0x00;
    }

    /**
     * check CRC16
     *
     * @return
     */
    public boolean checkCrc() {
        int crc16r = CRC16.CRC16_Check(message);
        if (crc == crc16r)
            return true;
        else
            return false;
    }

    public void decode() {
        switch (message[4]) {
            case 0x01:
                switch (message[5]) {
                    case 0x01:
                        HeartBeat heartBeat = new HeartBeat();
                        heartBeat.parseBody(message);
                        onDataListener.OnHeartBeatMsgReceive(heartBeat);
                        break;
                    case 0x02:
                        GpsInfo gpsInfo = new GpsInfo();
                        int res = gpsInfo.parseBody2(message);
                        if (res == 0)
                            onDataListener.OnGpsMsgReceive(gpsInfo);
                        break;
                    case 0x04:
                        Rfid rfid = new Rfid();
                        rfid.parseBody(message);
                        onDataListener.OnRfidMsgReceive(rfid);
                        break;
                    case 0x05:
                        Sensor sensor = new Sensor();
                        sensor.parseBody(message);
                        onDataListener.OnSensorMsgReceive(sensor);
                        break;
                    case 0x06:
                        GSensor gSensor = new GSensor();
                        gSensor.parseBody(message);
                        onDataListener.OnGsensorMsgReceive(gSensor);
                        break;

                    case 0x08:
                        NfcMsg nfcMsg = new NfcMsg();
                        nfcMsg.parseBody(message);
                        onDataListener.onNfcMsgReceive(nfcMsg);
                        break;
                }
                break;
            case 0x02:
                switch (message[5]) {
                    case 0x01:
                        IO io = new IO();
                        io.parseBody(message);
                        onDataListener.OnIoStateChange(io);
                        break;
                    case 0x02:
                        Reply reply = new Reply();
                        reply.parseBody(message);
                        onDataListener.OnReplyChange(reply);
                        break;
                }
                break;

            case 0x04:
                switch (message[5]) {
                    case 0x01:
                        RespBase res = new RespBase();
                        res.setCode(message[6]);
                        onCommandResultListener.onRfidDistanceResult(res);
                        break;
                    case 0x02:
                        RespBase res1 = new RespBase();
                        res1.setCode(message[6]);
                        onCommandResultListener.onSensorBindResult(res1);
                        break;
                }
                break;
            case 0x05:
                switch (message[5]) {
                    case 0x01:
                        DeviceStatusResult res = new DeviceStatusResult();
                        res.setCode(message[6]);
                        res.setVersion(ByteUtil.subBytesToShort(message, 7, 2));
                        res.setTimelong(ByteUtil.subBytesToInteger(message, 9, 4));
                        onCommandResultListener.onSearchDeviceStatus(res);
                        break;
                    case 0x02:
                        IoResult res1 = new IoResult();
                        res1.setCode(message[6]);
                        res1.setIoNo(message[7]);
                        res1.setIoVal(message[8]);
                        onCommandResultListener.onSearchIoStatusResult(res1);
                        break;
                    case 0x03:
                        RfidDistanceResult res2 = new RfidDistanceResult();
                        res2.setCode(message[6]);
                        res2.setLevel(message[7]);
                        onCommandResultListener.onSearchRfidDistanceResult(res2);
                        break;
                    case 0x04:
                        ReplyStateResult res3 = new ReplyStateResult();
                        res3.setCode(message[6]);
                        res3.setReplayNo(message[7]);
                        res3.setReplayVal(message[8]);
                        onCommandResultListener.onSearchReplyResult(res3);
                        break;
                }
                break;
            case 0x06:
                switch (message[5]) {
                    case 0x01:
                        RespBase res = new RespBase();
                        res.setCode(message[6]);
                        onCommandResultListener.onControlLedResult(res);
                        break;
                    case 0x02:
                        ControlReplyResult res1 = new ControlReplyResult();
                        res1.setCode(message[6]);
                        res1.setReplyNo(message[7]);
                        onCommandResultListener.onControlReplayResult(res1);
                        break;
                }
                break;
        }
    }
}
