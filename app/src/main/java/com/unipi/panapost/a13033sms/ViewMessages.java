package com.unipi.panapost.a13033sms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class ViewMessages extends AppCompatActivity {
    private static final int REC_RESULT = 876;
    RecyclerView recyclerView;
    MyTts myTts;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        myTts = new MyTts(this);
        setContentView(R.layout.activity_view_messages);
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DBHelper dbHelper = new DBHelper(this);
        List<model> messages = dbHelper.getmessages();
        if(messages.size()>0){
            MessagesAdapter messagesAdapter = new MessagesAdapter(messages,ViewMessages.this);
            recyclerView.setAdapter(messagesAdapter);
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {// boolean method for inflating the menu source, which is menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_3,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//boolean method for handling click event
        switch (item.getItemId()){
            case R.id.info://the information of this activity
                showMessage(R.string.menu_3);
                return true;
            case R.id.microphone://now the microphone is opening
                Intent intent_mic = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //starts an activity that will prompt the user for speech and send it through a speech recognizer
                intent_mic.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent_mic.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");
                startActivityForResult(intent_mic,REC_RESULT); // REC_RESULT has been defined in the main body of  MainActivity
                return  true;
            case R.id.sms://the activity for sending is now opening
                Intent intent = new Intent(this,SmsSendActivity.class);
                startActivity(intent);
                return true;
            case R.id.profile://activity for editing profile is now opening
                Intent intent1 = new Intent(this,EditProfile.class);
                startActivity(intent1);
                return true;
            case R.id.code://activity for adding a new message is now opening
                Intent intent2 = new Intent(this,AddMessage.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REC_RESULT && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);//array list for stroing the matches
            if (matches.contains("αποστολή sms") || matches.contains("αποστολή μηνύματος") || matches.contains("αποστολή")) {// checks if certain strings are stored
                //in the arraylist and if the vlaue of boolean isClicked(if true), if both conditions are true then   method speed(onclick) is executed
                Intent intent = new Intent(this, SmsSendActivity.class);
                startActivity(intent);
                myTts.speak("αποστολή μηνύματος");
            }
            if (matches.contains("διαχείριση προφίλ") || matches.contains("προφίλ") || matches.contains("επεξεργασία στοιχείων")) {
                Intent intent = new Intent(this, EditProfile.class);
                startActivity(intent);
                myTts.speak("επεξεργασία προφίλ");
            }
            if (matches.contains("προσθήκη μηνύματος") || matches.contains("προσθήκη")) {
                Intent intent = new Intent(this,AddMessage.class);
                startActivity(intent);
                myTts.speak("προσθήκη μηνύματος");
            }
            if (matches.contains("βοήθεια") || matches.contains("πληροφορίες")) {
                showMessage(R.string.menu_3);
                myTts.speak("πληροφορίες");
            }
        }
    }
    public void showMessage( int message){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true)
                .setTitle(R.string.info)
                .setMessage(message)
                .setIcon(R.drawable.ic_baseline_info_24)
                .setNegativeButton(R.string.back, (dialogInterface, i) -> dialogInterface.cancel())
                .show();
    }
}