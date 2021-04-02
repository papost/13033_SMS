package com.unipi.panapost.a13033sms;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.speech.RecognizerIntent;
import android.telephony.SmsManager;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SmsSendActivity extends AppCompatActivity implements LocationListener {
    private static final int REC_RESULT = 875 ; //define the rec_result for MyTts
    private TextView textView12, textView13;
    private DBHelper dbHelper; //create instance of DbHelper
    private Button button;
    private RecyclerView recyclerView;
    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private ArrayList<model> dataholder =new ArrayList<>(); // create an ArrayList  for storing objects type model
    private  final String phone = "13033";
    private LocationManager locationManager;
    ProgressDialog progressDialog;
    myadapter myadapter;
    MyTts myTts;
    String m,s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms_send);
        textView12 = findViewById(R.id.textView4);
        textView13 = findViewById(R.id.textview5);
        recyclerView=findViewById(R.id.recyclerview);
        button = findViewById(R.id.button4);
        myTts = new MyTts(this);
        isLocationEnabled(this); //call the method for checking if gps is enabled
        mAuth = FirebaseAuth.getInstance(); //get an instance of Firebase Authentication
        currentUser = mAuth.getCurrentUser(); //get the Current User
        recyclerView.setLayoutManager(new LinearLayoutManager(this)); //set LayoutManager
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //retrieve an instance of our database and reference the location we want to read
        databaseReference = FirebaseDatabase.getInstance().getReference("Users/"+currentUser.getUid()).child("Details");
        databaseReference.addValueEventListener(new ValueEventListener() {//with this listener we can update our data in realtime
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {//this method is called once with the initial value and again whenever data at this location is updated
                User userProfile = snapshot.getValue(User.class);
                textView13.setText(userProfile.address);
                textView12.setText(userProfile.fullname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                //method in case of an error has occured
                Toast.makeText(getApplicationContext(),R.string.error,Toast.LENGTH_LONG).show();
            }
        });
        dbHelper = new DBHelper(this); //we call the class DBhelper in order to read our data(messages)
        Cursor cursor = dbHelper.getData();   //in the cursor we store all the messages
        while (cursor.moveToNext()){
            //create an object type model and store the results of  the query
            model obj = new model(cursor.getString(0),cursor.getString(1));
            dataholder.add(obj); //store the objectsin dataholder
        }
        myadapter = new myadapter(dataholder);//create a new instance ofmyadapter
        recyclerView.setAdapter(myadapter);//attach adapter for rendering the results
        button.setOnClickListener(v -> { //set a new Click listener for the button ""sendsms"
            //we check for the required permissions/sms permissions
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.SEND_SMS)!=
                    PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.SEND_SMS},5435);
            }else {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    //αν δεν έχουν δοθεί τότε ζητείται η άδεια απο τον χρήστη
                    ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.ACCESS_FINE_LOCATION},234);
                    return;
                }else{
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);//request our location
                    progressDialog = new ProgressDialog(this); //we create a dialog in order to wait until our location is received
                    progressDialog.setMessage("Παρακαλώ Περιμένετε...");//set the message of our dialog
                    progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);//set the progress style
                    progressDialog.show();
                    StringBuilder builder = new StringBuilder(); //we create a new String builder in which we store the  string of sms
                    s = myadapter.getSelected().getId(); //store the code we have selected
                    m = myadapter.getSelected().getMessage(); //store the message of our choice
                    builder.append(s).append(" ").append((textView12.getText().toString())).append(" ").append(textView13.getText().toString());
                    SmsManager manager = SmsManager.getDefault();
                    //now we send the sms,phone is 13033 and text message is builder
                    manager.sendTextMessage(phone,null,builder.toString(),null,null);
                    //check for tje required permission/location permissions
                    Toast.makeText(this, R.string.success_sms, Toast.LENGTH_LONG).show();
                }


            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {// boolean method for inflating the menu source, which is menu
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {//boolean method for handling click event
        switch (item.getItemId()){
            case R.id.web://if the selection is this, a web browser tab is opened
                Intent intent8 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://forma.gov.gr/"));//we redirected to forma.gov.gr
                startActivity(intent8);
                return true;
            case R.id.info://the information of this activity
                showMessage(R.string.menu);
                return true;
            case R.id.microphone://now the microphone is opening
                Intent intent_mic = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //starts an activity that will prompt the user for speech and send it through a speech recognizer
                intent_mic.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
                intent_mic.putExtra(RecognizerIntent.EXTRA_PROMPT,"Please say something!");
                startActivityForResult(intent_mic,REC_RESULT); // REC_RESULT has been defined in the main body of  MainActivity
                return  true;
            case R.id.profile://the activity with our information is now opening
                Intent intent0 = new Intent(this,EditProfile.class);
                //intent0.putExtra("fullname",textView12.getText().toString());
                //intent0.putExtra("email", currentUser.getEmail());
                //intent0.putExtra("uid",currentUser.getUid());
                startActivity(intent0);
                return true;
            case R.id.code://activity for adding a new message is now opening
                Intent intent = new Intent(this,AddMessage.class);
                startActivity(intent);
                return true;
            case R.id.edit://activity for editing the messages is now opening
                Intent intent1 = new Intent(this,ViewMessages.class);
                startActivity(intent1);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==REC_RESULT && resultCode==RESULT_OK){
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);//array list for stroing the matches
            if (matches.contains("αποστολή sms")|| matches.contains("αποστολή μηνύματος")||matches.contains("αποστολή")){// checks if certain strings are stored
                //in the arraylist and if the vlaue of boolean isClicked(if true), if both conditions are true then   method speed(onclick) is executed
                button.performClick();
                myTts.speak("αποστολή μηνύματος");
            }
            if(matches.contains("διαχείριση προφίλ")||matches.contains("προφίλ")||matches.contains("επεξεργασία στοιχείων")){
                Intent intent0 = new Intent(this,EditProfile.class);
                //intent0.putExtra("fullname",textView12.getText().toString());
                //intent0.putExtra("email", currentUser.getEmail());
                //intent0.putExtra("uid",currentUser.getUid());
                startActivity(intent0);
                myTts.speak("επεξεργασία προφίλ");
            }
            if (matches.contains("προσθήκη μηνύματος")||matches.contains("προσθήκη")) {
                Intent intent = new Intent(this,AddMessage.class);
                startActivity(intent);
                myTts.speak("προσθήκη μηνύματος");
            }
            if (matches.contains("προβολή μηνυμάτων")||matches.contains("μηνύματα")){
                Intent intent1 = new Intent(this,ViewMessages.class);
                startActivity(intent1);
                myTts.speak("μηνύματα");
            }
            if (matches.contains("βοήθεια")||matches.contains("πληροφορίες")){
                showMessage(R.string.menu);
                myTts.speak("πληροφορίες");
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        DateFormat dateFormat = new SimpleDateFormat("YYYY-MM-dd HH:mm:ss"); // καθορισμός του "μοτίβου" της ημερομηνίας
        String date = dateFormat.format(Calendar.getInstance().getTime());
        double x = location.getLatitude(); //μεταβλητή τύπου double για την αποθήκευση του x
        double y = location.getLongitude();//μεταβλητή τύπου double για την αποθήκευση του y
        model model = new model(s, m, date, x, y);
        //set the reference in which we store our data (code, message,timestamp, x and y)
        FirebaseDatabase.getInstance().getReference("Users/" + currentUser.getUid() + "/History").child(date).setValue(model).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                progressDialog.dismiss();//close the progressDialog
            }
        });

        locationManager.removeUpdates(this);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(@NonNull String provider) {

    }

    @Override
    public void onProviderDisabled(@NonNull String provider) {

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
    public void isLocationEnabled(Context context)
    {
        locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        try {
            gps_enabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch(Exception ex) {}

        if(!gps_enabled) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setCancelable(true);
            builder.setTitle("Η ΤΟΠΟΘΕΣΙΑ ΣΥΣΚΕΥΗΣ ΕΙΝΑΙ ΑΠΕΝΕΡΓΟΠΟΙΗΜΕΝΗ")
                    .setMessage("Για να συνεχίσετε ενεργοποιήστε την τοποθεσία συσκευής")
                    .setPositiveButton("ΑΝΟΙΓΜΑ ΤΟΠΟΘΕΣΙΑΣ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                            context.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("ΕΠΙΣΤΡΟΦΗ", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    })
                    .show();
        }
    }
}

