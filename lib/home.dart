import 'package:flutter/material.dart';
import 'Posts.dart';
import 'package:image_picker/image_picker.dart';
import 'authentication.dart';
import 'dart:io';
import 'photoUpload.dart';
import 'package:firebase_database/firebase_database.dart';
class  HomePage extends StatefulWidget {


  HomePage({
    this.auth,
    this.onSignedOut,
});
  final Authentication auth;
  final VoidCallback onSignedOut;

  @override
  State<StatefulWidget> createState() {
    // TODO: implement createState
    return _HomepageState();
  }
}


class _HomepageState extends State<HomePage>{
  List <Posts> postList=[] ;

  @override
  void initState() {
    super.initState();
DatabaseReference postRef= FirebaseDatabase.instance.reference().child("posts");
    postRef.once().then((DataSnapshot snap){
      var KEYS= snap.value.keys;
      var DATA= snap.value;
      postList.clear();
      for (var individualKey in KEYS){
        Posts posts=new Posts(

          DATA[individualKey]['IMAGE'],
          DATA[individualKey]['DISCRIPTION'],
          DATA[individualKey]['DATE'],
          DATA[individualKey]['TIME'],
        );
        postList.add(posts);
      }
      setState((){
        print('length: $postList.length');
      });
    });

  }

  File sampleImage;
  void _LogoutUser()async{
    try{
      await widget.auth.signOut();
      widget.onSignedOut();
    }catch(e){
      print(e.toString());
    }
  }
  Future getImage() async{
    var tempImage =await ImagePicker.pickImage(source: ImageSource.camera);
    setState(() {
      sampleImage=tempImage;
    });
  }
  @override
  Widget build(BuildContext context) {
    // TODO: implement build
    return new Scaffold(
      appBar: new AppBar(
        title: new Text('Home'),
      ),
      body: new Container(
child: postList.length==0? new Text("NO post Available"): new ListView.builder(
  itemCount: postList.length,
  itemBuilder: (_ ,index) {
    return postUi(
      postList[index].IMAGE,
      postList[index].DISCRIPTION,
      postList[index].DATE,
      postList[index].TIME,
    );
  },
)
      ) ,
      bottomNavigationBar: new BottomAppBar(
        color: Colors.blue,
        child: new Container(
          child: new Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
          mainAxisSize: MainAxisSize.max,
          children: <Widget>[
            new IconButton(
                icon:new Icon(Icons.local_car_wash),
            iconSize: 30,
            color: Colors.white,
            onPressed: _LogoutUser,),
            new IconButton(
              icon:new Icon(Icons.add_a_photo),
              iconSize: 30,
              color: Colors.white,onPressed: (){
                Navigator.push(context,  MaterialPageRoute(builder: (context)=>UploadPhotoPage()));
            }, ),
          ],
          ),
        ) ,
      ),
    );
  }
  Widget postUi(String image,String discription,String date,String time){
    return new Card(
      elevation :10.0,
      margin: EdgeInsets.all(15.0),
      child: new Container(
        padding: new EdgeInsets.all(14.0),
        child: new Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: <Widget>[
new Row(
  mainAxisAlignment: MainAxisAlignment.spaceBetween,
  children: <Widget>[
    new Text(date,
      style: Theme.of(context).textTheme.subtitle,
      textAlign: TextAlign.center,),
    new Text(time,
      style: Theme.of(context).textTheme.subtitle,
      textAlign: TextAlign.center,)
  ],
),
 SizedBox (height:10.0,),
            new Image.network(image, fit: BoxFit.cover),
SizedBox (height:10.0,),
new Text(discription,
  style: Theme.of(context).textTheme.subhead,
  textAlign: TextAlign.center,)
          ],
        )
      ),
    );

  }

}