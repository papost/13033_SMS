package com.unipi.panapost.a13033sms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int REC_RESULT = 875 ;
    private TextView textView, textView1;
    private EditText editText,editText1,editText2;
    private FirebaseAuth mAuth; //declare an instance of FirebaseAuth
    private Button button;
    private CheckBox checkBox;
    private  SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private Boolean stayIn;//boolean variable για την αποθήκευση των στοιχείων του χρήστη
    FirebaseUser currentUser;
    MyTts myTts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myTts = new MyTts(this);
        textView = findViewById(R.id.register);
        textView1 = findViewById(R.id.reset);
        editText = findViewById(R.id.email);
        editText.setSelection(editText.length());
        editText1 = findViewById(R.id.password);
        button = findViewById(R.id.signin);
        checkBox = findViewById(R.id.checkBox);
        checkBox.setChecked(true);
        mAuth = FirebaseAuth.getInstance(); //initialize the FirebaseAuth
        currentUser = mAuth.getCurrentUser();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor;
        stayIn = sharedPreferences.getBoolean("stayIn",false); //login credentials save to Shared preferences
        if(stayIn){
            editText.setText(sharedPreferences.getString("username",""));
            editText1.setText(sharedPreferences.getString("password",""));
        }
        textView.setOnClickListener(v -> {//on Click Listener for opening register activity
            Intent intent = new Intent(MainActivity.this,RegisterActivity.class);
            startActivity(intent);
        });
        textView1.setOnClickListener(v->{//on Click listener for opening dialog in order to reset password
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater inflater = this.getLayoutInflater();
            View view= inflater.inflate(R.layout.layout_resetpassword,null);
            editText2 = view.findViewById(R.id.resetpassword);
            builder.setCancelable(true)
                    .setTitle(R.string.resettitle)
                    .setMessage(R.string.message)
                    .setView(view)
                    .setNegativeButton(R.string.back, (dialogInterface, i) -> dialogInterface.cancel())
                    .setPositiveButton(R.string.send, (dialog, whichButton) -> {
                        mAuth.sendPasswordResetEmail(editText2.getText().toString())//method for resetting password using email
                                .addOnCompleteListener(task -> {
                                    if(task.isSuccessful())
                                        Toast.makeText(this,R.string.resetpassword,Toast.LENGTH_LONG).show();
                                    else
                                        Toast.makeText(this,task.getException().getMessage(),Toast.LENGTH_LONG).show();
                                });
                    })

                    .show();
        });
        boolean firstrun = getSharedPreferences("PREFERENCE", MODE_PRIVATE).getBoolean("firstrun", true);
        //γίνεται έλεγχος της τιμής first run, αν ειναι η πρώτη εκτέλεση της εφαρμογής μετά από την εγκατάστασή της ανοίγει η φόρμα για την εγγραφή του χρήστη
        if (firstrun){
            textView.performClick();
            getSharedPreferences("PREFERENCE", MODE_PRIVATE)
                    .edit()
                    .putBoolean("firstrun", false)//αλλαγή της τιμής της boolean μεταβλητής firstrun
                    .apply();
        }
    }

    public void login(View view){//method for login
        String email = editText.getText().toString().trim();
        String password = editText1.getText().toString().trim();
        if(email.isEmpty()||password.isEmpty()){//check if email and password are empty
            Toast.makeText(this,R.string.fillall,Toast.LENGTH_LONG).show();
        }
        else {//if they are not, create a new signin method which takes in email address and password,validates them and then signs a user in with the signInWithEmailAndPasswor method
            mAuth.signInWithEmailAndPassword(editText.getText().toString(), editText1.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {//sign in success
                            if (currentUser.isEmailVerified()) {//if email is verified open SMSsend Activity
                                if (checkBox.isChecked())//remember me checked
                                {

                                    // Saving data locally in Shared Preference.
                                    editor= sharedPreferences.edit();
                                    editor.putBoolean("stayIn", true);
                                    editor.putString("username", email);
                                    editor.putString("password", password);
                                    editor.apply();
                                }
                                else {
                                    editor= sharedPreferences.edit();
                                    editor.putBoolean("stayIn", true);
                                    editor.putString("username", "");
                                    editor.putString("password", "");
                                    editor.apply();
                                    // Don't do anything
                                }

                                Toast.makeText(getApplicationContext(), R.string.success_login, Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), SmsSendActivity.class);
                                startActivity(intent);
                            } else {//if email is not verified
                                Toast.makeText(this, R.string.verify, Toast.LENGTH_LONG).show();
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {// boolean method for inflating the menu source, which is helper
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_general,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//boolean method for handling click event
        switch (item.getItemId()){
            case R.id.info://the information of this activity
                showMessage(R.string.menu_signin);
                return true;
            case R.id.microphone://now the microphone is openning
                Intent intent_mic = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //starts an activity that will prompt the user for speech and send it through a speech recognizer
                intent_mic.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent_mic.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");
                startActivityForResult(intent_mic,REC_RESULT); // REC_RESULT has been defined in the main body of  MainActivity
                return  true;
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REC_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);//array list for stroing the matches
            if (matches.contains("σύνδεση")){// checks if certain strings are stored
                //in the arraylist
                button.performClick();
                myTts.speak("σύνδεση");
            }
            if(matches.contains("επαναφορά")){
                textView1.performClick();
                myTts.speak("επαναφορά κωδικού");
            }
            if (matches.contains("εγγραφή")) {
                textView.performClick();
                myTts.speak("εγγραφή χρήστη");
            }
            if (matches.contains("βοήθεια")||matches.contains("πληροφορίες")){
                showMessage(R.string.menu);
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