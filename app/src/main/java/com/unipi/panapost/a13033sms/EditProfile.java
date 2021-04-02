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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class EditProfile extends AppCompatActivity {
    private EditText editText,editText1,editText2;
    private Button button;
    private DatabaseReference databaseReference;
    private static final int REC_RESULT = 875 ;
    private FirebaseAuth mAuth;//declare an instance of FirebaseAuth
    private FirebaseUser currentUser;
    MyTts myTts;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        myTts = new MyTts(this);
        mAuth = FirebaseAuth.getInstance();
        currentUser =mAuth.getCurrentUser();
        button = findViewById(R.id.edit_profile);
        editText = findViewById(R.id.fullname_edit);
        editText1 = findViewById(R.id.email_edit);
        editText2 = findViewById(R.id.address_edit);
        //editText.setText(getIntent().getStringExtra("fullname"));
        editText1.setText(currentUser.getEmail());
        //retrieve an instance of our database and reference the location we want to read
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/"+currentUser.getUid()).child("Details");
        databaseReference.addValueEventListener(new ValueEventListener() {//with this listener we can update our data in realtime
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//this method is called once with the initial value and again whenever data at this location is updated
                User userProfile = snapshot.getValue(User.class);
                editText.setText(userProfile.fullname);
                editText.setSelection(editText.length());
                editText2.setText(userProfile.address);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //method in case of an error has occured
                Toast.makeText(getApplicationContext(),R.string.error,Toast.LENGTH_LONG).show();
            }
        });
    }
    public void edit(View view){//method for saving the changes
        String name = editText.getText().toString().trim();
        String address = editText2.getText().toString().trim();
        User user = new User(name,address);
        if(address.isEmpty()||name.isEmpty()){//check if name or email is empty
            Toast.makeText(this,R.string.fillall,Toast.LENGTH_LONG).show();
        }
        else {
            //set the reference in which we store our data (name and address)
            FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getUid()).child("Details")
                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(),R.string.success_edit,Toast.LENGTH_LONG).show();
                }
            });
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {//boolean method for inflating the menu source, which is menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_1,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//boolean method for handling click event
        switch (item.getItemId()){
            case R.id.info://the information of this activity
                showMessage(R.string.menu_1);
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
            case R.id.code://activity for adding a new message is now opening
                Intent intent1 = new Intent(this,AddMessage.class);
                startActivity(intent1);
                return true;
            case R.id.edit://activity for editing the messages is now opening
                Intent intent2 = new Intent(this,ViewMessages.class);
                startActivity(intent2);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void showMessage(int message){
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
            if (matches.contains("αποθήκευση")) {
                button.performClick();
                myTts.speak("αποθήκευση στοιχείων");
            }
            if (matches.contains("προσθήκη μηνύματος") || matches.contains("προσθήκη")) {
                Intent intent = new Intent(this,AddMessage.class);
                startActivity(intent);
                myTts.speak("προσθήκη μηνύματος");
            }
            if (matches.contains("προβολή μηνυμάτων") || matches.contains("μηνύματα")) {
                Intent intent = new Intent(this,ViewMessages.class);
                startActivity(intent);
                myTts.speak("μηνύματα");
            }
            if (matches.contains("βοήθεια")||matches.contains("πληροφορίες")){
                showMessage(R.string.menu_1);
                myTts.speak("πληροφορίες");
            }
        }
    }

}
