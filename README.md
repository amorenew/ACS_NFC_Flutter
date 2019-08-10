# acs_nfc

A new flutter plugin project.

## Getting Started

- Import the library:
import 'package:acs_nfc/acs_nfc.dart';

- Open connection the connection
    await AcsNfc.openConnection(ip: '192.168.137.1', port: '8001');

- Listen to connection Status
    AcsNfc.connectionStatusStream.receiveBroadcastStream().listen(nfcConnectionStatus);

- List to Nfc data
    AcsNfc.nfcDataStream.receiveBroadcastStream().listen(nfcData);
    
- Data format is {"nfcBytes":"","nfcId":""}