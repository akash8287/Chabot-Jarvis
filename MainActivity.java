package com.example.jarvis;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private SpeechRecognizer recognizer;
    private TextView textView2;
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        chatsRV = findViewById(R.id.idRVChats);
        sendMsgIB = findViewById(R.id.idIBSend);
        userMsgEdt = findViewById(R.id.idEdtMessage);
        mRequestQueue = Volley.newRequestQueue(MainActivity.this);
        mRequestQueue.getCache().clear();
        messageModalArrayList = new ArrayList<>();
        @Override
        public void onClick(View v) {
            //checking if the message entered by user is empty or not.
            if (userMsgEdt.getText().toString().isEmpty()) {
                //if the edit text is empty display a toast message.
                Toast.makeText(MainActivity.this, "Please enter your message..", Toast.LENGTH_SHORT).show();
                return;
            }
            //calling a method to send message to our bot to get response.
            sendMessage(userMsgEdt.getText().toString());
            //below line we are setting text in our edit text as empty
            userMsgEdt.setText("");
//
//        }

        Dexter.withContext(this)
                .withPermission(Manifest.permission.RECORD_AUDIO)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {

                    }
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {
                        System.exit(0);
                    }
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                        token.continuePermissionRequest();
                    }
                }).check();

        findById();
        initializeTextToSpeech();
        initializeResult();
    }

    private void initializeTextToSpeech() {
        tts= new  TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if (tts.getEngines().size()==0){
                    Toast.makeText(MainActivity.this, "Engine isn't Available", Toast.LENGTH_SHORT).show();
                }
                else {
                    speak("Hi I Am Jarvis ....");
                }
            }


        });
    }
    private void speak(String msg) {

        tts.speak(msg,TextToSpeech.QUEUE_FLUSH,null,null);

    }

    private void findById() {
        textView2 = (TextView) findViewById(R.id.textView2);
    }

    private void initializeResult() {
        if (SpeechRecognizer.isRecognitionAvailable( this));
        recognizer = SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> result= bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                Toast.makeText(MainActivity.this, ""+result.get(0), Toast.LENGTH_SHORT).show();
                textView2.setText(result.get(0));
                response(result.get(0));



            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
    }

    private void response(String msg) {
        String msgs = msg.toLowerCase(Locale.ROOT);
        if (msgs.indexOf("hi")!= -1){
            speak("Hello Sir! How Can I help You");
        }
        if (msgs.indexOf("who created you")!=-1){
            speak("My Creator is Akash Mittal");
        }
        if (msgs.indexOf("i love you")!=-1){
            speak("Sorry You Are Like A Master To Me");
        }
        if (msgs.indexOf( "do you love me")!=-1){
            speak("I have a crush on my creator");
        }
        if (msgs.indexOf("who are you")!=-1){
            speak("I am Jarvis");
        }
        if (msgs.indexOf("how old are you")!=-1){
            speak("Just a few days");
        }
        if (msgs.indexOf("i want to fuck you")!=-1){
            speak("Nikal Lawde");
        }
        if (msgs.indexOf( "how are you")!=-1){
            speak("I am Fine. How Are You?");
        }
        if (msgs.indexOf("fine")!=-1){
            speak("Bye Sir");
        }
        if (msgs.indexOf("bastard")!=-1){
            speak("nikal lawde");
        }

//      api work start from here.......
        if(msg.isEmpty()){
            speak("You didnt speak anything");
            return;
        }
        getResponse(msg);

    }

    public void getResponse(String msg){
        String url = "http://api.brainshop.ai/get?bid=162331&key=e89Rmsa5bh1nfkUB&uid=[uid]&msg=" + msg;
        String Base_url = "http://api.brainshop.ai/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Base_url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RetrofitAPI retrofitAPI = retrofit.create(RetrofitAPI.class);
        Call<msgModal> call = retrofitAPI.getmessage(url);
        call.enqueue(new Callback<msgModal>() {
            @Override
            public void onResponse(Call<msgModal> call, Response<msgModal> response) {
                if(response.isSuccessful()){
                    msgModal modal = response.body();
                    speak(modal.getcnt(msg));
                }
            }

            @Override
            public void onFailure(Call<msgModal> call, Throwable t) {
                speak("i didn't get what you are saying");
            }
        });

    }

    public void startRecording(View view) {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);


        recognizer.startListening(intent);
    }

}