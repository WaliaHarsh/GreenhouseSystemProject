package com.ceng319.greenhousesystemproject;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class MainReadingsPageActivity extends AppCompatActivity{

    private FirebaseDatabase database;
    private DatabaseReference ref;
    DataStructure mData;
    public String pKey, path,newKey;
    private TextView name;
    private TextView temperature;
    private TextView humidity;
    private TextView message;
    private TextView timestamp;
    private Button buttonTemp;
    private Button buttonSoilMoisture;
    private boolean firstTimeReadingsSet;
    private int N = 7;
    List<DataStructure> firebaseData = new ArrayList<>();

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_readings_page);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        this.setTitle(R.string.MainGreenHouseReadings);
        //getDatabase();
        findAllViews();

        // TODO 2: Find the Database.
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();
        final String idk=user.getUid();

        DatabaseReference myDatabase;
        myDatabase = FirebaseDatabase.getInstance().getReference();

        myDatabase.child("Users").child(idk).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        pKey = (String) dataSnapshot.child("registeredProductKey").getValue();
                        Toast.makeText(getApplicationContext(), "User is:"+idk+" and Product id is:"+pKey, Toast.LENGTH_LONG).show();
                        path = "userdata/"+pKey+"/data/";
                        ref = database.getReference(path);
                        // TODO 3: Load the data from database.
                        loadDatabase(ref);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }


    private void gotoTemperatureGraphs() {
        // TODO : Start the read option After login
        Intent intentT = new Intent(getApplicationContext(), TemperatureDataActivity.class);
        startActivity(intentT);
        //finish();
    }
    private void gotoSoilMoistureGraphs() {
        // TODO : Start the read option After login
        Intent intentS = new Intent(getApplicationContext(), SoilMoistureDataActivity.class);
        startActivity(intentS);
        //finish();
    }


    private void findAllViews(){
        //name = findViewById(R.id.readname);
        temperature = findViewById(R.id.readtemperature);
        //message = findViewById(R.id.readmessage);
        timestamp = findViewById(R.id.readtimestamp);
        humidity=findViewById(R.id.readsoilmoisture);
        buttonTemp = findViewById(R.id.temperatureGraphs);
        buttonSoilMoisture = findViewById(R.id.soilmoistureGraphs);

        buttonTemp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTemperatureGraphs();
            }
        });
        buttonSoilMoisture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoSoilMoistureGraphs();
            }
        });
    }



    private void setReadings(){

        if (firebaseData.size() > N)  // Should have a guard to make sure we always draw the most recent N numbers.
        {
            firebaseData = firebaseData.subList(firebaseData.size()-N, firebaseData.size());
        }


        for (DataStructure ds: firebaseData) {
            temperature.setText(getString(R.string.MainTemp) + ds.getTemperature()+"°C");
            humidity.setText(getString(R.string.MainSoilMoisture) + ds.getHumidity());
            timestamp.setText(getString(R.string.MainTimeStamp)+convertTimestamp(ds.getTimestamp()));
        }
    }

    private void loadDatabase(DatabaseReference ref) {
        // Last N data entries from Database, these are automatically the N most recent data
        Query recentPostsQuery = ref.limitToLast(N).orderByChild("timestamp");

        // NOTICE: Firebase Value event is always called after the ChildAdded event.
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MapleLeaf", "finished");



                // TODO 4: Now all the query data is in List firebaseData, Follow the similar procedure in Line activity.
                setReadings();
                firstTimeReadingsSet = true;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        recentPostsQuery.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    // TODO: handle all the returned data. Similar to the Firebase read structure event.
                    DataStructure dataStructure = new DataStructure();
                    dataStructure.setName(dataSnapshot.getValue(DataStructure.class).getName());
                    dataStructure.setTemperature(dataSnapshot.getValue(DataStructure.class).getTemperature());
                    dataStructure.setHumidity(dataSnapshot.getValue(DataStructure.class).getHumidity());
                    dataStructure.setMessage(dataSnapshot.getValue(DataStructure.class).getMessage());
                    String timestamp = dataSnapshot.getValue(DataStructure.class).getTimestamp();
                    dataStructure.setTimestamp(timestamp);

                    firebaseData.add(dataStructure);  // now all the data is in arraylist.
                    Log.d("MapleLeaf", "dataStructure " + dataStructure.getTimestamp());
                }
                // TODO: if already drew but still come to here, there is only one possibility that a new node is added to the database.
                if (firstTimeReadingsSet)
                    setReadings();
            }


            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                if (dataSnapshot != null && dataSnapshot.getValue() != null) {
                    // TODO: handle all the returned data. Similar to the Firebase read structure event.
                    // TODO: This part of the code is to handle if there is any data changed on Firebase.
                    DataStructure dataStructure = new DataStructure();
                    dataStructure.setName(dataSnapshot.getValue(DataStructure.class).getName());
                    dataStructure.setTemperature(dataSnapshot.getValue(DataStructure.class).getTemperature());
                    dataStructure.setHumidity(dataSnapshot.getValue(DataStructure.class).getHumidity());
                    dataStructure.setMessage(dataSnapshot.getValue(DataStructure.class).getMessage());
                    String timestamp = dataSnapshot.getValue(DataStructure.class).getTimestamp();
                    dataStructure.setTimestamp(timestamp);
                    boolean updated = false;
                    for (int i = 0; i<firebaseData.size(); i++){
                        if (firebaseData.get(i).getTimestamp().equals(dataStructure.getTimestamp()))
                        {
                            firebaseData.set(i, dataStructure);
                            updated = true;
                        }
                    }
                    if (!updated)
                        firebaseData.add(dataStructure);  // now all the data is in arraylist.

                    Log.d("MapleLeaf", "dataStructure at " + dataStructure.getTimestamp() + " Updated");
                }
                // TODO 4: Now all the query data is in List firebaseData, Follow the similar procedure in Line activity.
                setReadings();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String convertTimestamp(String timestamp){

        // Convert timestamp to text.
        long yourSeconds = (long)Double.parseDouble(timestamp);
        Date mDate = new Date(yourSeconds*1000);
        DateFormat df = new SimpleDateFormat("dd MMM yyyy");
        // df.setTimeZone(TimeZone.getTimeZone("Etc/GMT-5"));
        DateFormat df1 = new SimpleDateFormat("hh:mm:ss");
        Log.d("MapleLeaf", df.format(mDate) +System.lineSeparator() + df1.format(mDate));
        return df.format(mDate) +System.lineSeparator() + df1.format(mDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.mainreadingspage, menu);
        //globalmenu = menu;
        // setMenuItem(R.id.action_write, false);  // enable the write function.
        //setMenuItem(R.id.action_read, false);  // enable the write function.
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_MainReadingsPage)
        {
            /* // TODO: Start the read option.
            Intent ReadingsData = new Intent(getApplicationContext(), MainReadingsPageActivity.class);
            startActivity(ReadingsData);
            //finish();*/
            return true;
        }
        else if (id == R.id.action_TemperatureData)
        {
            // TODO: Start the read option.
            Intent temperatureData = new Intent(getApplicationContext(), TemperatureDataActivity.class);
            startActivity(temperatureData);
            //finish();
            return true;
        }
        else if (id == R.id.action_SoilMoistureData){
            // TODO: Start the write option.
            Intent soilMoistureData = new Intent(getApplicationContext(), SoilMoistureDataActivity.class);
            startActivity(soilMoistureData);
            //finish();
            return true;
        }

        else if (id == R.id.action_WaterSuppyControl){
            // TODO: Start the write option.
            Intent waterSupplyControl = new Intent(getApplicationContext(), WaterSupplyControlActivity.class);
            startActivity(waterSupplyControl);
            //finish();
            return true;
        }
        else if (id == R.id.action_Settings){
            // TODO: Start the write option.
            Intent settings = new Intent(getApplicationContext(), SettingsActivity.class);
            startActivity(settings);
            //finish();
            return true;
        }
        else if (id == R.id.action_Help){
            // TODO: Start the write option.
            Intent help = new Intent(getApplicationContext(), HelpActivity.class);
            startActivity(help);
            //finish();
            return true;
        }

        else if (id == R.id.action_SignOut){
            // TODO: Start the write option.
            mAuth = FirebaseAuth.getInstance();
            mAuth.signOut();
            Intent signOut = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(signOut);
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }






}