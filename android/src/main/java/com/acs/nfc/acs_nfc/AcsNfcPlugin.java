package com.acs.nfc.acs_nfc;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.sztvis.mnvrlibrary.Msdk;
import com.sztvis.mnvrlibrary.domain.DeviceStatus;
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
import com.sztvis.mnvrlibrary.listener.OnConnectLinstener;
import com.sztvis.mnvrlibrary.listener.OnDataListener;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;

/**
 * AcsNfcPlugin
 */
public class AcsNfcPlugin implements FlutterPlugin, MethodCallHandler {

    private static final String ACS_NFC_CONNECTION_STATUS_CHANNEL_NAME = "com.acs.nfc/connection/status";
    private static final String ACS_NFC_DATA_NFC_CHANNEL_NAME = "com.acs.nfc/data/nfc";
    private static PublishSubject<Boolean> nfcConnectionStatus = PublishSubject.create();
    private static PublishSubject<String> nfcDataStatus = PublishSubject.create();
    Msdk msdk;
    private Result result;

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "acs_nfc");
        initConnectionStatus(registrar.messenger());
        initNFCData(registrar.messenger());

        final AcsNfcPlugin instance = new AcsNfcPlugin();

//        nfcConnectionStatusEventChannel.setStreamHandler(instance);
//        nfcDataEventChannel.setStreamHandler(instance);

        channel.setMethodCallHandler(instance);
    }

    @Override
    public void onAttachedToEngine(FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), "acs_nfc");
        initConnectionStatus(flutterPluginBinding.getBinaryMessenger());
        initNFCData(flutterPluginBinding.getBinaryMessenger());

        final AcsNfcPlugin instance = new AcsNfcPlugin();

//        nfcConnectionStatusEventChannel.setStreamHandler(instance);
//        nfcDataEventChannel.setStreamHandler(instance);

        channel.setMethodCallHandler(instance);

    }

    @Override
    public void onDetachedFromEngine(FlutterPluginBinding flutterPluginBinding) {

    }


    private static void initConnectionStatus(BinaryMessenger messenger) {
        final EventChannel nfcConnectionStatusEventChannel = new EventChannel(messenger,
                ACS_NFC_CONNECTION_STATUS_CHANNEL_NAME);
        nfcConnectionStatusEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, final EventChannel.EventSink eventSink) {
                nfcConnectionStatus
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<Boolean>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Boolean connected) {
                        Log.d("connection", "======Java========");
                        if (connected != null)
                            eventSink.success(connected);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    private static void initNFCData(BinaryMessenger messenger) {
        final EventChannel nfcDataEventChannel = new EventChannel(messenger, ACS_NFC_DATA_NFC_CHANNEL_NAME);
        nfcDataEventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object o, final EventChannel.EventSink eventSink) {
                nfcDataStatus
                        .subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String nfcJson) {
                        // Log.d("nfc","======Java========");
                        // Log.d("nfc","======nfc: "+nfcJson);
                        eventSink.success(nfcJson);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
            }

            @Override
            public void onCancel(Object o) {

            }
        });
    }

    @Override
    public void onMethodCall(MethodCall call, Result result) {
        this.result = result;

        if (call.method.equals("getPlatformVersion")) {
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("openConnection")) {
            String ip = call.argument("ip");
            String port = call.argument("port");
            openConnection(ip, port);
        } else {
            result.notImplemented();
        }
    }

    private void openConnection(String ip, String port) {
        msdk = new Msdk(ip, Integer.parseInt(port));
        initConnectionListener();
        initDataListener();
        initCommandListener();
        msdk.init();

        // //search device status
        msdk.searchDeviceStatus();
        //
        // //search io state
        msdk.searchIOState();
        //
        // //search reply state
        msdk.searchReplyState();
        //
        // //search rfid distance
        msdk.searchRfidDistance();
        //
        // //control led (0: led went out 1:green 2:red 3:orange)
        // msdk.controlLed(int status);
        //
        // //control reply (no: reply no state:1 open 0 close)
        // msdk.controlReply(int no,int state);
        //
        // //control buzzer
        // msdk.controlBuzzer();
        //
        // //bind sensor
        // //type:
        // //64: Infra-red 65：sos button 66:temperature and humidity sensor 68:smoke
        // sensor 77:door sensor 7:wireless alarm sensor
        // //val:
        // //sensor id
        // msdk.setSensorBind(int type,short val);
        //
        //
        // // set rfid distane level (1-4)
        // msdk.setRfidDistance(int level);
        result.success(true);

    }

    private void initConnectionListener() {
        msdk.setOnConnectLinstener(new OnConnectLinstener() {
            @Override
            public void OnConnectFailed(int errorCode, String msg) {
                if (msg == null)
                    msg = "null";
                Log.d("Connection", "Connection MSDK failed error code:" + errorCode + " Message: " + msg);
                nfcConnectionStatus.onNext(false);
//                eventSink.success(false);
            }

            @Override
            public void onConnectSuccess() {
                nfcConnectionStatus.onNext(true);
                // eventSink.success(true);

                msdk.searchDeviceStatus();
                msdk.searchIOState();
                msdk.searchReplyState();
                msdk.searchRfidDistance();
            }
        });
    }

    private void initDataListener() {
        msdk.setOnDataListener(new OnDataListener() {
            @Override
            public void OnHeartBeatMsgReceive(HeartBeat heartBeat) {
                Log.d("HeartBeat", JSON.toJSONString(heartBeat));
            }

            @Override
            public void OnGpsMsgReceive(GpsInfo gpsInfo) {
                Log.d("GpsInfo", JSON.toJSONString(gpsInfo));
            }

            @Override
            public void OnGsensorMsgReceive(GSensor gSensor) {
                Log.d("GSensor", JSON.toJSONString(gSensor));
            }

            @Override
            public void OnIoStateChange(IO io) {
                Log.d("IoState", JSON.toJSONString(io));
            }

            @Override
            public void OnReplyChange(Reply reply) {
                Log.d("Reply", JSON.toJSONString(reply));
            }

            @Override
            public void OnRfidMsgReceive(Rfid rfid) {
                Log.d("Rfid", JSON.toJSONString(rfid));
            }

            @Override
            public void OnDeviceStatusMsgReceive(DeviceStatus deviceStatus) {
                Log.d("DeviceStatus", JSON.toJSONString(deviceStatus));
            }

            @Override
            public void OnSensorMsgReceive(Sensor sensor) {
                Log.d("SensorAlarm", JSON.toJSONString(sensor));
            }

            @Override
            public void onNfcMsgReceive(final NfcMsg nfcMsg) {
                byte[] nfcBytes = nfcMsg.getNfcByte();
                String nfcJson = JSON.toJSONString(nfcMsg);
                Log.d("NfcMsg", nfcJson);
                nfcDataStatus.onNext(nfcJson);
                // eventSink.success(nfcJson);
            }
        });
    }

    private void initCommandListener() {
        msdk.setOnCommandResultListener(new OnCommandResultListener() {
            @Override
            public void onRfidDistanceResult(RespBase result) {

            }

            @Override
            public void onSensorBindResult(RespBase result) {

            }

            @Override
            public void onSearchDeviceStatus(DeviceStatusResult result) {

            }

            @Override
            public void onSearchIoStatusResult(IoResult result) {

            }

            @Override
            public void onSearchRfidDistanceResult(RfidDistanceResult result) {

            }

            @Override
            public void onSearchReplyResult(ReplyStateResult result) {

            }

            @Override
            public void onControlLedResult(RespBase result) {

            }

            @Override
            public void onControlReplayResult(ControlReplyResult result) {

            }
        });
    }


}
