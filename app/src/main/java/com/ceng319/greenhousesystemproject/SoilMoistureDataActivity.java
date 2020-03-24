package com.ceng319.greenhousesystemproject;


import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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

public class SoilMoistureDataActivity extends AppCompatActivity{

    private BarChart barchart;
    private FirebaseDatabase database = null;
    private FirebaseAuth mAuth;
    public String pKey, path,newKey;


    private DatabaseReference ref = null;
    private int N = 7;
    String[] XLabels = new String[N];
    List<DataStructure> firebaseData = new ArrayList<>();
    private boolean firstTimeDrew;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_soil_moisture_data);
        //setupTitleandHomeButton();
        // TODO 1: Find the chart
        barchart = findViewById(R.id.firebasebar_chart);

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
                        //Toast.makeText(getApplicationContext(), "User is "+idk+"Product id is :xxx"+pKey+"xxx", Toast.LENGTH_LONG).show();
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

    private void drawGraph() {
        if (firebaseData.size() > N)  // Should have a guard to make sure we always draw the most recent N numbers.
        {
            firebaseData = firebaseData.subList(firebaseData.size()-N, firebaseData.size());
        }


        // TODO: Set text description of the xAxis

        Description desc = new Description();
        desc.setText(getString(R.string.SoilDataGraph));
        desc.setTextSize(20);
        desc.setPosition(700,100);
        barchart.setDescription(desc);
        barchart.animateXY(2000,2000);
        // TODO: Set the X-axis labels
        setAndValidateLabels();

        // TODO: Set LineData Entries
        int i = 0;
        List<BarEntry> entrylist = new ArrayList();
        // TODO: Entry is the element of the data input to the chart. All the data should be organized as Entries' ArrayList
        for (DataStructure ds: firebaseData){
            BarEntry e = new BarEntry (i++, Float.parseFloat(ds.getHumidity()));
            entrylist.add(e);
        }

        // TODO: find the dataset of the ArrayList.
        BarDataSet dataset = new BarDataSet(entrylist, "Soil Moisture in %");
        dataset.setColors(ColorTemplate.COLORFUL_COLORS);  // set the color of this chart.
        dataset.setValueTextSize(14);
        // TODO: Get the LineData Object from dataset.
        BarData linedata = new BarData(dataset);
        barchart.setData(linedata);
        // TODO: This is a must to refresh the chart.
        barchart.invalidate(); // refresh
    }

    private void setAndValidateLabels() {
        // TODO: Set the labels to be displayed.
        XAxis xAxis = barchart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelRotationAngle(-30f);  // rotate the xAxis label for 30 degrees.
        xAxis.setValueFormatter(new IAxisValueFormatter(){
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                // Seems to be a bug in the library code value should not be less than 0 or more than N-1.
                // When asking for the most recent data.
                if ((value <0) || (value > N-1))return "";
                return XLabels[(int) value];
            }
        });
        xAxis.setGranularity(1f); // minimum axis-step (interval) is 3
    }

    private void loadDatabase(DatabaseReference ref) {
        // Last N data entries from Database, these are automatically the N most recent data
        Query recentPostsQuery = ref.limitToLast(N).orderByChild("timestamp");

        // NOTICE: Firebase Value event is always called after the ChildAdded event.
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Log.d("MapleLeaf", "finished");

                for (int i=0; i< firebaseData.size(); i++){
                    // TODO: Define the XLabels of the chart.
                    XLabels[i] = convertTimestamp(firebaseData.get(i).getTimestamp());
                }

                // TODO 4: Now all the query data is in List firebaseData, Follow the similar procedure in Line activity.
                drawGraph();
                firstTimeDrew = true;
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
                if (firstTimeDrew)
                    drawGraph();
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
                drawGraph();
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
/*
    private void setupTitleandHomeButton() {
        getSupportActionBar().setSubtitle(R.string.local_bar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }*/

    /**
     * This hook is called whenever an item in your options menu is selected.
     * The default implementation simply returns false to have the normal
     * processing happen (calling the item's Runnable or sending a message to
     * its Handler as appropriate).  You can use this method for any items
     * for which you would like to do processing without those other
     * facilities.
     *
     * <p>Derived classes should call through to the base class for it to
     * perform the default menu handling.</p>
     *
     * @param item The menu item that was selected.
     * @return boolean Return false to allow normal menu processing to
     * proceed, true to consume it here.
     * @see #onCreateOptionsMenu
     */
   /* @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }*/
}