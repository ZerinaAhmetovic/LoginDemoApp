package com.example.logindemo;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    private EditText etRegName;
    private EditText etRegPassword;
    private Button btnRegister;

    public Credentials credentials;

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor sharedPreferencesEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        etRegName = (EditText) findViewById(R.id.etRegName);
        etRegPassword = (EditText) findViewById(R.id.etRegPassword);
        btnRegister = (Button) findViewById(R.id.btnRegister);

        sharedPreferences = getApplicationContext().getSharedPreferences("CredentialsDB", MODE_PRIVATE);
        sharedPreferencesEditor = sharedPreferences.edit();

        credentials = new Credentials();

        //needed to load the data - credentials in this class are empty - different from those in MainActivity
        if (sharedPreferences != null) {
            //get values from sharedPreferences - load them into new Map
            Map<String, ?> loadedSharedPreferences = sharedPreferences.getAll();

            //load samo ako se ima sta loadovat
            if (loadedSharedPreferences.size() != 0) {
                credentials.loadCredentials(loadedSharedPreferences);
            }
        }

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputRName = etRegName.getText().toString();
                String inputRPassword = etRegPassword.getText().toString();

                if (validate(inputRName, inputRPassword)) {

                    if (credentials.alreadyTakenUsername(inputRName)) {
                        Toast.makeText(RegistrationActivity.this, R.string.toast_taken_username, Toast.LENGTH_SHORT).show();
                    } else {
                        credentials.addCredentials(inputRName, inputRPassword);
                        sharedPreferencesEditor.putString(inputRName, inputRPassword);

                        sharedPreferencesEditor.putString("LastSavedUsername", inputRName);
                        sharedPreferencesEditor.putString("LastSavedPassword", inputRPassword);
                        sharedPreferencesEditor.apply();

                        Intent intent = new Intent(RegistrationActivity.this, MainActivity.class);
                        intent.putExtra("newUserRegistered", true);
                        startActivity(intent);
                        //TODO
                        //startActivity(new Intent(RegistrationActivity.this, MainActivity.class));
                        Toast.makeText(RegistrationActivity.this, R.string.toast_success, Toast.LENGTH_SHORT).show();
                    }

                }
            }
        });

    }

    private boolean validate(String name, String password) {
        if (name.isEmpty() || password.isEmpty()) {
            Toast.makeText(RegistrationActivity.this, R.string.toast_no_entry, Toast.LENGTH_SHORT).show();
            return false;
        } else if (password.length() < 8) {
            Toast.makeText(RegistrationActivity.this, R.string.toast_short_pass, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}