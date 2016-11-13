package com.example.christopher.myapplication;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// 액티비티간 이동
import android.content.Intent;
import android.util.Log;
import android.view.View;

// autoLogin 에 쓰이는 것들
import android.content.SharedPreferences;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;


public class Login extends AppCompatActivity {


    EditText input_ID, input_PW;
    CheckBox Auto_LogIn;
    //Boolean loginChecked;

    SharedPreferences setting;
    SharedPreferences.Editor editor;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        input_ID = (EditText) findViewById(R.id.emailInput);
        input_PW = (EditText) findViewById(R.id.passwordInput);
        Auto_LogIn = (CheckBox) findViewById(R.id.checkBox);


        setting = getSharedPreferences("setting", 0);
        editor = setting.edit();


        /*
            첫번째 줄의 getBoolean은 Boolean타입의 데이터를 가져오겠다 라는 표시,
            처음에 실행을 하게 되면 설정된 값이 없음ㅁ
            그러므로 기본값인 false가 반환되어 처음 실행시에는 저 if문이 작동되지 않게 됨

            두번째와 세번째 줄을 보면 getString이라고 되어 있는대 이렇게 ID값과 PW값을 불러와서 EditText에 setText로 적용하는 모습

            마지막으로 자동로그인이 활성화 되었으므로 CheckBox도 활성화 표시
        */
        if(setting.getBoolean("Auto_Login_enabled", false)){
            input_ID.setText(setting.getString("ID", ""));
            input_PW.setText(setting.getString("PW", ""));
            Auto_LogIn.setChecked(true);
        }


        Auto_LogIn.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String ID = input_ID.getText().toString();
                    String PW = input_PW.getText().toString();

                    editor.putString("ID", ID);
                    editor.putString("PW", PW);
                    editor.putBoolean("Auto_Login_enabled", true);
                    editor.commit();
                }else{
                    /**
                     * remove·Î Áö¿ì´Â°ÍÀº ºÎºÐ»èÁ¦
                     * clear·Î Áö¿ì´Â°ÍÀº ÀüÃ¼ »èÁ¦ ÀÔ´Ï´Ù
                     */
//					editor.remove("ID");
//					editor.remove("PW");
//					editor.remove("Auto_Login_enabled");
                    editor.clear();
                    editor.commit();
                }
            }
        });









        /*

        // 만약 autoLogin 이 checked 이면, input 을 get 한다
        if(pref.getBoolean("autoLogin", false)){

            idInput.setText(pref.getString("id", ""));
            passwordInput.setText(pref.getString("pw", ""));
            autoLogin.setChecked(true);

            //goto mainActivity

        }else{

            // if autoLogin unChecked
            String id = idInput.getText().toString();
            String password = passwordInput.getText().toString();
            Boolean validation = loginValidation(id, password);

            if(validation){
                Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show();

                if(loginChecked){
                    // if autoLogin Checked, save values
                    editor.putString("id", id);
                    editor.putString("pw", password);
                    editor.putBoolean("autoLogin", true);
                    editor.commit();

                }

                // goto mainActivity

            }else{

                Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show();
                // goto LoginActivity
            }

        }

        // set checkBoxListener
        autoLogin.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    loginChecked = true;
                } else {
                    // if unChecked, removeAll
                    loginChecked = false;
                    editor.clear();
                    editor.commit();
                }
            }
        });


        */

    }

    /*
    private boolean loginValidation(String id, String password) {
        if(pref.getString("id","").equals(id) && pref.getString("pw","").equals(password)) {
            // login success
            return true;
        } else if (pref.getString("id","").equals("")){
            // sign in first
            Toast.makeText(this, "Please Sign in first", Toast.LENGTH_LONG).show();
            return false;
        } else {
            // login failed
            return false;
        }
    }


    */

    public void onClick_login (View v){
        loginTask login = new loginTask();
        login.execute(input_ID.getText().toString(), input_PW.getText().toString());

        //Intent intent_toMap = new Intent(getApplicationContext(), Map.class);
        //startActivity(intent_toMap);
    }

    private class loginTask extends AsyncTask<String, Integer, JSONObject>{
        Boolean cannotConnect = false;
        Boolean jsonError = false;
        @Override
        protected JSONObject doInBackground(String... params) {
            String user = params[0];
            String pass = params[1];
            JSONObject response = null;
            try{
                InetAddress address = java.net.InetAddress.getByName("csclserver.hopto.org");
                Socket mySocket = new Socket();
                mySocket.setSoTimeout(500);
                mySocket.connect(new InetSocketAddress(address, 50001), 500);
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_LOGIN);
                jsonMessage.put("username", user);
                jsonMessage.put("password", pass);
                printWriter.println(jsonMessage.toString());
                String serverResponse = bufferedReader.readLine();
                response = new JSONObject(serverResponse);
                mySocket.close();
            }
            catch (UnknownHostException e){
                e.printStackTrace();
                cannotConnect = true;
            }
            catch (IOException e){
                e.printStackTrace();
                cannotConnect = true;
            }
            catch (JSONException e){
                e.printStackTrace();
                jsonError = true;
            }
            finally {
                return response;
            }
        }

        @Override
        protected void onPostExecute(JSONObject jsonObject) {
            if (cannotConnect){
                Toast.makeText(getApplicationContext(), "Cannot connect to server", Toast.LENGTH_SHORT).show();
                return;
            }
            if (jsonError){
                Toast.makeText(getApplicationContext(), "Unexpected error", Toast.LENGTH_SHORT).show();
                return;
            }
            try{
                if (!jsonObject.getString("type").equals(NetworkConstants.TYPE_LOGIN)){ //Server will send a jsonobject with type = DEFAULT if request can't be handled
                    Toast.makeText(getApplicationContext(), "An error has occurred", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!jsonObject.getBoolean("loginSuccessful")){
                    Toast.makeText(getApplicationContext(), "username or password is incorrect", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(getApplicationContext(), "login successful!", Toast.LENGTH_SHORT).show();
                    Intent intent_toRegister = new Intent(getApplicationContext(), Map.class);
                    startActivity(intent_toRegister);
                }

            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }
    }

    public void onClick_register (View v){
        Intent intent_toRegister = new Intent(getApplicationContext(), Register.class);
        startActivity(intent_toRegister);
    }
}
