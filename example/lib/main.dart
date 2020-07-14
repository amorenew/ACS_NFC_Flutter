import 'package:flutter/material.dart';
import 'dart:async';

import 'package:flutter/services.dart';
import 'package:acs_nfc/acs_nfc.dart';

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;
    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await AcsNfc.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }
    AcsNfc.connectionStatusStream
        .receiveBroadcastStream()
        .listen(nfcConnectionStatus);

    await AcsNfc.openConnection(ip: '192.168.137.1', port: '8001');
    AcsNfc.nfcDataStream.receiveBroadcastStream().listen(nfcData);
    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  bool isMNVRConnected = false;
  void nfcConnectionStatus(dynamic isConnected) {
    setState(() {
      isMNVRConnected = isConnected as bool;
    });
  }

  List<String> nfcTags = [];
  void nfcData(dynamic nfcJsonData) {
    print('=========================================');
    print(nfcJsonData);
    setState(() {
      nfcTags.add(nfcJsonData as String);
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Center(
          child: Column(
            children: <Widget>[
              Text('Running on: $_platformVersion\n'),
              Text(
                'MNVR connection status: $isMNVRConnected',
                style: TextStyle(
                  color: Colors.purple,
                  fontSize: 28,
                ),
              ),
              Text(
                'ip: 192.168.137.1, port: 8001',
                style: TextStyle(
                  color: Colors.blueAccent,
                  fontSize: 28,
                ),
              ),
              Text(
                'Connect MNVR by sim card',
                style: TextStyle(
                  color: Colors.white,
                  backgroundColor: Colors.black,
                  fontSize: 20,
                ),
              ),
              Text(
                'adb tcpip 5555 ',
                style: TextStyle(
                  color: Colors.white,
                  backgroundColor: Colors.black,
                  fontSize: 20,
                ),
              ),
              Text(
                'adb connect 192.168.16.100:5555',
                style: TextStyle(
                  color: Colors.white,
                  backgroundColor: Colors.black,
                  fontSize: 20,
                ),
              ),
              ...nfcTags.map((text) => Text('NFC Tag: $text')),
            ],
          ),
        ),
      ),
    );
  }
}
