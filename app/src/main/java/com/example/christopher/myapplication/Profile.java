package com.example.christopher.myapplication;

import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// 액티비티간 이동
import android.content.Intent;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;

// Spinner 구현
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
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

// 이미지 업로드를 위한 노력1 열음
import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.Manifest;
// 이미지 업로드를 위한 노력1 닫음


public class Profile extends AppCompatActivity {

    SharedPreferences sessionInfo;

    //TODO add image functionality
    // 이미지 업로드를 위한 노력2 열음
    private static final int PICK_FROM_CAMERA = 0;
    private static final int PICK_FROM_ALBUM = 1;
    private static final int CROP_FROM_CAMERA = 2;

    private Uri mImageCaptureUri;
    private ImageView mPhotoImageView;
    //private Button mButton;
    // 이미지 업로드를 위한 노력2 닫음

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

        // 이미지 업로드를 위한 노력3 열음
        //mButton = (Button) findViewById(R.id.button);
        mPhotoImageView = (ImageView) findViewById(R.id.ProfilePicture);
        // 이미지 업로드를 위한 노력3 닫음

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


        //get UI elements
        editFirstName = (EditText) findViewById(R.id.editFirstName);
        editMiddleName = (EditText) findViewById(R.id.editMiddleName);
        editLastName = (EditText) findViewById(R.id.editLastName);
        editStatusMessage = (EditText) findViewById(R.id.editStatusMessage);

        radioGroupGender = (RadioGroup) findViewById(R.id.radioGroupGender);
        radioGroupOccupation = (RadioGroup) findViewById(R.id.radioGroupOccupation);

        new getProfileInfoTask().execute("");
        new GetProfileImage().execute();
    }

    // 이미지 업로드를 위한 노력4 열음
    // This is getting an image from the Gallery app
    public void onClick_upload_gallery (View v){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    Profile.PICK_FROM_ALBUM);

        } else {
            // permission has been granted, continue as usual
            doTakeAlbumAction();
        }
    }

    // This is getting an image from the Camera app
    // http://stackoverflow.com/questions/33244500/android-6-revoked-permissions-checked-as-granted
    public void onClick_upload_camera (View v){
        // Check permission for CAMERA
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Check Permissions Now
            // Callback onRequestPermissionsResult interceptado na Activity MainActivity
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE},
                    Profile.PICK_FROM_CAMERA);

            doTakePhotoAction();
        } else {
            // permission has been granted, continue as usual
            doTakePhotoAction();
        }
    }
    // 이미지 업로드를 위한 노력4 닫음


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PICK_FROM_CAMERA:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    doTakePhotoAction();
                }
                else {
                    finish();
                }
                break;
            case PICK_FROM_ALBUM:
                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    doTakeAlbumAction();
                }
                else {
                    finish();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    //function that attempts to request a user profile update on server
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


    //function that sends a request to update user profile to the server
    private class UpdateProfileTask extends AsyncTask<JSONObject, Integer, String>{
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
                mySocket.setSoTimeout(2000);
                mySocket.connect(new InetSocketAddress(address, 50001),2000);
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
            //check if session is valid
            checkSessionResponse(response);

            //acknowledge profile update in UI
            if (s != null){
                Toast.makeText(getBaseContext(), s, Toast.LENGTH_SHORT).show();
            }
            else{
                Toast.makeText(getBaseContext(), "Profile Updated!", Toast.LENGTH_SHORT).show();
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

    //Request profile info of current user from server
    private class getProfileInfoTask extends AsyncTask<String, Integer, JSONObject>{
        SharedPreferences sessionInfo = getSharedPreferences("sessionInfo", 0);
        @Override
        protected JSONObject doInBackground(String... params) {
            Socket mySocket;
            JSONObject response = null;
            try{
                //prepare socket IO
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(2000);
                mySocket.connect(new InetSocketAddress(address, 50001),2000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                //set request message
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_GET_PROFILE);
                jsonMessage.put("sessionid", sessionInfo.getString("sessionid", ""));

                //send request
                printWriter.println(jsonMessage);

                //read response
                String unparsed = bufferedReader.readLine();
                mySocket.close();

                //create jsonobject from response
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

    //function that sets the text in a given EditText UI item if the json profile has the corresponding field
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

    //set drop down lists for interests
    private void setInterestSpinner(JSONObject sProfile, Spinner spin){
        try{
            JSONArray interestTags = sProfile.getJSONArray("interests");
            Log.d("PROFILE", "INTERESTS: " + interestTags.toString());
            for (int j = 0; j < interestTags.length(); j++) {
                //get one of the interest tags
                String interestTag = (String) interestTags.get(j);
                for (int i = 0; i < spin.getCount(); i++) {
                    if (spin.getItemAtPosition(i).toString().equalsIgnoreCase(interestTag)) {
                        //set preselected drop down option if it matches interest
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


    //check if user session is still active. Return to login page if not
    public void checkSessionResponse(JSONObject serverMessage){
        if (serverMessage!= null){
            try{
                String sessionStatus = serverMessage.getString("sessionstatus");
                if (sessionStatus.equals("invalid")){
                    Bundle bundle = new Bundle();
                    //message to show on return to login
                    bundle.putString("startuptoast", "Unknown session identifier");
                    stopService(new Intent(this, SendLocationService.class));
                    startActivity(new Intent(getApplicationContext(), Login.class).putExtras(bundle));
                }
                else if (sessionStatus.equals("timeout")){
                    Bundle bundle = new Bundle();
                    //message to show on return to login
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

    //put info from ui elements into jsonarray
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


    // 이미지 업로드를 위한 노력5 열음
    /**
     * 앨범에서 이미지 가져오기
     */
    private void doTakeAlbumAction()
    {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(android.provider.MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, PICK_FROM_ALBUM);
    }


    /**
     * 카메라에서 이미지 가져오기
     */
    private void doTakePhotoAction()
    {
    /*
     * 참고 해볼곳
     * http://2009.hfoss.org/Tutorial:Camera_and_Gallery_Demo
     * http://stackoverflow.com/questions/1050297/how-to-get-the-url-of-the-captured-image
     * http://www.damonkohler.com/2009/02/android-recipes.html
     * http://www.firstclown.us/tag/android/
     */

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // 임시로 사용할 파일의 경로를 생성
        String url = "tmp_" + String.valueOf(System.currentTimeMillis()) + ".jpg";
        mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(), url));

        intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);
        // 특정기기에서 사진을 저장못하는 문제가 있어 다음을 주석처리.
        //intent.putExtra("return-data", true);
        startActivityForResult(intent, PICK_FROM_CAMERA);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(resultCode != RESULT_OK)
        {
            return;
        }

        switch(requestCode)
        {
            case CROP_FROM_CAMERA:
            {
                // 크롭이 된 이후의 이미지를 넘겨 받음.
                // 이미지뷰에 이미지를 보여준다거나 부가적인 작업 이후에
                // 임시 파일을 삭제.
                final Bundle extras = data.getExtras();

                if(extras != null)
                {
                    Bitmap photo = extras.getParcelable("data");
                    mPhotoImageView.setImageBitmap(photo);
                    new SendImageTask(photo).execute();
                }






                // This shows the file's path. /external/images/media/"randomnumber"
                Toast.makeText(getApplicationContext(), mImageCaptureUri.getPath(), Toast.LENGTH_SHORT).show();












                // 임시 파일 삭제
                File f = new File(mImageCaptureUri.getPath());
                if(f.exists())
                {
                    f.delete();
                }

                break;
            }

            case PICK_FROM_ALBUM:
            {
                // 이후의 처리가 카메라와 같으므로 일단  break없이 진행.
                // 좀더 합리적인 방법을 선택하자.

                mImageCaptureUri = data.getData();
                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                    mPhotoImageView.setImageBitmap(image);
                    new SendImageTask(image).execute();
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                break;
            }

            case PICK_FROM_CAMERA:
            {
                // 이미지를 가져온 이후의 리사이즈할 이미지 크기를 결정.
                // 이후에 이미지 크롭 어플리케이션을 호출.

                /*Intent intent = new Intent("com.android.camera.action.CROP");
                intent.setDataAndType(mImageCaptureUri, "image/*");

                intent.putExtra("outputX", 200);
                intent.putExtra("outputY", 200);
                intent.putExtra("aspectX", 1);
                intent.putExtra("aspectY", 1);
                intent.putExtra("scale", true);
                intent.putExtra("return-data", true);
                startActivityForResult(intent, CROP_FROM_CAMERA);
*/

                try {
                    Bitmap image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                    mPhotoImageView.setImageBitmap(image);
                    new SendImageTask(image).execute();
                }
                catch (IOException e){
                    e.printStackTrace();
                }

                break;
            }
        }
    }

    private class SendImageTask extends AsyncTask<Void, Void, Boolean>{
        private JSONObject response;
        private Bitmap image;

        public SendImageTask(Bitmap img){
            image = img;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            SharedPreferences sessionInfo = getSharedPreferences("sessionInfo", 0);
            Socket mySocket;
            boolean success = false;
            try{
                //prepare socket IO
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(10000);
                mySocket.connect(new InetSocketAddress(address, 50001),10000);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                //set request message
                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_UPLOAD_IMAGE);
                jsonMessage.put("sessionid", sessionInfo.getString("sessionid", ""));
                jsonMessage.put("image", encodeToBase64(image, Bitmap.CompressFormat.PNG, 100));

                //send request
                printWriter.println(jsonMessage);

                //read response
                String unparsed = bufferedReader.readLine();
                mySocket.close();

                //create jsonobject from response
                response = new JSONObject(unparsed);
                success = response.getBoolean("success");

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
                return success;
            }
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            checkSessionResponse(response);

            if(aBoolean){
                Toast.makeText(getApplicationContext(), "Profile picture updated!", Toast.LENGTH_SHORT);
            }
            else{
                Toast.makeText(getApplicationContext(), "Error uploading picture..", Toast.LENGTH_SHORT);
            }
        }
    }

    private class GetProfileImage extends AsyncTask<Void, Void, Bitmap>{
        private JSONObject response;
        private ArrayList<Integer> requestedIDs;
        private ArrayAdapter[] toNotify;

        @Override
        protected Bitmap doInBackground(Void... params) {
            Socket mySocket;
            Bitmap retval = null;
            try{
                InetAddress address = InetAddress.getByName("csclserver.hopto.org");
                mySocket = new Socket();
                mySocket.setSoTimeout(ApplicationConstants.SERVER_TIMEOUT_MS);
                mySocket.connect(new InetSocketAddress(address, 50001),ApplicationConstants.SERVER_TIMEOUT_MS);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(mySocket.getInputStream()));
                PrintWriter printWriter = new PrintWriter(mySocket.getOutputStream(), true);

                JSONObject jsonMessage = new JSONObject();
                jsonMessage.put("type", NetworkConstants.TYPE_GET_IMAGES);
                jsonMessage.put("sessionid", sessionInfo.getString("sessionid", ""));
                JSONArray requested = new JSONArray();
                JSONObject jsonRequest = new JSONObject();
                jsonRequest.put("size", "medium");
                jsonRequest.put("sessionid", sessionInfo.getString("sessionid", ""));
                jsonRequest.put("from sessionid", true);
                requested.put(jsonRequest);
                jsonMessage.put("requested", requested);
                printWriter.println(jsonMessage);
                String unparsed = bufferedReader.readLine();
                mySocket.close();

                response = new JSONObject(unparsed);
                JSONObject person = response.getJSONArray("images").getJSONObject(0);
                retval = decodeBase64(person.getString("image"));
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
                return retval;
            }
        }

        @Override
        protected void onPostExecute(Bitmap bitmapSparseArray) {
            checkSessionResponse(response);

            mPhotoImageView.setImageBitmap(bitmapSparseArray);
        }
    }


    //http://stackoverflow.com/questions/9768611/encode-and-decode-bitmap-object-in-base64-string-in-android
    public static String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality)
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    public static Bitmap decodeBase64(String input)
    {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }
    // 이미지 업로드를 위한 노력5 닫음
}

