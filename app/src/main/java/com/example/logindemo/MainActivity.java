package com.example.logindemo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.BackgroundColorSpan;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vorlonsoft.android.rate.AppRate;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText etName;
    private EditText etPassword;
    private TextView tvInfo;
    private Button btnLogin;
    private TextView tvSignUp;
    private CheckBox cbRememberMe;
    private int counter = 5;
    public Credentials credentials;

    private boolean isValid = false;

    SharedPreferences sharedPreferences;
    //editor needed bcs we want to add a boolen - if cb is checked
    SharedPreferences.Editor sharedPreferencesEditor;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        etName = (EditText) findViewById(R.id.etName);
        etPassword = (EditText) findViewById(R.id.etPassword);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvInfo = (TextView) findViewById(R.id.txtAttempts);
        tvSignUp = (TextView) findViewById(R.id.txtSignUp);
        cbRememberMe = (CheckBox) findViewById(R.id.cbRemember);



        tvInfo.setText("No. of attempts remaining: " + counter);

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        credentials = new Credentials();

        if (sharedPreferences != null) {
            //get values from sharedPreferences - load them into new Map
            Map<String, ?> loadedSharedPreferences = sharedPreferences.getAll();

            //load samo ako se ima sta loadovat
            if (loadedSharedPreferences.size() != 0) {
                credentials.loadCredentials(loadedSharedPreferences);
            }
            String usernameSP = sharedPreferences.getString("LastSavedUsername", "");
            String passwordSP = sharedPreferences.getString("LastSavedPassword", "");
           boolean shouldCheck = sharedPreferences.getBoolean("shouldRemember", false);

            Intent intent = getIntent();
            //if the boolean isnt retrieved from the Reg Act. - means that the new User ISNT registered - so value is false
            boolean newUserRegistered = intent.getBooleanExtra("newUserRegistered",false);
//this TODO odavdje

            if (shouldCheck) {
                etName.setText(usernameSP);
                etPassword.setText(passwordSP);
                cbRememberMe.setChecked(true);
                if(newUserRegistered){
                    cbRememberMe.setChecked(false);
                    sharedPreferencesEditor.remove("shouldRemember");
                    sharedPreferencesEditor.apply();
                }

            }
           /*
            boolean justRegistered = intent.getBooleanExtra("newUserRegistered",shouldCheck);
            if (shouldCheck) {
                etName.setText(usernameSP);
                etPassword.setText(passwordSP);
                if(justRegistered)
                cbRememberMe.setChecked(false);
                else
                    cbRememberMe.setChecked(true);

            }*/
        }
        //edit the SignUp so only text Sign Up is colored and clickable
        SpannableString spString = SpannableString.valueOf(getString(R.string.sign_up));

        ForegroundColorSpan fcsBlue = new ForegroundColorSpan(getColor(R.color.blue_link));

        ClickableSpan clickSpan = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                //what to do when clicked
                BackgroundColorSpan bcsBlue = new BackgroundColorSpan(getColor(R.color.blue_linkBox));
                spString.setSpan(bcsBlue, spString.length() - 7, spString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                tvSignUp.setText(spString);
                startActivity(new Intent(MainActivity.this, RegistrationActivity.class));
                spString.removeSpan(bcsBlue);
                tvSignUp.setText(spString);
            }
        };

        spString.setSpan(clickSpan, spString.length() - 7, spString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spString.setSpan(fcsBlue, spString.length() - 7, spString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        tvSignUp.setMovementMethod(LinkMovementMethod.getInstance());
        tvSignUp.setText(spString);

        cbRememberMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharedPreferencesEditor.putBoolean("shouldRemember", cbRememberMe.isChecked());
                sharedPreferencesEditor.apply();
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String inputName = etName.getText().toString();
                String inputPassword = etPassword.getText().toString();

                if (inputName.isEmpty() || inputPassword.isEmpty()) {
                    Toast.makeText(MainActivity.this, R.string.toast_no_entry, Toast.LENGTH_SHORT).show();
                } else {
                    isValid = validate(inputName, inputPassword);

                    if (isValid) {
                        Toast.makeText(MainActivity.this, R.string.toast_correct_entry, Toast.LENGTH_SHORT).show();

                        sharedPreferencesEditor.putString("LastSavedUsername", inputName);
                        sharedPreferencesEditor.putString("LastSavedPassword", inputPassword);
                        sharedPreferencesEditor.apply();

                        Intent intent = new Intent(MainActivity.this, SecondActivity.class);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, R.string.toast_incorrect_entry, Toast.LENGTH_SHORT).show();
                        counter--;
                        if (counter == 0) {
                            btnLogin.setEnabled(false);
                        }
                        tvInfo.setText("No. of attempts remaining: " + counter);
                    }
                }
            }
        });
    //TO show APP-RATE DIALOGUE
        AppRate.with(this)                  //needed byte - every number considered an int so you must cast it
                .setInstallDays((byte) 0)       //how many days after installation have to pass for dialog to be shown
                .setLaunchTimes((byte)2)        // how many launches
                .setRemindInterval((byte)2)     //how many time shall pass after user clicks Remind me later
                .monitor();
        AppRate.showRateDialogIfMeetsConditions(this);
    }

    private boolean validate(String name, String password) {
        //return credentials.checkCredentials(name,password);
        //accessing an object which is created in Reg.activity -
        // maybe by the time method is envoked it wont have been instantiated so check if null (maybe no saved entries in preferences)
        if ( sharedPreferences!= null) {
            if(credentials.checkCredentials(name,password)){
                return true;
            }
            else return false;
        }
        return true;
    }
}