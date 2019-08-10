import 'dart:async';

import 'package:flutter/services.dart';

class AcsNfc {
  static const MethodChannel _channel = const MethodChannel('acs_nfc');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> openConnection(
      {String port = '192.168.137.1', String ip = '8001'}) async {
    final bool success = await _channel.invokeMethod(
        'openConnection', <String, String>{'port': port, 'ip': ip});
    return success;
  }

  static const EventChannel connectionStatusStream =
      EventChannel('com.acs.nfc/connection/status');
  static const EventChannel nfcDataStream =
      EventChannel('com.acs.nfc/data/nfc');

//  final String result = await platform.invokeMethod('getBatteryLevel',{"text":text});

//  static Future<bool> get openConnection async {
//    final bool success = await _channel.invokeMethod('openConnection');
//    return success;
//  }
}
