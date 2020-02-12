package com.example.myapplication;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;

import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    public DBManager dbHelper;


    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        dbHelper = new DBManager(this);
        dbHelper.openDatabase();

        final SQLiteDatabase database;
        database = dbHelper.getDatabase();

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login(database);
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });
    }

    public void login(SQLiteDatabase database) {
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);

        final ProgressDialog progressDialog = new ProgressDialog(LoginActivity.this,
                R.style.AppTheme_Dark_Dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage("Authenticating...");
        progressDialog.show();

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        onLoginSuccess(database,email,password);
                        progressDialog.dismiss();
                    }
                }, 1500);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public void onLoginSuccess(SQLiteDatabase database, String email, String password) {
        Cursor cursor = database.query("User", null, null, null, null, null, null);
        int flag = 0;
        if (cursor.moveToFirst()) {
            do{
                System.out.println(cursor.getString(cursor.getColumnIndex("id")));
                System.out.println(cursor.getString(cursor.getColumnIndex("name")));
                System.out.println(cursor.getString(cursor.getColumnIndex("mail")));
                if(email.equals(cursor.getString(cursor.getColumnIndex("mail")))
                        &&password.equals(cursor.getString(cursor.getColumnIndex("password")))){
                    flag = 1;
                    break;
                }
            }while (cursor.moveToNext());
        }

        if(flag==1){
            _loginButton.setEnabled(true);
            System.out.println("finish!!!!!!");
            Bundle bundle = new Bundle();
            bundle.putString("id", cursor.getString(cursor.getColumnIndex("id")));
            cursor.close();
            Intent intent = new Intent(getApplicationContext(), MainPageActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
            finish();
        }else{
            cursor.close();
            onLoginFailed();
        }
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        System.out.println("Failed!!!!!!");
        _loginButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }
}
