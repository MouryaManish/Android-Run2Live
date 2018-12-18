package com.test.manish.run2live;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.test.manish.run2live.database.AppDatabase;
import com.test.manish.run2live.domain.userTable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Profile extends AppCompatActivity {

   // private TextView firstName;
    private EditText firstNameInput;
    //private TextView lastName;
    private EditText lastNameInput;
    private RadioButton male;
    private RadioButton female;
  //  private TextView height;
    private EditText heightInput;
  //  private TextView weight;
    private EditText weightInput;
   // private TextView dob;
    private EditText dobInput;
    private userTable user;
    private userTable user2;
    private String firstName;
    private String lastName;
    private String gender;
    private String height;
    private String weight;
    private String dob;
    private Matcher match;
    private Pattern nameCheck =Pattern.compile("(^[a-zA-Z]+)");
    private Pattern heightCheck =Pattern.compile("^([0-9])\\'[0-9]{1,2}");
    private Pattern weightCheck = Pattern.compile("(^[0-9]+)");
   // private Pattern dateCheck = Pattern.compile("((0[1-9]|1[012])\\/(0[1-9]|[12][0-9]|3[01])\\/[0-9]{4})");
    private AppDatabase db;
    private static final String status="success";
    private static final String startRoute1 = "startRoute1";
    private static final String startRoute2 = "startRoute2";
  //  private RadioButton maleButton;
   // private RadioButton femaleButton;
    private RadioGroup radioGroup;
    private Intent incommingIntent;
    private String massage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        firstNameInput = (EditText) findViewById(R.id.firstNameInputText);
        lastNameInput = (EditText) findViewById(R.id.lastNameInputText);
        heightInput = (EditText) findViewById(R.id.heightInputText);
        weightInput = (EditText) findViewById(R.id.weightInputText);
        dobInput = (EditText) findViewById(R.id.dobInputText);
        radioGroup = (RadioGroup) findViewById(R.id.radioGroup);
       // maleButton = (RadioButton) findViewById(R.id.maleButton);
        //femaleButton = (RadioButton) findViewById(R.id.femaleButton);

        db = AppDatabase.getInMemoryDatabase(getApplicationContext());
        incommingIntent = getIntent();
        massage = incommingIntent.getStringExtra(startRoute1);
        if(massage.equals(startRoute2)){
        user = db.userDb().listAllUsers();
        if(user!= null){
            firstNameInput.setText(user.firstName);
            lastNameInput.setText(user.lastName);
            String height = user.foot +"'"+ user.inch;
            heightInput.setText(height);
            weightInput.setText(Long.toString(user.weight));
            if(user.gender.equals("m")){
                radioGroup.check(R.id.maleButton);
            }else{
                radioGroup.check(R.id.femaleButton);
            }
            dobInput.setText(Long.toString(user.age));
            }
        }
    }

    public void Register(View view){
       user= new userTable();
       firstName = firstNameInput.getText().toString();
    lastName = lastNameInput.getText().toString();
    height = heightInput.getText().toString();
    weight = weightInput.getText().toString();
    dob = dobInput.getText().toString();
    match = nameCheck.matcher(firstName);
    if(match.matches()){
        match = nameCheck.matcher(lastName);
        if(match.matches()){
            match = heightCheck.matcher(height);
            if(match.matches()){
                match = weightCheck.matcher(weight);
                if(match.matches()){
                    if(gender != null){
                            user.firstName = firstName;
                            user.lastName = lastName;
                            user.age = Long.parseLong(dob);
                            String[] array = height.split("\'");
                            user.foot=Long.parseLong(array[0]);
                            user.inch = Long.parseLong(array[1]);
                            user.weight = Long.parseLong(weight);
                            user.gender= gender;
                            if(massage.equals(startRoute1)){
                                db.userDb().insertUsers(user);
                                Intent intent = new Intent();
                                intent.putExtra("gender",user.gender);
                                intent.putExtra("weight",Long.toString(user.weight));
                                intent.putExtra("age",Long.toString(user.age));
                                setResult(Activity.RESULT_OK,intent);
                                AppDatabase.destroyInstance();
                                finish();
                            }
                            if(massage.equals(startRoute2)){
                                db.userDb().updateUsers(user);
                                Intent intent = new Intent();
                                intent.putExtra("gender",user.gender);
                                intent.putExtra("weight",Long.toString(user.weight));
                                intent.putExtra("age",Long.toString(user.age));
                                setResult(Activity.RESULT_OK,intent);
                                AppDatabase.destroyInstance();
                                finish();
                            }
                    }else{
                        Toast toast = Toast.makeText(getApplicationContext(), "enter gender Information",
                                Toast.LENGTH_SHORT);
                        toast.setGravity(Gravity.CENTER, 0, 0);
                        toast.show();
                    }
                }else{
                    Toast toast = Toast.makeText(getApplicationContext(), "enter a valid data example 100",
                            Toast.LENGTH_SHORT);
                    toast.setGravity(Gravity.CENTER, 0, 0);
                    weightInput.setText("");
                    toast.show();
                }

            }else{
                Toast toast = Toast.makeText(getApplicationContext(), "enter a valid height example 9'99(max) or 1'9 ",
                        Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                heightInput.setText("");
                toast.show();
            }
        }else{
            Toast toast = Toast.makeText(getApplicationContext(), "enter a valid data example smith",
                    Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.CENTER, 0, 0);
            lastNameInput.setText("");
            toast.show();

        }

    }else{
        Toast toast = Toast.makeText(getApplicationContext(), "enter a valid data example Ryan",
                Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, 0, 0);
        firstNameInput.setText("");
        toast.show();
    }

    }

    public void onRadioButtonClicked(View view){
        boolean checked = ((RadioButton) view).isChecked();
        switch(view.getId()){
            case R.id.maleButton:{
                if(checked){
                    gender = "m";
                }
                break;
            }
            case R.id.femaleButton:{
                if(checked){
                    gender ="f";
                }
                break;
            }
        }

    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        Intent intent = new Intent();
        setResult(Activity.RESULT_CANCELED,intent);
        AppDatabase.destroyInstance();
        finish();
    }
}
