package com.test.manish.run2live;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.migration.Migration;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.util.Log;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.content.Intent;
import android.widget.PopupMenu;
import android.widget.Toast;
import android.widget.TextView;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Cap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.maps.model.RoundCap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.location.LocationResult;
import com.test.manish.run2live.dao.UserDb;
import com.test.manish.run2live.database.AppDatabase;
import com.test.manish.run2live.domain.userData;
import com.test.manish.run2live.domain.userTable;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.concurrent.TimeUnit;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private LatLng latLng;
    private ArrayList<Double>latitude;
    private ArrayList<Double>longitude;
    private LocationRequest mLocationrequest;
    private static final long requestInterval = 10000;
    private static final long fastInterval = 10000/ 2;
    private LocationSettingsRequest mLocationSetting;
    private LocationSettingsRequest.Builder builder;
    private SettingsClient settingClient;
    private Task<LocationSettingsResponse> task;
    private static final int request_validation = 50;
    private static final int removable_resolution = 100;
    private static boolean requestFlag;
    private static final String flagKey = "flag";
    private static final String listCoord = "coord";
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private Location lastLocation;
    private PolylineOptions polylineOption;
    private CameraUpdate zoom;
    private CameraUpdate center;
    private static int count = 1;
    private AppDatabase db;
    private TextView calText;
    private TextView distText;
    private FloatingActionButton start;
    private FloatingActionButton stop;
    private FloatingActionButton pause;
    private FloatingActionButton status;
    private Bitmap startImage;
    private Bitmap stopImage;
    private Bitmap pauseImage;
    private Bitmap statusImage;
    private Bitmap playImage;
    private Bitmap runImage;
    private Bitmap cycleImage;
    private float distance= 0;
    private static long totalTime = 0;
    private  long time1 = 0;
    private TextView caloriesView;
    private TextView distanceView;
    private TextView timeView;
    private userTable user;
    private NavigationView navigationView;
    private Intent intent;
    private boolean activityStateIndicator = false;
    private boolean startButtonClickedState = false;
    private static Polyline polyline;
    private boolean statusClickToggle = false;
    private long heartRate=0;
    private double calories = 0;
    private float minmumDistanceforRequest = 10;
    private UserService timeUpdate;
    private dataUpdate historyUpdate;
    private static double[] timeRecord= new double[4];
   private Thread timer;
   private boolean threadstate = false;
   private double speed=0;
    private double speed2=0;
    private boolean speedstate = true;






    /* activity indentity*/
    private static final int startClickTriggered = 1;
    private static final String startRoute1 = "startRoute1";
  //  private static final String startRoute2 = "startRoute2";
    private static final int registerTriggered = 2;
    private static final int historyId = 3;
    private static final int statisticId = 4;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //--------------------------------------------------code for restoring bundle------
        requestFlag = false;
        latitude = new ArrayList<Double>();
        longitude = new ArrayList<Double>();
        /* ui element*/
        calText = (TextView)findViewById(R.id.calories);
        distText = (TextView)findViewById(R.id.distance);
        start = (FloatingActionButton) findViewById(R.id.start);
        stop = (FloatingActionButton) findViewById(R.id.stop);
        pause = (FloatingActionButton) findViewById(R.id.pause);
        status = (FloatingActionButton) findViewById(R.id.status);
        caloriesView = (TextView) findViewById(R.id.calories);
        distanceView = (TextView) findViewById(R.id.distance);
        timeView = (TextView) findViewById(R.id.time);
        navigationView = (NavigationView) findViewById(R.id.my_navigation_view);
        startImage = textImage("start");
        stopImage = textImage("stop");
        pauseImage = textImage("pause");
        //statusImage = textImage("status");
        playImage = textImage("play");
        runImage = textImage("Run");
        cycleImage = textImage("Cycle");
        start.setImageBitmap(startImage);
        status.setImageBitmap(statusImage);
        stop.setVisibility(View.INVISIBLE);
        pause.setVisibility(View.INVISIBLE);
        status.setImageBitmap(runImage);
        //getting database instance
        db = AppDatabase.getInMemoryDatabase(getApplicationContext());
      // clearDatabse();
        navigationViewListerSetting(navigationView);
       // updateFromBundle(savedInstanceState);
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        settingClient = LocationServices.getSettingsClient(this);
        createLocationRequest();
        locationRequestSetting();
        gettingLastLocation();
        Log.d("create called:","inside create");
    }




/**nevigation view settup*/

public void navigationViewListerSetting(NavigationView navigationView){
    navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            if(item.getTitle().equals("Statistic")){
                intent = new Intent(MapsActivity.this,StatisticAtivity.class);
                startActivityForResult(intent,statisticId);
                return true;

            }else if(item.getTitle().equals("History")){
                intent = new Intent(MapsActivity.this,HistoryActivity.class);
                startActivityForResult(intent,historyId);
                return true;
            }else if(item.getTitle().equals("Update Profile")){
                intent = new Intent(MapsActivity.this,Profile.class);
                intent.putExtra(startRoute1,"startRoute2");
                startActivityForResult(intent,registerTriggered);
                return true;
            }else if(item.getTitle().equals("About Us")){
                return true;
            }else{
                return false;
            }
        }
    });
}

   /* /**
     * get last location
     * **/

    public void gettingLastLocation() {
        mLocationCallback = new LocationCallback(){
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                mCurrentLocation = locationResult.getLastLocation();
                 if(lastLocation!= null){
                                    float travelDistance = lastLocation.distanceTo(mCurrentLocation);
                                    distance = distance + travelDistance;
                                    float viewDistance = distance/1000;
                                    distanceView.setText(String.format("%.2f", viewDistance));
                                    if(viewDistance > 0.10) {// as calories burning is dependent only on time and we cant't
                                        //get real time update od it from google map.
                                        CaloriesUpdate(TimeUnit.MILLISECONDS.toMinutes(totalTime));
                                    }
                                updateMap(mCurrentLocation);
                 } else{
                     updateMap(mCurrentLocation);
                 }

            }
        };
    }

/*
*
* calories count
*
* */

public void CaloriesUpdate(long minutes){
    if(user!= null){
        if(!statusClickToggle){ /*running*/
            if(heartRate == 0){
                if(user.gender.equals("m")){  // running and male
                    if(user.age <= 20){
                        heartRate = 170;
                    }
                    else if(user.age > 20 && user.age <= 30){
                        heartRate = 162;
                    }
                    else if(user.age > 30 && user.age <= 35){
                        heartRate = 157;
                    }
                    else{
                        heartRate = 145;
                    }

                }else{//running and female
                    if(user.age <= 30){
                        heartRate = 157;
                    }
                    else if(user.age > 30 && user.age <= 40){
                        heartRate = 153;
                    }
                    else if(user.age > 40 && user.age <= 45){
                        heartRate = 149;
                    }
                    else{
                        heartRate = 145;
                    }
                }
            }
            if(user.gender.equals("m")){
                speedChange();
                calories = ((user.age * 0.2017) -(user.weight * 0.09036) + (heartRate * 0.6309) - 55.0969) *  minutes/4.184;
            }else{
                speedChange();
                calories = ((user.age * 0.074) - (user.weight * 05741) + (heartRate * 0.4472) - 20.4022) * minutes/4.184;
            }
            caloriesView.setText(Long.toString(Math.round(calories)));

        }else{ /*cycling we consider zones i.e 220 - age will give max heart beat we can go and hence determine different
        zones  */
            if(heartRate == 0) {// https://www.cycling-inform.com/how-to-use-heart-rate-monitor-and-zones-to-improve-your-cycling
                heartRate = 220 - user.age; //max heart beat
                heartRate = 75 / 100 * heartRate; // taking 75% for average hard work.
            }
            if(user.gender.equals("m")){
                speedChange();
                calories = ((user.age * 0.2017) -(user.weight * 0.09036) + (heartRate * 0.6309) - 55.0969) *  minutes/4.184;
            }else{
                speedChange();
                calories = ((user.age * 0.074) - (user.weight * 05741) + (heartRate * 0.4472) - 20.4022) * minutes/4.184;
            }
            caloriesView.setText(Long.toString(Math.round(calories)));
        }
    }
}


/*speed variation*/

public void speedChange(){
    speed =(Double.parseDouble(distanceView.getText().toString())) /(double) TimeUnit.MILLISECONDS.toMinutes(totalTime);
    if(speedstate){
        speed2=speed;
        speedstate = false;
    }
    if(Math.abs(speed - speed2) > 0.8){

        if(speed - speed2 > 0 ){
            heartRate += 5;
        }else{
            heartRate -= 5;
        }
        speed2 = speed;
    }
}


//method to convert your text to image

    public static Bitmap textImage(String text) {
        int textColor = Color.WHITE;
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTextSize(16);
        paint.setColor(textColor);
        paint.setTextAlign(Paint.Align.LEFT);
        float baseline = -paint.ascent(); // ascent() is negative
        int width = (int) (paint.measureText(text) + 0.0f); // round
        int height = (int) (baseline + paint.descent() + 0.0f);
        Bitmap image = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(image);
        canvas.drawText(text, 0, baseline, paint);
        return image;
    }



    /**
     * callback for getting result after resolvable permission task!
     **/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        /* returned after start button was clicked */
        if (requestCode == startClickTriggered) {
            if (resultCode == Activity.RESULT_OK) {
                    user = new userTable();
                    user.gender = data.getStringExtra("gender");
                    user.weight = Long.parseLong(data.getStringExtra("weight"));
                    user.age = Long.parseLong(data.getStringExtra("age"));
            }
            else{
                Toast toast = Toast.makeText(getApplicationContext(), "User need to register",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                finish();
            }
        }
        if (requestCode == registerTriggered) {
            if (resultCode == Activity.RESULT_OK) {
                    if(user == null){
                        user = new userTable();
                    }
                    user.gender = data.getStringExtra("gender");
                    user.weight = Long.parseLong(data.getStringExtra("weight"));
                    user.age = Long.parseLong(data.getStringExtra("age"));

            } else{
                    // not needed
            }
        }
        if(requestCode == historyId){
                // do nothing
        }
        if(requestCode == statisticId){
            //do nothing;
        }

    }


    /**
     * checking permission for the application:
     * checks whether user gives the permission to the application that is mentioned in the manifest
     **/

    private boolean checkingPermission() {
        int permission = ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION);
        return (permission == PackageManager.PERMISSION_GRANTED);
    }

    /**
     * requesting permission for the application
     * if not grantted by the user.
     **/

    private void requestPermission() {

    /* if user has rejected to accept the permission without choosing "never show again" option*/
        boolean should_show_detail = ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (should_show_detail) {
            displaySnackbar(R.string.permission_request,
                    android.R.string.ok, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            ActivityCompat.requestPermissions(MapsActivity.this,
                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                    request_validation);
                        }
                    });
        } else {
        /* requesting to user to give permission could be first time or when trying to use the app
        * even when "never ask was selected"*/
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    request_validation);
        }

    }

    /**
     * implementing callback for ActivityCompat
     **/
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == request_validation) {
            if (permissions.length <= 0) {
        /* user ingnored, and ther permission was not set or denied*/
            } else if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        /* permission granted*/
                //we can start our loaction work ---------------------------------------------------
                startLoactionUpdate();
            } else {
            /*permission denied*/
                displaySnackbar(R.string.permission_denied, R.string.setting, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions(MapsActivity.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                request_validation);
                    }
                });
            }
        }
    }

    /**
     * displaying snackbar for the permission
     **/

    private void displaySnackbar(final int id, final int message, View.OnClickListener barclick) {
        Snackbar.make(findViewById(android.R.id.content), getString(id), Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(message), barclick).show();
    }


    public void createLocationRequest() {
        mLocationrequest = new LocationRequest();
        mLocationrequest.setInterval(requestInterval);
        mLocationrequest.setFastestInterval(fastInterval);
        mLocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    //    mLocationrequest.setSmallestDisplacement(minmumDistanceforRequest);
    }

    public void locationRequestSetting() {
        builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationrequest);
    }

    /**
     * onResume
     **/

    @Override
    public void onResume() {
        Log.d("resume called:","inside resume");
        super.onResume();
        if(activityStateIndicator){
            if(startButtonClickedState){
            if (checkingPermission() ) {
                activityStateIndicator = false;
               startLoactionUpdate();
            } else if(!checkingPermission()) {
                activityStateIndicator = false;
                requestPermission();
                }
            }
        }
    }

    /* on pause*/

    @Override
    public void onPause(){
        super.onPause();
        Log.d("pause state","pause");
        activityStateIndicator = true;
    }


  /*
  * button click
  * */

  public void startClick(View view){
      mMap.clear();
      timeView.setText("");
      distanceView.setText("");
      caloriesView.setText("");
      speedstate = true;
      if(user == null){
            user = db.userDb().listAllUsers();
            if(user == null){
            // ask for user to register
            intent = new Intent(MapsActivity.this,Profile.class);
            intent.putExtra(startRoute1,"startRoute1");
            startActivityForResult(intent,startClickTriggered);
            }
         }
      polylineOption = new PolylineOptions().width(16).color(Color.RED).startCap(new RoundCap()).endCap(new RoundCap());
      stop.setImageBitmap(stopImage);
      pause.setImageBitmap(pauseImage);
      stop.setVisibility(View.VISIBLE);
      pause.setVisibility(View.VISIBLE);
      start.setVisibility(View.INVISIBLE);
      startButtonClickedState = true;
      if (checkingPermission() ) {
          requestFlag = true;
          time1 = System.currentTimeMillis();
          timeUpdate = new UserService();
          timer  = new Thread(timeUpdate);
          timer.start();
          startLoactionUpdate();
      } else if(!checkingPermission()) {
          requestPermission();
      }
  }


  public void stopClick(View view){
      //Double speed = 0.0;
      stopLocationUpdates();
    stop.setVisibility(View.INVISIBLE);
    pause.setVisibility(View.INVISIBLE);
    start.setVisibility(View.VISIBLE);
    userData userdata = new userData();
    DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy");
    Date date = new Date();
      userdata.setDate(dateFormat.format(date));
     if(timeView.getText()!=""){
        userdata.setTime(timeView.getText().toString());
      }else{
          userdata.setTime("0");
      }
      if(distanceView.getText()!=""){
      userdata.setDistance(Double.parseDouble(distanceView.getText().toString()));
      }else{
          userdata.setDistance(0.0);
      }
      if(caloriesView.getText()!= ""){
        userdata.setCalories(Double.parseDouble(caloriesView.getText().toString()));
      }else{
          userdata.setCalories(0.0);
      }
      if(distanceView.getText()!= "" && timeView.getText()!= ""){
          speed =(Double.parseDouble(distanceView.getText().toString())) /(double) TimeUnit.MILLISECONDS.toMinutes(totalTime);
          speed =  (double) Math.round(speed*100)/100;
          userdata.setSpeed(speed);
      }else{
          userdata.setSpeed(0);
      }

      lastLocation = null;
      distance = 0;
      totalTime = 0;
      calories = 0;
      historyUpdate = new dataUpdate();
      historyUpdate.execute(userdata);
      if(threadstate)
          timer.interrupt();

  }


  public void pauseClick(View view){
    if(count == 1){
        pause.setImageBitmap(playImage);
        stopLocationUpdates();
        timer.interrupt();
        count = 0;
    }
    else{
        pause.setImageBitmap(pauseImage);
        if (checkingPermission() ) {
            if(!threadstate){
                if(!timer.isAlive()){
                    timer = new Thread(timeUpdate);
                    time1 = System.currentTimeMillis();
                    timer.start();
                }
            }
            startLoactionUpdate();
        } else if(!checkingPermission()) {
            requestPermission();
        }
        count = 1;
    }
  }

public void statusClick(View view){
      if(statusClickToggle){
          status.setImageBitmap(runImage);
          statusClickToggle = false;
      }
      else{
          status.setImageBitmap(cycleImage);
          statusClickToggle = true;
      }

}
    /**
     * onStopLocation update
     **/

    @Override
    protected void onStop() {
        super.onStop();
        activityStateIndicator = true;
        Log.d("stop state","state");
    }

    /**
     * onDestroy
     * */
    @Override
    protected void onDestroy(){
        super.onDestroy();
        Log.d("distroy called:","inside destroy");
        stopLocationUpdates();
        AppDatabase.destroyInstance();
        if(threadstate){
            timer.interrupt();
        }
    }



    /**
     * startingLoaction update
     **/

    public void startLoactionUpdate() {
        task = settingClient.checkLocationSettings(builder.build());
        /**
         *  analysing the result of the task through callbacks
         * **/
        task.addOnCompleteListener(new OnCompleteListener<LocationSettingsResponse>() {
            @Override
            public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
                try {
                    LocationSettingsResponse response = task.getResult(ApiException.class);
                   /*according to the book we can call fused location object here but not in the code*/
                    if (checkingPermission()) {
                        mFusedLocationProviderClient.requestLocationUpdates(mLocationrequest,
                                mLocationCallback, null);
                    }
                } catch (ApiException e) {
                    switch (e.getStatusCode()) {
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            try {
                                ResolvableApiException resolvable = (ResolvableApiException) e;
                                resolvable.startResolutionForResult(MapsActivity.this, removable_resolution);
                            } catch (IntentSender.SendIntentException intendException) {
                            } catch (ClassCastException castException) {
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            String error = "Location settings cannot be fixed";
                            Toast.makeText(MapsActivity.this, error, Toast.LENGTH_LONG).show();
                            requestFlag = false;
                            break;
                    }
                }
                //    catch(SecurityException e){}
            }
        });
    }

    /**
     * stoping update of loaction
     **/

    public void stopLocationUpdates() {
        if (!requestFlag) {
            return;
        }else{
        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback)
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        requestFlag = false;
                        Log.d("stoploaction called","remove location");
                    }

                });
        }
    }


    /**
     * setting up onsaveInstance. invoked when activity is temporarily destroyed.
     **/
  /*  @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(flagKey, requestFlag);
    //    savedInstanceState.putParcelableArrayList(listCoord, coordinates);
        super.onSaveInstanceState(savedInstanceState);
    }*/

    /**
     * updating value from bundle
     **/
  /*  public void updateFromBundle(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.keySet().contains(flagKey)) {
                requestFlag = savedInstanceState.getBoolean(flagKey);
            }

        if (savedInstanceState.keySet().contains(listCoord)) {
            }
        }

    }*/

/**
 * loaction update for continues trigger
 * **/


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng temp = new LatLng(0, 0);
        zoom= CameraUpdateFactory.zoomTo(18);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(temp));
        center=CameraUpdateFactory.newLatLng(temp);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

    /**
     * Code to update google map
     */

    public void updateMap(Location location) {

           lastLocation = mCurrentLocation;
      //  for (Location location : coordinates) {
            latLng = new LatLng(location.getLatitude(), location.getLongitude());
            polylineOption.add(latLng);

            polyline = mMap.addPolyline(polylineOption);
       // }
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        center=CameraUpdateFactory.newLatLng(latLng);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
    }

/*** thread for running timer*/


    public class UserService implements Runnable{
        @Override
    public void run(){
            Log.d("inside runable"," Threat running");
                Thread.currentThread().setName("UserService");
                Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
                threadstate = true;
                    while(MapsActivity.requestFlag) {
                        long time2 = System.currentTimeMillis();
                        MapsActivity.totalTime = MapsActivity.totalTime + (time2 - time1);
                        time1 = time2;
                        long second = (MapsActivity.totalTime / 1000) % 60;
                        long minute = (MapsActivity.totalTime / (60 * 1000)) % 60;
                        long hour = (MapsActivity.totalTime / (60 * 60 * 1000)) % 24;
                        long day = MapsActivity.totalTime / (24 * 60 * 60 * 1000);
                        Message message = Message.obtain();
                        Bundle bundle = new Bundle();
                        bundle.putLong("hours",hour);
                        bundle.putLong("minute",minute);
                        bundle.putLong("second",second);
                        message.setData(bundle);
                        MainHandler.sendMessage(message);
                        try {
                            Thread.sleep(5000);
                        }catch (InterruptedException irexcption) {
                            Log.d("inside runnable","Intrupped");
                            threadstate = false;
                            break;
                        }
            }
        }
}

@SuppressLint("HandlerLeak")
public Handler MainHandler = new Handler(){
        public void handleMessage(Message inputMessage){
            Bundle bundle = inputMessage.getData();
            String hour = Long.toString(bundle.getLong("hours"));
            String minute = Long.toString(bundle.getLong("minute"));
            String second = Long.toString(bundle.getLong("second"));
            timeView.setText(hour+":"+minute+":"+second);
        }
};


    /***  writing to userData table database  ***/

    public class dataUpdate extends AsyncTask<userData,Void,Void> {

        @Override
        protected Void doInBackground(userData... param) {
            Log.d("second async","database update");
            db = AppDatabase.getInMemoryDatabase(getApplicationContext());
            db.totalDb().insertdb(param[0]);
            AppDatabase.destroyInstance();
            return null;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }


    }

}
