package com.trofimm.decorate68;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private EditText email, pass;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        init();
    }

    private void init(){
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.eMAil);
        pass = findViewById(R.id.pass);
        progressBar = findViewById(R.id.progressBarSign);
    }

    public void onClickEnterButton(View view) {
        String eMail = String.valueOf(email.getText());
        String password = String.valueOf(pass.getText());
        if ((!eMail.equals("")) && (!password.equals(""))) {
            if (password.length() > 6) {
            progressBar.setVisibility(View.VISIBLE);
            createAccount(eMail, password);
            } else {
                Toast.makeText(SignInActivity.this, "Пароль должен содержать не менее 6 символов!"
                                , Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void onClickCancelRegEdit(View view) {
        progressBar.setVisibility(View.GONE);
        email.setText("");
        pass.setText("");
        showStartScreen();
    }

    private void createAccount(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(SignInActivity.this, "Вы успешно зарегистрировались"
                                    , Toast.LENGTH_LONG).show();
                            showStartScreen();
                        } else {
                            Toast.makeText(SignInActivity.this, "Попробуйте еще раз",
                                    Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        }
                    }
                });
    }

    public void showStartScreen(){
        progressBar.setVisibility(View.GONE);
        Intent i = new Intent(SignInActivity.this, LoginActivity.class);
        startActivity(i);
    }
}