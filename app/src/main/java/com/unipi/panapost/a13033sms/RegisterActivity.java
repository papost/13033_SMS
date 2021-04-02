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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity {
    private static final int REC_RESULT = 875 ;
    private TextView textView;
    private EditText editText2,editText3,editText4,editText5;
    private FirebaseAuth mAuth;//declare an instance of FirebaseAuth
    private FirebaseUser currentUser;
    private Button button;
    MyTts myTts;


    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        myTts = new MyTts(this);
        mAuth = FirebaseAuth.getInstance();//initialize the FirebaseAuth
        currentUser = mAuth.getCurrentUser();
        textView = findViewById(R.id.login);
        button = findViewById(R.id.signup);
        editText2 = findViewById(R.id.fullname_edit);
        editText3 = findViewById(R.id.email_edit);
        editText4 = findViewById(R.id.password_signup);
        editText5 = findViewById(R.id.address_edit);
        textView.setOnClickListener(view -> this.finish());//onclick listener in order to close this activity
    }
   public void signup(View view){//method for signup
        String name = editText2.getText().toString().trim();
        String email = editText3.getText().toString().trim();
        String password = editText4.getText().toString().trim();
        String address = editText5.getText().toString().trim();
        //check if name or email or passwor or address is empty
        if(name.isEmpty()|| email.isEmpty()||password.isEmpty()||address.isEmpty()){
            Toast.makeText(this,R.string.fillall,Toast.LENGTH_LONG).show();
        }
        else {//in other case
            //create a new createAccount method which takes in an emailaddress and password, validates them and then create a new user with createUserWithEmailAndPassword method
            mAuth.createUserWithEmailAndPassword(editText3.getText().toString(), editText4.getText().toString())
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            User user = new User(name,address);
                            currentUser.sendEmailVerification()//send email verification
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            //set the reference in which we store our data (name and address)
                                            FirebaseDatabase.getInstance().getReference("Users/"+currentUser.getUid())
                                                    .child("Details")
                                                    .setValue(user).addOnCompleteListener(task11 -> {
                                                        if(task11.isSuccessful()){

                                                        }else
                                                            Toast.makeText(getApplicationContext(), task11.getException().getMessage(),Toast.LENGTH_LONG).show();
                                                    });
                                            Toast.makeText(getApplicationContext(), R.string.succes_register, Toast.LENGTH_LONG).show();
                                            Intent intent = new Intent(this,MainActivity.class);
                                            startActivity(intent);
                                            editText2.setText("");
                                            editText3.setText("");
                                            editText4.setText("");
                                            editText5.setText("");
                                        } else {
                                            Toast.makeText(getApplicationContext(), task1.getException().getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(getApplicationContext(),
                                    task.getException().getMessage(), Toast.LENGTH_LONG).show();
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
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.info://the information of this activity
                showMessage(R.string.menu_signup);
                return true;
            case R.id.microphone://now the microphone is opening
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
            if (matches.contains("εγγραφή")){// checks if certain strings are stored
                //in the arraylist
                button.performClick();
                myTts.speak("εγγραφή χρήστη");
            }
            if(matches.contains("σύνδεση")){
                textView.performClick();
                myTts.speak("σύνδεση");
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