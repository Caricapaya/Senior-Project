package com.example.christopher.myapplication;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Calendar;

public class Register2 extends AppCompatActivity {
    EditText firstNameField;
    EditText middleNameField;
    EditText lastNameField;
    RadioGroup genderGroup;
    RadioGroup occupationGroup;
    Spinner birthYearSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register2);

        firstNameField = (EditText) findViewById(R.id.register2_editFirstName);
        middleNameField = (EditText) findViewById(R.id.register2_editMiddleName);
        lastNameField = (EditText) findViewById(R.id.register2_editLastName);
        genderGroup = (RadioGroup) findViewById(R.id.register2_radiogroup_gender);
        occupationGroup = (RadioGroup) findViewById(R.id.register2_radiogroup_occupation);
        birthYearSpinner = (Spinner) findViewById(R.id.register2_spinner_age);

        int start = Calendar.getInstance().get(Calendar.YEAR) - 16;
        int end = start - 64;
        ArrayList<CharSequence> years = new ArrayList<>();
        for (int i = start; i > end; i--) {
            years.add(""+i);
        }

        ArrayAdapter<CharSequence> yearAdapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item, years);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        birthYearSpinner.setAdapter(yearAdapter);
    }

    public void onClick_create(View view){
        //Place profile information in jsonobject
        JSONObject newProfileInfo = new JSONObject();
        putInfo(newProfileInfo, "firstname", firstNameField);
        putInfo(newProfileInfo, "middlename", middleNameField);
        putInfo(newProfileInfo, "lastname", lastNameField);
        putInfo(newProfileInfo, "gender", genderGroup);
        putInfo(newProfileInfo, "occupation", occupationGroup);
        putInfo(newProfileInfo, "yearofbirth", birthYearSpinner);

        new UpdateProfileTask().execute(newProfileInfo);
    }

    private void putInfo(JSONObject nProfile, String name, Object uiItem){
        if (uiItem instanceof EditText){
            EditText edtxt = (EditText) uiItem;
            if (!TextUtils.isEmpty(edtxt.getText()) && TextUtils.getTrimmedLength(edtxt.getText()) > 0){
                try{
                    nProfile.put(name, edtxt.getText());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if (uiItem instanceof RadioGroup){
            RadioButton checked = (RadioButton) findViewById(((RadioGroup) uiItem).getCheckedRadioButtonId());
            try{
                nProfile.put(name, checked.getText());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

        if (uiItem instanceof Spinner){
            Spinner spinner = (Spinner) uiItem;
            if (spinner.getSelectedItemPosition() == 0){
                return;
            }
            try{
                nProfile.put(name, spinner.getSelectedItem());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    private class UpdateProfileTask extends AsyncTask<JSONObject, Integer, String> {
        JSONObject response;
        SharedPreferences sessionInfo = getSharedPreferences("sessionInfo", 0);

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject newProfileInfo = params[0];
            Socket mySocket;
            String serverMessage = null;
            try{
                //prepare socket IO
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(ApplicationConstants.SERVER_TIMEOUT_MS);
                mySocket.connect(new InetSocketAddress(address, 50001),ApplicationConstants.SERVER_TIMEOUT_MS);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                //prepare request to server with new profile info
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_UPDATE_PROFILE);
                jsonMessage.put("sessionid", sessionInfo.getString("sessionid", ""));
                jsonMessage.put("person", newProfileInfo);

                //send request
                printWriter.println(jsonMessage);

                //read response
                String unparsed = bufferedReader.readLine();
                mySocket.close();

                //turn response string into jsonobject and return "response" value
                response = new JSONObject(unparsed);
                serverMessage = response.optString("response", null);

            }
            catch (JSONException e){
                e.printStackTrace();
            }
            catch (UnknownHostException e){
                e.printStackTrace();
            }
            catch (IOException e){
                e.printStackTrace();
            }
            finally {
                return serverMessage;
            }
        }

        @Override
        protected void onPostExecute(String s) {

            //acknowledge profile update in UI
            if (s != null){
                Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getBaseContext(), "Account creation complete!", Toast.LENGTH_SHORT).show();
            }

            //create object that switches to main activity
            Runnable r = new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), Map.class));
                }
            };

            //run this object after a brief delay
            new Handler().postDelayed(r, ApplicationConstants.ACTIVITY_SWITCH_DELAY_MS);
        }
    }
}
