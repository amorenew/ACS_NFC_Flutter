import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
//import 'package:acs_nfc/acs_nfc.dart';

void main() {
  const MethodChannel channel = MethodChannel('acs_nfc');

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
//    expect(await AcsNfc.platformVersion, '42');
  });
}
