package com.example.christopher.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// 액티비티간 이동
import android.content.Intent;
import android.view.View;

// Spinner 구현
import java.util.ArrayList;

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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;


public class Profile extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        // 나이 스피너에 데이터 출력

        // xml에서 지정
        // android:prompt : 스피너에 타이틀메시지 넣기
        // android:entries : 스피너에 사용할 아이템(데이터)를 지정

        // 지역 스피너에 데이터 출력

        // arrays.xml에 정의한 String 배열 ( age ) 를 ArrayAdapter로 바인딩하고 스피너의 모양을 지정
        ArrayAdapter<CharSequence> adapterLocalAge = ArrayAdapter.createFromResource(this, R.array.age, android.R.layout.simple_spinner_item);
        // ArrayAdapter객체에 할당된 데이터들을 스피너가 클릭될때 보여줄 스피너 아이템의 출력형식을 지정함
        adapterLocalAge.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // xml에서 정의한 age 스피너 객체 얻어오기
        final Spinner spinnerAge = (Spinner) findViewById(R.id.spinner_age);
        // 스피너에 타이틀메시지 넣기
        //spinnerAge.setPrompt("Select your age!~!");
        // 스피너에 ArrayAdapter를 연결함
        spinnerAge.setAdapter(adapterLocalAge);


        // arrays.xml에 정의한 String 배열 ( music ) 를 ArrayAdapter로 바인딩하고 스피너의 모양을 지정
        ArrayAdapter<CharSequence> adapterLocalMusic = ArrayAdapter.createFromResource(this, R.array.music, android.R.layout.simple_spinner_item);
        // ArrayAdapter객체에 할당된 데이터들을 스피너가 클릭될때 보여줄 스피너 아이템의 출력형식을 지정함
        adapterLocalMusic.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // xml에서 정의한 music 스피너 객체 얻어오기
        final Spinner spinnerMusic = (Spinner) findViewById(R.id.spinner_music);
        // 스피너에 ArrayAdapter를 연결함
        spinnerMusic.setAdapter(adapterLocalMusic);


        // arrays.xml에 정의한 String 배열 ( sports ) 를 ArrayAdapter로 바인딩하고 스피너의 모양을 지정
        ArrayAdapter<CharSequence> adapterLocalSports = ArrayAdapter.createFromResource(this, R.array.sports, android.R.layout.simple_spinner_item);
        // ArrayAdapter객체에 할당된 데이터들을 스피너가 클릭될때 보여줄 스피너 아이템의 출력형식을 지정함
        adapterLocalSports.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // xml에서 정의한 sports 스피너 객체 얻어오기
        final Spinner spinnerSports = (Spinner) findViewById(R.id.spinner_sports);
        // 스피너에 ArrayAdapter를 연결함
        spinnerSports.setAdapter(adapterLocalSports);


        // arrays.xml에 정의한 String 배열 ( movie ) 를 ArrayAdapter로 바인딩하고 스피너의 모양을 지정
        ArrayAdapter<CharSequence> adapterLocalMovie = ArrayAdapter.createFromResource(this, R.array.movie, android.R.layout.simple_spinner_item);
        // ArrayAdapter객체에 할당된 데이터들을 스피너가 클릭될때 보여줄 스피너 아이템의 출력형식을 지정함
        adapterLocalMovie.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // xml에서 정의한 sports 스피너 객체 얻어오기
        final Spinner spinnerMovie = (Spinner) findViewById(R.id.spinner_movie);
        // 스피너에 ArrayAdapter를 연결함
        spinnerMovie.setAdapter(adapterLocalMovie);


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

        String updateMessage = "Profile Updated";
        Toast.makeText(Profile.this,updateMessage,Toast.LENGTH_SHORT).show();
        //Intent intent_toLogin = new Intent(getApplicationContext(), Login.class);
        //startActivity(intent_toLogin);
    }
}

