package com.sztvis.mnvrlibrary.listener;

import com.sztvis.mnvrlibrary.domain.resp.ControlReplyResult;
import com.sztvis.mnvrlibrary.domain.resp.DeviceStatusResult;
import com.sztvis.mnvrlibrary.domain.resp.IoResult;
import com.sztvis.mnvrlibrary.domain.resp.ReplyStateResult;
import com.sztvis.mnvrlibrary.domain.resp.RespBase;
import com.sztvis.mnvrlibrary.domain.resp.RfidDistanceResult;

/**
 * Created by Administrator on 2019/8/1.
 */

public interface OnCommandResultListener {
    void onRfidDistanceResult(RespBase result);

    void onSensorBindResult(RespBase result);

    void onSearchDeviceStatus(DeviceStatusResult result);

    void onSearchIoStatusResult(IoResult result);

    void onSearchRfidDistanceResult(RfidDistanceResult result);

    void onSearchReplyResult(ReplyStateResult result);

    void onControlLedResult(RespBase result);

    void onControlReplayResult(ControlReplyResult result);
}
