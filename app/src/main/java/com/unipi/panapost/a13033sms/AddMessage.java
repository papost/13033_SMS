package com.unipi.panapost.a13033sms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class AddMessage extends AppCompatActivity {
    private static final int REC_RESULT = 877 ;
    Button button,button1;
    EditText editText,editText1;
    MyTts myTts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_message);
        myTts = new MyTts(this);
        editText = findViewById(R.id.edittext_code);
        editText1 = findViewById(R.id.edittext_message);
        button = findViewById(R.id.button_add);
        button1 = findViewById(R.id.button_view);
        button.setOnClickListener(v -> {//on Click listener for adding a new message
            String code = editText.getText().toString();
            String message = editText1.getText().toString();
            if(code.isEmpty()||message.isEmpty()){//check iif code or message is empty
                Toast.makeText(this,R.string.fillall,Toast.LENGTH_LONG).show();
            }else {
                DBHelper dbHelper = new DBHelper(getApplicationContext());//create a new object of DBHelper
                model model = new model(code, message);
                dbHelper.addmessage(model);//add message
                Toast.makeText(getApplicationContext(), "Tο μήνυμα προστέθηκε με επιτυχία!", Toast.LENGTH_LONG).show();
                editText.setText("");
                editText1.setText("");
            }
        });
        button1.setOnClickListener(v -> {//on Click Listener for opening ViewMessages activity
            Intent intent =  new Intent(getApplicationContext(),ViewMessages.class);
            startActivity(intent);
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {// boolean method for inflating the menu source, which is menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_2,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//boolean method for handling click even
        switch (item.getItemId()){
            case R.id.info://the information of this activity
                showMessage(R.string.menu_2);
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
            case R.id.edit://activity for editing the messages is now opening
                Intent intent2 = new Intent(this,ViewMessages.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                button.performClick();
                myTts.speak("προσθήκη μηνύματος");
            }
            if (matches.contains("προβολή μηνυμάτων") || matches.contains("μηνύματα")) {
                button1.performClick();
                myTts.speak("μηνύματα");
            }
            if (matches.contains("βοήθεια")||matches.contains("πληροφορίες")){
                showMessage(R.string.menu_2);
                myTts.speak("πληροφορίες");
            }
        }
    }
}