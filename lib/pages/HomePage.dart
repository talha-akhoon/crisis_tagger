import 'package:crisis_tagger/components/BaseLayout.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import 'package:flutter_reactive_ble/flutter_reactive_ble.dart';

class HomePage extends StatefulWidget {
  const HomePage({super.key, required this.title});

  // This widget is the home page of your application. It is stateful, meaning
  // that it has a State object (defined below) that contains fields that affect
  // how it looks.

  // This class is the configuration for the state. It holds the values (in this
  // case the title) provided by the parent (in this case the App widget) and
  // used by the build method of the State. Fields in a Widget subclass are
  // always marked "final".

  final String title;

  @override
  State<HomePage> createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  final flutterReactiveBle = FlutterReactiveBle();
  @override
  void initState() {
    super.initState();
    initialiseBLE();
  }

  void initialiseBLE() {
    flutterReactiveBle.scanForDevices(withServices: [serviceId], scanMode: ScanMode.lowLatency).listen((device) {
      //code for handling results
    }, onError: () {
      //code for handling error
    });

  }

  @override
  Widget build(BuildContext context) {
    // This method is rerun every time setState is called, for instance as done
    // by the _incrementCounter method above.
    //
    // The Flutter framework has been optimized to make rerunning build methods
    // fast, so that you can just rebuild anything that needs updating rather
    // than having to individually change instances of widgets.
    return BaseLayout(
      title: 'Home',
      children: [
        MaterialButton(
            child: Text('Advertise'),
            onPressed: () => {},
          color: Colors.blueAccent,
        ),
        MaterialButton(
            child: Text('Scan'),
            onPressed: () => {},
          color: Colors.green,

        ),
      ],
    );
  }
}
