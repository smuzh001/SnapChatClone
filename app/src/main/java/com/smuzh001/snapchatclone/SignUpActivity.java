package com.smuzh001.snapchatclone;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpActivity extends AppCompatActivity {

    private TextView emailTextView;
    private TextView passwordTextView;
    private TextView confirmTextView;
    private Button loginButton;
    public FirebaseAuth auth;
    public FirebaseDatabase database;;
    public DatabaseReference myRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailTextView = (TextView) findViewById(R.id.newEmailField);
        passwordTextView = (TextView) findViewById(R.id.newPasswordField);
        confirmTextView = (TextView) findViewById(R.id.confirmField);
        //button
        loginButton = findViewById(R.id.CreateAccButton);
        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference("users");

    }
    public void CreateAcc(View view){
        Log.i("Button Clicked", "CreateACC button was clicked");
        //Intent intent = new Intent(SignUpActivity.this, SnapMain.class);
        //startActivity(intent);

        String email = emailTextView.getText().toString();
        String password = passwordTextView.getText().toString();
        String confirmPassword = confirmTextView.getText().toString();
        if(password.equals(confirmTextView.getText().toString())){
            Log.i("Button Clicked", "passwords are the same");
            //create account
            /**/
            auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d("CreateAcc", "createUserWithEmail:success");
                                //FirebaseUser user = auth.getCurrentUser();

                                //adduser to DB
                                FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid()).child("email").setValue(task.getResult().getUser().getEmail());
                                //Toast.makeText(SignUpActivity.this, task.getResult().getUser().getUid() +"\n"+task.getResult().getUser().getEmail(), Toast.LENGTH_SHORT).show();

                                Toast.makeText(SignUpActivity.this, "Authentication Succeeded.", Toast.LENGTH_SHORT).show();

                                //switch to main_menu Activity
                                Intent intent = new Intent(SignUpActivity.this, SnapMain.class);
                                startActivity(intent);

                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w("CreateAcc", "createUserWithEmail:failure", task.getException());
                            }                                Toast.makeText(SignUpActivity.this, task.getException().toString(), Toast.LENGTH_SHORT).show();


                            // ...
                        }
                    });
            //switch activity
            /**/
        }
        else{
            Toast.makeText(SignUpActivity.this, "Passwords don't match.", Toast.LENGTH_SHORT).show();

        }
    }

}
