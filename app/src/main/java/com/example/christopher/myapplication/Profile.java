package com.example.christopher.myapplication;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// 액티비티간 이동
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

// Spinner 구현
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
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;


public class Profile extends AppCompatActivity {
    SharedPreferences sessionInfo;

    //TODO add image functionality
    ImageView imageProgile;

    Spinner spinnerAge;
    Spinner spinnerMusic;
    Spinner spinnerSports;
    Spinner spinnerMovie;

    EditText editFirstName;
    EditText editMiddleName;
    EditText editLastName;
    EditText editStatusMessage;

    RadioGroup radioGroupGender;
    RadioGroup radioGroupOccupation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        sessionInfo = getSharedPreferences("sessionInfo", 0);


        // 나이 스피너에 데이터 출력

        // xml에서 지정
        // android:prompt : 스피너에 타이틀메시지 넣기
        // android:entries : 스피너에 사용할 아이템(데이터)를 지정

        // 지역 스피너에 데이터 출력


        int start = Calendar.getInstance().get(Calendar.YEAR) - 16;
        int end = start - 64;
        ArrayList<CharSequence> years = new ArrayList<>();
        for (int i = start; i > end; i--) {
            years.add(""+i);
        }
        // arrays.xml에 정의한 String 배열 ( age ) 를 ArrayAdapter로 바인딩하고 스피너의 모양을 지정
        ArrayAdapter<CharSequence> adapterLocalAge = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, years);
        // ArrayAdapter객체에 할당된 데이터들을 스피너가 클릭될때 보여줄 스피너 아이템의 출력형식을 지정함
        adapterLocalAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // xml에서 정의한 age 스피너 객체 얻어오기
        spinnerAge = (Spinner) findViewById(R.id.spinner_age);
        // 스피너에 타이틀메시지 넣기
        //spinnerAge.setPrompt("Select your age!~!");
        // 스피너에 ArrayAdapter를 연결함
        spinnerAge.setAdapter(adapterLocalAge);


        // arrays.xml에 정의한 String 배열 ( music ) 를 ArrayAdapter로 바인딩하고 스피너의 모양을 지정
        ArrayAdapter<CharSequence> adapterLocalMusic = ArrayAdapter.createFromResource(this, R.array.music, android.R.layout.simple_spinner_item);
        // ArrayAdapter객체에 할당된 데이터들을 스피너가 클릭될때 보여줄 스피너 아이템의 출력형식을 지정함
        adapterLocalMusic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // xml에서 정의한 music 스피너 객체 얻어오기
        spinnerMusic = (Spinner) findViewById(R.id.spinner_music);
        // 스피너에 ArrayAdapter를 연결함
        spinnerMusic.setAdapter(adapterLocalMusic);


        // arrays.xml에 정의한 String 배열 ( sports ) 를 ArrayAdapter로 바인딩하고 스피너의 모양을 지정
        ArrayAdapter<CharSequence> adapterLocalSports = ArrayAdapter.createFromResource(this, R.array.sports, android.R.layout.simple_spinner_item);
        // ArrayAdapter객체에 할당된 데이터들을 스피너가 클릭될때 보여줄 스피너 아이템의 출력형식을 지정함
        adapterLocalSports.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // xml에서 정의한 sports 스피너 객체 얻어오기
        spinnerSports = (Spinner) findViewById(R.id.spinner_sports);
        // 스피너에 ArrayAdapter를 연결함
        spinnerSports.setAdapter(adapterLocalSports);


        // arrays.xml에 정의한 String 배열 ( movie ) 를 ArrayAdapter로 바인딩하고 스피너의 모양을 지정
        ArrayAdapter<CharSequence> adapterLocalMovie = ArrayAdapter.createFromResource(this, R.array.movie, android.R.layout.simple_spinner_item);
        // ArrayAdapter객체에 할당된 데이터들을 스피너가 클릭될때 보여줄 스피너 아이템의 출력형식을 지정함
        adapterLocalMovie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // xml에서 정의한 sports 스피너 객체 얻어오기
        spinnerMovie = (Spinner) findViewById(R.id.spinner_movie);
        // 스피너에 ArrayAdapter를 연결함
        spinnerMovie.setAdapter(adapterLocalMovie);


        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editMiddleName = (EditText) findViewById(R.id.editMiddleName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editStatusMessage = (EditText) findViewById(R.id.editStatusMessage);

        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        radioGroupOccupation = (RadioGroup) findViewById(R.id.radioGroupOccupation);

        new getProfileInfoTask().execute("");
    }


    public void onClick_back (View v){
        //Intent intent_toRegister = new Intent(getApplicationContext(), Register.class);
        //startActivity(intent_toRegister);
    }

    public void onClick_create (View v){
        //Intent intent_toLogin = new Intent(getApplicationContext(), Login.class);
        //startActivity(intent_toLogin);
    }

    public void onClick_update (View v){
        //Place profile information in jsonobject
        JSONObject newProfileInfo = new JSONObject();
        putInfo(newProfileInfo, "firstname", editFirstName);
        putInfo(newProfileInfo, "middlename", editMiddleName);
        putInfo(newProfileInfo, "lastname", editLastName);
        putInfo(newProfileInfo, "gender", radioGroupGender);
        putInfo(newProfileInfo, "occupation", radioGroupOccupation);
        putInfo(newProfileInfo, "yearofbirth", spinnerAge);
        putInfo(newProfileInfo, "statusmessage", editStatusMessage);

        JSONArray interests = new JSONArray();
        putInfo(interests, "moviegenre", spinnerMovie);
        putInfo(interests, "musicgenre", spinnerMusic);
        putInfo(interests, "sports", spinnerSports);
        try{
            newProfileInfo.put("interests", interests);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        new UpdateProfileTask().execute(newProfileInfo);
    }

    private class UpdateProfileTask extends AsyncTask<JSONObject, Integer, String>{
        JSONObject response;
        SharedPreferences sessionInfo = getSharedPreferences("sessionInfo", 0);

        @Override
        protected String doInBackground(JSONObject... params) {
            JSONObject newProfileInfo = params[0];
            Socket mySocket;
            String serverMessage = null;
            try{
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(2000);
                mySocket.connect(new InetSocketAddress(address, 50001),2000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_UPDATE_PROFILE);
                jsonMessage.put("sessionid", sessionInfo.getString("sessionid", ""));
                jsonMessage.put("person", newProfileInfo);
                printWriter.println(jsonMessage);
                String unparsed = bufferedReader.readLine();
                mySocket.close();
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
            checkSessionResponse(response);

            if (s != null){
                Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getBaseContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
            }

            Runnable r = new Runnable() {
                @Override
                public void run() {
                    startActivity(new Intent(getApplicationContext(), Map.class));
                }
            };

            new Handler().postDelayed(r, ApplicationConstants.ACTIVITY_SWITCH_DELAY_MS);
        }
    }

    private class getProfileInfoTask extends AsyncTask<String, Integer, JSONObject>{
        SharedPreferences sessionInfo = getSharedPreferences("sessionInfo", 0);
        @Override
        protected JSONObject doInBackground(String... params) {
            Socket mySocket;
            JSONObject response = null;
            try{
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(2000);
                mySocket.connect(new InetSocketAddress(address, 50001),2000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_GET_PROFILE);
                jsonMessage.put("sessionid", sessionInfo.getString("sessionid", ""));
                printWriter.println(jsonMessage);
                String unparsed = bufferedReader.readLine();
                mySocket.close();
                response = new JSONObject(unparsed);

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
                return response;
            }
        }

        @Override
        protected void onPostExecute(JSONObject response) {
            checkSessionResponse(response);

            try{
                //extract profile info sent by server
                JSONObject sProfile = response.getJSONObject("person");

                //set text fields
                setIfExists(sProfile, "firstname", editFirstName);
                setIfExists(sProfile, "middlename", editMiddleName);
                setIfExists(sProfile, "lastname", editLastName);
                setIfExists(sProfile, "statusmessage", editStatusMessage);

                //set radiobuttons
                setIfExists(sProfile, "gender", radioGroupGender);
                setIfExists(sProfile, "occupation", radioGroupOccupation);

                //set spinners
                setIfExists(sProfile, "yearofbirth", spinnerAge);
                setInterestSpinner(sProfile, spinnerMovie);
                setInterestSpinner(sProfile, spinnerMusic);
                setInterestSpinner(sProfile, spinnerSports);
            }
            catch (JSONException e){
                e.printStackTrace();
            }

        }
    }

    private void setIfExists(JSONObject sProfile, String name, EditText edt){
        String field = sProfile.optString(name, null);
        if (field != null){
            edt.setText(field);
        }
    }

    //Attempt to select the radiobutton with the text value specified in the JSONObject
    private void setIfExists(JSONObject sProfile, String name, RadioGroup rGroup){
        String field = sProfile.optString(name, null);
        if (field != null){
            Log.d("PROFILE", "Childcount: " + rGroup.getChildCount());
            for (int i = 0; i < rGroup.getChildCount(); i++) {
                if (rGroup.getChildAt(i) instanceof RadioButton){
                    RadioButton button = (RadioButton) rGroup.getChildAt(i);
                    Log.d("PROFILE", "Buttontext: " + button.getText());
                    if (field.equalsIgnoreCase(button.getText().toString())){
                        button.setChecked(true);
                        return;
                    }
                }
            }
        }
    }

    //Set selected spinner value to the one specified in jsonobject or leave unselected
    private void setIfExists(JSONObject sProfile, String name, Spinner spin){
        String field = sProfile.optString(name, null);
        if (field != null){
            Log.d("PROFILE", "YEAR: " + field);
            for (int i = 0; i < spin.getCount(); i++) {
                Log.d("PROFILE", "LIST: " + spin.getItemAtPosition(i).toString());
                if (spin.getItemAtPosition(i).toString().equalsIgnoreCase(field)){
                    spin.setSelection(i);
                    return;
                }
            }
        }
    }

    private void setInterestSpinner(JSONObject sProfile, Spinner spin){
        try{
            JSONArray interestTags = sProfile.getJSONArray("interests");
            Log.d("PROFILE", "INTERESTS: " + interestTags.toString());
            for (int j = 0; j < interestTags.length(); j++) {
                String interestTag = (String) interestTags.get(j);
                for (int i = 0; i < spin.getCount(); i++) {
                    if (spin.getItemAtPosition(i).toString().equalsIgnoreCase(interestTag)) {
                        spin.setSelection(i);
                        return;
                    }
                }
            }
        }
        catch (JSONException e){
            e.printStackTrace();
        }

    }


    public void checkSessionResponse(JSONObject serverMessage){
        if (serverMessage!= null){
            try{
                String sessionStatus = serverMessage.getString("sessionstatus");
                if (sessionStatus.equals("invalid")){
                    Bundle bundle = new Bundle();
                    bundle.putString("startuptoast", "Unknown session identifier");
                    stopService(new Intent(this, SendLocationService.class));
                    startActivity(new Intent(getApplicationContext(), Login.class).putExtras(bundle));
                }
                else if (sessionStatus.equals("timeout")){
                    Bundle bundle = new Bundle();
                    bundle.putString("startuptoast", "Session timed out, please log in again");
                    stopService(new Intent(this, SendLocationService.class));
                    startActivity(new Intent(getApplicationContext(), Login.class).putExtras(bundle));
                }
                else if (sessionStatus.equals("update")){
                    SharedPreferences.Editor sessionEditor = sessionInfo.edit();
                    String newSessionID = serverMessage.getString("sessionid");
                    sessionEditor.putString("sessionid", newSessionID);
                    sessionEditor.commit();
                }
            }
            catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    //Generic function that extracts info from ui elements and places them into jsonobject
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

    private void putInfo(JSONArray nProfile, String name, Object uiItem){
        if (uiItem instanceof EditText){
            EditText edtxt = (EditText) uiItem;
            if (!TextUtils.isEmpty(edtxt.getText()) && TextUtils.getTrimmedLength(edtxt.getText()) > 0){
                try{
                    nProfile.put(edtxt.getText());
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

        if (uiItem instanceof RadioGroup){
            RadioButton checked = (RadioButton) findViewById(((RadioGroup) uiItem).getCheckedRadioButtonId());
            try{
                nProfile.put(checked.getText());
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
                nProfile.put(spinner.getSelectedItem());
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}

