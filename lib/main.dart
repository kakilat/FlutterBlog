import 'package:flutter/material.dart';
import 'Mapping.dart';
import 'authentication.dart';

void main (){
  runApp(new MyApp());
}
class MyApp extends StatelessWidget
{

  @override
  Widget build(BuildContext context) {
    return new MaterialApp(
        title: '',
        theme: new ThemeData(
          primarySwatch: Colors.blue,
        ),
        // home: LoginRegisterPage(),
        //home:MappingPage(auth:Auth()),//
        home :MappingPage(auth: Auth())
    );

  }

}
