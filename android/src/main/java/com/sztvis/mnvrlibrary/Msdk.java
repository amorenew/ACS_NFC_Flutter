package com.sztvis.mnvrlibrary;

import android.os.Build;
import android.util.Log;

import com.sztvis.mnvrlibrary.decoder.CRC16;
import com.sztvis.mnvrlibrary.decoder.MessageDecoder;
import com.sztvis.mnvrlibrary.listener.OnCommandResultListener;
import com.sztvis.mnvrlibrary.listener.OnDataListener;
import com.sztvis.mnvrlibrary.listener.OnConnectLinstener;
import com.sztvis.mnvrlibrary.util.ByteUtil;
import com.sztvis.mnvrlibrary.util.MsdkErrorCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2019/8/1.
 */

public class Msdk {
    public static List<String> gpsList = new ArrayList<>();
    private String host;
    private int port;
    private Socket clientSocket;
    private OutputStream outputStream;
    private InputStream inputStream;
    int ver = 0x03;
    SocketThread socketThread;
    public Msdk(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void init() {
        socketThread = new SocketThread();
        socketThread.start();
    }

    private class SocketThread extends Thread {
        @Override
        public void run() {
            clientSocket = new Socket();
            try {
                clientSocket.connect(new InetSocketAddress(host, port));
                clientSocket.setKeepAlive(true);
                outputStream = clientSocket.getOutputStream();
                inputStream = clientSocket.getInputStream();
                byte[] temp = new byte[1024];
                int size = 0;
                onConnectLinstener.onConnectSuccess();
                //connect success and send android serialnumber
                sendAndroidSerialNumber();
                while ((size = inputStream.read(temp)) > 0) {
                    byte[] res = new byte[size];
                    if (res.length > 5) {
                        System.arraycopy(temp, 0, res, 0, size);
                        int length = 0;
                        for (int i = 0; i < res.length; i++) {
                            if ((res[i] & 0xFF) == 0xDF && (res[i + 1] & 0xFF) == 0xEF) {
                                byte[] bts = new byte[i + 2 - length];
                                System.arraycopy(res, length, bts, 0, i + 2 - length);
                                length = i + 2;
                                MessageDecoder messageDecoder = new MessageDecoder(bts, onDataListener,onCommandResultListener);
                                if (messageDecoder.checkCrc()) {
                                    messageDecoder.decode();
                                } else
                                    onConnectLinstener.OnConnectFailed(MsdkErrorCode.MESSAGE_AUTH_FAILD, "");
                            }
                        }
                    }
                }
            } catch (IOException e) {
                onConnectLinstener.OnConnectFailed(MsdkErrorCode.SOCKET_CONNECT_FAILD, e.getLocalizedMessage());
            } catch (Exception ex) {
                onConnectLinstener.OnConnectFailed(MsdkErrorCode.SOCKET_CONNECT_FAILD, ex.getLocalizedMessage());
            }
        }
    }

    public void close(){
        if(clientSocket!=null && clientSocket.isConnected()){
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(socketThread!=null && socketThread.getState() == Thread.State.RUNNABLE){
            try {
                Thread.sleep(500);
                socketThread.interrupt();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        socketThread = null;
    }

    private OnDataListener onDataListener;

    private OnConnectLinstener onConnectLinstener;

    private OnCommandResultListener onCommandResultListener;


    public void setOnDataListener(OnDataListener onDataListener) {
        this.onDataListener = onDataListener;
    }

    public void setOnConnectLinstener(OnConnectLinstener onConnectLinstener) {
        this.onConnectLinstener = onConnectLinstener;
    }

    public void setOnCommandResultListener(OnCommandResultListener onCommandResultListener) {
        this.onCommandResultListener = onCommandResultListener;
    }

    private void writeMsg(final byte[] bytes) {
        Thread writeThread = new Thread() {
            @Override
            public void run() {
                if (clientSocket != null && clientSocket.isConnected() && !clientSocket.isOutputShutdown()) {
                    try {
                        outputStream.write(bytes, 0, bytes.length);
                        outputStream.flush();
                        Log.d("mnvrlibrary-send",ByteUtil.byteToHex(bytes));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
        };
        writeThread.start();
    }


    public void setRfidDistance(int level) {
        if (level < 0)
            level = 1;
        if (level > 4)
            level = 4;
        byte[] bytes = new byte[]{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x04, 0x01, (byte) level, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
        short crc16 = (short) CRC16.CRC16_Check(bytes);
        bytes[bytes.length - 4] = (byte) (crc16 >> 8);
        bytes[bytes.length - 3] = (byte) crc16;
        writeMsg(bytes);
    }

    public void setSensorBind(int type, boolean bind,short sensorId) {

        byte[] bytes = new byte[]{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x04, 0x02, (bind ? (byte)0x01 : (byte)0x02), (byte) type, (byte) (sensorId >> 8), (byte) sensorId, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
        short crc16 = (short) CRC16.CRC16_Check(bytes);
        bytes[bytes.length - 4] = (byte) (crc16 >> 8);
        bytes[bytes.length - 3] = (byte) crc16;
        writeMsg(bytes);
    }

    public void searchDeviceStatus(){
        byte[] bytes = new byte[]{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x05, 0x01, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
        short crc16 = (short) CRC16.CRC16_Check(bytes);
        bytes[bytes.length - 4] = (byte) (crc16 >> 8);
        bytes[bytes.length - 3] = (byte) crc16;
        writeMsg(bytes);
    }

    public void searchIOState(){
        byte[] bytes = new byte[]{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x05, 0x02, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
        short crc16 = (short) CRC16.CRC16_Check(bytes);
        bytes[bytes.length - 4] = (byte) (crc16 >> 8);
        bytes[bytes.length - 3] = (byte) crc16;
        writeMsg(bytes);
    }

    public void searchRfidDistance(){
        byte[] bytes = new byte[]{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x05, 0x03, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
        short crc16 = (short) CRC16.CRC16_Check(bytes);
        bytes[bytes.length - 4] = (byte) (crc16 >> 8);
        bytes[bytes.length - 3] = (byte) crc16;
        writeMsg(bytes);
    }

    public void searchReplyState(){
        byte[] bytes = new byte[]{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x05, 0x04, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
        short crc16 = (short) CRC16.CRC16_Check(bytes);
        bytes[bytes.length - 4] = (byte) (crc16 >> 8);
        bytes[bytes.length - 3] = (byte) crc16;
        writeMsg(bytes);
    }

    public void controlLed(int status){
        byte[] bytes = new byte[]{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x06, 0x01, (byte) status, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
        short crc16 = (short) CRC16.CRC16_Check(bytes);
        bytes[bytes.length - 4] = (byte) (crc16 >> 8);
        bytes[bytes.length - 3] = (byte) crc16;
        writeMsg(bytes);
    }

    public void controlReply(int no,int state){
        byte[] bytes = new byte[]{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x06, 0x02, (byte) no,(byte)state, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
        short crc16 = (short) CRC16.CRC16_Check(bytes);
        bytes[bytes.length - 4] = (byte) (crc16 >> 8);
        bytes[bytes.length - 3] = (byte) crc16;
        writeMsg(bytes);
    }

    public void controlBuzzer(){
        byte[] bytes = new byte[]{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x06, 0x03, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
        short crc16 = (short) CRC16.CRC16_Check(bytes);
        bytes[bytes.length - 4] = (byte) (crc16 >> 8);
        bytes[bytes.length - 3] = (byte) crc16;
        writeMsg(bytes);
    }

    //send android SN number to mnvr
    private void sendAndroidSerialNumber() {
        try {
            String serial = Build.SERIAL;
            byte[] bytes = new byte[40];//{(byte) 0xBF, (byte) 0xCF, 0x00, (byte) ver, 0x01, 0x09, 0x00, 0x00, (byte) 0xDF, (byte) 0xEF};
            bytes[0] = (byte) 0xBF;
            bytes[1] = (byte) 0xCF;
            bytes[2] = 0x00;
            bytes[3] = (byte) ver;
            bytes[4] = 0x01;
            bytes[5] = 0x09;
            bytes[36] = 0x00;
            bytes[37] = 0x00;
            bytes[38] = (byte) 0xDF;
            bytes[39] = (byte) 0xEF;
            byte[] serialBytes = serial.getBytes("ascii");
            System.arraycopy(serialBytes,0,bytes,6,serialBytes.length);
            short crc16 = (short) CRC16.CRC16_Check(bytes);
            bytes[36] = (byte) (crc16 >> 8);
            bytes[37] = (byte) crc16;
            writeMsg(bytes);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }


}

