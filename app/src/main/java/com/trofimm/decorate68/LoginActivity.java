package com.trofimm.decorate68;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private EditText loginEdit, passEdit;
    private Button startButton, signOutButton, regEditButton;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (currentUser != null){
            regEditButton.setVisibility(View.GONE);
            loginEdit.setText(currentUser.getEmail());
        } else {
            regEditButton.setVisibility(View.VISIBLE);
        }
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();
        loginEdit = findViewById(R.id.loginEdit);
        passEdit = findViewById(R.id.passwordEdit);
        startButton = findViewById(R.id.startButton);
        signOutButton = findViewById(R.id.sign_out);
        regEditButton = findViewById(R.id.regEditButton);
        progressBar = findViewById(R.id.progressBarLogin);
    }
    public void onClickStart(View view) {
        progressBar.setVisibility(View.VISIBLE);
        String login = loginEdit.getText().toString();
        String password = passEdit.getText().toString();
        if (!TextUtils.isEmpty(login)
                && (!TextUtils.isEmpty(password))) {
            signIn(login, password);
        } else {
            progressBar.setVisibility(View.GONE);
            Toast.makeText(LoginActivity.this, "Заполните поля ввода!!!"
                    ,Toast.LENGTH_LONG).show();
        }
    }

    private void showAutorization(){
        progressBar.setVisibility(View.GONE);
        Intent i = new Intent(LoginActivity.this, SignInActivity.class);
        startActivity(i);
    }



    public void onClickSignOut(View view) {
        progressBar.setVisibility(View.GONE);
        mAuth.signOut();
        loginEdit.setText("");
        passEdit.setText("");
        regEditButton.setVisibility(View.VISIBLE);
    }

    public void onClickNewSignIn(View view) {
        progressBar.setVisibility(View.GONE);
        showAutorization();
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Intent i = new Intent(LoginActivity.this, OrderActivity.class);
                            progressBar.setVisibility(View.GONE);
                            startActivity(i);
                        } else {
                            Toast.makeText(LoginActivity.this, "Нет такого пользователя...",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
        // [END sign_in_with_email]
    }
}