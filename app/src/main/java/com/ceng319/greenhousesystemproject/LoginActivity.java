package com.ceng319.greenhousesystemproject;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity{
    EditText mTextUsername;
    EditText mTextPassword;
    Button mButtonLogin;
    Button mButtonRegister;
    ProgressBar progressBar;

    private FirebaseAuth mAuth;

    protected void onCreate(Bundle savedInstanceState) {
        //setTheme(R.style.AppTheme_NoActionBar);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        findAllViewsfromLayout();
        handleLogin();
    }


    private void findAllViewsfromLayout() {
        mTextUsername = (EditText) findViewById(R.id.edittext_username);
        progressBar = findViewById(R.id.progressBar4);
        mTextPassword = (EditText) findViewById(R.id.edittext_password);
        mButtonLogin = (Button) findViewById(R.id.button_login);
        mButtonRegister = (Button) findViewById(R.id.button_register);
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createNewUser();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_quit){
            // TODO: Finish the APP.
            finishAndRemoveTask();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void handleLogin(){
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginUser(String.valueOf(mTextUsername.getText()), String.valueOf(mTextPassword.getText()));
            }
        });
    }


    private void loginUser(String email, String password){


        if(email.length()==0 || password.length()==0)
        {
            Toast.makeText(getApplicationContext(), R.string.LoginPasswordCantBeEmpty,
                    Toast.LENGTH_LONG).show();
            return;
        }
        // TODO: Login with Email and Password on Firebase.
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            // Log.d("MapleLeaf", "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            gotoMainReadingsPage();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("MapleLeaf", "signInWithEmail:failure", task.getException());
                            Toast.makeText(getApplicationContext(), R.string.LoginAuthenticationFailed,
                                    Toast.LENGTH_LONG).show();
                        }

                        // ...
                    }
                });


    }
    private void createNewUser(){
        Intent register = new Intent(getApplicationContext(), RegisterActivity.class);
        startActivity(register);
        finish();

    }

    private void gotoMainReadingsPage() {
        // TODO : Start the read option After login
        Intent intent1 = new Intent(getApplicationContext(), MainReadingsPageActivity.class);
        startActivity(intent1);
        finish();
    }



}
