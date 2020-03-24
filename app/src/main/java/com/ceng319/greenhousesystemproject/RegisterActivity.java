package com.ceng319.greenhousesystemproject;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
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
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity{

    EditText mTextUsername;
    EditText mTextPassword;
    EditText mTextCnfPassword;
    Button mButtonRegister;
    Button mButtonLogin;

    private FirebaseAuth mAuth;
    private TextView message;
    private EditText email;
    private EditText name;
    private EditText password1;
    private EditText password2;
    private EditText key;
    private Button register1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AppTheme_NoActionBar);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        findAllViewsfromLayout();
        mButtonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Login = new Intent(RegisterActivity.this,LoginActivity.class);
                startActivity(Login);
            }
        });
        register1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegisteration();
            }
        });
    }
    private void findAllViewsfromLayout()
    {
        mButtonLogin = findViewById(R.id.button_login_2);
        register1 = findViewById(R.id.button_register_2);
        name=findViewById(R.id.edittext_name);
        email = findViewById(R.id.edittext_username_2);
        password1 = findViewById(R.id.edittext_password_2);
        password2 = findViewById(R.id.edittext_cnf_password_2);
        key = findViewById(R.id.edittext_key);
    }
    private void startRegisteration() {
        // TODO: Create new users on Firebase.
        final String registeredEmail = String.valueOf(email.getText()).trim();
        String registerPassword1 = String.valueOf(password1.getText());
        String registerPassword2 = String.valueOf(password2.getText());
        final String registeredProductKey = String.valueOf(key.getText());
        final String registeredName=String.valueOf(name.getText()).trim();



        if (registeredEmail.length() == 0 || password1.length() == 0 || password2.length() == 0  || registeredProductKey.length() == 0 ){
            Toast.makeText(getApplicationContext(), R.string.RegisterEmailCantBeEmpty,
                    Toast.LENGTH_LONG).show();
            return; // do nothing if empty.
        }
        else if (!registerPassword1.equals(registerPassword2))
        {
            Toast.makeText(getApplicationContext(), R.string.RegisterPasswordDoesntMatch,
                    Toast.LENGTH_LONG).show();
        }
        mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(registeredEmail, registerPassword1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {


                            User user = new User(registeredName,registeredEmail,registeredProductKey);


                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                    if (task.isSuccessful()) {
                                        Toast.makeText(RegisterActivity.this, getString(R.string.registration_success), Toast.LENGTH_LONG).show();
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        //  message.setText("New user "+ user.getEmail() + " is now registered");


                                        Intent Login = new Intent(RegisterActivity.this,LoginActivity.class);
                                        startActivity(Login);

                                        finish();



                                    } else {
                                        //display a failure message
                                        // If sign in fails, display a message to the user.
                                        Log.w("MapleLeaf", "createUserWithEmail:failure", task.getException());
                                        Toast.makeText(getApplicationContext(), R.string.RegisterCreateNewUser,
                                                Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                        } else {
                            Toast.makeText(RegisterActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });


    }
}
