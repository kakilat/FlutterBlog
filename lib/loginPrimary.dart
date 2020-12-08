import 'package:flutter/material.dart';
import 'authentication.dart';
import 'DialogBox.dart';

class LoginRegisterPage extends StatefulWidget {
  LoginRegisterPage({
    this.auth,
    this.onSignedIn,
});
  final  Authentication auth;
  final VoidCallback onSignedIn;

  State<StatefulWidget> createState() {
    return _loginRegisterState();
  }

}

enum FormType { login, register }

class _loginRegisterState extends State<LoginRegisterPage> {
  DialogBox dialogBox =new DialogBox();
  final formKey = new GlobalKey<FormState>();
  FormType _formType = FormType.login;
  String _email = "";
  String _password = "";

  bool _validateAndSave() {
    final form = formKey.currentState;
    if (form.validate()) {
      form.save();
      return true;
    } else {
      return false;
    }
  }
  void validateAndSubmit()async{
    if (_validateAndSave()){
      try{
        if(_formType==FormType.login){
String userId=await widget.auth.signIn(_email, _password);
dialogBox.information(context, "Congratulations","You are Logedin Sucessfully");

print("Logn user ="+userId);
        }
else {
          String userId=await widget.auth.signup(_email, _password);
         // dialogBox.information(context, "Congratulations","Your Account Has Been Created Sucessfully ");
          print("Register UserId ="+userId);
        }
        widget.onSignedIn();
      }catch(e){
       // dialogBox.information(context, "Error =", e.toString());
        print(e.toString());
      }    }

  }

  void _moveToRegister() {
    formKey.currentState.reset();

    setState(() {
      _formType = FormType.register;
    });
  }

  void _moveToLogin() {
    formKey.currentState.reset();

    setState(() {
      _formType = FormType.login;
    });
  }

  @override
  Widget build(BuildContext context) {
    return new Scaffold(
      resizeToAvoidBottomInset: false,
      resizeToAvoidBottomPadding: false,
      appBar: new AppBar(
        title: new Text("Login"),
      ),
      body: new Container(
        margin: EdgeInsets.all(15.0),
        alignment: Alignment.center,
        width: double.infinity,
        height: double.infinity,
        //color: viewModel.color,
    child: SingleChildScrollView(
        child: new Form(
          key: formKey,
          child: new Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: createInputs() + createButtons(),
          ),
        ),
      ),
      ),
    );
  }

  List<Widget> createInputs() {
    return [
      SizedBox(
        height: 10.0,
      ),
      logo(),
      SizedBox(
        height: 20.0,
      ),
      new TextFormField(
        decoration: new InputDecoration(labelText: 'Email'),
        validator: (value) {
          return value.isEmpty ? 'Email is required ' : null;
        },
        onSaved: (value) {
          return _email = value;
        },
      ),
      SizedBox(
        height: 10.0,
      ),
      new TextFormField(
        decoration: new InputDecoration(labelText: 'Password'),
        obscureText: true,
        validator: (value) {
          return value.isEmpty ? 'Password is required ' : null;
        },
        onSaved: (value) {
          return _password = value;
        },
      ),
      SizedBox(
        height: 20.0,
      ),
    ];
  }

  List<Widget> createButtons() {
    if (_formType == FormType.login) {
      return [
        new RaisedButton(
            child: new Text('login', style: new TextStyle(fontSize: 20.0)),
            textColor: Colors.white,
            color: Colors.pink,
            onPressed: validateAndSubmit),
        new FlatButton(
          child: new Text('create new account', style: new TextStyle(fontSize: 14.0)),
          textColor: Colors.white,
          color: Colors.pink,
          onPressed: _moveToRegister,
        ),
      ];
    } else {
      return [
        new RaisedButton(
            child: new Text('Create Account ',
                style: new TextStyle(fontSize: 20.0)),
            textColor: Colors.white,
            color: Colors.pink,
            onPressed: validateAndSubmit),
        new RaisedButton(
          child: new Text('Already Have An Accoun? Login',
              style: new TextStyle(fontSize: 14.0)),
          textColor: Colors.white,
          color: Colors.pink,
          onPressed: _moveToLogin,
        ),
      ];
    }
  }

  Widget logo() {
    return new Hero(
      tag: 'hero',
      child: new CircleAvatar(
        backgroundColor: Colors.transparent,
        radius: 110.0,
        child: Image.asset('images/instant.jpg'),
      ),
    );
  }
}
