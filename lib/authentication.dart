import 'package:flutter/material.dart';
import 'package:firebase_auth/firebase_auth.dart';

abstract class Authentication{
  Future<String> signIn(String email, String password);
  Future<String> signup(String email, String password);
  Future<String> getCuttentUser();
  Future<void> signOut();
}
class Auth implements Authentication{
  final FirebaseAuth _firebaseAuth=FirebaseAuth.instance ;/*
  Future<String> signIn(String email, String password) async {
    try {
      FirebaseUser user = await _firebaseAuth.signInWithEmailAndPassword(email: email, password: password);
      assert(user != null);
      assert(await user.getIdToken() != null);
      final FirebaseUser currentUser = await _firebaseAuth.currentUser();
      assert(user.uid == currentUser.uid);
      return user.uid;
    } catch (e) {
    (e);
    return null;

  }

}*/
  Future<String> signIn(String email, String password) async {
    FirebaseUser user = await
    FirebaseAuth.instance.signInWithEmailAndPassword(
        email: email, password: password) as FirebaseUser;
    return user.uid;
  }

  Future<String> signup(String email, String password) async {
    FirebaseUser user = await
    FirebaseAuth.instance.createUserWithEmailAndPassword(
        email: email, password: password) as FirebaseUser;
    return user.uid;
  }/*
  Future<String> signup(email, password) async {
    try {
      FirebaseUser user = await _firebaseAuth.createUserWithEmailAndPassword(email: email, password: password);
      assert(user != null);
      assert(await user.getIdToken() != null);
      return user.uid;
    } catch (e) {
      (e);
      return null;
    }
  }*/

  Future<String> getCuttentUser() async {
    FirebaseUser user = await _firebaseAuth.currentUser();
    return user != null ? user.uid : null;
  }
  Future<void> signOut() async{
    _firebaseAuth.signOut();
  }
}
