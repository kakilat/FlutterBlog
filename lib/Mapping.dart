import 'package:flutter/material.dart';
import 'loginPrimary.dart';
import 'home.dart';
import 'authentication.dart';


class MappingPage extends StatefulWidget {
  final Authentication auth;
  MappingPage ({
    this.auth
});
  State<StatefulWidget> createState(){
    return _MappingPageState();
  }
}
enum AuthStatus {
  notSignedIn,
  signIn,
}
class _MappingPageState extends State<MappingPage>{
  AuthStatus authStatus =AuthStatus.notSignedIn;

  @override
  void initState() {
super.initState();
widget.auth.getCuttentUser().then((firebaseUserId){
setState(() {
  authStatus=firebaseUserId==null? AuthStatus.notSignedIn :AuthStatus.signIn;
});
});
  }
void _signedIn(){
    setState(() {
      authStatus=AuthStatus.signIn;
    });
}
  void _signedOut(){
    setState(() {
      authStatus=AuthStatus.notSignedIn;
    });
  }
  @override
  Widget build(BuildContext context) {
   switch (authStatus){
     case AuthStatus.notSignedIn:
       return new LoginRegisterPage(
         auth: widget.auth,
           onSignedIn:_signedIn
       );
     case AuthStatus.signIn:
       return new HomePage(
           auth: widget.auth,
           onSignedOut:_signedOut
       );
   }
    return null;
  }

}
