package com.example.christopher.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

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

public class Register extends AppCompatActivity {
    EditText input_ID;
    EditText input_PW;
    EditText input_CPW;
    RadioGroup input_gender;
    RadioGroup input_status;

    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        input_ID = (EditText) findViewById(R.id.emailInput);
        input_PW = (EditText) findViewById(R.id.passwordInput);
        input_CPW = (EditText) findViewById(R.id.passwordInputConfirm);
        input_gender = (RadioGroup) findViewById(R.id.genderGroup);
        input_status = (RadioGroup) findViewById(R.id.occupationGroup);

        preferences = getSharedPreferences("setting", 0);
        editor = preferences.edit();
    }

    public void onClick_create (View v){
        if (!isValidEmail(input_ID.getText())){
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isValidPassword(input_PW.getText())){
            Toast.makeText(this, "Password needs to be at least 8 characters long", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!confirmPassword(input_PW.getText(), input_CPW.getText())){
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }
        if (input_gender.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please select your gender", Toast.LENGTH_SHORT).show();
            return;
        }if (input_status.getCheckedRadioButtonId() == -1){
            Toast.makeText(this, "Please select your occupation", Toast.LENGTH_SHORT).show();
            return;
        }

        String email = input_ID.getText().toString();
        String pass = input_PW.getText().toString();
        String gender = ((RadioButton)findViewById(input_gender.getCheckedRadioButtonId())).getText().toString();
        String status = ((RadioButton)findViewById(input_status.getCheckedRadioButtonId())).getText().toString();
        editor.putString("ID", email);
        editor.putString("PW", pass);
        editor.putBoolean("Auto_Login_enabled", true);
        RegisterTask registerTask = new RegisterTask();
        registerTask.execute(email, pass, gender, status);

        //Intent intent_toLogin = new Intent(getApplicationContext(), Login.class);
        //startActivity(intent_toLogin);
    }

    public void onClick_back (View v){
        Intent intent_toLogin = new Intent(getApplicationContext(), Login.class);
        startActivity(intent_toLogin);
    }

    private class RegisterTask extends AsyncTask<String, Integer, JSONObject>{
        boolean cannotConnect = false;
        boolean jsonError = false;

        @Override
        protected JSONObject doInBackground(String... params) {
            String user = params[0];
            String pass = params[1];
            String gender = params[2];
            String status = params[3];
            JSONObject response = null;
            try{
                InetAddress address = java.net.InetAddress.getByName("csclserver.hopto.org");
                Socket mySocket = new Socket();
                mySocket.setSoTimeout(500);
                mySocket.connect(new InetSocketAddress(address, 50001), 500);
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_SIGNUP);
                jsonMessage.put("username", user);
                jsonMessage.put("password", pass);
                jsonMessage.put("gender", gender);
                jsonMessage.put("status", status);
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
                if (!jsonObject.getBoolean("signupsuccessful")){
                    Toast.makeText(getApplicationContext(), jsonObject.getString("why"), Toast.LENGTH_SHORT).show();
                    return;
                }
                else {
                    Toast.makeText(getApplicationContext(), "New user created!", Toast.LENGTH_SHORT).show();
                }
                editor.commit();
                Intent intent_toLogin = new Intent(getApplicationContext(), Login.class);
                startActivity(intent_toLogin);

            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
    }

    //code from http://stackoverflow.com/questions/1819142/how-should-i-validate-an-e-mail-address
    public boolean isValidEmail(CharSequence vemail){
        if (TextUtils.isEmpty(vemail)){
            return false;
        }
        else{
            return Patterns.EMAIL_ADDRESS.matcher(vemail).matches();
        }
    }

    public boolean isValidPassword(CharSequence vpass){
        return vpass.length() > 7;
    }

    public boolean confirmPassword(CharSequence p1, CharSequence p2){
        return p1.toString().equals(p2.toString());
    }



}
